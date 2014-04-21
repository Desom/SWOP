package domain.assembly;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.InternalFailureException;
import domain.assembly.AssemblyTask;
import domain.assembly.Workstation;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.user.CarMechanic;
import domain.user.GarageHolder;

public class WorkstationTest {

	private CarMechanic carMechanic;
	private Workstation workstation;
	private AssemblyTask validTask;
	private AssemblyTask invalidTask;
	
	@Before
	public void testCreate() throws InvalidConfigurationException, IOException, CarModelCatalogException, InternalFailureException {
		carMechanic = new CarMechanic(1);
		Company comp = new Company();
		ArrayList<OptionType> taskTypes = new ArrayList<OptionType>();
		taskTypes.add(OptionType.Body);
		taskTypes.add(OptionType.Color);
		workstation = comp.getAllWorkstations().getFirst();
		assertEquals(1, workstation.getId());
		assertEquals(taskTypes, workstation.getTaskTypes());
		assertTrue(workstation.getAllCompletedTasks().isEmpty());
		assertTrue(workstation.getAllPendingTasks().isEmpty());
		
		
		CarOrder order = createCar();
		validTask = order.getAssemblyprocess().compatibleWith(workstation).get(0);
		
		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Gearbox);
		Workstation workstation2 = new Workstation(null, 1, taskTypes2);
		invalidTask = order.getAssemblyprocess().compatibleWith(workstation2).get(0);
		
	}
	
	@Test
	public void testCar() throws IOException, CarModelCatalogException {
		workstation.clear();
		try {
			workstation.getActiveTaskInformation();
			fail("No IllegalStateException was thrown");
		}
		catch (IllegalStateException e) {
		}
		
	}
	
	@Test
	public void testCompleteTask() throws IllegalStateException, InternalFailureException {
		workstation.addAssemblyTask(validTask);
		workstation.addCarMechanic(carMechanic);
		workstation.selectTask(validTask);
		workstation.completeTask(carMechanic,60);
		assertTrue(workstation.getAllCompletedTasks().contains(validTask));
		assertTrue(workstation.getAllPendingTasks().isEmpty());
		assertTrue(workstation.hasAllTasksCompleted());
	}
	
	@Test
	public void testSelectTask() throws IllegalStateException, IllegalArgumentException {
		workstation.addAssemblyTask(validTask);
		workstation.addCarMechanic(carMechanic);
		try {
			workstation.selectTask(invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.selectTask(validTask);
		assertEquals(validTask.getType().toString(), workstation.getActiveTaskInformation().get(0));
		try {
			workstation.selectTask(validTask);
			assertTrue("IllegalStateException was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
		ArrayList<String> taskInformation = workstation.getActiveTaskInformation();
		assertEquals(validTask.getType().toString(), taskInformation.get(0));
		assertEquals(validTask.getActions().get(0), taskInformation.get(1));
	}
	
	
	@Test
	public void testAddTask() throws IllegalArgumentException {
		try {
			workstation.addAssemblyTask(invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.addAssemblyTask(validTask);
		assertTrue(workstation.getAllCompletedTasks().isEmpty());
		assertTrue(workstation.getAllPendingTasks().contains(validTask));
	}
	
	@Test
	public void testCompleteTaskWithoutCarMechanic() throws IllegalStateException, InternalFailureException {
		try {
			workstation.completeTask(carMechanic,60);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
	}
	
	
	
	private CarOrder createCar() throws InvalidConfigurationException, IOException, CarModelCatalogException{
		ArrayList<OptionType> List = new ArrayList<OptionType>();
		for(OptionType i: OptionType.values()){
			if(i != OptionType.Airco || i != OptionType.Spoiler ){
				List.add(i);
			}
		}
		Policy pol1 = new CompletionPolicy(null,List);
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;
		
		
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel carModel = null;
		for(CarModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}
		
		Configuration config = new Configuration(carModel, carOrderPolicy);
		
		for(Option option : catalog.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("no airco")
					||option.getDescription().equals("comfort")
					||option.getDescription().equals("no spoiler")
					)
				config.addOption(option);
		}
		
		GarageHolder garageHolder = new GarageHolder(1);
		
		GregorianCalendar now = new GregorianCalendar();
		CarOrder carOrder = new CarOrder(1, garageHolder, config, now);
		return carOrder;
	}
}
