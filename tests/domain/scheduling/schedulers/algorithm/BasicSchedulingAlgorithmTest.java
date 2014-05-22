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
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.models.VehicleModel;
import domain.configuration.taskables.Option;
import domain.configuration.taskables.OptionType;
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
		company = new Company();
		factoryAlgorithm = company.getFactoryScheduler().getCurrentAlgorithm();
	}

	@Test
	public void testSortSingleTaskNoFailure() throws InvalidConfigurationException{
		ArrayList<Order> list = makeOrderListWithSingleTaskOrderWithNoFailure();
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = factoryAlgorithm.assignOrders(list, company.getFactoryScheduler());
		
		ArrayList<AssemblyLine> assemblyLines = company.getAssemblyLines();
		
		AssemblyLineScheduler als0 = assemblyLines.get(0).getAssemblyLineScheduler();

		ArrayList<Order> map0 = mapping.get(als0);
		assertEquals(10,map0.get(0).getOrderID());
		assertEquals(8,map0.get(1).getOrderID());
		assertEquals(5,map0.get(2).getOrderID());
		assertEquals(2,map0.get(3).getOrderID());
		assertEquals(12,map0.get(4).getOrderID());
		assertEquals(15,map0.get(5).getOrderID());
		
		AssemblyLineScheduler als1 = assemblyLines.get(1).getAssemblyLineScheduler();
		ArrayList<Order> map1 = mapping.get(als1);
		assertEquals(11,map1.get(0).getOrderID());		
		assertEquals(7,map1.get(1).getOrderID());
		assertEquals(4,map1.get(2).getOrderID());
		assertEquals(1,map1.get(3).getOrderID());
		assertEquals(13,map1.get(4).getOrderID());
		

		AssemblyLineScheduler als2 = assemblyLines.get(2).getAssemblyLineScheduler();
		ArrayList<Order> map2 = mapping.get(als2);
		assertEquals(9,map2.get(0).getOrderID());
		assertEquals(6,map2.get(1).getOrderID());
		assertEquals(3,map2.get(2).getOrderID());
		assertEquals(0,map2.get(3).getOrderID());
		assertEquals(14,map2.get(4).getOrderID());
	}
	
	@Test
	public void testSortNoSingles() throws InvalidConfigurationException{
		ArrayList<Order> list = makeOrderListWithNoSingleTaskOrder();
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = factoryAlgorithm.assignOrders(list, company.getFactoryScheduler());
		
		ArrayList<AssemblyLine> assemblyLines = company.getAssemblyLines();
		
		AssemblyLineScheduler als0 = assemblyLines.get(0).getAssemblyLineScheduler();

		assertEquals(10,mapping.get(als0).get(0).getOrderID());
		assertEquals(8,mapping.get(als0).get(1).getOrderID());
		assertEquals(5,mapping.get(als0).get(2).getOrderID());
		assertEquals(2,mapping.get(als0).get(3).getOrderID());
		
		AssemblyLineScheduler als1 = assemblyLines.get(1).getAssemblyLineScheduler();
		assertEquals(11,mapping.get(als0).get(4).getOrderID());		
		assertEquals(7,mapping.get(als0).get(5).getOrderID());
		assertEquals(4,mapping.get(als0).get(6).getOrderID());
		assertEquals(1,mapping.get(als0).get(7).getOrderID());
		

		AssemblyLineScheduler als2 = assemblyLines.get(2).getAssemblyLineScheduler();
		assertEquals(9,mapping.get(als0).get(8).getOrderID());
		assertEquals(6,mapping.get(als0).get(9).getOrderID());
		assertEquals(3,mapping.get(als0).get(10).getOrderID());
		assertEquals(0,mapping.get(als0).get(11).getOrderID());
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
		orderList.add(new SingleTaskOrder(12, customShopManager, config1,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,0)));
		orderList.add(new SingleTaskOrder(13, customShopManager, config1,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,1)));
		orderList.add(new SingleTaskOrder(14, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,0)));
		orderList.add(new SingleTaskOrder(15, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,1)));
		return orderList;
	}
	
	private ArrayList<Order> makeOrderListWithNoSingleTaskOrder()
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
}
