package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.assembly.AssemblyLineTest;
import test.assembly.AssemblyTaskTest;
import test.assembly.CarAssemblyProcessTest;
import test.assembly.ProductionScheduleTest;
import test.assembly.WorkstationTest;
import test.configuration.Test_CarModel;
import test.configuration.Test_CarModelCatalog;
import test.configuration.Test_Options;
import test.order.CarOrderTest;
import test.order.CarTest;
import test.order.OrderManagerTest;

@RunWith(Suite.class)
@SuiteClasses({ AssemblyLineTest.class, AssemblyTaskTest.class,
	CarAssemblyProcessTest.class, CarOrderTest.class, CarTest.class,
	OrderManagerTest.class, ProductionScheduleTest.class,
	Test_CarModel.class, Test_CarModelCatalog.class, Test_Options.class,
	UserTest.class, WorkstationTest.class })
public class AllTests {

}
