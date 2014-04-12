package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import domain.order.CarOrder;
import domain.order.OrderManager;

public class GeneralSchedule {

	private OrderManager orderManager;
	private ArrayList<AssemblyLineSchedule> assemblyLineSchedules;

	 //TODO docs
	public GeneralSchedule() {
		// TODO Auto-generated constructor stub
	}

	 //TODO docs
	public ArrayList<CarOrder> getOrdersFor(
			AssemblyLineSchedule assemblyLineSchedule) {
		// TODO Auto-generated method stub
		return this.orderManager.getAllUnfinishedOrders();
	}
	
	public GregorianCalendar completionEstimate(CarOrder carOrder){
		for(Entry<AssemblyLineSchedule, ArrayList<CarOrder>> entry : this.getAllOrdersPerAssemblyLineSchedule().entrySet()){
			if(entry.getValue().contains(carOrder)){
				return entry.getKey().completionEstimate(carOrder);
			}
		}
		throw new IllegalArgumentException("The given is unknown or already finished : "+ carOrder.toString());
	}
	
	/**
	 * Matches every AssemblyLineSchedule with a list of CarOrders that the respective AssemblyLineSchedule should schedule.
	 * The matching will returned in the form of a HashMap.
	 * 
	 * @return A HashMap containing a mapping between the AssemblyLineSchedules and the CarOrders that will be scheduled on them.
	 */
	private HashMap<AssemblyLineSchedule,ArrayList<CarOrder>> getAllOrdersPerAssemblyLineSchedule(){
		HashMap<AssemblyLineSchedule, ArrayList<CarOrder>> hashMap = new HashMap<AssemblyLineSchedule,ArrayList<CarOrder>>();
		//TODO verwijder de carOrders die al op de assemblyLine staan uit getAllUnfinishedOrders
		// of steek ze bij de juiste schedule
		hashMap.put(this.assemblyLineSchedules.get(0), this.orderManager.getAllUnfinishedOrders());
		return hashMap;
	}

}
