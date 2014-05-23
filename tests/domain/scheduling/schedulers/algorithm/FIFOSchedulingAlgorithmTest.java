package domain.scheduling.schedulers.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.BrokenStatus;
import domain.assembly.assemblyline.status.MaintenanceStatus;
import domain.assembly.assemblyline.status.OperationalStatus;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.scheduling.order.Order;
import domain.scheduling.order.VehicleOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FIFOSchedulingAlgorithm;
import domain.user.GarageHolder;

public class FIFOSchedulingAlgorithmTest {

	FIFOSchedulingAlgorithm algorithm;
	AssemblyLineSchedulingAlgorithm basicAlgorithm;
	GarageHolder garageHolder;
	VehicleCatalog cmc;
	AssemblyLineScheduler als;
	ArrayList<AssemblyLineStatus> statuses;
	private Company company;
	
	@Before
	public void testCreate() throws IOException, VehicleCatalogException {
		this.algorithm = new FIFOSchedulingAlgorithm();
//		ArrayList<AssemblyLineSchedulingAlgorithm> list = new ArrayList<AssemblyLineSchedulingAlgorithm>();
//		basicAlgorithm = new BasicSchedulingAlgorithm(algorithm);
//		list.add(basicAlgorithm);
//		this.garageHolder = new GarageHolder(0);
//		this.cmc = new VehicleCatalog();
//		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
//		statuses = new ArrayList<AssemblyLineStatus>();
//		statuses.add(new OperationalStatus());
//		statuses.add(new MaintenanceStatus());
//		statuses.add(new BrokenStatus());
//		@SuppressWarnings("unused")
//		AssemblyLine assembly = new AssemblyLine(als, statuses);
		this.garageHolder = new GarageHolder(0);
		company = new Company("testData/noOrders.txt");
		cmc = company.getCatalog();
		this.basicAlgorithm = new BasicSchedulingAlgorithm(new FIFOSchedulingAlgorithm());

	}
	
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, company.getAssemblyLines().get(0).getAssemblyLineScheduler());

		assertEquals(10,scheduleList.get(0).getOrderID());
		assertEquals(11,scheduleList.get(1).getOrderID());
		assertEquals(9,scheduleList.get(2).getOrderID());
		assertEquals(8,scheduleList.get(3).getOrderID());
		assertEquals(7,scheduleList.get(4).getOrderID());
		assertEquals(6,scheduleList.get(5).getOrderID());
		assertEquals(5,scheduleList.get(6).getOrderID());
		assertEquals(4,scheduleList.get(7).getOrderID());
		assertEquals(3,scheduleList.get(8).getOrderID());
		assertEquals(2,scheduleList.get(9).getOrderID());
		assertEquals(1,scheduleList.get(10).getOrderID());
		assertEquals(0,scheduleList.get(11).getOrderID());
	}

	@Test
	public void testScheduleToScheduledOrderList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<ScheduledOrder> scheduleList = basicAlgorithm.scheduleToScheduledOrderList(orderList, company.getAssemblyLines().get(0));
		GregorianCalendar time = company.getOrderManager().getScheduler().getCurrentTime();//12h
		assertEquals(10,scheduleList.get(0).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7h10
		assertEquals(11,scheduleList.get(1).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8h20
		assertEquals(9,scheduleList.get(2).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9h30
		assertEquals(8,scheduleList.get(3).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//10h30
		assertEquals(7,scheduleList.get(4).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11h40
		assertEquals(6,scheduleList.get(5).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12h50
		assertEquals(5,scheduleList.get(6).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//14h00
		
		
		
		assertEquals(4,scheduleList.get(7).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7h10
		assertEquals(3,scheduleList.get(8).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8h20
		assertEquals(2,scheduleList.get(9).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9h30
		assertEquals(1,scheduleList.get(10).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//10h40
		assertEquals(0,scheduleList.get(11).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11h50
		
		assertEquals(null,scheduleList.get(12).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//13h10
		assertEquals(null,scheduleList.get(13).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//14h10
		assertEquals(null,scheduleList.get(14).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(14).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);
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

}
