package Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import Assembly.AssemblyLine;
import Assembly.AssemblyTask;
import Assembly.CannotAdvanceException;
import Assembly.CarAssemblyProcess;
import Assembly.ProductionSchedule;
import Assembly.Workstation;
import Car.Car;
import Main.DoesNotExistException;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import Order.OrderManager;
import User.CarMechanic;
import User.Manager;
import User.UserAccessException;

public class CarAssemblyProccessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	CarAssemblyProcess process;
	
	@Before
	public void testCreate(){
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		process = new Car().getAssemblyprocess();
		
		ArrayList<String> taskTypes1 = new ArrayList<String>();
		taskTypes1.add("Body");
		taskTypes1.add("Color");
		Workstation workStation1 = new Workstation(1, taskTypes1);
		
		ArrayList<String> taskTypes2 = new ArrayList<String>();
		taskTypes2.add("Engine");
		taskTypes2.add("GearBox");
		Workstation workStation2 = new Workstation(2, taskTypes2);
		
		ArrayList<String> taskTypes3 = new ArrayList<String>();
		taskTypes3.add("Seats");
		taskTypes3.add("Airco");
		taskTypes3.add("Wheels");
		Workstation workStation3 = new Workstation(3, taskTypes3);
		
		w1 = workStation1;
		w2 = workStation2;
		w3 = workStation3;
	}
	
	@Test
	public void testMatching(){ // VUL TASKS IN AFHANKELIJK VAN CAR OPTIONS
		ArrayList<AssemblyTask> tasks1 = new ArrayList<AssemblyTask>();
		for(int i = 0; i<tasks1.size(); i++){
			assertEquals(process.compatibleWith(w1).get(i), tasks1.get(i));
		}
		
		ArrayList<AssemblyTask> tasks2 = new ArrayList<AssemblyTask>();
		for(int i = 0; i<tasks2.size(); i++){
			assertEquals(process.compatibleWith(w2).get(i), tasks2.get(i));
		}
		
		
		ArrayList<AssemblyTask> tasks3 = new ArrayList<AssemblyTask>();
		for(int i = 0; i<tasks3.size(); i++){
			assertEquals(process.compatibleWith(w3).get(i), tasks3.get(i));
		}
	}
	
	
}
