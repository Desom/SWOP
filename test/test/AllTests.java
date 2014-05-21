package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import domain.StatisticsTest;
import domain.assembly.AssemblyLineSchedulerTest;
import domain.assembly.algorithm.EfficiencySchedulingAlgorithmTest;
import domain.assembly.algorithm.FIFOSchedulingAlgorithmTest;
import domain.assembly.algorithm.SpecificationBatchSchedulingAgorithmTest;
import domain.assembly.assemblyline.AssemblyLineTest;
import domain.assembly.workstations.AssemblyTaskTest;
import domain.assembly.workstations.VehicleAssemblyProcessTest;
import domain.assembly.workstations.WorkstationTest;
import domain.configuration.VehicleCatalogTest;
import domain.configuration.models.VehicleModelTest;
import domain.configuration.taskables.OptionTest;
import domain.policies.PolicyTest;
import domain.scheduling.CarTest;
import domain.scheduling.OrderManagerTest;
import domain.scheduling.VehicleOrderTest;
import domain.user.UserTest;

@RunWith(Suite.class)
@SuiteClasses({SpecificationBatchSchedulingAgorithmTest.class, EfficiencySchedulingAlgorithmTest.class, FIFOSchedulingAlgorithmTest.class, 
	AssemblyLineSchedulerTest.class, AssemblyLineTest.class, AssemblyTaskTest.class,	
	VehicleAssemblyProcessTest.class, VehicleOrderTest.class, CarTest.class,
	OrderManagerTest.class,	VehicleModelTest.class, VehicleCatalogTest.class, OptionTest.class,
	UserTest.class, WorkstationTest.class, StatisticsTest.class, PolicyTest.class })
public class AllTests {

}
