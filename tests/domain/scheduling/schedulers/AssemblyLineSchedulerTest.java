package domain.scheduling.schedulers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.InternalFailureException;
import domain.Statistics;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.AssemblyLineCreator;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.BrokenStatus;
import domain.assembly.assemblyline.status.MaintenanceStatus;
import domain.assembly.assemblyline.status.OperationalStatus;
import domain.assembly.assemblyline.status.StatusCreator;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.configuration.taskables.Option;
import domain.configuration.taskables.OptionType;
import domain.configuration.taskables.TaskTypeCreator;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.scheduling.NoOrdersToBeScheduledException;
import domain.scheduling.order.Order;
import domain.scheduling.order.OrderManager;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.algorithm.AlgorithmCreator;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FIFOSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.user.Mechanic;
import domain.user.CustomShopManager;

public class AssemblyLineSchedulerTest {

	private AssemblyLine line;
	private AssemblyLineScheduler scheduler;
	private Order order1;
	private Order order2;
	private Order order3;
	private Order order50;
	private ArrayList<Order> unFinishedOrdered;
	private ArrayList<AssemblyLineStatus> statuses;
	private VehicleCatalog catalog;
	private Mechanic m1;
	private Mechanic m2;
	private Mechanic m3;
	
	@Before
	public void create() throws InvalidConfigurationException, IOException, VehicleCatalogException, DoesNotExistException{
		WorkstationTypeCreator workstationTypeCreator = new WorkstationTypeCreator();
		catalog = new VehicleCatalog(workstationTypeCreator);
		StatusCreator statusCreator = new StatusCreator();
		SchedulerCreator schedulerCreator = new SchedulerCreator(new AlgorithmCreator());
		line =new AssemblyLineCreator(workstationTypeCreator,schedulerCreator,statusCreator,catalog).create().get(1);
		ArrayList<AssemblyLineScheduler>	list = new ArrayList<AssemblyLineScheduler>();
		list.add(line.getAssemblyLineScheduler());
		
		this.scheduler = line.getAssemblyLineScheduler();
		this.scheduler.addCurrentTime(360);
		OrderManager orderManager = new OrderManager(schedulerCreator.createFactoryScheduler(list), "testData/testData_OrderManager.txt", catalog);
		ArrayList<Order> unfinished = orderManager.getAllUnfinishedOrders();
		FIFOSchedulingAlgorithm fifo = new FIFOSchedulingAlgorithm();
		unFinishedOrdered = fifo.scheduleToList(unfinished, null);
		order1 = unFinishedOrdered.get(0);
		assertEquals(2,order1.getOrderID());
		order2 = unFinishedOrdered.get(1);
		assertEquals(3,order2.getOrderID());
		order3 = unFinishedOrdered.get(2);
		assertEquals(4,order3.getOrderID());
		order50 = unFinishedOrdered.get(51);
		assertEquals(53,order50.getOrderID());
		
		m1 = new Mechanic(2);
		m2 = new Mechanic(3);
		m3 = new Mechanic(4);
		
		line.selectWorkstationById(1).addMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getMechanic(), m1);

