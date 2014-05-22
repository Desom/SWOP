package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.workstations.Workstation;
import domain.configuration.models.VehicleModel;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;

public class dummyAssemblyLine extends AssemblyLine {
	LinkedList<Order> orders;
	GregorianCalendar time;
	public dummyAssemblyLine(AssemblyLineScheduler assemblyLineScheduler,
			int num,AssemblyLineStatus possibleStatuses, ArrayList<VehicleModel> arrayList, GregorianCalendar time) {
		super(assemblyLineScheduler, possibleStatuses, arrayList);
		orders= new LinkedList<Order>();
		this.time = time;
		for(int i=0; i<num;i++){
			orders.add(null);
		}
	}

	public void add(Order order, int time){
		orders.removeLast();
		orders.addFirst(order);
		this.time.add(GregorianCalendar.MINUTE, time);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<Order> getAllOrders() {
		return (LinkedList<Order>) orders.clone();
	}
	@Override
	public void advanceLine() throws CannotAdvanceException {
		
	}
	@Override
	public GregorianCalendar timeWhenAcceptingOrders() {
		
		return time;
	}
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<Order> stateWhenAcceptingOrders() {
		LinkedList<Order> temp = (LinkedList<Order>) this.orders.clone();
		return temp;
	}
	public void addWorkstations(List<Workstation> list){
		this.addAllWorkstation(list);
	}
}
