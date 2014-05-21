package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public interface SchedulerCreatorInterface {

	public abstract AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime);

	public abstract FactoryScheduler createFactoryScheduler(ArrayList<AssemblyLineScheduler> alsList);

}