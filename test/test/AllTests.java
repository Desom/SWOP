package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import domain.StatisticsTest;
import domain.assembly.AssemblyLineSchedulerTest;
import domain.assembly.AssemblyLineTest;
import domain.assembly.AssemblyTaskTest;
import domain.assembly.VehicleAssemblyProcessTest;
import domain.assembly.WorkstationTest;
import domain.assembly.algorithm.EfficiencySchedulingAlgorithmTest;
import domain.assembly.algorithm.FIFOSchedulingAlgorithmTest;
import domain.assembly.algorithm.SpecificationBatchSchedulingAgorithmTest;
import domain.configuration.VehicleModelTest;
import domain.configuration.VehicleModelCatalogTest;
import domain.configuration.OptionTest;
import domain.configuration.PolicyTest;
import domain.order.VehicleOrderTest;
import domain.order.CarTest;
import domain.order.OrderManagerTest;
import domain.user.UserTest;

@RunWith(Suite.class)
@SuiteClasses({SpecificationBatchSchedulingAgorithmTest.class, EfficiencySchedulingAlgorithmTest.class, FIFOSchedulingAlgorithmTest.class, 
	AssemblyLineSchedulerTest.class, AssemblyLineTest.class, AssemblyTaskTest.class,	
	VehicleAssemblyProcessTest.class, VehicleOrderTest.class, CarTest.class,
	OrderManagerTest.class,	VehicleModelTest.class, VehicleModelCatalogTest.class, OptionTest.class,
	UserTest.class, WorkstationTest.class, StatisticsTest.class, PolicyTest.class })
public class AllTests {

}
