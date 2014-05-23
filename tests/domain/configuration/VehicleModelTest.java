package domain.configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import domain.configuration.Option;
import domain.configuration.TaskTypeCreator;
import domain.configuration.VehicleCatalogException;
import domain.configuration.VehicleModel;


public class VehicleModelTest {
	ArrayList<Option> a;
	Option A;
	Option B;
	Option C;
	Option E; 
	Option G;
	Option S;
	Option W;
	String Name;
	ArrayList<String> types;
	TaskTypeCreator taskTypeCreator = (TaskTypeCreator) VehicleCatalog.taskTypeCreator;
	Option Sp;
	@Before
	public void setUp() throws Exception {
		a = new ArrayList<Option>();
		A = new Option("a",taskTypeCreator.Airco);
		B = new Option("b",taskTypeCreator.Body);
		C = new Option("c",taskTypeCreator.Color);
		E = new Option("e",taskTypeCreator.Engine);
		G = new Option("g",taskTypeCreator.Gearbox);
		S = new Option("s",taskTypeCreator.Seats);
		W = new Option("w",taskTypeCreator.Wheels);
		Sp = new Option("sp",taskTypeCreator.Spoiler);
		Name = "BMW";
		a.add(E);
		a.add(A);
		a.add(B);
		a.add(C);
		a.add(G);
		a.add(S);
		a.add(W);
		a.add(Sp);
	}

	@Test
	public void testconstructer() throws VehicleCatalogException {
		VehicleModel car = new VehicleModel(Name,a,null, null);
		
		assertEquals(Name, car.getName());
		assertEquals(a, car.getPossibleOptions());
		assertFalse(a == car.getPossibleOptions());
		assertTrue(car.getOfOptionType(taskTypeCreator.Airco).contains(A));
		assertTrue( car.getOfOptionType(taskTypeCreator.Body).contains(B));
		assertTrue(car.getOfOptionType(taskTypeCreator.Color).contains(C));
		assertTrue(car.getOfOptionType(taskTypeCreator.Engine).contains(E));
		assertTrue( car.getOfOptionType(taskTypeCreator.Gearbox).contains(G));
		assertTrue( car.getOfOptionType(taskTypeCreator.Seats).contains(S));
		assertTrue( car.getOfOptionType(taskTypeCreator.Wheels).contains(W));
		assertTrue( car.getOfOptionType(taskTypeCreator.Spoiler).contains(Sp));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testnull()  {
		try {
			new VehicleModel(null,a, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Model",e.getMessage());
		}
		try {
			new VehicleModel(Name,null, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Model",e.getMessage());
		}
	
		ArrayList<Option> fake;
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(A);
			new VehicleModel(Name,fake, null, null);
			
		} catch (VehicleCatalogException e) {
			fail();
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(B);
			new VehicleModel(Name,fake, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Body");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(C);
			new VehicleModel(Name,fake, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Color");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(E);
			new VehicleModel(Name,fake, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Engine");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(G);
			new VehicleModel(Name,fake, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Gearbox");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(S);
			new VehicleModel(Name,fake, null, null);
			fail();
		} catch (VehicleCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Seats");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(Sp);
			new VehicleModel(Name,fake, null, null);
		} catch (VehicleCatalogException e) {
			fail();
		}
		
	}
	@Test
	public void testgetOfOptionType() {
		VehicleModel car = null;
		try {
			car = new VehicleModel(Name,a, null, null);
		} catch (VehicleCatalogException e) {
			fail();
		}
		ArrayList<Option> temp = car.getOfOptionType(taskTypeCreator.Wheels);
		assertTrue(temp.contains(W));
		assertFalse(temp.contains(E));
	}
	

}
