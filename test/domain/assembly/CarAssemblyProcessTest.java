package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.OptionType;
import domain.order.OrderManager;
import domain.policies.InvalidConfigurationException;
import domain.assembly.CarAssemblyProcess;
import domain.assembly.Scheduler;
import domain.assembly.Workstation;

public class CarAssemblyProcessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	ArrayList<OptionType> taskTypes1;
	ArrayList<OptionType> taskTypes2;
	ArrayList<OptionType> taskTypes3;
	
	CarAssemblyProcess process;
	
	@Before
	public void testCreate() throws IOException, CarModelCatalogException, InvalidConfigurationException{
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		
		OrderManager orderManager = new OrderManager("testData/testData_OrderManager.txt", new CarModelCatalog(), new GregorianCalendar(2014, 1, 1, 12, 0, 0));
		Scheduler schedule = orderManager.getScheduler();
		process = schedule.getNextCarOrder(100).getAssemblyprocess();
		
		taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(null, 1, taskTypes1);
		
		taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(null, 2, taskTypes2);
		
		taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		Workstation workStation3 = new Workstation(null, 3, taskTypes3);
		
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
