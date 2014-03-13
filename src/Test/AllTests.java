package Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AssemblyLineTest.class, AssemblyTaskTest.class,
		CarAssemblyProccessTest.class, CarOrderTest.class,
		OrderManagerTest.class, ProductionScheduleTest.class,
		Test_CarModel.class, Test_CarModelCatalog.class, Test_Options.class,
		UserTest.class, WorkstationTest.class })
public class AllTests {

}
