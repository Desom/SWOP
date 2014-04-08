package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineSchedule;
import domain.order.CarOrder;

public interface SchedulingAlgorithm {
	
	public ArrayList<CarOrder> schedule(AssemblyLineSchedule assemblyLineSchedule);
}
