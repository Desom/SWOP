package test.configuration;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;


public class Test_CarModelCatalog {
	@Before
	public void setUp() throws Exception {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_option.txt"));
		write.write("sedan;Body;,\n");
		write.write("break;Body;,\n");
		write.write("red;Color;,\n");
		write.write("blue;Color;,\n");
		write.write("black;Color;,\n");
		write.write("white;Color;,\n");
		write.write("standard 2l 4 cilinders;Engine;,\n");
		write.write("performance 2.5l 6 cilinders;Engine;,\n");
		write.write("6 speed manual;Gearbox;,\n");
		write.write("5 speed automatic;Gearbox;,\n");
		write.write("leather black;Seats;,\n");
		write.write("leather white;Seats;,\n");
		write.write("vinyl grey;Seats;,\n");
		write.write("manual;Airco;,\n");
		write.write("automatic climate control;Airco;,\n");
		write.write("comfort;Wheels;,\n");
		write.write("sports (low profle);Wheels;,\n");
		write.write("no spoiler;Spoiler;,");
		write.close();
		BufferedWriter write2 = new BufferedWriter(new FileWriter("testData/init_model.txt"));
		write2.write("Ford;manual,sedan,red,performance 2.5l 6 cilinders,6 speed manual,leather black,comfort,black,blue,no spoiler");
		write2.close();
	}
	@Test
	public void testcreate() throws IOException, CarModelCatalogException {
		CarModelCatalog catalog =new CarModelCatalog("testData/test_option.txt","testData/init_model.txt");
//		assertTrue(catalog.getOption("red").conflictsWith(catalog.getOption("blue")));
//		assertFalse(catalog.getOption("manual").conflictsWith(catalog.getOption("blue")));
	}
	@Test
	public void test_option_wrongsize() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_options.txt"));
		write.write("a;Body");
		write.close();
		try {
			new CarModelCatalog("testData/test_options.txt","testData/test_models.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Option: wrong input format: a;Body",e.getMessage());
		}
	}
	@Test
	public void test_option_doublename() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_options.txt"));
		write.write("a;Body;,\n");
		write.write("a;Gear;,");
		write.close();
		try {
			new CarModelCatalog("testData/test_options.txt","testData/test_models.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Option already exists: a",e.getMessage());
		}
	}

	@Test
	public void test_option_non_existanceoption() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_options.txt"));
		write.write("a;Body;b,");
		write.close();
		try {
			new CarModelCatalog("testData/test_options.txt","testData/test_models.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Option does not exists: b",e.getMessage());
		}
	}

	@Test
	public void test_option_non_existancetype() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_options.txt"));
		write.write("a;fake;,");
		write.close();
		try {
			new CarModelCatalog("testData/test_options.txt","testData/test_models.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("no valid type: fake",e.getMessage());
		}
	}
	@Test
	public void test_model_wrongsize() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_model.txt"));
		write.write("a;Body;,");
		write.close();
		try {
			new CarModelCatalog("testData/test_option.txt","testData/test_model.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Model: wrong input format: a;Body;,",e.getMessage());
		}
	}
	@Test
	public void test_model_doublename() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_model.txt"));
		write.write("a;manual,sedan,red,standard 2l 4 cilinders,6 speed manual,leather black,comfort,no spoiler,\n");
		write.write("a;manual,sedan,red,standard 2l 4 cilinders,6 speed manual,leather black,comfort,no spoiler,");
		write.close();
		try {
			new CarModelCatalog("testData/test_option.txt","testData/test_model.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Model name already exists: a",e.getMessage());
		}
	}
	@Test
	public void test_model_nonexisting_Option() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("testData/test_model.txt"));
		write.write("a;manual,sedan,red,nospoiler,standard 2l 4 cilinders,6 speed manual,leather black,comfort,");
		write.close();
		try {
			new CarModelCatalog("testData/test_option.txt","testData/test_model.txt");
			fail();
		} catch (CarModelCatalogException e) {
			// TODO Auto-generated catch block
			assertEquals("Option does not exists: nospoiler",e.getMessage());
		}
	}
	
	

}
