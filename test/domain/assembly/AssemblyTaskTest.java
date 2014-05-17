package domain.assembly;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import domain.assembly.AssemblyTask;
import domain.configuration.VehicleModel;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.VehicleModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.VehicleOrder;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.user.GarageHolder;

public class AssemblyTaskTest {
	
	private static AssemblyTask task;

	@BeforeClass
	public static void testCreate() throws IOException, VehicleModelCatalogException, InvalidConfigurationException {
		VehicleOrder o = buildCar();
		ArrayList<OptionType> taskTypes = new ArrayList<OptionType>();
		taskTypes.add(OptionType.Body);
		taskTypes.add(OptionType.Color);
		Workstation w = new Workstation(null, "name", taskTypes);
		task = w.compatibleWith(o.getAssemblyprocess()).get(0);;
	}
	
	@Test
	public void testCompleteTask() {
		task.completeTask(60);
		assert(task.isCompleted());
		assertEquals("dummy action", task.getActions().get(0));
	}
	
	public static VehicleOrder buildCar() throws IOException, VehicleModelCatalogException, InvalidConfigurationException{
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN

		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;
		
		
		VehicleModelCatalog catalog = new VehicleModelCatalog();
		VehicleModel carModel = null;
		for(VehicleModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}
		
		Configuration config = new Configuration(carModel, carOrderPolicy);
		
		for(Option option : catalog.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					)
				try {
					config.addOption(option);
				} catch (InvalidConfigurationException e) {
					fail();
				}
		}
		config.complete();
		
		GarageHolder garageHolder = new GarageHolder(1);
		
		GregorianCalendar now = new GregorianCalendar();
		return new VehicleOrder(1, garageHolder, config, now);
	}

}
