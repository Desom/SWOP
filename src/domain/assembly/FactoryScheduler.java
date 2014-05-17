package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;

import domain.assembly.algorithm.FactorySchedulingAlgorithm;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class FactoryScheduler implements Scheduler,OrderHandler {

	private ArrayList<AssemblyLineScheduler> schedulerList;
	private FactorySchedulingAlgorithm currentAlgorithm;
	private boolean outDated;
	private ArrayList<ArrayList<Order>> ordersForSchedulers;
	private OrderHandler orderHandler;
	
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
		AssemblyLineScheduler scheduler = this.findScheduler(order);
		return scheduler.completionEstimate(order);
	}

	private AssemblyLineScheduler findScheduler(Order order) {
		ArrayList<ArrayList<Order>> ordersForSchedulers = this.getOrdersForSchedulers();
		for(int i = 0; i < ordersForSchedulers.size(); i++){
			if(ordersForSchedulers.get(i).contains(order)){
				return this.schedulerList.get(i);
			}
		}
		throw new IllegalArgumentException("The FactoryScheduler:" + this + " doesn't schedule the given Order:" + order);

	}

	private ArrayList<ArrayList<Order>> getOrdersForSchedulers() {
		//controleer of de orders die nog moeten worden gedaan, nog steeds moeten worden gedaan.
		//Mss vergelijken met AssemblyLineScheduler ipv OrderHandler?
		ArrayList<Order> orders = this.orderHandler.getOrdersFor(this);
		for(ArrayList<Order> orderList : this.ordersForSchedulers){
			if(!orders.containsAll(orderList)){
				this.updateSchedule();
				break;
			}
		}
		
		//als outDated schedule, maak nieuw.
		if(outDated){
			this.ordersForSchedulers = this.currentAlgorithm.allocateOrders(orders,this);
			outDated = false;
		}
		
		return this.ordersForSchedulers;
	}

	@Override
	public void setOrderHandler(OrderHandler orderHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSchedule() {
		this.outDated = true;
		for(AssemblyLineScheduler scheduler : this.schedulerList){
			scheduler.updateSchedule();
		}
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
