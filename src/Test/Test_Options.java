package Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import Main.Option;
import Main.inconsistent_state_Exception;
import OptionSubTypes.Color;


public class Test_Options {

	@Test
	public void testcreate() throws inconsistent_state_Exception {
		ArrayList<Option> a = new ArrayList<Option>();
		ArrayList<Option> b = new ArrayList<Option>();
		Option opt = new Color("green", a, b);
		assertEquals(opt.getdescription(), "green");
		assertEquals(opt.getCompatibles(),a);
		assertEquals(opt.getIncompatibles(),b);
		assertFalse(opt.getCompatibles() == b);
		assertFalse(opt.getIncompatibles()== a);
	}

	@Test
	public void nulltest(){
		ArrayList<Option> a = new ArrayList<Option>();
		try {
			 new Color("red", null , a);
			 fail();
		} catch (inconsistent_state_Exception e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
		try {
			 new Color("green", a , null);
			 fail();
		} catch (inconsistent_state_Exception e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
		try {
			 new Color(null, a , a);
			 fail();
		} catch (inconsistent_state_Exception e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
	}
	
	@Test
	public void paradoxtest() throws inconsistent_state_Exception{
		ArrayList<Option> a = new ArrayList<Option>();
		ArrayList<Option> b = new ArrayList<Option>();
		b.add(new Color("blue", a, a));
		try {
			 new Color("red", b , b);
			 fail();
		} catch (inconsistent_state_Exception e) {
			assertEquals("Option is both Compatible and incompatiblle with another option at the same type" , e.GetMessage());
		}
	}
	
	@Test
	public void testconflictsWith_False() throws inconsistent_state_Exception {
		ArrayList<Option> a = new ArrayList<Option>();
		Option opt = new Color("green", new ArrayList<Option>(), new ArrayList<Option>());
		a.add(opt);
		Option opt2 = new Color("red", a, new ArrayList<Option>());
		assertFalse(opt.conflictsWith(opt2));
		assertFalse(opt2.conflictsWith(opt));
	}

	@Test
	public void testconflictsWith_True() throws inconsistent_state_Exception {
		ArrayList<Option> a = new ArrayList<Option>();
		Option opt = new Color("green", new ArrayList<Option>(), new ArrayList<Option>());
		a.add(opt);
		Option opt2 = new Color("red",  new ArrayList<Option>(),a);
		assertTrue(opt.conflictsWith(opt2));
		assertTrue(opt2.conflictsWith(opt));
	}

}