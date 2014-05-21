package domain.assembly.workstations;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Taskables.Option;
import domain.configuration.Taskables.OptionType;
import domain.configuration.Taskables.TaskType;
import domain.configuration.Taskables.TaskTypeCreator;
import domain.configuration.models.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.scheduling.order.VehicleOrder;
import domain.user.GarageHolder;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationType;
import domain.assembly.workstations.WorkstationTypeCreator;

public class VehicleAssemblyProcessTest {

	Workstation w1;
	Workstation w2;
	Workstation w3;
	
	ArrayList<OptionType> taskTypes1;
	ArrayList<OptionType> taskTypes2;
	ArrayList<OptionType> taskTypes3;
	
	VehicleAssemblyProcess process;

	
	@Before
	public void testCreate() throws IOException, VehicleCatalogException, InvalidConfigurationException{
		
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN
		
		
		process = createCar().getAssemblyprocess();

		LinkedList<TaskType> bodyPost = new LinkedList<TaskType>();
		bodyPost.add(new TaskTypeCreator().Body);
		bodyPost.add(new TaskTypeCreator().Color);
		Workstation workStation1 = new Workstation("W1", new WorkstationType("Body Post", bodyPost));
		
		LinkedList<TaskType> driveTrainPost = new LinkedList<TaskType>();
		driveTrainPost.add(new TaskTypeCreator().Engine);
		driveTrainPost.add(new TaskTypeCreator().Gearbox);
		Workstation workStation2 = new Workstation("W2", new WorkstationType("DriveTrain Post", driveTrainPost));
		
		LinkedList<TaskType> accessoriesPost = new LinkedList<TaskType>();
		accessoriesPost.add(new TaskTypeCreator().Seats);
		accessoriesPost.add(new TaskTypeCreator().Airco);
		accessoriesPost.add(new TaskTypeCreator().Wheels);
		accessoriesPost.add(new TaskTypeCreator().Spoiler);
		Workstation workStation3 = new Workstation("W3", new WorkstationType("Accessories Post", accessoriesPost));
		
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
	
	private VehicleOrder createCar() throws InvalidConfigurationException, IOException, VehicleCatalogException{
		
		Policy pol1 = new CompletionPolicy(null,VehicleCatalog.taskTypeCreator.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;
		
		
		VehicleCatalog catalog = new VehicleCatalog(new WorkstationTypeCreator());
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
