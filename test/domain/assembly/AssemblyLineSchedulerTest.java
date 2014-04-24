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
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.Order;
import domain.order.OrderManager;
import domain.order.SingleTaskOrder;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.user.CarMechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;

public class AssemblyLineSchedulerTest {

	private AssemblyLine line;
	private AssemblyLineScheduler scheduler;
	private Order order1;
	private Order order2;
	private Order order3;
	private Order order50;
	private ArrayList<Order> unFinishedOrdered;

	private CarMechanic m1;
	private CarMechanic m2;
	private CarMechanic m3;
	
	@Before
	public void create() throws InvalidConfigurationException, IOException, CarModelCatalogException, DoesNotExistException{
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		this.scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
		Statistics statistics = new Statistics(orderManager);
		line = new AssemblyLine(scheduler, statistics);

		ArrayList<Order> unfinished = orderManager.getAllUnfinishedOrders();
		FIFOSchedulingAlgorithm fifo = new FIFOSchedulingAlgorithm();
		unFinishedOrdered = fifo.scheduleToList(unfinished, null);
		order1 = unFinishedOrdered.get(0);
		assertEquals(2,order1.getCarOrderID());
		order2 = unFinishedOrdered.get(1);
		assertEquals(3,order2.getCarOrderID());
		order3 = unFinishedOrdered.get(2);
		assertEquals(4,order3.getCarOrderID());
		order50 = unFinishedOrdered.get(51);
		assertEquals(53,order50.getCarOrderID());
		
		m1 = new CarMechanic(2);
		m2 = new CarMechanic(3);
		m3 = new CarMechanic(4);
		
		line.selectWorkstationById(1).addCarMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getCarMechanic(), m1);

		line.selectWorkstationById(2).addCarMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getCarMechanic(), m2);

		line.selectWorkstationById(3).addCarMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getCarMechanic(), m3);		
	}
	@Test
	public void testCompletionEstimate() {
		assertEquals(new GregorianCalendar(2014, 1, 1, 15, 10, 0), scheduler.completionEstimate(order1));
		assertEquals(new GregorianCalendar(2014, 1, 1, 16, 20, 0), scheduler.completionEstimate(order2));
		assertEquals(new GregorianCalendar(2014, 1, 1, 17, 20, 0), scheduler.completionEstimate(order3));
		assertEquals(new GregorianCalendar(2014, 1, 1, 18, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(3)));
		assertEquals(new GregorianCalendar(2014, 1, 1, 19, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(4)));
		assertEquals(new GregorianCalendar(2014, 1, 1, 20, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(5)));
		assertEquals(new GregorianCalendar(2014, 1, 1, 21, 20, 0), scheduler.completionEstimate(unFinishedOrdered.get(6)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 9, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(7)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 10, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(8)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 11, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(9)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 12, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(10)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 13, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(11)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 14, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(12)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 15, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(13)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 16, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(14)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 17, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(15)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 18, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(16)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 19, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(17)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 20, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(18)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 21, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(19)));
		assertEquals(new GregorianCalendar(2014, 1, 2, 22, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(20)));
		assertEquals(new GregorianCalendar(2014, 1, 3, 9, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(21)));
		assertEquals(new GregorianCalendar(2014, 1, 3, 10, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(22)));
		assertEquals(new GregorianCalendar(2014, 1, 3, 11, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(23)));
		assertEquals(new GregorianCalendar(2014, 1, 3, 12, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(24)));
		assertEquals(new GregorianCalendar(2014, 1, 3, 13, 0, 0), scheduler.completionEstimate(unFinishedOrdered.get(25)));
		
	}

	@Test
	public void testCanFinishOrderBeforeDeadline() throws InvalidConfigurationException, CarModelCatalogException {
		SingleTaskOrder singleTask1 = this.createSingleTask(new GregorianCalendar(2014, 2, 1, 12, 0, 0));
		assertTrue(scheduler.canFinishOrderBeforeDeadline(singleTask1));
		SingleTaskOrder singleTask2 = this.createSingleTask(new GregorianCalendar(2014, 1, 1, 3, 0, 0));
		assertFalse(scheduler.canFinishOrderBeforeDeadline(singleTask2));
		SingleTaskOrder singleTask3 = this.createSingleTask(new GregorianCalendar(2014, 1, 1, 16, 10, 0));
		assertTrue(scheduler.canFinishOrderBeforeDeadline(singleTask3));
		SingleTaskOrder singleTask4 = this.createSingleTask(new GregorianCalendar(2014, 1, 1, 16, 0, 0));
		assertFalse(scheduler.canFinishOrderBeforeDeadline(singleTask4));
		SingleTaskOrder singleTask5 = this.createSingleTask(new GregorianCalendar(2014, 1, 1, 16, 20, 0));
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

		assertEquals(new GregorianCalendar(2014,1,1,22,0,0), scheduler.getRealEndOfDay());
		fullDefaultAdvance(600);
		assertEquals(new GregorianCalendar(2014,1,1,22,0,0), scheduler.getCurrentTime());
		fullDefaultAdvance(60);
		fullDefaultAdvance(30);
		assertEquals(new GregorianCalendar(2014,1,2,20,30,0), scheduler.getRealEndOfDay());
		fullDefaultAdvance(840);
		fullDefaultAdvance(25);
		fullDefaultAdvance(52);
		assertEquals(new GregorianCalendar(2014,1,3,21,13,0), scheduler.getRealEndOfDay());
	}

	@Test
	public void testSetSchedulingAlgorithm() throws NoOrdersToBeScheduledException {
		SpecificationBatchSchedulingAlgorithm specBatch = (SpecificationBatchSchedulingAlgorithm) scheduler.getPossibleAlgorithms().get(1);
		ArrayList<Configuration> batchable = specBatch.searchForBatchConfiguration(scheduler);
		specBatch.setConfiguration(batchable.get(0));
		scheduler.setSchedulingAlgorithm(specBatch);
		
		assertEquals(specBatch, scheduler.getCurrentAlgorithm());
		
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
			CarMechanic mechanic = w.getCarMechanic();
			if(mechanic == null)
				mechanic = new CarMechanic(100*w.getId()); // randomize ID een beetje
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
	
	private CarOrder createCar() throws InvalidConfigurationException, IOException, CarModelCatalogException{

		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
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

	private SingleTaskOrder createSingleTask(GregorianCalendar now) throws InvalidConfigurationException, CarModelCatalogException{
		
		Policy singleTaskPolicy = new SingleTaskOrderNumbersOfTasksPolicy(null);
		Configuration config = new Configuration(null, singleTaskPolicy);
		config.addOption(new Option("test", OptionType.Color));
		config.complete();
		CustomShopManager customShop = new CustomShopManager(1);
		
		return new SingleTaskOrder(0, customShop, config, now, now);
	}

}
