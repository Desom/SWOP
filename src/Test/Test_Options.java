package Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import Car.Option;
import Car.OptionType;
import Order.CarModelCatalogException;


public class Test_Options {
	
	@Test
	public void testcreate() throws CarModelCatalogException {
		ArrayList<Option> a = new ArrayList<Option>();
		ArrayList<Option> b = new ArrayList<Option>();
		Option opt = new Option("green", a, b, OptionType.Color);
		assertEquals(opt.getDescription(), "green");
		assertEquals(opt.getCompatibles(),a);
		assertEquals(opt.getIncompatibles(),b);
		assertFalse(opt.getCompatibles() == b);
		assertFalse(opt.getIncompatibles()== a);
	}

	@Test
	public void nulltest(){
		ArrayList<Option> a = new ArrayList<Option>();
		try {
			 new Option("red", null , a,OptionType.Color);
			 fail();
		} catch (CarModelCatalogException e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
		try {
			 new Option("green", a , null,OptionType.Color);
			 fail();
		} catch (CarModelCatalogException e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
		try {
			 new Option(null, a , a,OptionType.Color);
			 fail();
		} catch (CarModelCatalogException e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
		try {
			 new Option("red", null , a, null);
			 fail();
		} catch (CarModelCatalogException e) {
			assertEquals("null in non null value of Option", e.GetMessage());
		}
	}
	
	@Test
	public void paradoxtest() throws CarModelCatalogException{
		ArrayList<Option> a = new ArrayList<Option>();
		ArrayList<Option> b = new ArrayList<Option>();
		b.add(new Option("blue", a, a,OptionType.Color));
		try {
			 new Option("red", b , b,OptionType.Color);
			 fail();
		} catch (CarModelCatalogException e) {
			assertEquals("Option is both Compatible and incompatiblle with another option at the same type" , e.GetMessage());
		}
	}
	
	@Test
	public void testconflictsWith_False() throws CarModelCatalogException {
		ArrayList<Option> a = new ArrayList<Option>();
		Option opt = new Option("green", new ArrayList<Option>(), new ArrayList<Option>(),OptionType.Color);
		a.add(opt);
		Option opt2 = new Option("red", a, new ArrayList<Option>(),OptionType.Color);
		assertFalse(opt.conflictsWith(opt2));
		assertFalse(opt2.conflictsWith(opt));
	}

	@Test
	public void testconflictsWith_True() throws CarModelCatalogException {
		ArrayList<Option> a = new ArrayList<Option>();
		Option opt = new Option("green", new ArrayList<Option>(), new ArrayList<Option>(),OptionType.Color);
		a.add(opt);
		Option opt2 = new Option("red",  new ArrayList<Option>(),a,OptionType.Color);
		assertTrue(opt.conflictsWith(opt2));
		assertTrue(opt2.conflictsWith(opt));
	}

}
