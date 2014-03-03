package Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Main.CarModel;
import Main.Option;
import Main.inconsistent_state_Exception;
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
	}

	@Test
	public void testconstructer() throws inconsistent_state_Exception {
		CarModel car = new CarModel(Name,a,A,B,C,E,G,S,W);
		assertEquals(Name, car.getName());
		assertEquals(a, car.getOptions());
		assertFalse(a == car.getOptions());
		assertEquals(A, car.getDefault_Airco());
		assertEquals(B, car.getDefault_Body());
		assertEquals(C, car.getDefault_Color());
		assertEquals(E, car.getDefault_Engine());
		assertEquals(G, car.getDefault_Gearbox());
		assertEquals(S, car.getDefault_Seats());
		assertEquals(W, car.getDefault_Wheels());
	}
	@Test
	public void testnull()  {
		try {
			new CarModel(null,a,A,B,C,E,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,null,A,B,C,E,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,null,B,C,E,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,null,C,E,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,B,null,E,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,B,C,null,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,B,C,null,G,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,B,C,E,null,S,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");		}
		try {
			new CarModel(Name,a,A,B,C,E,G,null,W);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
		try {
			new CarModel(Name,a,A,B,C,E,G,S,null);
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals(e.GetMessage(),"null in non null value of Model");
		}
	}
	

}
