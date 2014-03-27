package Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import Assembly.CarAssemblyProcess;
import Assembly.ProductionSchedule;
import Assembly.Workstation;
import Car.OptionType;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import Order.OrderManager;

public class CarAssemblyProccessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	ArrayList<OptionType> taskTypes1;
	ArrayList<OptionType> taskTypes2;
	ArrayList<OptionType> taskTypes3;
	
	CarAssemblyProcess process;
	
	@Before
	public void testCreate() throws IOException, CarModelCatalogException{
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		
		OrderManager orderManager = new OrderManager("testData_OrderManager.txt", new CarModelCatalog(), new GregorianCalendar(2014, 1, 1, 12, 0, 0));
		ProductionSchedule schedule = orderManager.getProductionSchedule();
		process = schedule.getNextCarOrder(100).getCar().getAssemblyprocess();
		
		taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(1, taskTypes1);
		
		taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(2, taskTypes2);
		
		taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		Workstation workStation3 = new Workstation(3, taskTypes3);
		
		w1 = workStation1;
		w2 = workStation2;
		w3 = workStation3;
	}
	
	@Test
	public void testMatching(){
		for(int i = 0; i<process.compatibleWith(w1).size(); i++){
			assertTrue(taskTypes1.contains(process.compatibleWith(w1).get(i).getType()));
		}
		
		for(int i = 0; i<process.compatibleWith(w2).size(); i++){
			assertTrue(taskTypes2.contains(process.compatibleWith(w2).get(i).getType()));
		}
		
		for(int i = 0; i<process.compatibleWith(w3).size(); i++){
			assertTrue(taskTypes3.contains(process.compatibleWith(w3).get(i).getType()));
		}
	}
	
	
}
