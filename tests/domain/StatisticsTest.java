package domain;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.assembly.algorithm.BasicSchedulingAlgorithm;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.OperationalStatus;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.models.VehicleModel;
import domain.policies.InvalidConfigurationException;
import domain.scheduling.order.OrderManager;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.user.Mechanic;

public class StatisticsTest {

	AssemblyLine line = null;
	Statistics stat = null;
	@Before
	public void testCreate() throws DoesNotExistException, IOException, VehicleCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm())));
		GregorianCalendar time = new GregorianCalendar(2014, 9, 1, 6, 0, 0);
		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog);
		stat = new Statistics(orderManager);
		ArrayList<AssemblyLineStatus> possibleStates = new ArrayList<AssemblyLineStatus>();
		possibleStates.add(new OperationalStatus());
		ArrayList<VehicleModel> models = new ArrayList<VehicleModel>(catalog.getAllModels());
		line = new AssemblyLine(scheduler, possibleStates,models);
		
		Mechanic m1 = new Mechanic(2);
		Mechanic m2 = new Mechanic(3);
		Mechanic m3 = new Mechanic(4);
		
		line.selectWorkstationById(1).addMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getMechanic(), m1);

		line.selectWorkstationById(2).addMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getMechanic(), m2);

		line.selectWorkstationById(3).addMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getMechanic(), m3);
		
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
			Mechanic mechanic = w.getMechanic();
			if(mechanic == null)
				mechanic = new Mechanic(w.getName().hashCode()); // randomize ID een beetje
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
		assertEquals(11, stat.getAverageVehiclesPerDay());
	}
	
	@Test
	public void testMedianCars(){
		assertEquals(14, stat.getMedianVehiclesPerDay());
	}
	
	@Test
	public void amountOfCarsLastDays(){
		assertEquals(12, stat.getAmountOfVehicles1DayAgo());
		assertEquals(14, stat.getAmountOfVehicles2DaysAgo());
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
	
}