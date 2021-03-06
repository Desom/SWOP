package domain.scheduling.schedulers.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.Statistics;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.BrokenStatus;
import domain.assembly.assemblyline.status.MaintenanceStatus;
import domain.assembly.assemblyline.status.OperationalStatus;
import domain.assembly.assemblyline.status.StatusCreator;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.configuration.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.scheduling.order.Order;
import domain.scheduling.order.OrderManager;
import domain.scheduling.order.VehicleOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FIFOSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.user.GarageHolder;

public class SpecificationBatchSchedulingAgorithmTest {

	SpecificationBatchSchedulingAlgorithm algorithm;
	AssemblyLineSchedulingAlgorithm basicAlgorithm;
	GarageHolder garageHolder;
	VehicleCatalog cmc;
	AssemblyLineScheduler als;
	AssemblyLine line;
	Configuration specified = null;
	private OperationalStatus status;
	private ArrayList<VehicleModel> possibleModels;
	private Company company;
	
	@Before
	public void testCreate() throws IOException, VehicleCatalogException, InvalidConfigurationException {
		this.cmc = new VehicleCatalog(new WorkstationTypeCreator());
		makeConfiguration();
		this.algorithm = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		this.algorithm.setConfiguration(specified);
		this.basicAlgorithm = new BasicSchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));

//		ArrayList<AssemblyLineSchedulingAlgorithm> list = new ArrayList<AssemblyLineSchedulingAlgorithm>();
//		basicAlgorithm = new BasicSchedulingAlgorithm(algorithm);
//		list.add(new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
//		list.add(basicAlgorithm);
//		this.garageHolder = new GarageHolder(0);
//		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
//		this.als.setSchedulingAlgorithm(this.basicAlgorithm);
//		status = new StatusCreator().getOperationalStatus();
//		possibleModels = new ArrayList<VehicleModel>(cmc.getAllModels());
//		@SuppressWarnings("unused")
//		AssemblyLine assembly = new AssemblyLine(als, status, possibleModels);
		company = new Company("testData/testData_OrderManager.txt");
	}
	
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, company.getAssemblyLines().get(0).getAssemblyLineScheduler());

		assertEquals(9,scheduleList.get(0).getOrderID());
		assertEquals(6,scheduleList.get(1).getOrderID());
		assertEquals(3,scheduleList.get(2).getOrderID());
		assertEquals(0,scheduleList.get(3).getOrderID());
		assertEquals(10,scheduleList.get(4).getOrderID());
		assertEquals(11,scheduleList.get(5).getOrderID());
		assertEquals(8,scheduleList.get(6).getOrderID());
		assertEquals(7,scheduleList.get(7).getOrderID());
		assertEquals(5,scheduleList.get(8).getOrderID());
		assertEquals(4,scheduleList.get(9).getOrderID());
		assertEquals(2,scheduleList.get(10).getOrderID());
		assertEquals(1,scheduleList.get(11).getOrderID());
	}

	
	
	/**
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private ArrayList<Order> makeOrderList()
			throws InvalidConfigurationException {
		ArrayList<Order> orderList = new ArrayList<Order>();
		Configuration config1 = new Configuration(cmc.getAllModels().get(1), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config2 = new Configuration(cmc.getAllModels().get(3), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config3 = new Configuration(cmc.getAllModels().get(4), new CompletionPolicy(null,new ArrayList<OptionType>()));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					){
				config1.addOption(option); // Model C
				config2.addOption(option); // Model B = 70
				config3.addOption(option); // Model A = 50
			}
		}
		config1.complete();
		config2.complete();
		config3.complete();
		GregorianCalendar time = new GregorianCalendar(2000,1,1,6,0,0);
		
		orderList.add(new VehicleOrder(0, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(1, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(2, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(3, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(4, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(5, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(6, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(7, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(8, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new VehicleOrder(9, garageHolder, config1, time));
		time.add(GregorianCalendar.HOUR, -1);
		orderList.add(new VehicleOrder(10, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, 1);
		orderList.add(new VehicleOrder(11, garageHolder, config3, time));
		
		return orderList;
	}
	
	@Test
	public void testSearchConfiguration() throws IOException, VehicleCatalogException, InvalidConfigurationException{
//		ArrayList<	AssemblyLineSchedulingAlgorithm> possibleAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
//		SpecificationBatchSchedulingAlgorithm algo = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
//		possibleAlgorithms.add(new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
//		BasicSchedulingAlgorithm basicSpec = new BasicSchedulingAlgorithm(algo);
//		possibleAlgorithms.add(basicSpec);
//		GregorianCalendar time = new GregorianCalendar(2014, 9, 1, 6, 0, 0);
//		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
//		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
//		scheduler.setSchedulingAlgorithm(basicSpec);
//		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog);
//		Statistics stat = new Statistics(orderManager);
//		@SuppressWarnings("unused")
//		AssemblyLine line = new AssemblyLine(scheduler, status, possibleModels);

		SpecificationBatchSchedulingAlgorithm algo = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());

		
		assertEquals(algo.searchForBatchConfiguration(company.getFactoryScheduler()).size(), 1);
	}
	
	public void makeConfiguration() throws InvalidConfigurationException{
		Configuration config1 = new Configuration(cmc.getAllModels().get(1), new CompletionPolicy(null,new ArrayList<OptionType>()));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					){
				config1.addOption(option); // Model C
			}
		}
		this.specified = config1;
	}

}
