package domain.configuration;
import static org.junit.Assert.*;

import org.junit.Test;

import domain.configuration.VehicleModelCatalogException;
import domain.configuration.Option;
import domain.configuration.OptionType;


public class OptionTest {
	
	@Test
	public void testcreate() throws VehicleModelCatalogException {
		Option opt = new Option("green", OptionType.Color);
		assertEquals(opt.getDescription(), "green");
	}

	@Test
	public void nulltest(){
		try {
			 new Option("red", null);
			 fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option("green", null);
			 fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option(null, OptionType.Color);
			 fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option("red", null);
			 fail();
		} catch (VehicleModelCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
	}
	
	
	@Test
	public void testconflictsWith_False() throws VehicleModelCatalogException {
		Option opt = new Option("green", OptionType.Color);
		Option opt2 = new Option("red", OptionType.Seats);
		assertFalse(opt.conflictsWith(opt2));
		assertFalse(opt2.conflictsWith(opt));
	}

	@Test
	public void testconflictsWith_True() throws VehicleModelCatalogException {
		Option opt = new Option("green", OptionType.Color);
		Option opt2 = new Option("red",  OptionType.Seats);
		opt.addIncompatible(opt2);
		opt2.addIncompatible(opt);
		assertTrue(opt.conflictsWith(opt2));
		assertTrue(opt2.conflictsWith(opt));
	}

}
