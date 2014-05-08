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
	private AssemblyTask validTask2;
	private AssemblyTask invalidTask;
	
	@Before
	public void testCreate() throws InvalidConfigurationException, IOException, CarModelCatalogException, InternalFailureException {
		carMechanic = new CarMechanic(1);
		Company comp = new Company();
		ArrayList<OptionType> taskTypes = new ArrayList<OptionType>();
		taskTypes.add(OptionType.Seats);
		taskTypes.add(OptionType.Airco);
		taskTypes.add(OptionType.Wheels);
		taskTypes.add(OptionType.Spoiler);
		workstation = comp.getAllWorkstations().get(2);
		assertEquals("3", workstation.getName());
		assertEquals(taskTypes, workstation.getTaskTypes());
		assertTrue(workstation.getAllCompletedTasks().isEmpty());
		assertTrue(workstation.getAllPendingTasks().isEmpty());
		
		
		CarOrder order = createCar();
		validTask = workstation.compatibleWith(order.getAssemblyprocess()).get(0);
		validTask2 = workstation.compatibleWith(order.getAssemblyprocess()).get(1);
		
		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Gearbox);
		Workstation workstation2 = new Workstation(null, "W1", taskTypes2);
		invalidTask = workstation2.compatibleWith(order.getAssemblyprocess()).get(0);
		
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
		workstation.addAssemblyTask(validTask2);
		workstation.addCarMechanic(carMechanic);
		workstation.selectTask(validTask);
		workstation.completeTask(carMechanic,60);
		assertTrue(workstation.getAllCompletedTasks().contains(validTask));
		workstation.selectTask(validTask2);
		workstation.completeTask(carMechanic,60);
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
	
		Policy pol1 = new CompletionPolicy(null, OptionType.getAllMandatoryTypes());
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
					||option.getDescription().equals("manual")
					||option.getDescription().equals("comfort")
					)
				config.addOption(option);
		}
		config.complete();
		
		GarageHolder garageHolder = new GarageHolder(1);
		
		GregorianCalendar now = new GregorianCalendar();
		CarOrder carOrder = new CarOrder(1, garageHolder, config, now);
		return carOrder;
	}
}
