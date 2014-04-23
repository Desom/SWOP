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
import domain.assembly.Workstation;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.OrderManager;
import domain.policies.InvalidConfigurationException;
import domain.user.CarMechanic;

public class AssemblyLineTest {


	private AssemblyLine line;
	private AssemblyLineScheduler scheduler;
	private CarMechanic m1;
	private CarMechanic m2;
	private CarMechanic m3;

	@Before
	public void testCreate() throws DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		m1 = new CarMechanic(2);
		m2 = new CarMechanic(3);
		m3 = new CarMechanic(4);

		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		this.scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
		Statistics statistics = new Statistics(orderManager);
		line = new AssemblyLine(scheduler, statistics);

		line.selectWorkstationById(1).addCarMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getCarMechanic(), m1);

		line.selectWorkstationById(2).addCarMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getCarMechanic(), m2);

		line.selectWorkstationById(3).addCarMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getCarMechanic(), m3);

		fullDefaultAdvance();
		fullDefaultAdvance();

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
	public void testAdvanceLineSucces() throws DoesNotExistException, CannotAdvanceException, InternalFailureException, NoOrdersToBeScheduledException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesBefore.add(w.getCarAssemblyProcess());
		}
		
		Order order = null;
		try{
			order = scheduler.seeNextOrder(60);
		}
		catch(NoOrdersToBeScheduledException e){}
		
		CarAssemblyProcess next;
		if(order != null){
			next = order.getAssemblyprocess();
		}else{
			next = null;
		}
		
		fullDefaultAdvance();

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
				assertTrue(list.containsAll(current.getAllTasksAt(i)));
				assertTrue(current.getAllTasksAt(i).containsAll(list));
				
				if(current.getCarOrderIdAt(i) != -1){
					assertEquals(current.getCarOrderIdAt(i), line.selectWorkstationById(i).getCarAssemblyProcess().getOrder().getCarOrderID());
				}
				assertTrue(current.getHeader().compareToIgnoreCase("Current Status") == 0);
			}

		} catch (DoesNotExistException e) {
			fail();
		}
	}

	/*@Test
	public void testFutureStatus() throws InternalFailureException {
		try {

			AssemblyStatusView future = line.futureStatus(100);
			
			fullDefaultAdvance();
			
			AssemblyStatusView current = line.currentStatus();
			for(int i : line.getWorkstationIDs()){
				assertEquals(current.getAllTasksAt(i), future.getAllTasksAt(i));
				assertEquals(current.getCarOrderIdAt(i), future.getCarOrderIdAt(i));
			}
			for(int i=0; i<current.getAllWorkstationIds().length; i++){
				assertEquals(current.getAllWorkstationIds()[i], future.getAllWorkstationIds()[i]);
			}
			assertEquals(future.getHeader().toLowerCase(), "Future Status".toLowerCase());
		} catch (DoesNotExistException e) {
			fail();
		} catch (CannotAdvanceException e) {
			fail();
		}
	}*/

	
	/**
	 * This method is only to be used for testing. It will complete all tasks the workstations are currently working on.
	 * It will complete those tasks in a way where the time spent on each workstation is the expected time for that specific car order.
	 * When the last workstation finishes it's last task the line will ofcourse automatically advance.
	 * 
	 * @throws InternalFailureException 
	 * @throws IllegalStateException 
	 * @throws CannotAdvanceException 
	 */
	private void fullDefaultAdvance() throws IllegalStateException, InternalFailureException, CannotAdvanceException{
		LinkedList<Workstation> wList = line.getAllWorkstations();
		LinkedList<Workstation> remove = new LinkedList<Workstation>();
		for(Workstation w: wList){ // filter the already completed workstations so the line won't accidentally advance twice.
			if(w.hasAllTasksCompleted()){
				remove.add(w);
			}
		}
		wList.removeAll(remove);
		
		if(wList.isEmpty())
			line.advanceLine();
		for(Workstation w : wList){
			CarMechanic mechanic = w.getCarMechanic();
			if(mechanic == null)
				mechanic = new CarMechanic(100*w.getId()); // randomize ID een beetje
			while(w.getAllPendingTasks().size() > 1){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,0);
			}
			if(w.getAllPendingTasks().size() != 0){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,w.getCarAssemblyProcess().getOrder().getConfiguration().getExpectedWorkingTime());
			}
		}
	}
	
	
}
