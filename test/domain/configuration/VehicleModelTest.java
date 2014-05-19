package domain.configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


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
	Option Sp;
	@Before
	public void setUp() throws Exception {
		a = new ArrayList<Option>();
		A = new Option("a",new TaskTypeCreator().Airco);
		B = new Option("b",new TaskTypeCreator().Body);
		C = new Option("c",new TaskTypeCreator().Color);
		E = new Option("e",new TaskTypeCreator().Engine);
		G = new Option("g",new TaskTypeCreator().Gearbox);
		S = new Option("s",new TaskTypeCreator().Seats);
		W = new Option("w",new TaskTypeCreator().Wheels);
		Sp = new Option("sp",new TaskTypeCreator().Spoiler);
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
	public void testconstructer() throws VehicleModelCatalogException {
		VehicleModel car = new VehicleModel(Name,a,60);
		
		assertEquals(Name, car.getName());
		assertEquals(a, car.getPossibleOptions());
		assertFalse(a == car.getPossibleOptions());
		assertTrue(car.getOfOptionType(new TaskTypeCreator().Airco).contains(A));
		assertTrue( car.getOfOptionType(new TaskTypeCreator().Body).contains(B));
		assertTrue(car.getOfOptionType(new TaskTypeCreator().Color).contains(C));
		assertTrue(car.getOfOptionType(new TaskTypeCreator().Engine).contains(E));
		assertTrue( car.getOfOptionType(new TaskTypeCreator().Gearbox).contains(G));
		assertTrue( car.getOfOptionType(new TaskTypeCreator().Seats).contains(S));
		assertTrue( car.getOfOptionType(new TaskTypeCreator().Wheels).contains(W));
		assertTrue( car.getOfOptionType(new TaskTypeCreator().Spoiler).contains(Sp));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testnull()  {
		try {
			new VehicleModel(null,a);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Model",e.getMessage());
		}
		try {
			new VehicleModel(Name,null);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Model",e.getMessage());
		}
	
		ArrayList<Option> fake;
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(A);
			new VehicleModel(Name,fake);
			
		} catch (VehicleModelCatalogException e) {
			fail();
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(B);
			new VehicleModel(Name,fake);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Body");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(C);
			new VehicleModel(Name,fake);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Color");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(E);
			new VehicleModel(Name,fake);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Engine");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(G);
			new VehicleModel(Name,fake);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Gearbox");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(S);
			new VehicleModel(Name,fake);
			fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Seats");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(Sp);
			new VehicleModel(Name,fake);
		} catch (VehicleModelCatalogException e) {
			fail();
		}
		
	}
	@Test
	public void testgetOfOptionType() {
		VehicleModel car = null;
		try {
			car = new VehicleModel(Name,a);
		} catch (VehicleModelCatalogException e) {
			fail();
		}
		ArrayList<Option> temp = car.getOfOptionType(new TaskTypeCreator().Wheels);
		assertTrue(temp.contains(W));
		assertFalse(temp.contains(E));
	}
	

}
