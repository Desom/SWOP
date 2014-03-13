package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import Assembly.AssemblyLine;
import Assembly.AssemblyStatusView;
import Assembly.CannotAdvanceException;
import Assembly.CarAssemblyProcess;
import Assembly.DoesNotExistException;
import Assembly.ProductionSchedule;
import Assembly.Workstation;
import Main.InternalFailureException;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import Order.OrderManager;
import User.CarMechanic;
import User.Manager;
import User.UserAccessException;

public class AssemblyLineTest {

	
	private AssemblyLine line;
	private ProductionSchedule schedule;
	private Manager manager;
	private CarMechanic m1;
	private CarMechanic m2;
	private CarMechanic m3;
	
	@Before
	public void testCreate() throws UserAccessException, DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException {
		manager = new Manager(1);
		m1 = new CarMechanic(2);
		m2 = new CarMechanic(3);
		m3 = new CarMechanic(4);
		
		// maak een andere orderManager met enkele voorbeeld orders
		OrderManager orderManager = new OrderManager("testData_OrderManager.txt", new CarModelCatalog(), new GregorianCalendar(2014, 1, 1, 12, 0, 0));
		schedule = orderManager.getProductionSchedule();
		line = new AssemblyLine(schedule);
		
		line.selectWorkstation(m1, 1);
		assertEquals(line.selectWorkstationById(1, manager).getCarMechanic(), m1);
		
		line.selectWorkstation(m2, 2);
		assertEquals(line.selectWorkstationById(2, manager).getCarMechanic(), m2);
		
		line.selectWorkstation(m3, 3);
		assertEquals(line.selectWorkstationById(3, manager).getCarMechanic(), m3);
		
		line.advanceLine(manager, 100);
		for(Workstation w : line.getAllWorkstations(manager)){
			while(w.getAllPendingTasks(w.getCarMechanic()).size() > 0){ // complete alle tasks
				w.selectTask(w.getCarMechanic(), w.getAllPendingTasks(w.getCarMechanic()).get(0));
				w.completeTask(w.getCarMechanic());
			}
		}
		line.advanceLine(manager, 100);
	}
	
	@Test
	public void testgetAllWorkStations() throws UserAccessException {
		LinkedList<Workstation> stations = line.getAllWorkstations(manager);
		for(Workstation w : stations){
			assertNotNull(w);
		}
	}
	
	@Test
	public void testSelectWorkStationId() throws DoesNotExistException, UserAccessException, InternalFailureException{
		assertNotNull(line.selectWorkstationById(1, manager));
		assertEquals(line.selectWorkstationById(1, manager).getId(), 1);
		
		assertNotNull(line.selectWorkstationById(2, manager));
		assertEquals(line.selectWorkstationById(2, manager).getId(), 2);
		
		assertNotNull(line.selectWorkstationById(3, manager));
		assertEquals(line.selectWorkstationById(3, manager).getId(), 3);
	}
	
	@Test
	public void testAdvanceLineSucces() throws UserAccessException, DoesNotExistException, CannotAdvanceException, InternalFailureException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations(manager)){
			while(w.getAllPendingTasks(w.getCarMechanic()).size() > 0){ // complete alle tasks
				w.selectTask(w.getCarMechanic(), w.getAllPendingTasks(w.getCarMechanic()).get(0));
				w.completeTask(w.getCarMechanic());
			}
			processesBefore.add(w.getCurrentCar());
		}
		
		CarAssemblyProcess next = schedule.seeNextCarOrder().getCar().getAssemblyprocess();
		line.advanceLine(manager, 100);
		
		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations(manager)){
			processesAfter.add(w.getCurrentCar());
		}
		
		assertEquals(processesAfter.get(0), next);
		for(int i = 0; i<processesAfter.size()-1; i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i+1));
		}
	}
	
	@Test(expected = CannotAdvanceException.class)  
	public void testAdvanceLineBlocking() throws UserAccessException, DoesNotExistException, CannotAdvanceException, InternalFailureException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations(manager)){
			processesBefore.add(w.getCurrentCar());
		}
		
		line.advanceLine(manager, 100);
		
		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations(manager)){
			processesAfter.add(w.getCurrentCar());
		}
		
		for(int i = 0; i<processesAfter.size(); i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i));
		}
	}
	
	@Test
	public void testCurrentStatus() throws InternalFailureException {
		try {
			AssemblyStatusView current = line.currentStatus(manager);
			
			for(int i : line.getWorkstationIDs()){
				assertEquals(current.getAllTasksAt(i), line.selectWorkstationById(i, manager).getAllTasks(manager));
				assertEquals(current.getCarOrderIdAt(i), line.selectWorkstationById(i, manager).getCurrentCar().getCar().getOrder());
				assertTrue(current.getHeader().compareToIgnoreCase("Current Status") == 0);
			}
			
		} catch (UserAccessException | DoesNotExistException e) {
			fail();
		}
	}
	
	@Test
	public void testFutureStatus() throws InternalFailureException {
		try {
			AssemblyStatusView future = line.futureStatus(manager);
			line.advanceLine(manager, 100);
			AssemblyStatusView current = line.currentStatus(manager);
			for(int i : line.getWorkstationIDs()){
				assertEquals(current.getAllTasksAt(i), future.getAllTasksAt(i));
				assertEquals(current.getCarOrderIdAt(i), future.getCarOrderIdAt(i));
			}
			assertEquals(current.getAllWorkstationIds(), future.getAllWorkstationIds());
			assertTrue(current.getHeader().compareToIgnoreCase(future.getHeader()) == 0);
			
		} catch (UserAccessException | DoesNotExistException | CannotAdvanceException e) {
			fail();
		}
	}
	
	
}
