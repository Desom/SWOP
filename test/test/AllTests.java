package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import domain.StatisticsTest;
import domain.assembly.AssemblyLineTest;
import domain.assembly.AssemblyTaskTest;
import domain.assembly.CarAssemblyProcessTest;
import domain.assembly.WorkstationTest;
import domain.assembly.algorithm.EffinciencySchedulingAlgorithmTest;
import domain.assembly.algorithm.FIFOSchedulingAlgorithmTest;
import domain.assembly.algorithm.SpecificationBatchSchedulingAgorithmTest;
import domain.configuration.CarModelTest;
import domain.configuration.CarModelCatalogTest;
import domain.configuration.OptionTest;
import domain.configuration.PolicyTest;
import domain.order.CarOrderTest;
import domain.order.CarTest;
import domain.order.OrderManagerTest;
import domain.user.UserTest;

@RunWith(Suite.class)
@SuiteClasses({SpecificationBatchSchedulingAgorithmTest.class, EffinciencySchedulingAlgorithmTest.class, FIFOSchedulingAlgorithmTest.class, AssemblyLineTest.class, AssemblyTaskTest.class,
	CarAssemblyProcessTest.class, CarOrderTest.class, CarTest.class,
	OrderManagerTest.class,	CarModelTest.class, CarModelCatalogTest.class, OptionTest.class,
	UserTest.class, WorkstationTest.class, StatisticsTest.class, PolicyTest.class })
public class AllTests {

}
