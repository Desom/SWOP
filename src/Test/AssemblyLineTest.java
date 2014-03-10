package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import Main.AssemblyLine;
import Main.CannotAdvanceException;
import Main.CarAssemblyProcess;
import Main.CarMechanic;
import Main.CarModelCatalog;
import Main.CarModelCatalogException;
import Main.DoesNotExistException;
import Main.Manager;
import Main.OrderManager;
import Main.ProductionSchedule;
import Main.UserAccessException;
import Main.Workstation;

public class AssemblyLineTest {

	
	private AssemblyLine line;
	private ProductionSchedule schedule;
	private Manager manager;
	private CarMechanic m1;
	private CarMechanic m2;
	private CarMechanic m3;
	
	@Before
	public void testCreate() throws UserAccessException, DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException {
		manager = new Manager(1);
		m1 = new CarMechanic(2);
		m2 = new CarMechanic(3);
		m3 = new CarMechanic(4);
		
		// maak een andere orderManager met enkele voorbeeld orders
		OrderManager orderManager = new OrderManager(new CarModelCatalog()); 
		schedule = orderManager.getProductionSchedule();
		line = new AssemblyLine(schedule);
		
		line.selectWorkStation(m1, 1);
		assertEquals(line.selectWorkStationId(1, manager).getCarMechanic(), m1);
		
		line.selectWorkStation(m2, 2);
		assertEquals(line.selectWorkStationId(2, manager).getCarMechanic(), m2);
		
		line.selectWorkStation(m3, 3);
		assertEquals(line.selectWorkStationId(3, manager).getCarMechanic(), m3);
		
		//line.advanceLine(manager, 100);
		//line.advanceLine(manager, 100);
	}
	
	@Test
	public void testgetAllWorkStations() throws UserAccessException {
		LinkedList<Workstation> stations = line.getAllWorkStations(manager);
		for(Workstation w : stations){
			assertNotNull(w);
		}
	}
	
	@Test
	public void testSelectWorkStationId() throws DoesNotExistException, UserAccessException{
		assertNotNull(line.selectWorkStationId(1, manager));
		assertEquals(line.selectWorkStationId(1, manager).getId(), 1);
		
		assertNotNull(line.selectWorkStationId(2, manager));
		assertEquals(line.selectWorkStationId(2, manager).getId(), 2);
		
		assertNotNull(line.selectWorkStationId(3, manager));
		assertEquals(line.selectWorkStationId(3, manager).getId(), 3);
	}
	
	@Test
	public void testAdvanceLineSucces() throws UserAccessException, DoesNotExistException, CannotAdvanceException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkStations(manager)){
			while(w.getAllPendingTasks(w.getCarMechanic()).size() > 0){ // complete alle tasks
				w.selectTask(w.getCarMechanic(), w.getAllPendingTasks(w.getCarMechanic()).get(0));
				w.completeTask(w.getCarMechanic());
			}
			processesBefore.add(w.getCurrentCar());
		}
		
		line.advanceLine(manager, 100);
		CarAssemblyProcess next = schedule.seeNextCarOrder().getCar().getAssemblyprocess();
		
		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkStations(manager)){
			processesAfter.add(w.getCurrentCar());
		}
		
		assertEquals(processesAfter.get(0), next);
		for(int i = 0; i<processesAfter.size()-1; i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i+1));
		}
	}
	
	@Test(expected = CannotAdvanceException.class)  
	public void testAdvanceLineBlocking() throws UserAccessException, DoesNotExistException, CannotAdvanceException {
		ArrayList<CarAssemblyProcess> processesBefore = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkStations(manager)){
			processesBefore.add(w.getCurrentCar());
		}
		
		line.advanceLine(manager, 100);
		
		ArrayList<CarAssemblyProcess> processesAfter = new ArrayList<CarAssemblyProcess>();
		for(Workstation w : line.getAllWorkStations(manager)){
			processesAfter.add(w.getCurrentCar());
		}
		
		for(int i = 0; i<processesAfter.size(); i++){
			assertEquals(processesBefore.get(i), processesAfter.get(i));
		}
	}
	
	
	
}
