package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.FactorySchedulingAlgorithm;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class FactoryScheduler implements Scheduler,OrderHandler {

	private ArrayList<AssemblyLineScheduler> schedulerList;
	private FactorySchedulingAlgorithm currentAlgorithm;
	
	@Override
	public GregorianCalendar getCurrentTime() {
		//TODO goede currentTime of moet die anders?
		GregorianCalendar time = null;
		for(AssemblyLineScheduler als: schedulerList){
			if(time == null || als.getCurrentTime().before(time)){
				time = als.getCurrentTime();
			}
		}
		
		return time;
	}

	@Override
	public GregorianCalendar completionEstimate(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrderHandler(OrderHandler orderHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSchedule() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canFinishOrderBeforeDeadline(
			SingleTaskOrder orderWithDeadline) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canScheduleOrder(Order order) {
		for(AssemblyLineScheduler als : this.schedulerList){
			if(als.canScheduleOrder(order)){
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<Order> getOrdersFor(Scheduler scheduler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasScheduler(Scheduler scheduler) {
		return this.schedulerList.contains(scheduler);
	}

}
