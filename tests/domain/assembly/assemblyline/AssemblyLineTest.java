package domain.assembly.assemblyline;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.InternalFailureException;
import domain.Statistics;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.AssemblyStatusView;
import domain.assembly.assemblyline.status.StatusCreator;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.configuration.taskables.Option;
import domain.configuration.taskables.TaskType;
import domain.configuration.taskables.TaskTypeCreator;
import domain.configuration.models.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.scheduling.NoOrdersToBeScheduledException;
import domain.scheduling.order.Order;
import domain.scheduling.order.OrderManager;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.order.VehicleOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FIFOSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SpecificationBatchSchedulingAlgorithm;
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
	public void testCreate() throws DoesNotExistException, IOException, VehicleCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		m1 = new Mechanic(2);
		m2 = new Mechanic(3);
		m3 = new Mechanic(4);
		Company comp = new Company("testData/testData_OrderManager.txt");
		line = comp.getAssemblyLines().get(0);
		/*ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm())));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
		this.scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog);
		Statistics statistics = new Statistics(orderManager);
		ArrayList<VehicleModel> models = new ArrayList<VehicleModel>();
		models.add(catalog.getAllModels().get(1));
		models.add(catalog.getAllModels().get(3));
		models.add(catalog.getAllModels().get(4));
		AssemblyLineStatus status = new StatusCreator().getOperationalStatus();
		line = new AssemblyLine(scheduler, status,models);*/

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
	public void testFilterWorkstation() throws InvalidConfigurationException, VehicleCatalogException, IOException{
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

	private VehicleOrder createCar() throws InvalidConfigurationException, IOException, VehicleCatalogException{

		Policy pol1 = new CompletionPolicy(null,VehicleCatalog.taskTypeCreator.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;


		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
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
	
	private SingleTaskOrder createSingleTask() throws InvalidConfigurationException, VehicleCatalogException{
		
		Policy singleTaskPolicy = new SingleTaskOrderNumbersOfTasksPolicy(null);
		Configuration config = new Configuration(null, singleTaskPolicy);
		config.addOption(new Option("test", new TaskTypeCreator().Color));
		config.complete();
		CustomShopManager customShop = new CustomShopManager(1);
		
		GregorianCalendar now = new GregorianCalendar();
		
		return new SingleTaskOrder(0, customShop, config, now, now);
	}
	


}