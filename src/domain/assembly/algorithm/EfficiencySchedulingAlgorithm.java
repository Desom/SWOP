package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.DoesNotExistException;
import domain.assembly.ScheduledOrder;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class EfficiencySchedulingAlgorithm extends AbstractAssemblyLineSchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;

	/**
	 * Constructor of EfficiencySchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this efficiency scheduling algorithm.
	 */
	public EfficiencySchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	/**
	 * Schedules the given orders and returns it,
	 * but with 2 SingleTaskOrders at the beginning of the day and 2 at the end of the day.
	 * 
	 *  @param orderList
	 *  	The orders to be sorted.
	 *  @param allTasksCompletedTime
	 *  	The time when all task on assemblyLine will be done.
	 *  @param assemblyLineScheduler
	 *  	The assymblylineScheduler to which the orders belong.
	 *  @return The sorted Orders.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			AssemblyLine assemblyLine) {
		//separates the the Orders in specific types and sorts them
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> STOrderList = combSingleTaskOrders(orderList2);
		orderList2 = innerAlgorithm.scheduleToList(orderList2, assemblyLine.getAssemblyLineScheduler());
		ArrayList<SingleTaskOrder> STOrderListWorkStation3 = combSingleTaskOrdersByType( STOrderList,VehicleModelCatalog.taskTypeCreator.Seats);
		ArrayList<SingleTaskOrder> STOrderListWorkStation1 = combSingleTaskOrdersByType( STOrderList,VehicleModelCatalog.taskTypeCreator.Color);
		deadlineSort(STOrderListWorkStation1);
		deadlineSort(STOrderListWorkStation3); 
		//combine the three lists into one schedule 
		ArrayList<ScheduledOrder> deadlines = new ArrayList<ScheduledOrder>();
		ArrayList<ScheduledOrder> temp = completeSchedule(STOrderListWorkStation1,STOrderListWorkStation3, orderList2,assemblyLine, deadlines);

		//handle deadlines which are endangered
		ArrayList<SingleTaskOrder> endangeredOrders = new ArrayList<SingleTaskOrder>();
		SingleTaskOrder endangeredOrder = retrieveFirstDeadlineFailure(deadlines,endangeredOrders);
		while(endangeredOrder != null){
			endangeredOrders.add(endangeredOrder);
			STOrderListWorkStation3.remove(endangeredOrder);
			STOrderListWorkStation1.remove(endangeredOrder);
			this.deadlineSort(endangeredOrders);
			deadlines = new ArrayList<ScheduledOrder>();
			temp = completeSchedule(STOrderListWorkStation1,STOrderListWorkStation3,append(endangeredOrders, orderList2),assemblyLine, deadlines);
			endangeredOrder = retrieveFirstDeadlineFailure(deadlines,endangeredOrders);
		}

		return temp;
	}

	/**
	 * Appends a List of SingleTaskOrders and a list of Orders into a new list of Orders.
	 * 
	 * @param singleTaskOrders
	 * 		The list of SingleTaskOrders to be appended
	 * @param orders
	 * 		The list of Orders to be appended
	 * @return a list which contains and singleTasdkOrders and orders of both list with the SingleTaskOrders first. 
	 * The order of elements in both lists are still respected
	 */
	private ArrayList<Order> append(
			ArrayList<SingleTaskOrder> singleTaskOrders,
			ArrayList<Order> orders) {
		ArrayList<Order> result = new ArrayList<Order>();
		for(SingleTaskOrder i :singleTaskOrders) result.add(i);
		for(Order i :orders) result.add(i);
		return result;
	}

	/**
	 * Finds in a list the first SingleTaskOrder whose deadline is to be broken, that is not already known.
	 * 
	 * @param timeOfBelt
	 * 		A list which the Orders and their time to be completed
	 * @param endangeredOrders
	 * 		A List which are already known to be completed late 		
	 * @return the first SingleTaskOrder whose deadline is scheduled to be completed after it's deadline that is not already known.
	 * 		null if none are found
	 */
	private SingleTaskOrder retrieveFirstDeadlineFailure(
			ArrayList<ScheduledOrder> timeOfBelt,
			ArrayList<SingleTaskOrder> endangeredOrders) {
		for(ScheduledOrder i:timeOfBelt){
			if(i.getScheduledOrder() != null && i.getScheduledOrder() instanceof SingleTaskOrder
					&& i.getScheduledTime().after(((SingleTaskOrder) i.getScheduledOrder()).getDeadLine())
					&& !endangeredOrders.contains(i.getScheduledOrder()) ) return ((SingleTaskOrder) i.getScheduledOrder());
		}
		return null;
	}

	/**
	 * Converts a list of ScheduledOrders into a list of Orders.
	 * 
	 * @param scheduledOrders
	 * 		The list of ScheduledOrder objects to be converted
	 * @return A list of Orders which are all in arrayList and in the same order
	 */
	private ArrayList<Order> convert(ArrayList<ScheduledOrder> scheduledOrders) {
		ArrayList<Order> orders = new ArrayList<Order>();
		for(ScheduledOrder i : scheduledOrders)orders.add(i.getScheduledOrder());
		return orders;
	}

	/**
	 * Combines the three lists into one array of ScheduledOrders, with (if possible) 
	 * 2 SingleTaskOrders at the beginning of the day and 2 at the end of the day.
	 * 
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day.
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day.
	 * @param orderList
	 * 		A List of Orders.
	 * @param allTasksCompletedTime
	 * 		The time when all tasks on assemblyLine will be done.
	 * @param stateOfAssemblyLine 
	 * @param assemblyLineScheduler 
	 * 		The assymblylineScheduler to which the orders belong.
	 * @param scheduledOrdersWithDeadline 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 * @return An array of ScheduledOrders with if possible 
	 * 		2 SingleTaskOrders at the beginning of the day and 2 at the end of the day
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeSchedule(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, ArrayList<Order> orderList,
			AssemblyLine assemblyLine, ArrayList<ScheduledOrder> scheduledOrdersWithDeadline) {
		// clone the information that will be changed
		GregorianCalendar time = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> sTOrderList1clone = (ArrayList<SingleTaskOrder>) sTOrderList1.clone();
		ArrayList<SingleTaskOrder> sTOrderList3clone = (ArrayList<SingleTaskOrder>) sTOrderList3.clone();
		// Complete the current day
		ArrayList<ScheduledOrder> result = completeDay(sTOrderList1clone, sTOrderList3clone, orderList2,assemblyLine,scheduledOrdersWithDeadline);
		time = nextDay(time);
		//complete the rest of the days
		while(!sTOrderList1clone.isEmpty() || !sTOrderList3clone.isEmpty() || !orderList2.isEmpty() ){
			addDay(result,sTOrderList1clone,sTOrderList3clone,orderList2,time,assemblyLine.getAssemblyLineScheduler(),scheduledOrdersWithDeadline);
			time = nextDay(time);
		}
		return result;
	}

	/**
	 * Creates an array of ScheduledOrders that supplements the status of the AssemblyLineScheduler so that the end of day proceeds correctly.
	 * 
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day.
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day.
	 * @param orderList2
	 * 		A List of Orders.
	 * @param time
	 * 		The time when all tasks on assemblyLine will be done.
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong.
	 * @param stateOfAssemblyLine 
	 * @param scheduledOrdersWithCompletionTime 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 * @return An array of ScheduledOrders so that the end of day proceeds correctly.
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeDay(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, AssemblyLine assemblyLine, ArrayList<ScheduledOrder> scheduledOrdersWithCompletionTime) {
		ArrayList<Order> temp = new ArrayList<Order>();

		for(int i=0;i<canPlaceAtbeginning(assemblyLine);i++){
			if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
			else{
				if(i!=0) temp.add(null);
				else{
					break;
				}
			}
		}
		int corectionfactor = 0;
		for(int i= 1;i<assemblyLine.getNumberOfWorkstations();i++){
			// looks if a SingleTaskOrder can be placed at the end of the day
			ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
			if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
			GregorianCalendar temporayTime = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
			temporayTime.add(GregorianCalendar.MINUTE, timeToFinishWithFilled(temp2,assemblyLine));
			if(assemblyLine.getAssemblyLineScheduler().getRealEndOfDay().before(temporayTime)){
				//finishes the day with three empty orders
				for(int j =0; j< assemblyLine.getNumberOfWorkstations();j++){
					temp.add(null);
				}
				timeOfBeltWithAssembly(temp, assemblyLine, scheduledOrdersWithCompletionTime);
				return this.transformToScheduledOrderWithAssembly(temp, assemblyLine);
			}
			// places a SingleTaskOrder  at the end of the day
			temp=temp2;
			if(!sTOrderList1.isEmpty()){
				sTOrderList1.remove(0);
				corectionfactor++;
			}
		}
		ArrayList<Order> temp2;
		GregorianCalendar temporayTime;

		//prepares a test dummy for an extra Order
		temp2 = (ArrayList<Order>) temp.clone();
		if(!orderList2.isEmpty())temp2.add(temp2.size()-corectionfactor, orderList2.get(0));
		else{
			if(!sTOrderList3.isEmpty()){
				temp2.add(sTOrderList3.get(0));
				if(!sTOrderList1.isEmpty())temp2.add(null);
			}
			if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
		}
		//checks if the test dummy exceeds the end of the day
		temporayTime = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
		temporayTime.add(GregorianCalendar.MINUTE, timeToFinishWithFilled(temp2,assemblyLine));
		while(!assemblyLine.getAssemblyLineScheduler().getRealEndOfDay().before(temporayTime)){
			//make the dummy the real situation 
			temp = (ArrayList<Order>) temp2.clone();
			if(!orderList2.isEmpty()) orderList2.remove(0);
			else{
				if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
				if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
			}
			//prepares a test dummy for an extra Order
			if(!orderList2.isEmpty())temp2.add(temp2.size()-corectionfactor, orderList2.get(0));
			else{
				boolean empty = true;
				if(!sTOrderList3.isEmpty()){
					temp2.add(sTOrderList3.get(0));
					temp2.add(null);
					empty = false;
				}
				if(!sTOrderList1.isEmpty()){
					temp2.add(sTOrderList1.get(0));
					empty = false;
				}
				if(empty) break;
			}
			temporayTime = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
			temporayTime.add(GregorianCalendar.MINUTE, timeToFinishWithFilled(temp2,assemblyLine));
		}
		//finishes the day with three empty orders
		for(int j =0; j< assemblyLine.getNumberOfWorkstations();j++){
			temp.add(null);
		}
		timeOfBeltWithAssembly(temp, assemblyLine,scheduledOrdersWithCompletionTime);
		return this.transformToScheduledOrderWithAssembly(temp, assemblyLine);
	}

	private int canPlaceAtbeginning(AssemblyLine assemblyLine) {
		int k =0;
		LinkedList<Order> stateOfAssemblyLine = assemblyLine.stateWhenAcceptingOrders();
		while(!assemblyLine.getAllWorkstations().get(k).getTaskTypes().contains(VehicleModelCatalog.taskTypeCreator.Seats)){
			k++;
		}
		for(Order order:stateOfAssemblyLine){
			if(order != null && (!(order instanceof SingleTaskOrder) ||  ((SingleTaskOrder) order).getType() != VehicleModelCatalog.taskTypeCreator.Seats)) return 0;
		}
		int i =0;
		for(Order order :stateOfAssemblyLine){
			if(order != null && (order instanceof SingleTaskOrder) && ((SingleTaskOrder) order).getType() == VehicleModelCatalog.taskTypeCreator.Seats) i++;
		}
		return k-1-i;
	}

	/**
	 * Calculates for every order the time an order will leave the AssemblyLine starting from an empty line.
	 * 
	 * @param ordersToComplete
	 * 		All orders to complete.
	 * @param time
	 * 		The start time of the AssemblyLine.
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the AssemblyLine .
	 * @param result
	 * 		The list where the new Orders and their completion estimate will be added.
	 */
	@SuppressWarnings("unchecked")
	private void timeOfBelt(ArrayList<Order> ordersToComplete,
			GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> result) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		for(int j =0; j< assemblyLineScheduler.getAssemblyLine().getNumberOfWorkstations();j++){
			simulator.add(null);
		}
		int timespent = 0;
		int j= 0;
		// places  the orders one by one and remove the last one which will be added to result
		ArrayList<Order> orderList = (ArrayList<Order>) ordersToComplete.clone();
		while(!orderList.isEmpty() || !allNull(simulator)){
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			ArrayList<Order> completed = advanceReturnOfBelt(simulator, orderList, assemblyLineScheduler.getAssemblyLine());
			for(Order order:completed){
				if(order != null){
					GregorianCalendar clone = (GregorianCalendar) time.clone();
					clone.add(GregorianCalendar.MINUTE, timespent);
					result.add(new ScheduledOrder(clone, order));
				}
			}
		}
	}

	private ArrayList<Order> advanceReturnOfBelt(LinkedList<Order> simulator,
			ArrayList<Order> orderList, AssemblyLine assemblyLine) {
		ArrayList<Order> result = new ArrayList<Order>();
		@SuppressWarnings("unchecked")
		LinkedList<Order> simulAssembly = (LinkedList<Order>) simulator.clone();
		simulAssembly.removeLast();
		simulAssembly.addFirst(null);
		for(int j= assemblyLine.getNumberOfWorkstations()-1; j>=0;j--){
			try {
				if(simulAssembly.get(j) != null && !assemblyLine.filterWorkstations(simulAssembly.get(j).getAssemblyprocess()).contains(assemblyLine.selectWorkstationById(j))){
					if(j== 1 && !orderList.isEmpty()){
						simulAssembly.removeFirst();
						simulAssembly.addFirst(orderList.remove(0));
						j+=2;
					}else
					if(j==assemblyLine.getNumberOfWorkstations()-1){
						result.add(simulAssembly.removeLast());
						simulAssembly.addLast(null);
					}
					else{
						if(simulAssembly.get(j+1)==null){
							simulAssembly.remove(j+1);
							simulAssembly.add(j+1, simulAssembly.get(j));
							simulAssembly.remove(j);
							simulAssembly.add(j, null);
							j+=2;
						}
					}
				}
			} catch (DoesNotExistException e) {
				// onmogelijk
			}
		}
		return result;
	}

	private boolean allNull(LinkedList<Order> simulator) {
		for(Order order: simulator){
			if(order!=null) return false;
		}
		return true;
	}

	/**
	 * Calculates for every order the time an order will leave the AssemblyLine starting from the current AssemblyLine.
	 * 
	 * @param ordersToComplete
	 * 		All orders to complete.
	 * @param time
	 * 		The start time of the AssemblyLine.
	 * @param stateOfAssemblyLine 
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the AssemblyLine.
	 * @param result
	 * 		The list where the new Orders and their completion estimate will be added.
	 */
	private void timeOfBeltWithAssembly(ArrayList<Order> ordersToComplete,
			AssemblyLine assemblyLine, ArrayList<ScheduledOrder> result) {
		@SuppressWarnings("unchecked")
		LinkedList<Order> simulator = (LinkedList<Order>) assemblyLine.stateWhenAcceptingOrders().clone();
		int timespent = 0;
		int j= 0;
		// places  the orders one by one and remove the last one which will be added to result
		ArrayList<Order> orderList = (ArrayList<Order>) ordersToComplete.clone();
		while(!orderList.isEmpty() || !allNull(simulator)){
			timespent += assemblyLine.calculateTimeTillAdvanceFor(simulator);
			ArrayList<Order> completed = advanceReturnOfBelt(simulator, orderList, assemblyLine);
			for(Order order:completed){
				if(order != null && !assemblyLine.stateWhenAcceptingOrders().contains(order)){
					GregorianCalendar clone = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
					clone.add(GregorianCalendar.MINUTE, timespent);
					result.add(new ScheduledOrder(clone, order));
				}
			}
		}
	}

	/**
	 * Calculates the time necessary to finish the schedule given the current state of the assemblyLine.
	 * 
	 * @param ordersToComplete
	 * 		All orders to complete.
	 * @param stateOfAssemblyLine 
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the AssemblyLine.
	 * @return The time necessary to finish the schedule given the current state of the assemblyLine in minutes.
	 */
	private int timeToFinishWithFilled(ArrayList<Order> ordersToComplete,
			AssemblyLine assemblyLine) {
		@SuppressWarnings("unchecked")
		LinkedList<Order> simulator = (LinkedList<Order>) assemblyLine.stateWhenAcceptingOrders().clone();
		int time = 0;
		ArrayList<Order> orderList = (ArrayList<Order>) ordersToComplete.clone();
		while(!orderList.isEmpty() || !allNull(simulator)){
			this.advanceReturnOfBelt(simulator, orderList, assemblyLine);
			time += assemblyLine.calculateTimeTillAdvanceFor(simulator);
		}
		time += assemblyLine.calculateTimeTillEmptyFor(simulator);
		return time;
	}

	/**
	 * Adds a day worth of ScheduledOrders to the given schedule.
	 * 
	 * @param scheduledOrders
	 * 		The given schedule.
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day.
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day.
	 * @param orderList2
	 * 		A List of Orders.
	 * @param time
	 * 		The time when all tasks on assemblyLine will be done.
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong.
	 * @param scheduledOrdersWithCompletionTime 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 */
	@SuppressWarnings("unchecked")
	private void addDay(ArrayList<ScheduledOrder> scheduledOrders,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> scheduledOrdersWithCompletionTime) {
		ArrayList<Order> temp = new ArrayList<Order>();
		int k =0;
		while(!assemblyLineScheduler.getAssemblyLine().getAllWorkstations().get(k).getTaskTypes().contains(VehicleModelCatalog.taskTypeCreator.Seats)){
			k++;
		}
		for(int i=0;i<k;i++){
			if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
			else{
				if(i!=0) temp.add(null);
				else{
					break;
				}
			}
		}
		int corectionfactor = 0;
		for(int i= 1;i<assemblyLineScheduler.getAssemblyLine().getNumberOfWorkstations() && !sTOrderList1.isEmpty();i++){
			temp.add(sTOrderList1.remove(0));
			corectionfactor++;
		}
		//prepares a test dummy for an extra Order
		ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
		if(!orderList2.isEmpty()) temp2.add(temp2.size()-corectionfactor, orderList2.get(0));
		//checks if the test dummy exceeds the end of the day
		while(calculatefulltimeAtstart(temp2,assemblyLineScheduler )< (AssemblyLineScheduler.END_OF_DAY-AssemblyLineScheduler.BEGIN_OF_DAY)*60){
			if(!orderList2.isEmpty())orderList2.remove(0);
			// checks if there are still 'normal' orders to be placed
			if(orderList2.isEmpty()){
				//fills the day with SingleTaskOrders
				completeAddDaywith1and3(temp2,sTOrderList1,sTOrderList3,time, assemblyLineScheduler);
				temp = temp2;
				break;
			}
			//make the dummy the real situation 
			temp = (ArrayList<Order>) temp2.clone();
			//prepares a test dummy for an extra Order
			temp2.add(temp2.size()-corectionfactor, orderList2.get(0));
		}
		//finishes the day with three empty orders
		temp.add(null);
		temp.add(null);
		temp.add(null);
		timeOfBelt(temp,time,assemblyLineScheduler,scheduledOrdersWithCompletionTime);
		scheduledOrders.addAll(transformToScheduledOrder(temp,time,assemblyLineScheduler));
	}
	/**
	 * Completes the day with SingleTaskOrders.
	 * 
	 * @param orders
	 * 		The given schedule.
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that are processed on the first workstation.
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that are processed on the third workstation.
	 * @param time
	 * 		The time when all task on assemblyLine will be done.
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong.
	 */
	@SuppressWarnings("unchecked")
	private void completeAddDaywith1and3(ArrayList<Order> orders,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler) {
		ArrayList<Order> temp = (ArrayList<Order>) orders.clone();
		//prepares a test dummy for 2 extra SingleTaskOrders
		if(!sTOrderList3.isEmpty()){
			orders.add(sTOrderList3.get(0));
			orders.add(null);
		}
		if(!sTOrderList1.isEmpty())orders.add(sTOrderList1.get(0));
		//checks if the test dummy exceeds the end of the day
		while((!sTOrderList3.isEmpty() || !sTOrderList1.isEmpty())&& calculatefulltimeAtstart(orders,assemblyLineScheduler )< (AssemblyLineScheduler.END_OF_DAY-AssemblyLineScheduler.BEGIN_OF_DAY)*60){
			//make the dummy the real situation 
			if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
			if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
			temp= (ArrayList<Order>) orders.clone();
			//prepares a test dummy for 2 extra SingleTaskOrders
			if(!sTOrderList3.isEmpty()){
				orders.add(sTOrderList3.get(0));
				orders.add(null);
			}
			if(!sTOrderList1.isEmpty())orders.add(sTOrderList1.get(0));

		}
		orders =temp;
	}

	/**
	 * Transforms a list of Orders into a list of ScheduledOrders starting from an empty AssemblyLine.
	 * 
	 * @param orders
	 * 		The list of Orders that will be transformed
	 * @param time
	 * 		The time used as reference for the calenders in the scheduledOrders
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the orders
	 * @return A list of ScheduledOrder objects conform to the given order objects.
	 */
	private ArrayList<ScheduledOrder> transformToScheduledOrder(
			ArrayList<Order> orders, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		ArrayList<ScheduledOrder> scheduledOrders = new ArrayList<ScheduledOrder>();
		simulator.add(null);
		simulator.add(null);
		simulator.add(null);
		int timespent = 0;
		// places  the orders one by one and remove the last one which will be added to result
		ArrayList<Order> orderList = (ArrayList<Order>) orders.clone();
		while(!orderList.isEmpty() || !allNull(simulator)){
			ArrayList<Order> placedOrders = this.advanceReturnOnBelt(simulator, orderList, assemblyLineScheduler.getAssemblyLine());
			for(Order order:placedOrders){
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			scheduledOrders.add(new ScheduledOrder(clone, order));
			}
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		return scheduledOrders;

	}

	private ArrayList<Order> advanceReturnOnBelt(LinkedList<Order> simulator,
			ArrayList<Order> orderList, AssemblyLine assemblyLine) {
		ArrayList<Order> result = new ArrayList<Order>();
		LinkedList<Order> simulAssembly = (LinkedList<Order>) simulator.clone();
		simulAssembly.removeLast();
		simulAssembly.addFirst(null);
		for(int j= assemblyLine.getNumberOfWorkstations()-1; j>=0;j--){
			try {
				if(simulAssembly.get(j) != null && !assemblyLine.filterWorkstations(simulAssembly.get(j).getAssemblyprocess()).contains(assemblyLine.selectWorkstationById(j))){
					if(j== 1 && !orderList.isEmpty()){
						simulAssembly.removeFirst();
						result.add(orderList.get(0));
						simulAssembly.addFirst(orderList.remove(0));
						j+=2;
					}else
					if(j==assemblyLine.getNumberOfWorkstations()-1){
						simulAssembly.removeLast();
						simulAssembly.addLast(null);
					}
					else{
						if(simulAssembly.get(j+1)==null){
							simulAssembly.remove(j+1);
							simulAssembly.add(j+1, simulAssembly.get(j));
							simulAssembly.remove(j);
							simulAssembly.add(j, null);
							j+=2;
						}
					}
				}
			} catch (DoesNotExistException e) {
				// onmogelijk
			}
		}
		return result;
	}

	/**
	 * Transforms a list of Orders into a list of ScheduledOrders starting from the current AssemblyLine.
	 * 
	 * @param orders
	 * 		The list of Orders that will be transformed.
	 * @param time
	 * 		The time used as reference for the calenders in the ScheduledOrders.
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the orders and the current AssemblyLine.
	 * @return A list of scheduledOrders.
	 */
	private ArrayList<ScheduledOrder> transformToScheduledOrderWithAssembly(
			ArrayList<Order> orders, AssemblyLine assemblyLine) {
		LinkedList<Order> simulator = assemblyLine.getAllOrders();
		ArrayList<ScheduledOrder> scheduledOrders = new ArrayList<ScheduledOrder>();
		int timespent = 0;
		// places  the orders one by one and remove the last one which will be added to result
		ArrayList<Order> orderList = (ArrayList<Order>) orders.clone();
		while(!orderList.isEmpty() || !allNull(simulator)){
			ArrayList<Order> placedOrders = this.advanceReturnOnBelt(simulator, orderList, assemblyLine);
			for(Order order:placedOrders){
			GregorianCalendar clone = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders(assemblyLine).clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			scheduledOrders.add(new ScheduledOrder(clone, order));
			}
			timespent += assemblyLine.calculateTimeTillAdvanceFor(simulator);
		}
		return scheduledOrders;
	}

	/**
	 * Calculates the time necessary to finish the schedule given the current state of the AssemblyLine.
	 * 
	 * @param orders
	 * 		The schedule to finish
	 * @param assemblyLineScheduler 
	 * 		The AssemblyLineScheduler where the orders would be placed.
	 * @return The time necessary to finish the schedule given the current state of the assemblyLine in minutes.
	 */
	private int calculatefulltimeAtstart(ArrayList<Order> orders, AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		simulator.add(null);
		simulator.add(null);
		simulator.add(null);
		int time = 0;
		// places  the orders one by one 
		for(Order i : orders){
			simulator.addFirst(i);
			simulator.removeLast();
			time += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		// finishes the last orders
		simulator.addFirst(null);
		simulator.removeLast();
		time += assemblyLineScheduler.getAssemblyLine().calculateTimeTillEmptyFor(simulator);
		return time;
	}

	/**
	 * Sorts SingleTaskOrders on their deadlines.
	 * 
	 * @param sTOrderList
	 * 		A list of SingleTaskOrders that will be sorted.
	 */
	private void deadlineSort(ArrayList<SingleTaskOrder> sTOrderList) {
		Comparator<SingleTaskOrder> comparator = new Comparator<SingleTaskOrder>(){
			@Override
			public int compare(SingleTaskOrder order1, SingleTaskOrder order2){
				if(order1.getDeadLine().after(order2.getDeadLine())) return 1;
				if(order1.getDeadLine().before(order2.getDeadLine())) return -1;
				return 0;
			}
		};
		Collections.sort(sTOrderList, comparator);
	}

	/**
	 * Filters all single task orders out of the given list of orders and returns these single tasks orders.
	 * 
	 * @param orders
	 *		The list where SingleTaskOrders will be extracted from.
	 * @return A list with all the SingleTaskOrders out of orderList.
	 * 		OrderList does not contain any SingleTaskOrders anymore.
	 */
	private ArrayList<SingleTaskOrder> combSingleTaskOrders(
			ArrayList<Order> orders) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(int i = 0; i< orders.size(); i++){
			if(orders.get(i) instanceof SingleTaskOrder){
				result.add((SingleTaskOrder) orders.remove(i));
				i--;
			}
		}
		return result;
	}

	/**
	 * Returns the OptionType of the option in the configuration of the order.
	 * 
	 * @param order
	 * 		The order where the optionType will be retrieved from.
	 * @return the OptionType of the option in the configuration of the order.
	 * 		
	 */
	private OptionType getType(SingleTaskOrder order) {
		return order.getType();
	}

	/**
	 * Returns all SingleTaskOrders with a specific type in the given list.
	 * @param singleTaskOrders
	 *		The list where SingleTaskOrders with a specific type will be retrieved from.
	 * @param type
	 * 		The type of the SingleTaskOrders that will be extracted.
	 * @return A list with all the SingleTaskOrders out of orderList.
	 */
	private ArrayList<SingleTaskOrder> combSingleTaskOrdersByType(
			ArrayList<SingleTaskOrder> singleTaskOrders, OptionType type) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:singleTaskOrders){
			if(getType(i) == type){
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * Returns the inner algorithm of this efficiency scheduling algorithm.
	 * 
	 * @return The inner algorithm of this efficiency scheduling algorithm.
	 */
	public SchedulingAlgorithm getInnerAlgorithm() {
		return innerAlgorithm;
	}

	@Override
	public String toString(){
		return "Efficiency algorithm using " + this.getInnerAlgorithm().toString();
	}
}
