package domain.scheduling.schedulers.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.status.StatusCreator;
import domain.assembly.assemblyline.status.StatusCreatorInterface;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.assembly.workstations.WorkstationTypeCreatorInterface;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.scheduling.order.Order;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.order.VehicleOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.EfficiencySchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FIFOSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FactorySchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SameOrderSchedulingAlgorithm;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;

public class BasicSchedulingAlgorithmTest {
	
	FactorySchedulingAlgorithm factoryAlgorithm;
	GarageHolder garageHolder;
	VehicleCatalog cmc;
	AssemblyLineScheduler als;
	AssemblyLine AssemblyLine;
	AssemblyLineScheduler alsT;
	AssemblyLine AssemblyLineT;
	CustomShopManager customShopManager;
	private Company company;
	
	@Before
	public void testCreate() throws IOException, VehicleCatalogException {
		company = new Company("testData/noOrders.txt");
		factoryAlgorithm = company.getFactoryScheduler().getCurrentAlgorithm();
		cmc = company.getCatalog();
	}

	@Test
	public void testSortSingleTaskWithFailure() throws InvalidConfigurationException{
		ArrayList<Order> list = makeOrderListWithSingleTaskOrderWithFailure();
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = factoryAlgorithm.assignOrders(list, company.getFactoryScheduler());
		
		ArrayList<AssemblyLine> assemblyLines = company.getAssemblyLines();
		
		AssemblyLineScheduler als0 = assemblyLines.get(0).getAssemblyLineScheduler();

		ArrayList<Order> map0 = mapping.get(als0);
		assertEquals(7,map0.size());
		assertEquals(10,map0.get(0).getOrderID());//70
		assertEquals(5,map0.get(1).getOrderID());//50
		assertEquals(2,map0.get(2).getOrderID());//70
		assertEquals(14,map0.get(3).getOrderID());
		assertEquals(16,map0.get(4).getOrderID());
		assertEquals(19,map0.get(5).getOrderID());
		assertEquals(12,map0.get(6).getOrderID());
		
		AssemblyLineScheduler als1 = assemblyLines.get(1).getAssemblyLineScheduler();
		ArrayList<Order> map1 = mapping.get(als1);
		assertEquals(8,map1.size());
		assertEquals(11,map1.get(0).getOrderID());//50
		assertEquals(8,map1.get(1).getOrderID());//50
		assertEquals(6,map1.get(2).getOrderID());//60
		assertEquals(3,map1.get(3).getOrderID());//70
		assertEquals(0,map1.get(4).getOrderID());//70
		assertEquals(15,map1.get(5).getOrderID());
		assertEquals(17,map1.get(6).getOrderID());
		assertEquals(13,map1.get(7).getOrderID());
		

		AssemblyLineScheduler als2 = assemblyLines.get(2).getAssemblyLineScheduler();
		ArrayList<Order> map2 = mapping.get(als2);
		assertEquals(5,map2.size());
		assertEquals(9,map2.get(0).getOrderID());//60
		assertEquals(7,map2.get(1).getOrderID());//70
		assertEquals(4,map2.get(2).getOrderID());//60
		assertEquals(1,map2.get(3).getOrderID());//60
		assertEquals(18,map2.get(4).getOrderID());
	}
	
	@Test
	public void testSortSingleTaskNoFailure() throws InvalidConfigurationException{
		ArrayList<Order> list = makeOrderListWithSingleTaskOrderWithNoFailure();
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = factoryAlgorithm.assignOrders(list, company.getFactoryScheduler());
		
		ArrayList<AssemblyLine> assemblyLines = company.getAssemblyLines();
		
		AssemblyLineScheduler als0 = assemblyLines.get(0).getAssemblyLineScheduler();

		assertEquals(10,mapping.get(als0).get(0).getOrderID());//70
		assertEquals(5,mapping.get(als0).get(1).getOrderID());//50
		assertEquals(2,mapping.get(als0).get(2).getOrderID());//70
		assertEquals(14,mapping.get(als0).get(3).getOrderID());
		assertEquals(12,mapping.get(als0).get(4).getOrderID());
		
		AssemblyLineScheduler als1 = assemblyLines.get(1).getAssemblyLineScheduler();
		assertEquals(11,mapping.get(als1).get(0).getOrderID());//50
		assertEquals(8,mapping.get(als1).get(1).getOrderID());//50
		assertEquals(6,mapping.get(als1).get(2).getOrderID());//60
		assertEquals(3,mapping.get(als1).get(3).getOrderID());//70
		assertEquals(0,mapping.get(als1).get(4).getOrderID());//70
		assertEquals(15,mapping.get(als1).get(5).getOrderID());
		assertEquals(13,mapping.get(als1).get(6).getOrderID());
		

		AssemblyLineScheduler als2 = assemblyLines.get(2).getAssemblyLineScheduler();
		assertEquals(9,mapping.get(als2).get(0).getOrderID());//60
		assertEquals(7,mapping.get(als2).get(1).getOrderID());//70
		assertEquals(4,mapping.get(als2).get(2).getOrderID());//60
		assertEquals(1,mapping.get(als2).get(3).getOrderID());//60
	}
	
