package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

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
	@SuppressWarnings("unchecked")
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<Order> schedule = new ArrayList<Order>();
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		orderList2 = innerAlgorithm.scheduleToList(orderList2, assemblyLineSchedule);
		ArrayList<SingleTaskOrder> STOrderList = combSingleTaskOrders(orderList2);
		STOrderList= SingleTaskSchedule(STOrderList,assemblyLineSchedule);
		SingleTaskOrder EndangeredOrder = removeEndangeredOrder(STOrderList, assemblyLineSchedule.getCurrentTime());
		ArrayList<SingleTaskOrder> EndangeredOrders = new ArrayList<SingleTaskOrder>();
		while(EndangeredOrder != null){
			EndangeredOrders.add(EndangeredOrder);
			STOrderList= SingleTaskSchedule(STOrderList, assemblyLineSchedule);
			EndangeredOrder = removeEndangeredOrder(STOrderList, assemblyLineSchedule.getCurrentTime());
		}
		return completeSchedule(STOrderList,append2(EndangeredOrders,orderList2), assemblyLineSchedule);
	}

	private ArrayList<Order> append2(ArrayList<SingleTaskOrder> list1,
			ArrayList<Order> list2) {
		ArrayList<Order> result= new ArrayList<Order>();
		for(Order i:list1){
			result.add(i);
		}
		for(Order i:list2){
			result.add(i);
		}
		return result;
	}

	private ArrayList<SingleTaskOrder> SingleTaskSchedule(ArrayList<SingleTaskOrder> sTOrderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		ArrayList<SingleTaskOrder> STOrderListWorkStation1 = combSingleTaskOrdersW1(sTOrderList.clone());
		deadlineSort(STOrderListWorkStation1);
		ArrayList<SingleTaskOrder> STOrderListWorkStation3 = combSingleTaskOrdersW3(sTOrderList.clone());
		deadlineSort(STOrderListWorkStation3);
		if(assemblyLineSchedule.getNumberOfOrdersOfToday() == 0){
			return startOfDaySchedule(STOrderListWorkStation1,STOrderListWorkStation3);
		}else{
			if(assemblyLineSchedule.getNumberOfOrdersOfToday() == 1){
				ArrayList<SingleTaskOrder> incompletedag = new ArrayList<SingleTaskOrder>();
				incompletedag.add(removeOrNull(STOrderListWorkStation3));
				incompletedag.add(removeOrNull(STOrderListWorkStation1));
				incompletedag.add(removeOrNull(STOrderListWorkStation1));
				return append(incompletedag ,startOfDaySchedule(STOrderListWorkStation1,STOrderListWorkStation3));
			}else{
				if(assemblyLineSchedule.canDoSingleTaskOrders() > 2){
					ArrayList<SingleTaskOrder> incompletedag = new ArrayList<SingleTaskOrder>();
					incompletedag.add(removeOrNull(STOrderListWorkStation1));
					incompletedag.add(removeOrNull(STOrderListWorkStation1));
					return append(incompletedag ,startOfDaySchedule(STOrderListWorkStation1,STOrderListWorkStation3));
				} else{
					if(assemblyLineSchedule.canDoSingleTaskOrders() == 1){
						ArrayList<SingleTaskOrder> incompletedag = new ArrayList<SingleTaskOrder>();
						incompletedag.add(removeOrNull(STOrderListWorkStation1));
						return append(incompletedag ,startOfDaySchedule(STOrderListWorkStation1,STOrderListWorkStation3));
					}else{
						if(assemblyLineSchedule.canDoSingleTaskOrders() == 0){
							return startOfDaySchedule(STOrderListWorkStation1,STOrderListWorkStation3);
						}
					}
				}
			}
		}

	}

	private ArrayList<SingleTaskOrder> combSingleTaskOrdersW1(ArrayList<SingleTaskOrder> list) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list){
			if(i.getType() == OptionType.Color)result.add(i);
		}
		return result;
	}
	private ArrayList<SingleTaskOrder> combSingleTaskOrdersW3(ArrayList<SingleTaskOrder> list) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list){
			if(i.getType() == OptionType.Seats)result.add(i);
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

	private ArrayList<SingleTaskOrder> append(
			ArrayList<SingleTaskOrder> list1,
			ArrayList<SingleTaskOrder> list2) {
		ArrayList<SingleTaskOrder> result= new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list1){
			result.add(i);
		}
		for(SingleTaskOrder i:list2){
			result.add(i);
		}
		return result;
	}

	private ArrayList<SingleTaskOrder> startOfDaySchedule(
			ArrayList<SingleTaskOrder> sTOrderListWorkStation1,
			ArrayList<SingleTaskOrder> sTOrderListWorkStation3) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		while(sTOrderListWorkStation1.size() >0 || sTOrderListWorkStation3.size() >0){
			result.add(removeOrNull(sTOrderListWorkStation3));
			result.add(removeOrNull(sTOrderListWorkStation3));
			result.add(removeOrNull(sTOrderListWorkStation1));
			result.add(removeOrNull(sTOrderListWorkStation1));
		}
		return result;
	}

	private SingleTaskOrder removeOrNull(
			ArrayList<SingleTaskOrder> list) {
		if(list.size()>0) return(list.remove(0));
		return null;
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



	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			GregorianCalendar allTasksCompletedTime,
			AssemblyLineScheduler assemblyLineSchedule) {
		return null;
	}



}
