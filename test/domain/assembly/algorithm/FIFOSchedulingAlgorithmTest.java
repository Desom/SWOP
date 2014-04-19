package domain.assembly.algorithm;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.assembly.AssemblyLineScheduler;

public class FIFOSchedulingAlgorithmTest {

	FIFOSchedulingAlgorithm algorithm;
	
	@Before
	public void testCreate() {
		this.algorithm = new FIFOSchedulingAlgorithm();
	}
	
	@Test
	public void testNextDay() {
		GregorianCalendar time1 = new GregorianCalendar(2000, 5, 7, 22, 0, 0);
		GregorianCalendar time2 = new GregorianCalendar(2000, 5, 7, 1, 0, 0);
		GregorianCalendar time3 = new GregorianCalendar(2000, 5, 30, 23, 0, 0);
		
		GregorianCalendar solution1 = this.algorithm.nextDay(time1);
		assertEquals(2000, solution1.get(GregorianCalendar.YEAR));
		assertEquals(5, solution1.get(GregorianCalendar.MONTH));
		assertEquals(8, solution1.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution1.get(GregorianCalendar.HOUR_OF_DAY));
		
		GregorianCalendar solution2 = this.algorithm.nextDay(time2);
		assertEquals(2000, solution2.get(GregorianCalendar.YEAR));
		assertEquals(5, solution2.get(GregorianCalendar.MONTH));
		assertEquals(7, solution2.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution2.get(GregorianCalendar.HOUR_OF_DAY));
		
		GregorianCalendar solution3 = this.algorithm.nextDay(time3);
		assertEquals(2000, solution3.get(GregorianCalendar.YEAR));
		assertEquals(6, solution3.get(GregorianCalendar.MONTH));
		assertEquals(1, solution3.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(AssemblyLineScheduler.BEGIN_OF_DAY, solution3.get(GregorianCalendar.HOUR_OF_DAY));
	}

}
