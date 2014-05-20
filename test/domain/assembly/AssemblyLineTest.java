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
import domain.assembly.VehicleAssemblyProcess;
import domain.assembly.DoesNotExistException;
import domain.assembly.Workstation;
import domain.assembly.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.assembly.algorithm.BasicSchedulingAlgorithm;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.TaskType;
import domain.configuration.TaskTypeCreator;
import domain.configuration.VehicleModel;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.VehicleModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.VehicleOrder;
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
import domain.user.Mechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;

public class AssemblyLineTest {


	private AssemblyLine line;
	private AssemblyLineScheduler scheduler;
	private Mechanic m1;
	private Mechanic m2;
	private Mechanic m3;

	@Before
	public void testCreate() throws DoesNotExistException, IOException, VehicleModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		m1 = new Mechanic(2);
		m2 = new Mechanic(3);
		m3 = new Mechanic(4);

		ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm())));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		VehicleModelCatalog catalog = new VehicleModelCatalog(new WorkstationTypeCreator());
		this.scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog);
		Statistics statistics = new Statistics(orderManager);
		ArrayList<AssemblyLineStatus> possibleStates = new ArrayList<AssemblyLineStatus>();
		possibleStates.add(new OperationalStatus());
		line = new AssemblyLine(scheduler, possibleStates);

		line.selectWorkstationById(1).addMechanic(m1);
		assertEquals(line.selectWorkstationById(1).getMechanic(), m1);

		line.selectWorkstationById(2).addMechanic(m2);
		assertEquals(line.selectWorkstationById(2).getMechanic(), m2);

		line.selectWorkstationById(3).addMechanic(m3);
		assertEquals(line.selectWorkstationById(3).getMechanic(), m3);

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
	/*
	@Test
	public void testSelectWorkStationId() throws DoesNotExistException, InternalFailureException{
		assertNotNull(line.selectWorkstationById(1));
		assertEquals(line.selectWorkstationById(1).getId(), 1);

		assertNotNull(line.selectWorkstationById(2));
		assertEquals(line.selectWorkstationById(2).getId(), 2);

		assertNotNull(line.selectWorkstationById(3));
		assertEquals(line.selectWorkstationById(3).getId(), 3);
	}
*/

	@Test
	public void testAdvanceLineSucces() throws DoesNotExistException, CannotAdvanceException, InternalFailureException, NoOrdersToBeScheduledException {
		ArrayList<VehicleAssemblyProcess> processesBefore = new ArrayList<VehicleAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesBefore.add(w.getVehicleAssemblyProcess());
		}

		Order order = null;
		try{
			order = scheduler.seeNextOrder(60);
		}
		catch(NoOrdersToBeScheduledException e){}

		VehicleAssemblyProcess next;
		if(order != null){
			next = order.getAssemblyprocess();
		}else{
			next = null;
		}

		fullDefaultAdvance();

		ArrayList<VehicleAssemblyProcess> processesAfter = new ArrayList<VehicleAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesAfter.add(w.getVehicleAssemblyProcess());
		}

		assertEquals(processesAfter.get(0), next);
		for(int i = 0; i<processesAfter.size()-1; i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i+1));
		}
	}

	@Test(expected = CannotAdvanceException.class)  
	public void testAdvanceLineBlocking() throws DoesNotExistException, CannotAdvanceException, InternalFailureException {
		ArrayList<VehicleAssemblyProcess> processesBefore = new ArrayList<VehicleAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesBefore.add(w.getVehicleAssemblyProcess());
		}

		line.advanceLine();

		ArrayList<VehicleAssemblyProcess> processesAfter = new ArrayList<VehicleAssemblyProcess>();
		for(Workstation w : line.getAllWorkstations()){
			processesAfter.add(w.getVehicleAssemblyProcess());
		}

		for(int i = 0; i<processesAfter.size(); i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i));
		}
	}

	@Test
	public void testCurrentStatus() throws InternalFailureException {
		try {
			AssemblyStatusView current = line.currentStatus();

			for(int i =0;i< line.getNumberOfWorkstations();i++){
				LinkedList<TaskType> list = new LinkedList<TaskType>();
				for(int j = 0; j<line.selectWorkstationById(i).getAllTasks().size() ; j++ ){
					list.add(line.selectWorkstationById(i).getAllTasks().get(j).getType());
				}
				assertTrue(list.containsAll(current.getAllTasksAt(line.selectWorkstationById(i).getWorkstationType())));
				assertTrue(current.getAllTasksAt(line.selectWorkstationById(i).getWorkstationType()).containsAll(list));

				if(current.getOrderIdOf(line.selectWorkstationById(i).getWorkstationType()) != -1){
					assertEquals(current.getOrderIdOf(line.selectWorkstationById(i).getWorkstationType()), line.selectWorkstationById(i).getVehicleAssemblyProcess().getOrder().getOrderID());
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
			Mechanic mechanic = w.getMechanic();
			if(mechanic == null)
				mechanic = new Mechanic((int)Math.random()*100); // randomize ID een beetje
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

	@Test
	public void testFilterWorkstation() throws InvalidConfigurationException, VehicleModelCatalogException, IOException{
		VehicleAssemblyProcess process = this.createCar().getAssemblyprocess();

		ArrayList<Workstation> filtered = line.filterWorkstations(process);
		
		assertEquals(3,filtered.size());
		assertTrue(filtered.contains(line.getAllWorkstations().get(0)));
		assertTrue(filtered.contains(line.getAllWorkstations().get(1)));
		assertTrue(filtered.contains(line.getAllWorkstations().get(2)));

		process = createSingleTask().getAssemblyprocess();

		ArrayList<Workstation> filtered2 = line.filterWorkstations(process);
		assertEquals(1,filtered2.size());
		assertTrue(filtered2.contains(line.getAllWorkstations().get(0)));
		assertFalse(filtered2.contains(line.getAllWorkstations().get(1)));
		assertFalse(filtered2.contains(line.getAllWorkstations().get(2)));
	}

	private VehicleOrder createCar() throws InvalidConfigurationException, IOException, VehicleModelCatalogException{

		Policy pol1 = new CompletionPolicy(null,VehicleModelCatalog.taskTypeCreator.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;


		VehicleModelCatalog catalog = new VehicleModelCatalog(new WorkstationTypeCreator());
		VehicleModel carModel = null;
		for(VehicleModel m : catalog.getAllModels()){
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
		VehicleOrder carOrder = new VehicleOrder(1, garageHolder, config, now);
		return carOrder;
	}
	
	private SingleTaskOrder createSingleTask() throws InvalidConfigurationException, VehicleModelCatalogException{
		
		Policy singleTaskPolicy = new SingleTaskOrderNumbersOfTasksPolicy(null);
		Configuration config = new Configuration(null, singleTaskPolicy);
		config.addOption(new Option("test", new TaskTypeCreator().Color));
		config.complete();
		CustomShopManager customShop = new CustomShopManager(1);
		
		GregorianCalendar now = new GregorianCalendar();
		
		return new SingleTaskOrder(0, customShop, config, now, now);
	}
	


}