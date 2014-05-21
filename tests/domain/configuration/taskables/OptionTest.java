package domain.configuration.taskables;
import static org.junit.Assert.*;

import org.junit.Test;

import domain.configuration.VehicleCatalogException;
import domain.configuration.taskables.Option;
import domain.configuration.taskables.TaskTypeCreator;


public class OptionTest {
	
	@Test
	public void testcreate() throws VehicleCatalogException {
		Option opt = new Option("green", new TaskTypeCreator().Color);
		assertEquals(opt.getDescription(), "green");
	}

	@Test
	public void nulltest(){
		try {
			 new Option("red", null);
			 fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option("green", null);
			 fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option(null, new TaskTypeCreator().Color);
			 fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
		try {
			 new Option("red", null);
			 fail();
		} catch (VehicleCatalogException e) {
			assertEquals("null in non null value of Option", e.getMessage());
		}
	}
	
	
	@Test
	public void testconflictsWith_False() throws VehicleCatalogException {
		Option opt = new Option("green", new TaskTypeCreator().Color);
		Option opt2 = new Option("red", new TaskTypeCreator().Seats);
		assertFalse(opt.conflictsWith(opt2));
		assertFalse(opt2.conflictsWith(opt));
	}

	@Test
	public void testconflictsWith_True() throws VehicleCatalogException {
		Option opt = new Option("green", new TaskTypeCreator().Color);
		Option opt2 = new Option("red",  new TaskTypeCreator().Seats);
		opt.addIncompatible(opt2);
		opt2.addIncompatible(opt);
		assertTrue(opt.conflictsWith(opt2));
		assertTrue(opt2.conflictsWith(opt));
	}

}