		line.selectWorkstationById(2).addMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getMechanic(), m2);

		line.selectWorkstationById(3).addMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getMechanic(), m3);		
	}
	@Test
	public void testCompletionEstimate() {
		assertEquals(new GregorianCalendar(2014, 0, 1, 15, 10, 0), scheduler.completionEstimate(order1));
		assertEquals(new GregorianCalendar(2014, 0, 1, 16, 20, 0), scheduler.completionEstimate(order2));
		assertEquals(new GregorianCalendar(2014, 0, 1, 17, 20, 0), scheduler.completionEstimate(order3));
		assertEquals(new GregorianCalendar(2014, 0, 1, 18, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(3)));
		assertEquals(new GregorianCalendar(2014, 0, 1, 19, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(4)));
		assertEquals(new GregorianCalendar(2014, 0, 1, 20, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(5)));
		assertEquals(new GregorianCalendar(2014, 0, 1, 21, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(6)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 9, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(7)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 10, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(8)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 11, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(9)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 12, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(10)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 13, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(11)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 14, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(12)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 15, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(13)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 16, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(14)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 17, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(15)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 18, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(16)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 19, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(17)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 20, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(18)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 21, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(19)));
		assertEquals(new GregorianCalendar(2014, 0, 2, 22, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(20)));
		assertEquals(new GregorianCalendar(2014, 0, 3, 9, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(21)));
		assertEquals(new GregorianCalendar(2014, 0, 3, 10, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(22)));
		assertEquals(new GregorianCalendar(2014, 0, 3, 11, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(23)));
		assertEquals(new GregorianCalendar(2014, 0, 3, 12, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(24)));
		assertEquals(new GregorianCalendar(2014, 0, 3, 13, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(25)));
		
	}

	@Test
	public void testCanFinishOrderBeforeDeadline() throws InvalidConfigurationException, VehicleCatalogException {
		SingleTaskOrder singleTask1 = this.createSingleTask(new GregorianCalendar(2014, 1, 1, 12, 0, 0));
		assertTrue(scheduler.canFinishOrderBeforeDeadline(singleTask1));
		SingleTaskOrder singleTask2 = this.createSingleTask(new GregorianCalendar(2014, 0, 1, 3, 0, 0));
		assertFalse(scheduler.canFinishOrderBeforeDeadline(singleTask2));
		SingleTaskOrder singleTask3 = this.createSingleTask(new GregorianCalendar(2014, 0, 1, 16, 10, 0));
		assertTrue(scheduler.canFinishOrderBeforeDeadline(singleTask3));
		SingleTaskOrder singleTask4 = this.createSingleTask(new GregorianCalendar(2014, 0, 1, 16, 0, 0));
		assertFalse(scheduler.canFinishOrderBeforeDeadline(singleTask4));
		SingleTaskOrder singleTask5 = this.createSingleTask(new GregorianCalendar(2014, 0, 1, 16, 20, 0));
		assertTrue(scheduler.canFinishOrderBeforeDeadline(singleTask5));
	}

	@Test
	public void getNextCar() throws NoOrdersToBeScheduledException, IllegalStateException, InternalFailureException, CannotAdvanceException {
		Order next = scheduler.seeNextOrder(50);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(70);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(70);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(70);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		assertEquals(null, next);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		assertEquals(null, next);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		assertEquals(null, next);
		this.fullDefaultAdvance();
		assertEquals(unFinishedOrdered.get(7), scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
		next = scheduler.seeNextOrder(60);
		this.fullDefaultAdvance();
		assertEquals(next, scheduler.getAssemblyLine().getAllOrders().get(0));
	}

	@Test
	public void testOverTime() throws IllegalStateException, InternalFailureException, CannotAdvanceException {

		assertEquals(new GregorianCalendar(2014,0,1,22,0,0), scheduler.getRealEndOfDay());
		fullDefaultAdvance(600);
		assertEquals(new GregorianCalendar(2014,0,1,22,0,0), scheduler.getCurrentTime());
		fullDefaultAdvance(60);
		fullDefaultAdvance(30);
		assertEquals(new GregorianCalendar(2014,0,2,20,30,0), scheduler.getRealEndOfDay());
		fullDefaultAdvance(840);
		fullDefaultAdvance(25);
		fullDefaultAdvance(52);
		assertEquals(new GregorianCalendar(2014,0,3,21,13,0), scheduler.getRealEndOfDay());
	}

	@Test
	public void testSetSchedulingAlgorithm() throws NoOrdersToBeScheduledException {
		BasicSchedulingAlgorithm basicSchedulingAlgorithm = (BasicSchedulingAlgorithm) scheduler.getPossibleAlgorithms().get(1);
		SpecificationBatchSchedulingAlgorithm specBatch = (SpecificationBatchSchedulingAlgorithm) basicSchedulingAlgorithm.getInnerAlgorithm();
		ArrayList<Configuration> batchable = specBatch.searchForBatchConfiguration(scheduler);
		specBatch.setConfiguration(batchable.get(0));
		scheduler.setSchedulingAlgorithm(scheduler.getPossibleAlgorithms().get(1));
		
		assertEquals(basicSchedulingAlgorithm, scheduler.getCurrentAlgorithm());
		
		assertEquals(order3, scheduler.seeNextOrder(10));
	}
	
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
			Mechanic mechanic = w.getMechanic();
			if(mechanic == null)
				mechanic = new Mechanic(w.getName().hashCode()); // randomize ID een beetje
			while(w.getAllPendingTasks().size() > 1){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,0);
			}
			if(w.getAllPendingTasks().size() != 0){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,w.getVehicleAssemblyProcess().getOrder().getConfiguration().getExpectedWorkingTime(w.getWorkstationType()));
			}
		}
	}
	
	private void fullDefaultAdvance(int duration) throws IllegalStateException, InternalFailureException, CannotAdvanceException{
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
			Mechanic mechanic = w.getMechanic();
			if(mechanic == null)
				mechanic = new Mechanic(w.getName().hashCode()); // randomize ID een beetje
			while(w.getAllPendingTasks().size() > 1){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,0);
			}
			if(w.getAllPendingTasks().size() != 0){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,duration);
			}
		}
	}
	

	private SingleTaskOrder createSingleTask(GregorianCalendar now) throws InvalidConfigurationException, VehicleCatalogException{
		
		Policy singleTaskPolicy = new SingleTaskOrderNumbersOfTasksPolicy(null);
		Configuration config = new Configuration(null, singleTaskPolicy);
		config.addOption(new Option("test",catalog.taskTypeCreator.Color));
		config.complete();
		CustomShopManager customShop = new CustomShopManager(1);
		
		return new SingleTaskOrder(0, customShop, config, now, now);
	}

}
