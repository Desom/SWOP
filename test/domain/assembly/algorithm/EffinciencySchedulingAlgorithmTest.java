package domain.assembly.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.Order;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.user.GarageHolder;

public class EffinciencySchedulingAlgorithmTest {

	SchedulingAlgorithm algorithm;
	GarageHolder garageHolder;
	CarModelCatalog cmc;
	AssemblyLineScheduler als;
	
	@Before
	public void testCreate() throws IOException, CarModelCatalogException {
		this.algorithm = new EfficiencySchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		ArrayList<SchedulingAlgorithm> list = new ArrayList<SchedulingAlgorithm>();
		list.add(algorithm);
		this.garageHolder = new GarageHolder(0);
		this.cmc = new CarModelCatalog();
		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
		AssemblyLine assembly = new AssemblyLine(als, null);
	}
	
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, als);

		assertEquals(10,scheduleList.get(0).getCarOrderID());
		assertEquals(11,scheduleList.get(1).getCarOrderID());
		assertEquals(9,scheduleList.get(2).getCarOrderID());
		assertEquals(8,scheduleList.get(3).getCarOrderID());
		assertEquals(7,scheduleList.get(4).getCarOrderID());
		assertEquals(6,scheduleList.get(5).getCarOrderID());
		assertEquals(5,scheduleList.get(6).getCarOrderID());
		assertEquals(4,scheduleList.get(7).getCarOrderID());
		assertEquals(3,scheduleList.get(8).getCarOrderID());
		assertEquals(2,scheduleList.get(9).getCarOrderID());
		assertEquals(1,scheduleList.get(10).getCarOrderID());
		assertEquals(0,scheduleList.get(11).getCarOrderID());
	}

	@Test
	public void testScheduleToScheduledOrderList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,this.als.getCurrentTime(), als);
		GregorianCalendar time = (GregorianCalendar) this.als.getCurrentTime().clone();//6u00
		assertEquals(10,scheduleList.get(0).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7u10
		assertEquals(11,scheduleList.get(1).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8u20
		assertEquals(9,scheduleList.get(2).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9u30
		assertEquals(8,scheduleList.get(3).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//10u30
		assertEquals(7,scheduleList.get(4).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//11u40
		assertEquals(6,scheduleList.get(5).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//12u50
		assertEquals(5,scheduleList.get(6).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//14u00
		assertEquals(4,scheduleList.get(7).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//15h10
		assertEquals(3,scheduleList.get(8).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//16h20
		assertEquals(2,scheduleList.get(9).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h30
		assertEquals(1,scheduleList.get(10).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h40
		assertEquals(null,scheduleList.get(11).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h50
		assertEquals(null,scheduleList.get(12).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//21h00
		assertEquals(null,scheduleList.get(13).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		// new day
		time = new GregorianCalendar(2000,0,2,6,0,0);//6u
		assertEquals(0,scheduleList.get(14).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(14).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//7u
		assertEquals(null,scheduleList.get(15).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(15).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//8u
		assertEquals(null,scheduleList.get(16).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(16).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//9u
		assertEquals(null,scheduleList.get(17).getScheduledOrder());//0
		assertEquals(time,scheduleList.get(17).getScheduledTime());
		assertEquals(18,scheduleList.size());
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
		
		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);
		
		orderList.add(new CarOrder(0, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(1, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(2, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(3, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(4, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(5, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(6, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(7, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(8, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(9, garageHolder, config1, time));
		time.add(GregorianCalendar.HOUR, -1);
		orderList.add(new CarOrder(10, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, 1);
		orderList.add(new CarOrder(11, garageHolder, config3, time));
		return orderList;
	}
	
	//TODO
//	@Test
//	public void testNextDay() {
//		GregorianCalendar time1 = new GregorianCalendar(2000, 5, 7, 22, 0, 0);
//		GregorianCalendar time2 = new GregorianCalendar(2000, 5, 7, 1, 0, 0);
//		GregorianCalendar time3 = new GregorianCalendar(2000, 5, 30, 23, 0, 0);
//		
//		GregorianCalendar solution1 = this.algorithm.nextDay(time1);
//		assertEquals(2000, solution1.get(GregorianCalendar.YEAR));
//		assertEquals(5, solution1.get(GregorianCalendar.MONTH));
//		assertEquals(8, solution1.get(GregorianCalendar.DAY_OF_MONTH));
//		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution1.get(GregorianCalendar.HOUR_OF_DAY));
//		
//		GregorianCalendar solution2 = this.algorithm.nextDay(time2);
//		assertEquals(2000, solution2.get(GregorianCalendar.YEAR));
//		assertEquals(5, solution2.get(GregorianCalendar.MONTH));
//		assertEquals(7, solution2.get(GregorianCalendar.DAY_OF_MONTH));
//		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution2.get(GregorianCalendar.HOUR_OF_DAY));
//		
//		GregorianCalendar solution3 = this.algorithm.nextDay(time3);
//		assertEquals(2000, solution3.get(GregorianCalendar.YEAR));
//		assertEquals(6, solution3.get(GregorianCalendar.MONTH));
//		assertEquals(1, solution3.get(GregorianCalendar.DAY_OF_MONTH));
//		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution3.get(GregorianCalendar.HOUR_OF_DAY));
//	}

}
