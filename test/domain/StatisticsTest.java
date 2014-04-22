package domain;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.CannotAdvanceException;
import domain.assembly.DoesNotExistException;
import domain.assembly.Scheduler;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.order.OrderManager;
import domain.policies.InvalidConfigurationException;
import domain.user.CarMechanic;

public class StatisticsTest {

	AssemblyLine line = null;
	@Before
	public void testCreate() throws DoesNotExistException, IOException, CarModelCatalogException, CannotAdvanceException, IllegalStateException, InternalFailureException, InvalidConfigurationException {
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
		Statistics statistics = new Statistics(orderManager);
		line = new AssemblyLine(scheduler, statistics);
	}
	
	
	
}
