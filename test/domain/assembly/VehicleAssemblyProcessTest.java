package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

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
import domain.assembly.VehicleAssemblyProcess;
import domain.assembly.Workstation;

public class VehicleAssemblyProcessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	ArrayList<OptionType> taskTypes1;
	ArrayList<OptionType> taskTypes2;
	ArrayList<OptionType> taskTypes3;
	
	VehicleAssemblyProcess process;

	
	@Before
	public void testCreate() throws IOException, VehicleModelCatalogException, InvalidConfigurationException{
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		
		
		process = createCar().getAssemblyprocess();

		LinkedList<OptionType> bodyPost = new LinkedList<OptionType>();
		bodyPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Body"));
		bodyPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Color"));
		Workstation workStation1 = new Workstation("W1", new WorkstationType("Body Post", bodyPost));
		
		LinkedList<OptionType> driveTrainPost = new LinkedList<OptionType>();
		driveTrainPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Engine"));
		driveTrainPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Gearbox"));
		Workstation workStation2 = new Workstation("W2", new WorkstationType("DriveTrain Post", bodyPost));
		
		LinkedList<OptionType> accessoriesPost = new LinkedList<OptionType>();
		accessoriesPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Seats"));
		accessoriesPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Airco"));
		accessoriesPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Wheels"));
		accessoriesPost.add(VehicleModelCatalog.optionTypeCreator.getOptionType("Spoiler"));
		Workstation workStation3 = new Workstation("W3", new WorkstationType("Accessories Post", bodyPost));
		
		w1 = workStation1;
		w2 = workStation2;
		w3 = workStation3;
	}
	
	@Test
	public void testMatching(){
		for(int i = 0; i<w1.compatibleWith(process).size(); i++){
			assertTrue(taskTypes1.contains(w1.compatibleWith(process).get(i).getType()));
		}
		
		for(int i = 0; i<w2.compatibleWith(process).size(); i++){
			assertTrue(taskTypes2.contains(w2.compatibleWith(process).get(i).getType()));
		}
		
		for(int i = 0; i<w3.compatibleWith(process).size(); i++){
			assertTrue(taskTypes3.contains(w3.compatibleWith(process).get(i).getType()));
		}
	}
	
	@Test
	public void testTimeWorked(){
		assertEquals(0, process.getTotalTimeSpend());
		process.addTimeWorked(10);
		assertEquals(10, process.getTotalTimeSpend());
		process.addTimeWorked(1000);
		assertEquals(1010, process.getTotalTimeSpend());
		process.addTimeWorked(10);
		assertEquals(1020, process.getTotalTimeSpend());
	}
	
	private VehicleOrder createCar() throws InvalidConfigurationException, IOException, VehicleModelCatalogException{
		
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
				config.addOption(option);
		}
		config.complete();
		GarageHolder garageHolder = new GarageHolder(1);
		
		GregorianCalendar now = new GregorianCalendar();
		VehicleOrder carOrder = new VehicleOrder(1, garageHolder, config, now);
		return carOrder;
	}
	
	
}
