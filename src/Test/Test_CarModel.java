package Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Main.CarModel;
import Main.Option;
import Main.CarModelCatalogException;
import OptionSubTypes.*;


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
	@Before
	public void setUp() throws Exception {
		a = new ArrayList<Option>();
		A = new Airco("a",a,a);
		B = new Body("b",a,a);
		C = new Color("c",a,a);
		E = new Engine("e",a,a);
		G = new Gearbox("g",a,a);
		S = new Seats("s",a,a);
		W = new Wheels("w",a,a);
		Name = "BMW";
		a.add(E);
		a.add(A);
		a.add(B);
		a.add(C);
		a.add(G);
		a.add(S);
		a.add(W);
		types = new ArrayList<String>();
		types.add("Airco");
		types.add("Body");
		types.add("Color");
		types.add("Engine");
		types.add("Gearbox");
		types.add("Seats");
		types.add("Wheels");
	}

	@Test
	public void testconstructer() throws CarModelCatalogException {
		CarModel car = new CarModel(Name,a,types);
		
		assertEquals(Name, car.getName());
		assertEquals(a, car.getOptions());
		assertFalse(a == car.getOptions());
		assertTrue(car.getOfOptionType("Airco").contains(A));
		assertTrue( car.getOfOptionType("Body").contains(B));
		assertTrue(car.getOfOptionType("Color").contains(C));
		assertTrue(car.getOfOptionType("Engine").contains(E));
		assertTrue( car.getOfOptionType("Gearbox").contains(G));
		assertTrue( car.getOfOptionType("Seats").contains(S));
		assertTrue( car.getOfOptionType("Wheels").contains(W));
	}
	@Test
	public void testnull()  {
		try {
			new CarModel(null,a,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,null,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,null);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		ArrayList<Option> fake;
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(A);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(B);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(C);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(E);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(G);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		try {
			fake = ((ArrayList<Option>) a.clone());
			fake.remove(S);
			new CarModel(Name,fake,types);
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"Missing type");
		}
		
	}
	

}
