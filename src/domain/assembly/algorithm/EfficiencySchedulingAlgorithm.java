package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class EfficiencySchedulingAlgorithm implements SchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;

	public EfficiencySchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		GregorianCalendar allTasksCompletedTime = new GregorianCalendar();
		return convert(this.scheduleToScheduledOrderList(orderList, allTasksCompletedTime , assemblyLineSchedule));

	}

	private ArrayList<Order> convert(ArrayList<ScheduledOrder> arrayList) {
		ArrayList<Order> result = new ArrayList<Order>();
		for(ScheduledOrder i : arrayList)result.add(i.getScheduledOrder());
		return result;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeSchedule(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		GregorianCalendar time = (GregorianCalendar) assemblyLineSchedule.getCurrentTime().clone();
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<ScheduledOrder> result = completeDay(sTOrderList1, sTOrderList3, orderList2,time, assemblyLineSchedule);
		
		while(!sTOrderList1.isEmpty() || !sTOrderList3.isEmpty() || !orderList2.isEmpty() ){
			addDay(result,sTOrderList1,sTOrderList3,orderList2,time,assemblyLineSchedule);
			time = nextDay(time);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeDay(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<Order> result = new ArrayList<Order>();
		if(this.startOfDay(assemblyLineSchedule)){
			return this.transformToScheduledOrder(result, time, assemblyLineSchedule);
		}else{
			if(secondAdvance(assemblyLineSchedule)){
				ArrayList<Order> temp = new ArrayList<Order>();
				ArrayList<Order> temp2 = new ArrayList<Order>();
				
				
				if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.get(0));
				GregorianCalendar temporayTime = (GregorianCalendar) time.clone();
				temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineSchedule));
				if(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
						&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )) return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
				temp = (ArrayList<Order>) temp2.clone();
				if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
				
				
				if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
				temporayTime = (GregorianCalendar) time.clone();
				temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineSchedule));
				if(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
						&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )) return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
				temp = (ArrayList<Order>) temp2.clone();
				if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
				
				
				if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
				temporayTime = (GregorianCalendar) time.clone();
				temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineSchedule));
				if(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
						&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )) return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
				if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
				if(!orderList2.isEmpty())temp2.add(temp2.size() -2, orderList2.get(0));
				else{
					if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.get(0));
					temp2.add(null);
					if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
				}
				while(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
						&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )){
					temp = (ArrayList<Order>) temp2.clone();
					if(temp2.get(temp2.size()-1) != null && temp2.get(temp2.size()-1) instanceof CarOrder){
						orderList2.remove(0);
					}
					else{
						if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
						temp2.add(null);
						if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
					}
					if(!orderList2.isEmpty())temp2.add(temp2.size() -2, orderList2.remove(0));
					else{
						if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.remove(0));
						temp2.add(null);
						if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.remove(0));
					}
				}
				return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
			}else{
				if(canDoSingleTaskOrders(assemblyLineSchedule) == 2){
					if(!sTOrderList1.isEmpty())result.add(sTOrderList1.remove(0));
					if(!sTOrderList1.isEmpty())result.add(sTOrderList1.remove(0));
					time = this.nextDay(time);
					return this.transformToScheduledOrder(result, time, assemblyLineSchedule);
				} else{
					if(canDoSingleTaskOrders(assemblyLineSchedule) == 1){
						if(!sTOrderList1.isEmpty())result.add(sTOrderList1.remove(0));
						time = this.nextDay(time);
						return this.transformToScheduledOrder(result, time, assemblyLineSchedule);
					}else{
						if(canDoSingleTaskOrders(assemblyLineSchedule) == 0){
							time = this.nextDay(time);
							return this.transformToScheduledOrder(result, time, assemblyLineSchedule);
						}
						else{
							ArrayList<Order> temp = new ArrayList<Order>();
							ArrayList<Order> temp2 = new ArrayList<Order>();
							if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
							GregorianCalendar temporayTime = (GregorianCalendar) time.clone();
							temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineSchedule));
							if(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
									&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )) return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
							temp = (ArrayList<Order>) temp2.clone();
							if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
							
							
							if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
							temporayTime = (GregorianCalendar) time.clone();
							temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineSchedule));
							if(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
									&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )) return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
							if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
							if(!orderList2.isEmpty())temp2.add(temp2.size() -2, orderList2.get(0));
							else{
								if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.get(0));
								temp2.add(null);
								if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
							}
							while(temporayTime.get(GregorianCalendar.HOUR)> AssemblyLineScheduler.END_OF_DAY || (GregorianCalendar.MINUTE> 0  
									&& GregorianCalendar.HOUR == AssemblyLineScheduler.END_OF_DAY  )){
								temp = (ArrayList<Order>) temp2.clone();
								if(temp2.get(temp2.size()-1) != null && temp2.get(temp2.size()-1) instanceof CarOrder){
									orderList2.remove(0);
								}
								else{
									if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
									temp2.add(null);
									if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
								}
								if(!orderList2.isEmpty())temp2.add(temp2.size() -2, orderList2.remove(0));
								else{
									if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.remove(0));
									temp2.add(null);
									if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.remove(0));
								}
							}
							return this.transformToScheduledOrder(temp, time, assemblyLineSchedule);
						}
					}
				}
			}
		}
		
	}

	private int timetofinishwithfilled(ArrayList<Order> temp,
			AssemblyLineScheduler assemblyLineSchedule) {
		LinkedList<Order> simulator = assemblyLineSchedule.getAssemblyLine().getAllOrders();
		int time = 0;
		for(Order i : temp){
			simulator.addFirst(i);
			simulator.removeLast();
			time += assemblyLineSchedule.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		simulator.addFirst(null);
		simulator.removeLast();
		time += assemblyLineSchedule.getAssemblyLine().calculateTimeTillEmptyFor(simulator);
		return time;
	}

	@SuppressWarnings("unchecked")
	private void addDay(ArrayList<ScheduledOrder> result,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, GregorianCalendar time, AssemblyLineScheduler assemblyLineSchedule) {
		if(orderList2.isEmpty()) {
			this.finish(result, sTOrderList1, sTOrderList3, time, assemblyLineSchedule);
			return;
		}
		ArrayList<Order> temp = new ArrayList<Order>();
		if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
		if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
		if(!sTOrderList1.isEmpty())temp.add(sTOrderList1.remove(0));
		if(!sTOrderList1.isEmpty())temp.add(sTOrderList1.remove(0));
		ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
		temp2.add(temp2.size()-2, orderList2.get(0));
		while(calculatefulltimeAtstart(temp2,assemblyLineSchedule )< (AssemblyLineScheduler.END_OF_DAY-AssemblyLineScheduler.BEGIN_OF_DAY)*60){
			orderList2.remove(0);
			if(orderList2.isEmpty()){
				completeAddDaywith1and3(temp2,sTOrderList1,sTOrderList3,time, assemblyLineSchedule);
				temp = temp2;
				break;
			}
			temp = (ArrayList<Order>) temp2.clone();
			temp2.add(temp2.size()-2, orderList2.get(0));
		}
		result.addAll(transformToScheduledOrder(temp,time,assemblyLineSchedule));
	}

	@SuppressWarnings("unchecked")
	private void completeAddDaywith1and3(ArrayList<Order> temp2,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, GregorianCalendar time, AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<Order> temp = (ArrayList<Order>) temp2.clone();
		if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.get(0));
		temp2.add(null);
		if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
		while((!sTOrderList1.isEmpty() || !sTOrderList1.isEmpty())&& calculatefulltimeAtstart(temp2,assemblyLineSchedule )< (AssemblyLineScheduler.END_OF_DAY-AssemblyLineScheduler.BEGIN_OF_DAY)*60){
			if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
			temp2.add(null);
			if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
			temp= (ArrayList<Order>) temp2.clone();
			if(!sTOrderList3.isEmpty())temp2.add(sTOrderList3.get(0));
			temp2.add(null);
			if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
			
		}
		temp2 =temp;
	}

	private ArrayList<ScheduledOrder> transformToScheduledOrder(
			ArrayList<Order> temp, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineSchedule) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		ArrayList<ScheduledOrder> result = new ArrayList<ScheduledOrder>();
		int j =0;
		int timespent = 0;
		for(Order i : temp){
			simulator.addFirst(i);

			Order last = simulator.removeLast();

			timespent += assemblyLineSchedule.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			if(j>1){
				GregorianCalendar clone = (GregorianCalendar) time.clone();
				clone.add(GregorianCalendar.MINUTE, timespent);
				result.add(new ScheduledOrder(clone, last));
			}
			j++;
		}
		while(simulator.getLast() != null){
			simulator.addFirst(null);

			Order last = simulator.removeLast();

			timespent += assemblyLineSchedule.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			result.add(new ScheduledOrder(clone, last));
		}

		return result;
	}

	private int calculatefulltimeAtstart(ArrayList<Order> temp, AssemblyLineScheduler assemblyLineSchedule) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		int time = 0;
		for(Order i : temp){
			simulator.addFirst(i);
			simulator.removeLast();
			time += assemblyLineSchedule.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		simulator.addFirst(null);
		simulator.removeLast();
		time += assemblyLineSchedule.getAssemblyLine().calculateTimeTillEmptyFor(simulator);
		return time;
	}

	private void finish(ArrayList<ScheduledOrder> result,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, GregorianCalendar time, AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<Order> temp = new ArrayList<Order>();
		for(int i = 0; i< Math.max(sTOrderList1.size(), sTOrderList3.size());i++){
			if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
			if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
			if(!sTOrderList1.isEmpty())temp.add(sTOrderList1.remove(0));
			if(!sTOrderList1.isEmpty())temp.add(sTOrderList1.remove(0));
		}
		result.addAll(transformToScheduledOrder(temp, time, assemblyLineSchedule));
	}

	private GregorianCalendar nextDay(GregorianCalendar calendar) {
		//TODO alle mogelijke uitzonderlijke situaties controleren?? 
		// bv calendar = 1-1-1000 01h00 => nextDay == 1-1-1000 06h00 of nextDay == 2-1-1000 06h00
		GregorianCalendar nextDay = new GregorianCalendar(
				calendar.get(GregorianCalendar.YEAR),
				calendar.get(GregorianCalendar.MONTH),
				calendar.get(GregorianCalendar.DAY_OF_MONTH),
				AssemblyLineScheduler.BEGIN_OF_DAY,
				0,
				0);

		nextDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
		return nextDay;
	}





	

	




	private boolean secondAdvance(AssemblyLineScheduler assemblyLineSchedule) {
		SingleTaskOrder order;
		try{
			order = (SingleTaskOrder) assemblyLineSchedule.getAssemblyLine().getAllOrders().get(0);
		}catch(ClassCastException e){
			return false;
		}
		if(order == null) return false;
		if(getType(order) != OptionType.Seats ) return false;
		for(int i = 1; i <assemblyLineSchedule.getAssemblyLine().getAllOrders().size(); i++){
			if(assemblyLineSchedule.getAssemblyLine().getAllOrders().get(i) != null) return false;
		}
		return true;
	}

	private boolean startOfDay(AssemblyLineScheduler assemblyLineSchedule) {
		if(assemblyLineSchedule.getCurrentTime().get(GregorianCalendar.HOUR_OF_DAY) != AssemblyLineScheduler.BEGIN_OF_DAY 
				|| assemblyLineSchedule.getCurrentTime().get(GregorianCalendar.MINUTE )!=0 ) return false;
		for(Order i:assemblyLineSchedule.getAssemblyLine().getAllOrders()){
			if(i != null) return false;
		}	
		return true;
	}

	private int canDoSingleTaskOrders(AssemblyLineScheduler assemblyLineSchedule) {
		int result = 1;
		GregorianCalendar time = (GregorianCalendar) assemblyLineSchedule.getCurrentTime().clone();
		while(time.get(GregorianCalendar.HOUR_OF_DAY) < AssemblyLineScheduler.END_OF_DAY){
			if(result ==1){
				time.add(GregorianCalendar.MINUTE, Math.max(0,  Math.max(getEstimateTime(assemblyLineSchedule.getAssemblyLine().getAllOrders().get(0)), getEstimateTime(assemblyLineSchedule.getAssemblyLine().getAllOrders().get(1)))));
			}
			if(result ==2){
				time.add(GregorianCalendar.MINUTE, Math.max(0,getEstimateTime(assemblyLineSchedule.getAssemblyLine().getAllOrders().get(0))  ));
			}
			if(result >2){
				time.add(GregorianCalendar.MINUTE, 60);
			}
		}
		return result-1;
	}

	private int getEstimateTime(Order order) {
		if(order == null) return 0;
		return order.getConfiguration().getModel().getExpectedTaskTime();
	}

	private ArrayList<SingleTaskOrder> combSingleTaskOrdersW1(ArrayList<SingleTaskOrder> list) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list){
			if(getType(i) == OptionType.Color)result.add(i);
		}
		return result;
	}
	private ArrayList<SingleTaskOrder> combSingleTaskOrdersW3(ArrayList<SingleTaskOrder> list) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list){
			if(getType(i) == OptionType.Seats)result.add(i);
		}
		return result;
	}
	private void deadlineSort(ArrayList<SingleTaskOrder> sTOrderList) {
		Comparator<SingleTaskOrder> comparator = new Comparator<SingleTaskOrder>(){
			@Override
			public int compare(SingleTaskOrder order1, SingleTaskOrder order2){
				return order1.getDeadLine().compareTo(order2.getDeadLine());
			}
		};
		Collections.sort(sTOrderList, comparator);
	}


	private ArrayList<SingleTaskOrder> combSingleTaskOrders(
			ArrayList<Order> orderList) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(int i = 0; i< orderList.size(); i++){
			if(orderList.get(i) instanceof SingleTaskOrder){
				result.add((SingleTaskOrder) orderList.remove(i));
				i--;
			}
		}
		return result;
	}
	private OptionType getType(SingleTaskOrder order) {
		if(order.isCompleted()) return order.getConfiguration().getAllOptions().get(0).getType();
		return null;
	}



	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			GregorianCalendar allTasksCompletedTime,
			AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> STOrderList = combSingleTaskOrders(orderList2);
		orderList2 = innerAlgorithm.scheduleToList(orderList2, assemblyLineSchedule);
		ArrayList<SingleTaskOrder> STOrderListWorkStation1 = combSingleTaskOrdersW1((ArrayList<SingleTaskOrder>) STOrderList.clone());
		deadlineSort(STOrderListWorkStation1);
		ArrayList<SingleTaskOrder> STOrderListWorkStation3 = combSingleTaskOrdersW3((ArrayList<SingleTaskOrder>) STOrderList.clone());
		deadlineSort(STOrderListWorkStation3); 
		ArrayList<ScheduledOrder> temp = completeSchedule(STOrderListWorkStation1,STOrderListWorkStation3, orderList2, assemblyLineSchedule);
		return temp;
	}


}
