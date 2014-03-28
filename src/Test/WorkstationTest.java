package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Assembly.AssemblyTask;
import Assembly.CarAssemblyProcess;
import Assembly.Workstation;
import Car.Car;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import Car.OptionType;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import User.CarMechanic;
import User.GarageHolder;

public class WorkstationTest {

	private CarMechanic carMechanic;
	private Workstation workstation;
	private AssemblyTask validTask;
	private AssemblyTask invalidTask;
	
	@Before
	public void testCreate() {
		carMechanic = new CarMechanic(1);
		ArrayList<OptionType> taskTypes = new ArrayList<OptionType>();
		taskTypes.add(OptionType.Airco);
		taskTypes.add(OptionType.Body);
		taskTypes.add(OptionType.Color);
		workstation = new Workstation(1, taskTypes);
		assertEquals(1, workstation.getId());
		assertEquals(taskTypes, workstation.getTaskTypes());
		assertTrue(workstation.getAllCompletedTasks().isEmpty());
		assertTrue(workstation.getAllPendingTasks().isEmpty());
		
		ArrayList<String> actions1 = new ArrayList<String>();
		actions1.add("action1");
		actions1.add("action2");
		OptionType type1 = OptionType.Body;
		validTask = new AssemblyTask(actions1, type1);
		
		ArrayList<String> actions2 = new ArrayList<String>();
		actions2.add("action1");
		actions2.add("action2");
		OptionType type2 = OptionType.Gearbox;
		invalidTask = new AssemblyTask(actions2, type2);
	}
	
	@Test
	public void testCar() throws IOException, CarModelCatalogException {
		GarageHolder holder = new GarageHolder(1);
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel model= catalog.getCarModel("Ford");
		ArrayList<Option> allOptions = model.getOptions();
		ArrayList<Option> selectedOptions = new ArrayList<Option>();
		selectedOptions.add(allOptions.get(0));
		selectedOptions.add(allOptions.get(1));
		selectedOptions.add(allOptions.get(2));
		selectedOptions.add(allOptions.get(3));
		selectedOptions.add(allOptions.get(4));
		selectedOptions.add(allOptions.get(5));
		selectedOptions.add(allOptions.get(6));
		CarOrder order = new CarOrder(1, holder, model, selectedOptions);
		Car car = order.getCar();
		CarAssemblyProcess process = car.getAssemblyprocess();
		try {
			workstation.getActiveTaskInformation();
			fail("No IllegalStateException was thrown");
		}
		catch (IllegalStateException e) {
		}
		
	}
	
	@Test
	public void testCompleteTask() throws IllegalStateException {
		workstation.addAssemblyTask(validTask);
		workstation.addCarMechanic(carMechanic);
		workstation.selectTask(validTask);
		workstation.completeTask(carMechanic);
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
		assertEquals(validTask.getActions().get(1), taskInformation.get(2));
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
	public void testCompleteTaskWithoutCarMechanic() throws IllegalStateException {
		try {
			workstation.completeTask(carMechanic);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
	}
}
