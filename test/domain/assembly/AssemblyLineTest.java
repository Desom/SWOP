package domain.assembly;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.InternalFailureException;
import domain.Statistics;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyStatusView;
import domain.assembly.CannotAdvanceException;
import domain.assembly.CarAssemblyProcess;
import domain.assembly.DoesNotExistException;
import domain.assembly.Scheduler;
import domain.assembly.Workstation;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.OrderManager;
import domain.policies.InvalidConfigurationException;
import domain.user.CarMechanic;

public class AssemblyLineTest {


	private AssemblyLine line;
	private Scheduler scheduler;
	private CarMechanic m1;
	private CarMechanic m2;
	private CarMechanic m3;

	@Before
	public void testCreate() throws DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		m1 = new CarMechanic(2);
		m2 = new CarMechanic(3);
		m3 = new CarMechanic(4);

		// maak een andere orderManager met enkele voorbeeld orders
		OrderManager orderManager = new OrderManager("testData/testData_OrderManager.txt", new CarModelCatalog() , new GregorianCalendar(2014, 1, 1, 12, 0, 0));
		scheduler = orderManager.getScheduler();
		line = new AssemblyLine(scheduler, new Statistics(orderManager));

		line.selectWorkstationById(1).addCarMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getCarMechanic(), m1);

		line.selectWorkstationById(2).addCarMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getCarMechanic(), m2);

		line.selectWorkstationById(3).addCarMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getCarMechanic(), m3);

		line.advanceLine();
		for(Workstation w : line.getAllWorkstations()){
			while(w.getAllPendingTasks().size() > 0){ // complete alle tasks
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(w.getCarMechanic(),60);
			}
		}
		line.advanceLine();
	}

	@Test
	public void testgetAllWorkStations(){
		LinkedList<Workstation> stations = line.getAllWorkstations();
		for(Workstation w : stations){
			assertNotNull(w);
		}
	}

	@Test
	public void testSelectWorkStationId() throws DoesNotExistException, InternalFailureException{
		assertNotNull(line.selectWorkstationById(1));
		assertEquals(line.selectWorkstationById(1).getId(), 1);

		assertNotNull(line.selectWorkstationById(2));
		assertEquals(line.selectWorkstationById(2).getId(), 2);

		assertNotNull(line.selectWorkstationById(3));
		assertEquals(line.selectWorkstationById(3).getId(), 3);
	}

	@Test
	public void testAdvanceLineSucces() throws DoesNotExistException, CannotAdvanceException, InternalFailureException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			while(w.getAllPendingTasks().size() > 0){ // complete alle tasks
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(w.getCarMechanic(),60);
			}
			processesBefore.add(w.getCarAssemblyProcess());
		}

		Order order = scheduler.seeNextOrder(100);
		CarAssemblyProcess next;
		if(order != null){
			next = order.getAssemblyprocess();
		}else{
			next = null;
		}
		
		line.advanceLine();

		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesAfter.add(w.getCarAssemblyProcess());
		}

		assertEquals(processesAfter.get(0), next);
		for(int i = 0; i<processesAfter.size()-1; i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i+1));
		}
	}

	@Test(expected = CannotAdvanceException.class)  
	public void testAdvanceLineBlocking() throws DoesNotExistException, CannotAdvanceException, InternalFailureException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesBefore.add(w.getCarAssemblyProcess());
		}

		line.advanceLine();

		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesAfter.add(w.getCarAssemblyProcess());
		}

		for(int i = 0; i<processesAfter.size(); i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i));
		}
	}

	@Test
	public void testCurrentStatus() throws InternalFailureException {
		try {
			AssemblyStatusView current = line.currentStatus();

			for(int i : line.getWorkstationIDs()){
				LinkedList<OptionType> list = new LinkedList<OptionType>();
				for(int j = 0; j<line.selectWorkstationById(i).getAllTasks().size() ; j++ ){
					list.add(line.selectWorkstationById(i).getAllTasks().get(j).getType());
				}
				assertEquals(current.getAllTasksAt(i), list);
				if(current.getCarOrderIdAt(i) != -1){
					assertEquals(current.getCarOrderIdAt(i), line.selectWorkstationById(i).getCarAssemblyProcess().getOrder().getCarOrderID());
				}
				assertTrue(current.getHeader().compareToIgnoreCase("Current Status") == 0);
			}

		} catch (DoesNotExistException e) {
			fail();
		}
	}

	@Test
	public void testFutureStatus() throws InternalFailureException {
		try {
			for(Workstation w : line.getAllWorkstations()){
				while(w.getAllPendingTasks().size() > 0){ // complete alle tasks
					w.selectTask(w.getAllPendingTasks().get(0));
					w.completeTask(w.getCarMechanic(),60);
				}
			}
			AssemblyStatusView future = line.futureStatus(100);
			line.advanceLine();
			AssemblyStatusView current = line.currentStatus();
			for(int i : line.getWorkstationIDs()){
				assertEquals(current.getAllTasksAt(i), future.getAllTasksAt(i));
				assertEquals(current.getCarOrderIdAt(i), future.getCarOrderIdAt(i));
			}
			for(int i=0; i<current.getAllWorkstationIds().length; i++){
				assertEquals(current.getAllWorkstationIds()[i], future.getAllWorkstationIds()[i]);
			}
			assertTrue(future.getHeader().compareToIgnoreCase("Future Status")== 0);
		} catch (DoesNotExistException e) {
			fail();
		} catch (CannotAdvanceException e) {
			fail();
		}
	}

	/*@Test
	public void testAccessOk() throws InternalFailureException, CannotAdvanceException, IllegalStateException, IllegalArgumentException, UserAccessException{
		Manager manager = new Manager(1);
		CarMechanic mechanic = new CarMechanic(2);
		
		for(Workstation w : line.getAllWorkstations(manager)){
			while(w.getAllPendingTasks(w.getCarMechanic()).size() > 0){ // complete alle tasks
				w.selectTask(w.getCarMechanic(), w.getAllPendingTasks(w.getCarMechanic()).get(0));
				w.completeTask(w.getCarMechanic());
			}
		}
		
		try {
			line.advanceLine(manager, 100);
			line.currentStatus(manager);
			line.futureStatus(manager, 100);
			line.selectWorkstationById(1, manager);
			line.getAllWorkstations(manager);
			
			line.selectWorkstationById(1, mechanic);
			line.getAllWorkstations(mechanic);
		} catch (UserAccessException e) {
			fail();
		}
	}*/
}
