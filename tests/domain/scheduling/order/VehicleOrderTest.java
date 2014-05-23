package domain.scheduling.order;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.TaskTypeCreator;
import domain.configuration.VehicleCatalog;
import domain.configuration.Configuration;
import domain.configuration.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.scheduling.order.VehicleOrder;
import domain.user.GarageHolder;

public class VehicleOrderTest {

	public static ArrayList<Option> arrayOption;
	public static VehicleModel carModel;
	public static GarageHolder garageHolder;
	public static Configuration config;
	public static Policy carOrderPolicy;

	public VehicleOrder carOrder;
	public VehicleOrder carOrder1;
	public VehicleOrder carOrder2;
	public VehicleOrder carOrderNew;
	public GregorianCalendar now;
	public GregorianCalendar now1;
	public GregorianCalendar now3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
		
		@SuppressWarnings("static-access")
		Policy pol1 = new CompletionPolicy(null,catalog.taskTypeCreator.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		carOrderPolicy= pol4;
		
		carModel = null;
		for(VehicleModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}
		
		config = new Configuration(carModel, carOrderPolicy);
		
		for(Option option : catalog.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					)
				config.addOption(option);
		}
		config.complete();
		
		garageHolder = new GarageHolder(1);
	}

	@Before
	public void setUp() throws Exception {
		
		now = new GregorianCalendar();
		now1 = (GregorianCalendar) now.clone();
		now1.add(GregorianCalendar.HOUR_OF_DAY, 1);
		now3 = (GregorianCalendar) now.clone();
		now3.add(GregorianCalendar.HOUR_OF_DAY, 3);
		carOrder = new VehicleOrder(0, garageHolder, config, now, now3, true);
		carOrder1 = new VehicleOrder(1, garageHolder, config, now1);
		carOrder2 = new VehicleOrder(3, garageHolder, config, now3);
		//carOrderNew = new CarOrder(5, garageHolder,carModel, arrayOption);
	}

	@Test
	public void testCreation() {
		//TODO meer testen?
		assertEquals(carModel,carOrder.getConfiguration().getModel());
		assertEquals(0,carOrder.getOrderID());
		assertEquals(now3,carOrder.getDeliveredTime());
		assertEquals(now,carOrder.getOrderedTime());
		assertEquals(garageHolder.getId(),carOrder.getUserId());
	}

	@Test
	public void testGetDeliveredTime() {
		assertEquals(now3, carOrder.getDeliveredTime());
		
		try{
			carOrder1.getDeliveredTime();
			fail();
		}
		catch(IllegalStateException e){
			assertEquals("This order hasn't been delivered yet",e.getMessage());
		}
		
		try{
			carOrder2.getDeliveredTime();
			fail();
		}
		catch(IllegalStateException e){
			assertEquals("This order hasn't been delivered yet",e.getMessage());
		}
	}

}

