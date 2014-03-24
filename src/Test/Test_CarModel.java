package Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Car.CarModel;
import Car.Option;
import Order.CarModelCatalogException;
import Order.OptionSubTypes.*;


public class Test_CarModel {
	ArrayList<Option> a;
	Airco A;
	Body B;
	Color C;
	Engine E; 
	Gearbox G;
	Seats S;
	Wheels W;
	String Name;
	ArrayList<String> types;
	Spoiler Sp;
	@Before
	public void setUp() throws Exception {
		a = new ArrayList<Option>();
		ArrayList<Option> b = new ArrayList<Option>();
		ArrayList<Option> c = new ArrayList<Option>();
		A = new Airco("a",b,c);
		B = new Body("b",b,c);
		C = new Color("c",b,c);
		E = new Engine("e",b,c);
		G = new Gearbox("g",b,c);
		S = new Seats("s",b,c);
		W = new Wheels("w",b,c);
		Sp = new Spoiler("sp",b,c);
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
		CarModel car = new CarModel(Name,a);
		
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
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testnull()  {
		try {
			new CarModel(null,a);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,null);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
	
		ArrayList<Option> fake;
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(A);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Airco");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(B);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Body");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(C);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Color");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(E);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Engine");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(G);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Gearbox");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(S);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Seats");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(Sp);
			new CarModel(Name,fake);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type: Spoiler");
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
