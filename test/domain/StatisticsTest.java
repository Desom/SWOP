package domain;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.CannotAdvanceException;
import domain.assembly.DoesNotExistException;
import domain.assembly.Scheduler;
import domain.assembly.Workstation;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.order.OrderManager;
import domain.policies.InvalidConfigurationException;
import domain.user.CarMechanic;

public class StatisticsTest {

	AssemblyLine line = null;
	Statistics stat = null;
	@Before
	public void testCreate() throws DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		GregorianCalendar time = new GregorianCalendar(2014, 9, 1, 6, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
		stat = new Statistics(orderManager);
		line = new AssemblyLine(scheduler, stat);
		
		
		CarMechanic m1 = new CarMechanic(2);
		CarMechanic m2 = new CarMechanic(3);
		CarMechanic m3 = new CarMechanic(4);
		
		line.selectWorkstationById(1).addCarMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getCarMechanic(), m1);

		line.selectWorkstationById(2).addCarMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getCarMechanic(), m2);

		line.selectWorkstationById(3).addCarMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getCarMechanic(), m3);
		
		// advance alle behalve laatste order
		while(orderManager.getAllUnfinishedOrders().size() != 1){
			fullDefaultAdvance();
		}
		
		//advance laatste order met delay
		
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
				w.completeTask(mechanic,500);
			}
		}
		fullDefaultAdvance();
		fullDefaultAdvance();
		fullDefaultAdvance();
	}
	
	@Test
	public void testAverageCars(){
		assertEquals(11, stat.getAverageCarsPerDay());
	}
	
	@Test
	public void testMedianCars(){
		assertEquals(13, stat.getMedianCarsPerDay());
	}
	
	@Test
	public void amountOfCarsLastDays(){
		assertEquals(13, stat.getAmountOfCars1DayAgo());
		assertEquals(13, stat.getAmountOfCars2DaysAgo());
	}
	
	@Test
	public void testAverageDelay(){
		assertEquals(440, stat.getMedianDelay());
	}
	
	@Test
	public void testMedianDelay(){
		assertEquals(440, stat.getAverageDelay());
	}
	
	@Test
	public void testLastDelay(){
		assertEquals(440, stat.getLastDelay());
	}
	
	@Test
	public void testSecondToLastDelay(){
		assertEquals(-1, stat.getSecondToLastDelay());
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
	
}
