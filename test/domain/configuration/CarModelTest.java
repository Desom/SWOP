package domain.configuration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


public class CarModelTest {
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
		A = new Option("a",OptionType.Airco);
		B = new Option("b",OptionType.Body);
		C = new Option("c",OptionType.Color);
		E = new Option("e",OptionType.Engine);
		G = new Option("g",OptionType.Gearbox);
		S = new Option("s",OptionType.Seats);
		W = new Option("w",OptionType.Wheels);
		Sp = new Option("sp",OptionType.Spoiler);
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
	public void testconstructer() throws CarModelCatalogException {
		CarModel car = new CarModel(Name,a,60);
		
		assertEquals(Name, car.getName());
		assertEquals(a, car.getOptions());
		assertFalse(a == car.getOptions());
		assertTrue(car.getOfOptionType(OptionType.Airco).contains(A));
		assertTrue( car.getOfOptionType(OptionType.Body).contains(B));
		assertTrue(car.getOfOptionType(OptionType.Color).contains(C));
		assertTrue(car.getOfOptionType(OptionType.Engine).contains(E));
		assertTrue( car.getOfOptionType(OptionType.Gearbox).contains(G));
		assertTrue( car.getOfOptionType(OptionType.Seats).contains(S));
		assertTrue( car.getOfOptionType(OptionType.Wheels).contains(W));
		assertTrue( car.getOfOptionType(OptionType.Spoiler).contains(Sp));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testnull()  {
		try {
			new CarModel(null,a);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,null);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"null in non null value of Model");
		}
	
		ArrayList<Option> fake;
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(A);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Airco");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(B);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Body");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(C);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Color");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(E);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Engine");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(G);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Gearbox");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(S);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Seats");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(Sp);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			assertEquals(e.getMessage(),"Missing type: Spoiler");
		}
		
	}
	@Test
	public void testgetOfOptionType() {
		CarModel car = null;
		try {
			car = new CarModel(Name,a);
		} catch (CarModelCatalogException e) {
			fail();
		}
		ArrayList<Option> temp = car.getOfOptionType(OptionType.Wheels);
		assertTrue(temp.contains(W));
		assertFalse(temp.contains(E));
	}
	

}
