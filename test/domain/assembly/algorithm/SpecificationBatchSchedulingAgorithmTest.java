package domain.assembly.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.Statistics;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.AssemblyLineStatus;
import domain.assembly.BrokenStatus;
import domain.assembly.MaintenanceStatus;
import domain.assembly.OperationalStatus;
import domain.assembly.ScheduledOrder;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.VehicleModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.VehicleOrder;
import domain.order.Order;
import domain.order.OrderManager;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.user.GarageHolder;

public class SpecificationBatchSchedulingAgorithmTest {

	SpecificationBatchSchedulingAlgorithm algorithm;
	GarageHolder garageHolder;
	VehicleModelCatalog cmc;
	AssemblyLineScheduler als;
	AssemblyLine line;
	Configuration specified = null;
	ArrayList<AssemblyLineStatus> statuses;
	
	@Before
	public void testCreate() throws IOException, VehicleModelCatalogException, InvalidConfigurationException {
		this.cmc = new VehicleModelCatalog();
		makeConfiguration();
		this.algorithm = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		this.algorithm.setConfiguration(specified);
		ArrayList<SchedulingAlgorithm> list = new ArrayList<SchedulingAlgorithm>();
		list.add(new FIFOSchedulingAlgorithm());
		list.add(algorithm);
		this.garageHolder = new GarageHolder(0);
		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
		this.als.setSchedulingAlgorithm(this.algorithm);
		statuses = new ArrayList<AssemblyLineStatus>();
		statuses.add(new OperationalStatus());
		statuses.add(new MaintenanceStatus());
		statuses.add(new BrokenStatus());
		@SuppressWarnings("unused")
		AssemblyLine assembly = new AssemblyLine(als, statuses);

	}
	
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, als);

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

	@Test
	public void testScheduleToScheduledOrderList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,new GregorianCalendar(2000,0,1,12,0,0),this.als.getAssemblyLine().stateWhenAcceptingOrders(), als);
		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);//12h
		assertEquals(9,scheduleList.get(0).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//13h00
		assertEquals(6,scheduleList.get(1).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//14h00
		assertEquals(3,scheduleList.get(2).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//15h00
		assertEquals(0,scheduleList.get(3).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//16h00
		assertEquals(10,scheduleList.get(4).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h10
		assertEquals(11,scheduleList.get(5).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h20
		assertEquals(8,scheduleList.get(6).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h30
		
		assertEquals(null,scheduleList.get(7).getScheduledOrder());//0 (70)
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 50);//22h20
		assertEquals(null,scheduleList.get(8).getScheduledOrder());//0 (50)
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 50);//21h50
		assertEquals(null,scheduleList.get(9).getScheduledOrder());//0 (50)
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);
		
		//nieuwe dag
		time = new GregorianCalendar(2000,0,2,6,0,0);//06h00
		
		
		assertEquals(7,scheduleList.get(10).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7h10
		assertEquals(5,scheduleList.get(11).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8h20
		assertEquals(4,scheduleList.get(12).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9h30
		assertEquals(2,scheduleList.get(13).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//10h40
		assertEquals(1,scheduleList.get(14).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(14).getScheduledTime());
	}
	
	/**
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private ArrayList<Order> makeOrderList()
			throws InvalidConfigurationException {
		ArrayList<Order> orderList = new ArrayList<Order>();
		Configuration config1 = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config2 = new Configuration(cmc.getAllModels().get(1), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config3 = new Configuration(cmc.getAllModels().get(2), new CompletionPolicy(null,new ArrayList<OptionType>()));
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
		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);
		
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
	public void testSearchConfiguration() throws IOException, VehicleModelCatalogException, InvalidConfigurationException{
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		SpecificationBatchSchedulingAlgorithm algo = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(algo);
		GregorianCalendar time = new GregorianCalendar(2014, 9, 1, 6, 0, 0);
		VehicleModelCatalog catalog = new VehicleModelCatalog();
		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		scheduler.setSchedulingAlgorithm(algo);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog);
		Statistics stat = new Statistics(orderManager);
		@SuppressWarnings("unused")
		AssemblyLine line = new AssemblyLine(scheduler, statuses);
		
		assertEquals(algo.searchForBatchConfiguration(scheduler).size(), 1);
	}
	
	public void makeConfiguration() throws InvalidConfigurationException{
		Configuration config1 = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null,new ArrayList<OptionType>()));
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
