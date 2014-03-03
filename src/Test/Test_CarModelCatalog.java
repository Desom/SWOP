package Test;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import Main.CarModelCatalog;
import Main.inconsistent_state_Exception;


public class Test_CarModelCatalog {

	@Test
	public void testcreate() throws IOException, inconsistent_state_Exception {
		new CarModelCatalog();
	}
	@Test
	public void test_option_wrongsize() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_options.txt"));
		write.write("a;Body");
		write.close();
		try {
			new CarModelCatalog("test_options.txt","test_models.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Option: wrong input format: a;Body",e.GetMessage());
		}
	}
	@Test
	public void test_option_doublename() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_options.txt"));
		write.write("a;Body;,\n");
		write.write("a;Gear;,");
		write.close();
		try {
			new CarModelCatalog("test_options.txt","test_models.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Option already exists: a",e.GetMessage());
		}
	}

	@Test
	public void test_option_non_existanceoption() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_options.txt"));
		write.write("a;Body;b,");
		write.close();
		try {
			new CarModelCatalog("test_options.txt","test_models.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Option does not exists: b",e.GetMessage());
		}
	}

	@Test
	public void test_option_non_existancetype() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_options.txt"));
		write.write("a;fake;,");
		write.close();
		try {
			new CarModelCatalog("test_options.txt","test_models.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("no valid type: fake",e.GetMessage());
		}
	}
	@Test
	public void test_model_wrongsize() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_model.txt"));
		write.write("a;Body;,");
		write.close();
		try {
			new CarModelCatalog("options.txt","test_model.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Model: wrong input format: a;Body;,",e.GetMessage());
		}
	}
	@Test
	public void test_model_doublename() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_model.txt"));
		write.write("a;manual;sedan;red;standard 2l 4 cilinders;6 speed manual;leather black;comfort;,\n");
		write.write("a;,;,;,;,;,;,;,;,");
		write.close();
		try {
			new CarModelCatalog("options.txt","test_model.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Model name already exists: a",e.GetMessage());
		}
	}
	@Test
	public void test_model_nonexisting_Option() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_model.txt"));
		write.write("a;,;,;,;,;,;,;,;,");
		write.close();
		try {
			new CarModelCatalog("options.txt","test_model.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Option does not exists: ,",e.GetMessage());
		}
	}
	@Test
	public void test_model_wrongtype() throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter("test_model.txt"));
		write.write("a;sedan;sedan;red;standard 2l 4 cilinders;6 speed manual;leather black;comfort;,\n");
		write.close();
		try {
			new CarModelCatalog("options.txt","test_model.txt");
			fail();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			assertEquals("Wrong Option Type in form: a;sedan;sedan;red;standard 2l 4 cilinders;6 speed manual;leather black;comfort;,",e.GetMessage());
		}
	}

}
