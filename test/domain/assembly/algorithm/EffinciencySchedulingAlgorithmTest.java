package domain.assembly.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.AssemblyLineCreator;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.assembly.Workstation;
import domain.assembly.WorkstationType;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.VehicleModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.VehicleOrder;
import domain.order.Order;
import domain.order.SingleTaskOrder;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;

public class EffinciencySchedulingAlgorithmTest {

	SchedulingAlgorithm algorithm;
	GarageHolder garageHolder;
	VehicleModelCatalog cmc;
	AssemblyLineScheduler als;
	dummyAssemblyLine AssemblyLine;
	AssemblyLineScheduler alsT;
	dummyAssemblyLine AssemblyLineT;
	CustomShopManager customShopManager;
	
	@Before
	public void testCreate() throws IOException, VehicleModelCatalogException {
		this.algorithm = new EfficiencySchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		ArrayList<SchedulingAlgorithm> list = new ArrayList<SchedulingAlgorithm>();
		list.add(algorithm);
		this.garageHolder = new GarageHolder(0);
		this.customShopManager = new CustomShopManager(0);
		this.cmc = new VehicleModelCatalog();
		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
		this.AssemblyLine = new dummyAssemblyLine(als, 3);
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		AssemblyLineCreator alsc = new AssemblyLineCreator(new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list));
		workstations.add(new Workstation("Body Post", alsc.getWorkstationType("Body Post")));
		workstations.add(new Workstation("DriveTrain Post", alsc.getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", alsc.getWorkstationType("Accessories Post")));
		this.AssemblyLine.addWorkstations(workstations);
		this.alsT = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
		this.AssemblyLineT = new dummyAssemblyLine(alsT, 4);
		ArrayList<Workstation> workstationsT = new ArrayList<Workstation>();
		AssemblyLineCreator alscT = new AssemblyLineCreator(new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list));
		workstationsT.add(new Workstation("Body Post", alscT.getWorkstationType("Body Post")));
		workstationsT.add(new Workstation("Cargo Post",  alscT.getWorkstationType("Cargo Post")));
		workstationsT.add(new Workstation("DriveTrain Post", alscT.getWorkstationType("DriveTrain Post")));
		workstationsT.add(new Workstation("Accessories Post", alscT.getWorkstationType("Accessories Post")));
		this.AssemblyLineT.addWorkstations(workstationsT);
	}
	
	
	@Test
	public void testScheduleToList50only() throws InvalidConfigurationException{
		ArrayList<Order> orderList = new ArrayList<Order>();
		Configuration config = new Configuration(cmc.getAllModels().get(2), new CompletionPolicy(null,new ArrayList<OptionType>()));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					){
				config.addOption(option); // Model A = 50
			}
		}
		config.complete();

		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);
		for(int i =1; i<= 17;i++){
			orderList.add(new VehicleOrder(i, garageHolder, config, time));
			time.add(GregorianCalendar.MILLISECOND, 1);
		}
		Configuration config2 = new Configuration(null,new SingleTaskOrderNumbersOfTasksPolicy(null));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("red")){
				config2.addOption(option);
			}
		}
		config2.complete();
		orderList.add(new SingleTaskOrder(18, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,0)));
		orderList.add(new SingleTaskOrder(19, customShopManager, config2,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(2002,0,1,12,0,1)));
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, als);
		//6u00
		assertEquals(1,scheduleList.get(0).getOrderID());
		//6u50
		assertEquals(2,scheduleList.get(1).getOrderID());
		//7u40
		assertEquals(3,scheduleList.get(2).getOrderID());
		//8u30
		assertEquals(4,scheduleList.get(3).getOrderID());
		//9u20
		assertEquals(5,scheduleList.get(4).getOrderID());
		//10u10
		assertEquals(6,scheduleList.get(5).getOrderID());
		//11u00
		assertEquals(7,scheduleList.get(6).getOrderID());
		//11u50
		assertEquals(8,scheduleList.get(7).getOrderID());
		//12u40
		assertEquals(9,scheduleList.get(8).getOrderID());
		//13u30
		assertEquals(10,scheduleList.get(9).getOrderID());
		//14u20
		assertEquals(11,scheduleList.get(10).getOrderID());
		//15u10
		assertEquals(12,scheduleList.get(11).getOrderID());
		//16u00
		assertEquals(13,scheduleList.get(12).getOrderID());
		//16u50
		assertEquals(14,scheduleList.get(13).getOrderID());
		//17u40
		assertEquals(15,scheduleList.get(14).getOrderID());
		//18u30
		assertEquals(16,scheduleList.get(15).getOrderID());
		//19u20
		assertEquals(18,scheduleList.get(16).getOrderID());
		//20u20
		assertEquals(19,scheduleList.get(17).getOrderID());
		//21u20
		assertEquals(null,scheduleList.get(18));
		//21u20
		assertEquals(null,scheduleList.get(19));
		//21u20
		assertEquals(null,scheduleList.get(20));

	}
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithNoSingleTaskOrder();

		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, als);

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
		assertEquals(null,scheduleList.get(12));
		assertEquals(null,scheduleList.get(13));
		assertEquals(null,scheduleList.get(14));
		assertEquals(15,scheduleList.size());
	}
	@Test
	public void testScheduleToListForTruckLineNoSTOrders() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithNoSingleTaskOrder();

		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, alsT);

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
		assertEquals(null,scheduleList.get(12));
		assertEquals(null,scheduleList.get(13));
		assertEquals(null,scheduleList.get(14));
		assertEquals(null,scheduleList.get(15));
		assertEquals(16,scheduleList.size());
	}
	
	@Test
	public void testScheduleToListForTruckLineSTOrdersNoFailure() throws InvalidConfigurationException{
		ArrayList<Order> orderList = this.makeOrderListWithSingleTaskOrderWithNoFailure();

		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, alsT);
		assertEquals(12,scheduleList.get(0).getOrderID());
		assertEquals(13,scheduleList.get(1).getOrderID());
		assertEquals(null,scheduleList.get(2));
		assertEquals(10,scheduleList.get(3).getOrderID());
		assertEquals(11,scheduleList.get(4).getOrderID());
		assertEquals(9,scheduleList.get(5).getOrderID());
		assertEquals(8,scheduleList.get(6).getOrderID());
		assertEquals(7,scheduleList.get(7).getOrderID());
		assertEquals(6,scheduleList.get(8).getOrderID());
		assertEquals(5,scheduleList.get(9).getOrderID());
		assertEquals(4,scheduleList.get(10).getOrderID());
		assertEquals(3,scheduleList.get(11).getOrderID());
		assertEquals(2,scheduleList.get(12).getOrderID());
		assertEquals(1,scheduleList.get(13).getOrderID());
		assertEquals(0,scheduleList.get(14).getOrderID());
		assertEquals(14,scheduleList.get(15).getOrderID());
		assertEquals(15,scheduleList.get(16).getOrderID());
		assertEquals(null,scheduleList.get(17));
		assertEquals(null,scheduleList.get(18));
		assertEquals(null,scheduleList.get(19));
		assertEquals(null,scheduleList.get(20));
		assertEquals(21,scheduleList.size());
	}
	@Test
	public void testScheduleToScheduledOrderListNoSingleTaskOrder() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithNoSingleTaskOrder();

		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(), als);
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		assertEquals(10,scheduleList.get(0).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7u10
		assertEquals(11,scheduleList.get(1).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u20
		assertEquals(9,scheduleList.get(2).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u30
		assertEquals(8,scheduleList.get(3).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//10u30
		assertEquals(7,scheduleList.get(4).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11u40
		assertEquals(6,scheduleList.get(5).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u50
		assertEquals(5,scheduleList.get(6).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//14u00
		assertEquals(4,scheduleList.get(7).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15h10
		assertEquals(3,scheduleList.get(8).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h20
		assertEquals(2,scheduleList.get(9).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h30
		assertEquals(1,scheduleList.get(10).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h40
		assertEquals(0,scheduleList.get(11).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h50
		assertEquals(null,scheduleList.get(12).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//21h00
		assertEquals(null,scheduleList.get(13).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//22h00
		assertEquals(null,scheduleList.get(14).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(14).getScheduledTime());
		assertEquals(15,scheduleList.size());
	}
	@Test
	public void testScheduleToScheduledOrderListSingleTaskOrderNoDeadLineFailure() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithSingleTaskOrderWithNoFailure();

		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(),als);
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		assertEquals(12,scheduleList.get(0).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		assertEquals(13,scheduleList.get(1).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		assertEquals(10,scheduleList.get(2).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7u10
		assertEquals(11,scheduleList.get(3).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u20
		assertEquals(9,scheduleList.get(4).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u30
		assertEquals(8,scheduleList.get(5).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//10u30
		assertEquals(7,scheduleList.get(6).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11u40
		assertEquals(6,scheduleList.get(7).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u50
		assertEquals(5,scheduleList.get(8).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//14u00
		assertEquals(4,scheduleList.get(9).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15h10
		assertEquals(3,scheduleList.get(10).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h20
		assertEquals(2,scheduleList.get(11).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h30
		assertEquals(1,scheduleList.get(12).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h40
		assertEquals(0,scheduleList.get(13).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19u50
		assertEquals(14,scheduleList.get(14).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(14).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//21h00
		assertEquals(15,scheduleList.get(15).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(15).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//22u
		assertEquals(null,scheduleList.get(16).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(16).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList.get(17).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(17).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList.get(18).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(18).getScheduledTime());
		assertEquals(19,scheduleList.size());
	}
	@Test
	public void testScheduleToScheduledOrderListSingleTaskOrderDeadLineFailure() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithSingleTaskOrderWithFailure();

		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(),als);
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		assertEquals(12,scheduleList.get(0).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		assertEquals(13,scheduleList.get(1).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		assertEquals(16,scheduleList.get(2).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//7u10
		assertEquals(10,scheduleList.get(3).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u10
		assertEquals(11,scheduleList.get(4).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u20
		assertEquals(9,scheduleList.get(5).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//10u30
		assertEquals(8,scheduleList.get(6).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//11u30
		assertEquals(7,scheduleList.get(7).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u40
		assertEquals(6,scheduleList.get(8).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//13u50
		assertEquals(5,scheduleList.get(9).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15u00
		assertEquals(4,scheduleList.get(10).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h10
		assertEquals(3,scheduleList.get(11).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h20
		assertEquals(2,scheduleList.get(12).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h30
		assertEquals(1,scheduleList.get(13).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h40
		assertEquals(14,scheduleList.get(14).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(14).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//20h50
		assertEquals(15,scheduleList.get(15).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList.get(15).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//22u
		assertEquals(null,scheduleList.get(16).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(16).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList.get(17).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(17).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList.get(18).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(18).getScheduledTime());
		// new day
		time = new GregorianCalendar(2000,0,2,6,0,0);//6u
		assertEquals(0,scheduleList.get(19).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList.get(19).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//7u
		assertEquals(null,scheduleList.get(20).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(20).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//8u
		assertEquals(null,scheduleList.get(21).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(21).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//9u
		assertEquals(null,scheduleList.get(22).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(22).getScheduledTime());
		assertEquals(23,scheduleList.size());
	}

	@Test
	public void testScheduleToScheduledOrderListOneAdvance() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithSingleTaskOrderWithFailure();

		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(), als);
		orderList.remove(scheduleList.get(0).getScheduledOrder());
		((dummyAssemblyLine) this.als.getAssemblyLine()).add(scheduleList.get(0).getScheduledOrder());
		ArrayList<ScheduledOrder> scheduleList2 = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(), als);
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		assertEquals(13,scheduleList2.get(0).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList2.get(0).getScheduledTime());
		assertEquals(16,scheduleList2.get(1).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//7u10
		assertEquals(10,scheduleList2.get(2).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u10
		assertEquals(11,scheduleList2.get(3).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u20
		assertEquals(9,scheduleList2.get(4).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//10u30
		assertEquals(8,scheduleList2.get(5).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//11u30
		assertEquals(7,scheduleList2.get(6).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u40
		assertEquals(6,scheduleList2.get(7).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//13u50
		assertEquals(5,scheduleList2.get(8).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15u00
		assertEquals(4,scheduleList2.get(9).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h10
		assertEquals(3,scheduleList2.get(10).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h20
		assertEquals(2,scheduleList2.get(11).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h30
		assertEquals(1,scheduleList2.get(12).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h40
		assertEquals(14,scheduleList2.get(13).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList2.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//20h50
		assertEquals(15,scheduleList2.get(14).getScheduledOrder().getOrderID());
		assertEquals(time,scheduleList2.get(14).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//22u
		assertEquals(null,scheduleList2.get(15).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(15).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList2.get(16).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(16).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);//22u
		assertEquals(null,scheduleList2.get(17).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(17).getScheduledTime());
		// new day
		time = new GregorianCalendar(2000,0,2,6,0,0);//6u
		assertEquals(0,scheduleList2.get(18).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(18).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//7u
		assertEquals(null,scheduleList2.get(19).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(19).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//8u
		assertEquals(null,scheduleList2.get(20).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(20).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//9u
		assertEquals(null,scheduleList2.get(21).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(21).getScheduledTime());
		assertEquals(22,scheduleList2.size());
	}
	@Test
	public void testScheduleToScheduledOrderListOneAdvanceNr2() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderListWithNoSingleTaskOrder();

		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(),this.als.getAssemblyLine().stateWhenAcceptingOrders(),als);
		orderList.remove(scheduleList.get(0).getScheduledOrder());
		((dummyAssemblyLine) this.als.getAssemblyLine()).add(scheduleList.get(0).getScheduledOrder());
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		time.add(GregorianCalendar.MINUTE, 70);//7u10
		ArrayList<ScheduledOrder> scheduleList2 = algorithm.scheduleToScheduledOrderList(orderList,(GregorianCalendar) time.clone(),this.als.getAssemblyLine().stateWhenAcceptingOrders(), als);
		assertEquals(11,scheduleList2.get(0).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u20
		assertEquals(9,scheduleList2.get(1).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u30
		assertEquals(8,scheduleList2.get(2).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//10u30
		assertEquals(7,scheduleList2.get(3).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11u40
		assertEquals(6,scheduleList2.get(4).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u50
		assertEquals(5,scheduleList2.get(5).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//14u00
		assertEquals(4,scheduleList2.get(6).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15h10
		assertEquals(3,scheduleList2.get(7).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h20
		assertEquals(2,scheduleList2.get(8).getScheduledOrder().getOrderID());//50
		assertEquals(time,scheduleList2.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h30
		assertEquals(1,scheduleList2.get(9).getScheduledOrder().getOrderID());//70
		assertEquals(time,scheduleList2.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h40
		assertEquals(0,scheduleList2.get(10).getScheduledOrder().getOrderID());//60
		assertEquals(time,scheduleList2.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h50
		assertEquals(null,scheduleList2.get(11).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//21h00
		assertEquals(null,scheduleList2.get(12).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//22h00
		assertEquals(null,scheduleList2.get(13).getScheduledOrder());//0
		assertEquals(time,scheduleList2.get(13).getScheduledTime());
		assertEquals(14,scheduleList2.size());

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
		orderList.add(new SingleTaskOrder(16, customShopManager, config1,new GregorianCalendar(2000,0,1,12,0,0), new GregorianCalendar(1998,0,1,12,0,0)));
		return orderList;
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
	/**
	 * @return
	 * @throws InvalidConfigurationException
	 */
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
