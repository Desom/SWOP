package domain.assembly.workstations;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import domain.assembly.workstations.AssemblyTask;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationType;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.configuration.Configuration;
import domain.configuration.taskables.Option;
import domain.configuration.taskables.TaskType;
import domain.configuration.taskables.TaskTypeCreator;
import domain.configuration.models.VehicleModel;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.scheduling.order.VehicleOrder;
import domain.user.GarageHolder;

public class AssemblyTaskTest {
	
	private static AssemblyTask task;

	@BeforeClass
	public static void testCreate() throws IOException, VehicleCatalogException, InvalidConfigurationException {
		VehicleOrder o = buildCar();
		LinkedList<TaskType> bodyPost = new LinkedList<TaskType>();
		bodyPost.add(VehicleCatalog.taskTypeCreator.Body);
		bodyPost.add(VehicleCatalog.taskTypeCreator.Color);
		Workstation w = new Workstation("name", new WorkstationType("Body Post", bodyPost));
		task = w.compatibleWith(o.getAssemblyprocess()).get(0);;
	}
	
	@Test
	public void testCompleteTask() {
		task.completeTask(60);
		assert(task.isCompleted());
		assertEquals("dummy action", task.getActions().get(0));
	}
	
	public static VehicleOrder buildCar() throws IOException, VehicleCatalogException, InvalidConfigurationException{
		// MAAK EEN AUTO MET OPTIONS EN MODEL AAN

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
