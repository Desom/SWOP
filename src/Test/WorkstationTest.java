package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import Main.AssemblyTask;
import Main.CarMechanic;
import Main.GarageHolder;
import Main.Manager;
import Main.UserAccessException;
import Main.Workstation;

public class WorkstationTest {

	private static CarMechanic carMechanic;
	private static GarageHolder garageHolder;
	private static Manager manager;
	private static Workstation workstation;
	private static AssemblyTask validTask;
	private static AssemblyTask invalidTask;
	
	@BeforeClass
	public static void testCreate() throws UserAccessException {
		carMechanic = new CarMechanic(1);
		garageHolder = new GarageHolder(2);
		manager = new Manager(3);
		ArrayList<String> taskTypes = new ArrayList<String>();
		taskTypes.add("Task type 1");
		taskTypes.add("Task type 2");
		taskTypes.add("Task type 3");
		workstation = new Workstation(1, taskTypes);
		assertEquals(1, workstation.getId());
		assertEquals(taskTypes, workstation.getTaskTypes());
		assertTrue(workstation.getAllCompletedTasks(carMechanic).isEmpty());
		assertTrue(workstation.getAllPendingTasks(carMechanic).isEmpty());
		
		ArrayList<String> actions1 = new ArrayList<String>();
		actions1.add("action1");
		actions1.add("action2");
		String type1 = "Task type 2";
		validTask = new AssemblyTask(actions1, type1);
		
		ArrayList<String> actions2 = new ArrayList<String>();
		actions2.add("action1");
		actions2.add("action2");
		String type2 = "invalidType";
		invalidTask = new AssemblyTask(actions2, type2);
	}
	
	@Test
	public void testCompleteTask() throws IllegalStateException, UserAccessException {
		try {
			workstation.completeTask(garageHolder);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		workstation.completeTask(carMechanic);
		assertTrue(workstation.getAllCompletedTasks(carMechanic).contains(validTask));
		assertTrue(workstation.getAllPendingTasks(carMechanic).isEmpty());
	}
	
	@Test
	public void testSelectTask() throws IllegalStateException, UserAccessException, IllegalArgumentException {
		try {
			workstation.selectTask(carMechanic, invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.selectTask(carMechanic, validTask);
		assertEquals(validTask.getType(), workstation.getActiveTaskInformation(carMechanic).get(0));
		try {
			workstation.selectTask(carMechanic, validTask);
			assertTrue("IllegalStateException was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
	}
	
	
	@Test
	public void testAddTask() throws IllegalArgumentException, UserAccessException {
		try {
			workstation.addAssemblyTask(garageHolder, validTask);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.addAssemblyTask(carMechanic, validTask);
			assertTrue("UserAccessException was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.addAssemblyTask(manager, invalidTask);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.addAssemblyTask(manager, validTask);
		assertTrue(workstation.getAllCompletedTasks(carMechanic).isEmpty());
		assertTrue(workstation.getAllPendingTasks(carMechanic).contains(validTask));
	}
	
	@Test
	public void testAddCarMechanic() {
		try {
			workstation.addCarMechanic(garageHolder);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		try {
			workstation.addCarMechanic(manager);
			assertTrue("IllegalArgumentException was not thrown", false);
		}
		catch (IllegalArgumentException e) {
		}
		workstation.addCarMechanic(carMechanic);
	}
	
	@Test
	public void testCompleteTaskWithoutCarMechanic() throws IllegalStateException, UserAccessException {
		try {
			workstation.completeTask(manager);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (UserAccessException e) {
		}
		try {
			workstation.completeTask(carMechanic);
			assertTrue("IllegalArgumentExcpetion was not thrown", false);
		}
		catch (IllegalStateException e) {
		}
	}
}