	@Test
	public void testSortNoSingles() throws InvalidConfigurationException{
		ArrayList<Order> list = makeOrderListWithNoSingleTaskOrder();
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = factoryAlgorithm.assignOrders(list, company.getFactoryScheduler());
		
		ArrayList<AssemblyLine> assemblyLines = company.getAssemblyLines();
		
		AssemblyLineScheduler als0 = assemblyLines.get(0).getAssemblyLineScheduler();

		assertEquals(10,mapping.get(als0).get(0).getOrderID());//70
		assertEquals(5,mapping.get(als0).get(1).getOrderID());//50
		assertEquals(2,mapping.get(als0).get(2).getOrderID());//70
		
		AssemblyLineScheduler als1 = assemblyLines.get(1).getAssemblyLineScheduler();
		assertEquals(11,mapping.get(als1).get(0).getOrderID());//50
		assertEquals(8,mapping.get(als1).get(1).getOrderID());//50
		assertEquals(6,mapping.get(als1).get(2).getOrderID());//60
		assertEquals(3,mapping.get(als1).get(3).getOrderID());//70
		assertEquals(0,mapping.get(als1).get(4).getOrderID());//70
		

		AssemblyLineScheduler als2 = assemblyLines.get(2).getAssemblyLineScheduler();
		assertEquals(9,mapping.get(als2).get(0).getOrderID());//60
		assertEquals(7,mapping.get(als2).get(1).getOrderID());//70
		assertEquals(4,mapping.get(als2).get(2).getOrderID());//60
		assertEquals(1,mapping.get(als2).get(3).getOrderID());//60
	}
	
	private ArrayList<Order> makeOrderListWithSingleTaskOrderWithNoFailure()
			throws InvalidConfigurationException {
		ArrayList<Order> orderList  = this.makeOrderListWithNoSingleTaskOrder();
		Configuration config1 = new Configuration(null,new SingleTaskOrderNumbersOfTasksPolicy(null));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("leather black")){
				config1.addOption(option);
			}
		}
		config1.complete();
		Configuration config2 = new Configuration(null,new SingleTaskOrderNumbersOfTasksPolicy(null));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("red")){
				config2.addOption(option);
			}
		}
		config2.complete();
		orderList.add(new SingleTaskOrder(12, customShopManager, config1,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2022,0,1,12,0,0)));
		orderList.add(new SingleTaskOrder(13, customShopManager, config1,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2022,0,1,12,0,1)));
		orderList.add(new SingleTaskOrder(14, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2022,0,1,12,0,0)));
		orderList.add(new SingleTaskOrder(15, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2022,0,1,12,0,1)));
		return orderList;
	}
	
	private ArrayList<Order> makeOrderListWithNoSingleTaskOrder()
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
	
	private ArrayList<Order> makeOrderListWithSingleTaskOrderWithFailure()
			throws InvalidConfigurationException {
		ArrayList<Order> orderList  = this.makeOrderListWithSingleTaskOrderWithNoFailure();
		Configuration config1 = new Configuration(null,new SingleTaskOrderNumbersOfTasksPolicy(null));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("red")){
				config1.addOption(option);
			}
		}
		config1.complete();
		orderList.add(new SingleTaskOrder(16, customShopManager, config1,new GregorianCalendar(2000,0,1,6,0,0), new GregorianCalendar(2000,0,1,6,1,0)));
		orderList.add(new SingleTaskOrder(17, customShopManager, config1,new GregorianCalendar(2000,0,1,6,0,0), new GregorianCalendar(2000,0,1,6,1,0)));
		orderList.add(new SingleTaskOrder(18, customShopManager, config1,new GregorianCalendar(2000,0,1,6,0,0), new GregorianCalendar(2000,0,1,6,1,0)));
		orderList.add(new SingleTaskOrder(19, customShopManager, config1,new GregorianCalendar(2000,0,1,6,0,0), new GregorianCalendar(2000,0,1,6,1,0)));
		return orderList;
	}
}
