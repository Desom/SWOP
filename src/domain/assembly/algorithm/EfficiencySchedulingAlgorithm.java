package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class EfficiencySchedulingAlgorithm implements SchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;

	public EfficiencySchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineScheduler) {
		GregorianCalendar allTasksCompletedTime = assemblyLineScheduler.getCurrentTime();
		return convert(this.scheduleToScheduledOrderList(orderList, allTasksCompletedTime , assemblyLineScheduler));

	}
	/**
	 * Sorts the arraylist in a scheduling order given a certain algorithm but
	 *  with 2 SingleTaskOrders at the beginning of the day and 2 at the end of the day
	 *  @param orderList
	 *  		The orders to be sorted 
	 *  @param allTasksCompletedTime
	 *  		The time when all task on assemblyLine will be done
	 *  @param assemblyLineScheduler
	 *  		The assymblylineScheduler to which the orders belong
	 *  @return The sorted Orders 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			GregorianCalendar allTasksCompletedTime,
			AssemblyLineScheduler assemblyLineScheduler) {
		//separates the the Orders in specific types and sorts them
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> STOrderList = combSingleTaskOrders(orderList2);
		orderList2 = innerAlgorithm.scheduleToList(orderList2, assemblyLineScheduler);
		ArrayList<SingleTaskOrder> STOrderListWorkStation3 = combSingleTaskOrdersByType( STOrderList,OptionType.Seats);
		ArrayList<SingleTaskOrder> STOrderListWorkStation1 = combSingleTaskOrdersByType( STOrderList,OptionType.Color);
		deadlineSort(STOrderListWorkStation1);
		deadlineSort(STOrderListWorkStation3); 
		//combine the three lists into one schedule 
		ArrayList<ScheduledOrder> deadlines = new ArrayList<ScheduledOrder>();
		ArrayList<ScheduledOrder> temp = completeSchedule(STOrderListWorkStation1,STOrderListWorkStation3, orderList2,allTasksCompletedTime, assemblyLineScheduler, deadlines);

		//handle deadlines which are endangered
		ArrayList<SingleTaskOrder> endangeredOrders = new ArrayList<SingleTaskOrder>();
		SingleTaskOrder endangeredOrder = retrieveFirstDeadlineFailure(deadlines,endangeredOrders);
		while(endangeredOrder != null){
			endangeredOrders.add(endangeredOrder);
			STOrderListWorkStation3.remove(endangeredOrder);
			STOrderListWorkStation1.remove(endangeredOrder);
			this.deadlineSort(endangeredOrders);
			deadlines = new ArrayList<ScheduledOrder>();
			temp = completeSchedule(STOrderListWorkStation1,STOrderListWorkStation3,append(endangeredOrders, orderList2),allTasksCompletedTime, assemblyLineScheduler, deadlines);
			endangeredOrder = retrieveFirstDeadlineFailure(deadlines,endangeredOrders);
		}

		return temp;
	}

	/**
	 * 	Appends a List of SingleTaskOrders and a list of Orders into a new list of orders
	 * @param endangeredOrders
	 * 			The list of SingleTaskOrders to be appended
	 * @param orderList2
	 * 			The list of SingleTaskOrders to be appended
	 * @return a list which contains and singleTasdkOrders and orders of both list with the SingleTaskOrders first. 
	 * The order of elements in both lists are still respected
	 */
	private ArrayList<Order> append(
			ArrayList<SingleTaskOrder> endangeredOrders,
			ArrayList<Order> orderList2) {
		ArrayList<Order> result = new ArrayList<Order>();
		for(SingleTaskOrder i :endangeredOrders) result.add(i);
		for(Order i :orderList2) result.add(i);
		return result;
	}
	/**
	 * Find in a list the first SingleTaskOrder whose deadline is scheduled to be completed after it's deadline that is not already known
	 * @param timeOfBelt
	 * 			A list which the Orders and their time to be completed
	 * @param endangeredOrders
	 * 			A List which are already known to be completed late 		
	 * @return the first SingleTaskOrder whose deadline is scheduled to be completed after it's deadline that is not already known
	 * 			null if none are found
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
	 * converts a list of ScheduledOrders into a list of Orders
	 * @param arrayList
	 * 			the list to be converted
	 * @return A list of Orders which are all in arrayList and in the same order
	 */
	private ArrayList<Order> convert(ArrayList<ScheduledOrder> arrayList) {
		ArrayList<Order> result = new ArrayList<Order>();
		for(ScheduledOrder i : arrayList)result.add(i.getScheduledOrder());
		return result;
	}


	/**
	 * combines the three lists into one array of ScheduledOrders with if possible 
	 * 2 SingleTaskOrders at the beginning of the day and 2 at the end of the day
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day 
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day
	 * @param orderList
	 * 		A List of Orders
	 * @param allTasksCompletedTime
	 * 		The time when all task on assemblyLine will be done
	 * @param assemblyLineScheduler 
	 * 		The assymblylineScheduler to which the orders belong
	 * @param deadlines 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 * @return An array of ScheduledOrders with if possible 
	 * 		2 SingleTaskOrders at the beginning of the day and 2 at the end of the day
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeSchedule(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, ArrayList<Order> orderList,
			GregorianCalendar allTasksCompletedTime, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> deadlines) {
		// clone the information that will be changed
		GregorianCalendar time = (GregorianCalendar) allTasksCompletedTime.clone();
		ArrayList<Order> orderList2 = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> sTOrderList1clone = (ArrayList<SingleTaskOrder>) sTOrderList1.clone();
		ArrayList<SingleTaskOrder> sTOrderList3clone = (ArrayList<SingleTaskOrder>) sTOrderList3.clone();
		// Complete the current day
		ArrayList<ScheduledOrder> result = completeDay(sTOrderList1clone, sTOrderList3clone, orderList2,time, assemblyLineScheduler,deadlines);
		time = nextDay(time);
		//complete the rest of the days
		while(!sTOrderList1clone.isEmpty() || !sTOrderList3clone.isEmpty() || !orderList2.isEmpty() ){
			addDay(result,sTOrderList1clone,sTOrderList3clone,orderList2,time,assemblyLineScheduler,deadlines);
			time = nextDay(time);
		}
		return result;
	}
	/**
	 * creates an array of ScheduledOrders that supplements the status of the AssemblyLineScheduler so that the day finishes proceeds correctly
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day 
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day
	 * @param orderList2
	 * 		A List of Orders
	 * @param time
	 * 		The time when all task on assemblyLine will be done
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong
	 * @param deadlines 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 * @return An array of ScheduledOrders so that the day finishes proceeds correctly
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<ScheduledOrder> completeDay(
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> deadlines) {
		ArrayList<Order> temp = new ArrayList<Order>();
		int corectionfactor = 2;
		// looks if a SingleTaskOrder can be placed at the end of the day
		if(canDoSingleTaskOrders(assemblyLineScheduler, time) == 0){
			//finishes the day with three empty orders
			temp.add(null);
			temp.add(null);
			temp.add(null);
			timeOfBeltWithAssembly(temp, time, assemblyLineScheduler, deadlines);
			return this.transformToScheduledOrderWithAssembly(temp, time, assemblyLineScheduler);
		}
		// places a SingleTaskOrder  at the end of the day
		if(!sTOrderList1.isEmpty()){
			temp.add(sTOrderList1.remove(0));
			corectionfactor--;
		}
		// looks if an extra SingleTaskOrders can be placed at the end of the day
		if(canDoSingleTaskOrders(assemblyLineScheduler, time) == 1){
			//finishes the day with three empty orders
			temp.add(null);
			temp.add(null);
			temp.add(null);
			timeOfBeltWithAssembly(temp, time, assemblyLineScheduler, deadlines);
			return this.transformToScheduledOrderWithAssembly(temp, time, assemblyLineScheduler);
		}
		// places an extra SingleTaskOrder at the end of the day
		if(!sTOrderList1.isEmpty()){
			temp.add(sTOrderList1.remove(0));
			corectionfactor--;
		}
		// looks if it is the start of the day
		if(this.startOfDay(assemblyLineScheduler)){
			// adds one or two SingleTaskOrders at the beginning of the day
			if(!sTOrderList3.isEmpty()){
				temp.add(0,sTOrderList3.remove(0));
				if(!sTOrderList3.isEmpty()){
					temp.add(1,sTOrderList3.remove(0));
				}
				else{
					if(corectionfactor != 2)temp.add(null);
				}
			}
		}else{
			// looks if only one advanced has passed
			if(secondAdvance(assemblyLineScheduler)){
				// adds one extra SingleTaskOrders at the beginning of the day

				//prepares a test dummy for an extra SingleTaskOrder
				ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
				if(!sTOrderList3.isEmpty()){
					temp2.add(0,sTOrderList3.get(0));
				}
				//checks if the test dummy exceeds the end of the day
				GregorianCalendar temporayTime = (GregorianCalendar) time.clone();
				temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineScheduler));
				if(assemblyLineScheduler.getRealEndOfDay().before(temporayTime)) {
					//finishes the day with three empty orders
					temp.add(null);
					temp.add(null);
					temp.add(null);
					timeOfBeltWithAssembly(temp, time, assemblyLineScheduler,deadlines);
					return this.transformToScheduledOrderWithAssembly(temp, time, assemblyLineScheduler);
				}
				//make the dummy the real situation 
				temp = (ArrayList<Order>) temp2.clone();
				sTOrderList3.remove(0);}
		}
		//prepares a test dummy for an extra Order
		ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
		if(!orderList2.isEmpty())temp2.add(temp2.size()+corectionfactor-2, orderList2.get(0));
		else{
			if(!sTOrderList3.isEmpty()){
				temp2.add(sTOrderList3.get(0));
				if(!sTOrderList1.isEmpty())temp2.add(null);
			}
			if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
		}
		//checks if the test dummy exceeds the end of the day
		GregorianCalendar temporayTime = (GregorianCalendar) time.clone();
		temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineScheduler));
		while(!assemblyLineScheduler.getRealEndOfDay().before(temporayTime)){
			//make the dummy the real situation 
			temp = (ArrayList<Order>) temp2.clone();
			if(!orderList2.isEmpty()) orderList2.remove(0);
			else{
				if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
				if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
			}
			//prepares a test dummy for an extra Order
			if(!orderList2.isEmpty())temp2.add(temp2.size()+corectionfactor-2, orderList2.get(0));
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
			temporayTime = (GregorianCalendar) time.clone();
			temporayTime.add(GregorianCalendar.MINUTE, timetofinishwithfilled(temp2,assemblyLineScheduler));
		}
		//finishes the day with three empty orders
		temp.add(null);
		temp.add(null);
		temp.add(null);
		timeOfBeltWithAssembly(temp, time, assemblyLineScheduler,deadlines);
		return this.transformToScheduledOrderWithAssembly(temp, time, assemblyLineScheduler);
	}
	/**
	 * Calculates for every order the time an order will leave the AssemblyLine starting from an empty line
	 * @param temp
	 * 		The schedule to finish
	 * @param time
	 * 		The start of the AssemblyLine
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the AssemblyLine 
	 * @param result the list where the new Orders and their completion estimate will be added
	 */
	private void timeOfBelt(ArrayList<Order> temp,
			GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> result) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		simulator.add(null);
		simulator.add(null);
		simulator.add(null);
		int timespent = 0;
		int j= 0;
		// places  the orders one by one and remove the last one which will be added to result
		for(Order i : temp){
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			Order last = simulator.removeLast();
			simulator.addFirst(i);
			j++;
			if(j>3){
				GregorianCalendar clone = (GregorianCalendar) time.clone();
				clone.add(GregorianCalendar.MINUTE, timespent);
				result.add(new ScheduledOrder(clone, last));
			}

		}
		for(int i=0; i< 3; i++){
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			Order last = simulator.removeLast();
			simulator.addFirst(null);
			j++;
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			result.add(new ScheduledOrder(clone, last));
		}
	}
	/**
	 * Calculates for every order the time an order will leave the AssemblyLine starting from the current AssemblyLine
	 * @param temp
	 * 		The schedule to finish
	 * @param time
	 * 		The start of the AssemblyLine
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the AssemblyLine 
	 * @param result the list where the new Orders and their completion estimate will be added
	 */
	private void timeOfBeltWithAssembly(ArrayList<Order> temp,
			GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> result) {
		LinkedList<Order> simulator = assemblyLineScheduler.getAssemblyLine().getAllOrders();
		int timespent = 0;
		int j= 0;
		// places  the orders one by one and remove the last one which will be added to result
		for(Order i : temp){
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			Order last = simulator.removeLast();
			simulator.addFirst(i);
			j++;
			if(j>3){
				GregorianCalendar clone = (GregorianCalendar) time.clone();
				clone.add(GregorianCalendar.MINUTE, timespent);
				result.add(new ScheduledOrder(clone, last));
			}

		}
		for(int i=0; i< 3; i++){
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
			Order last = simulator.removeLast();
			simulator.addFirst(null);
			j++;
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			result.add(new ScheduledOrder(clone, last));
		}
	}

	/**
	 * Calculates the time necessary to finish the schedule given the current state of the assemblyLine
	 * @param temp
	 * 		The schedule to finish
	 * @param assemblyLineScheduler 
	 * 			The AssemblyLineScheduler  where the assemblyLine will be retreived from
	 * @return the time necessary to finish the schedule given the current state of the assemblyLine in minutes
	 */
	private int timetofinishwithfilled(ArrayList<Order> temp,
			AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = assemblyLineScheduler.getAssemblyLine().getAllOrders();
		int time = 0;
		for(Order i : temp){
			simulator.addFirst(i);
			simulator.removeLast();
			time += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		simulator.addFirst(null);
		simulator.removeLast();
		time += assemblyLineScheduler.getAssemblyLine().calculateTimeTillEmptyFor(simulator);
		return time;
	}



	/**
	 * Adds a day worth of ScheduledOrders to the given schedule
	 * @param result
	 * 		The given schedule
	 * @param sTOrderList1
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the end of the day 
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that if possible needs to be placed at the beginning of the day
	 * @param orderList2
	 * 		A List of Orders
	 * @param time
	 * 		The time when all task on assemblyLine will be done
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong
	 * @param deadlines 
	 * 		The list which will contain the scheduled Orders and their estimated completion time
	 */
	@SuppressWarnings("unchecked")
	private void addDay(ArrayList<ScheduledOrder> result,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3,
			ArrayList<Order> orderList2, GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler, ArrayList<ScheduledOrder> deadlines) {
		ArrayList<Order> temp = new ArrayList<Order>();
		int corectionfactor = 2;
		// adds one or two SingleTaskOrders at the beginning of the day
		if(!sTOrderList3.isEmpty()){
			temp.add(sTOrderList3.remove(0));
			if(!sTOrderList3.isEmpty()){
				temp.add(sTOrderList3.remove(0));
			}
			else{
				if(!sTOrderList1.isEmpty())temp.add(null);
			}
		}
		// places 2 SingleTaskOrders at the end of the day
		if(!sTOrderList1.isEmpty()){
			temp.add(sTOrderList1.remove(0));
			corectionfactor--;
		}
		if(!sTOrderList1.isEmpty()){
			temp.add(sTOrderList1.remove(0));
			corectionfactor--;
		}
		//prepares a test dummy for an extra Order
		ArrayList<Order> temp2 = (ArrayList<Order>) temp.clone();
		if(!orderList2.isEmpty()) temp2.add(temp2.size()+corectionfactor-2, orderList2.get(0));
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
			temp2.add(temp2.size()+corectionfactor-2, orderList2.get(0));
		}
		//finishes the day with three empty orders
		temp.add(null);
		temp.add(null);
		temp.add(null);
		timeOfBelt(temp,time,assemblyLineScheduler,deadlines);
		result.addAll(transformToScheduledOrder(temp,time,assemblyLineScheduler));
	}
	/**
	 * completes the day with singleCarOrders
	 * @param temp2
	 * 		The given schedule
	 * @param sTOrderList1
	 * 	A List of SingleTaskOrders that are processed on the first workstation
	 * @param sTOrderList3
	 * 		A List of SingleTaskOrders that are processed on the third workstation
	 * @param time
	 * 		The time when all task on assemblyLine will be done
	 * @param assemblyLineScheduler
	 * 		The assymblylineScheduler to which the orders belong
	 */
	@SuppressWarnings("unchecked")
	private void completeAddDaywith1and3(ArrayList<Order> temp2,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler) {
		ArrayList<Order> temp = (ArrayList<Order>) temp2.clone();
		//prepares a test dummy for 2 extra SingleTaskOrders
		if(!sTOrderList3.isEmpty()){
			temp2.add(sTOrderList3.get(0));
			temp2.add(null);
		}
		if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));
		//checks if the test dummy exceeds the end of the day
		while((!sTOrderList3.isEmpty() || !sTOrderList1.isEmpty())&& calculatefulltimeAtstart(temp2,assemblyLineScheduler )< (AssemblyLineScheduler.END_OF_DAY-AssemblyLineScheduler.BEGIN_OF_DAY)*60){
			//make the dummy the real situation 
			if(!sTOrderList3.isEmpty())sTOrderList3.remove(0);
			if(!sTOrderList1.isEmpty())sTOrderList1.remove(0);
			temp= (ArrayList<Order>) temp2.clone();
			//prepares a test dummy for 2 extra SingleTaskOrders
			if(!sTOrderList3.isEmpty()){
				temp2.add(sTOrderList3.get(0));
				temp2.add(null);
			}
			if(!sTOrderList1.isEmpty())temp2.add(sTOrderList1.get(0));

		}
		temp2 =temp;
	}
	/**
	 * Transforms a list of Orders into a list of scheduledOrders starting from an empty assemblyline
	 * @param temp
	 * 		The list of Orders that will be transformed
	 * @param time
	 * 		The time used as reference for the calenders in the scheduledOrders
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the orders
	 * @return a list of scheduledOrders
	 */
	private ArrayList<ScheduledOrder> transformToScheduledOrder(
			ArrayList<Order> temp, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		ArrayList<ScheduledOrder> result = new ArrayList<ScheduledOrder>();
		simulator.add(null);
		simulator.add(null);
		simulator.add(null);
		int timespent = 0;
		// places  the orders one by one and remove the last one which will be added to result
		for(Order i : temp){
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			result.add(new ScheduledOrder(clone, i));
			simulator.removeLast();
			simulator.addFirst(i);
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}
		return result;

	}
	/**
	 * Transforms a list of Orders into a list of scheduledOrders starting from the current AssemblyLine
	 * @param temp
	 * 		The list of Orders that will be transformed
	 * @param time
	 * 		The time used as reference for the calenders in the scheduledOrders
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler that contains the orders and the current AssemblyLine
	 * @return a list of scheduledOrders
	 */
	private ArrayList<ScheduledOrder> transformToScheduledOrderWithAssembly(
			ArrayList<Order> temp, GregorianCalendar time,
			AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = assemblyLineScheduler.getAssemblyLine().getAllOrders();
		ArrayList<ScheduledOrder> result = new ArrayList<ScheduledOrder>();
		int timespent = 0;
		// places  the orders one by one and remove the last one which will be added to result
		for(Order i : temp){
			GregorianCalendar clone = (GregorianCalendar) time.clone();
			clone.add(GregorianCalendar.MINUTE, timespent);
			result.add(new ScheduledOrder(clone, i));
			simulator.removeLast();
			simulator.addFirst(i);
			timespent += assemblyLineScheduler.getAssemblyLine().calculateTimeTillAdvanceFor(simulator);
		}

		return result;

	}
	/**
	 * Calculates the time necessary to finish the schedule given the current state of the assemblyLine
	 * @param temp
	 * 		The schedule to finish
	 * @param assemblyLineScheduler 
	 * 			The AssemblyLineScheduler where the orders whould be placed
	 * @return the time necessary to finish the schedule given the current state of the assemblyLine in minutes
	 */
	private int calculatefulltimeAtstart(ArrayList<Order> temp, AssemblyLineScheduler assemblyLineScheduler) {
		LinkedList<Order> simulator = new LinkedList<Order>();
		simulator.add(null);
		simulator.add(null);
		simulator.add(null);
		int time = 0;
		// places  the orders one by one 
		for(Order i : temp){
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
	/*
	private void finish(ArrayList<ScheduledOrder> result,
			ArrayList<SingleTaskOrder> sTOrderList1,
			ArrayList<SingleTaskOrder> sTOrderList3, GregorianCalendar time, AssemblyLineScheduler assemblyLineScheduler) {
		ArrayList<Order> temp = new ArrayList<Order>();
		for(int i = 0; i< Math.min(AssemblyLineScheduler.END_OF_DAY - AssemblyLineScheduler.BEGIN_OF_DAY, Math.max(sTOrderList1.size(), sTOrderList3.size()));i++){
			if(sTOrderList3.isEmpty() && sTOrderList1.isEmpty()) break;
			if(!sTOrderList3.isEmpty())temp.add(sTOrderList3.remove(0));
			temp.add(null);
			if(!sTOrderList1.isEmpty())temp.add(sTOrderList1.remove(0));
		}
		result.addAll(transformToScheduledOrder(temp, time, assemblyLineScheduler));
	}
	 */
	/**
	 * Makes a GregorianCalendar which represents the beginning of the first workday after the given calendar.
	 * @param currentTime
	 * 		The current time and date.
	 * @return The GregorianCalendar representing the beginning of the first workday after the day in currentTime. 
	 */
	private GregorianCalendar nextDay(GregorianCalendar currentTime) {
		GregorianCalendar nextDay = (GregorianCalendar) currentTime.clone();
		if(nextDay.get(GregorianCalendar.HOUR_OF_DAY) <= 6)nextDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
		nextDay.set(GregorianCalendar.HOUR_OF_DAY, AssemblyLineScheduler.BEGIN_OF_DAY);
		nextDay.set(GregorianCalendar.MINUTE, 0);
		nextDay.set(GregorianCalendar.SECOND, 0);
		return nextDay;
	}
	/**
	 * Checks if only on advance has happened in the assemblyLine
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler where the AssemblyLine will be retrieved from
	 * @return true if only on advance has happened in the assemblyLine else false
	 */
	private boolean secondAdvance(AssemblyLineScheduler assemblyLineScheduler) {
		SingleTaskOrder order;
		try{
			order = (SingleTaskOrder) assemblyLineScheduler.getAssemblyLine().getAllOrders().get(0);
		}catch(ClassCastException e){
			return false;
		}
		if(order == null) return false;
		if(getType(order) != OptionType.Seats ) return false;
		for(int i = 1; i <assemblyLineScheduler.getAssemblyLine().getAllOrders().size(); i++){
			if(assemblyLineScheduler.getAssemblyLine().getAllOrders().get(i) != null) return false;
		}
		return true;
	}
	/**
	 * Checks if it's the start of the day
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler where will be checked if it's the start of the day
	 * @return true if it's the start of the day else false
	 */
	private boolean startOfDay(AssemblyLineScheduler assemblyLineScheduler) {
		if(assemblyLineScheduler.getCurrentTime().get(GregorianCalendar.HOUR_OF_DAY) != AssemblyLineScheduler.BEGIN_OF_DAY 
				|| assemblyLineScheduler.getCurrentTime().get(GregorianCalendar.MINUTE )!=0 ) return false;
		for(Order i:assemblyLineScheduler.getAssemblyLine().getAllOrders()){
			if(i != null) return false;
		}	
		return true;
	}
	/**
	 * Checks how many SingleTaskOrders can be done till the end of the day given the time and AssemblyLineScheduler
	 * @param assemblyLineScheduler
	 * 			The AssemblyLineScheduler where the assembly line will be retrieved from
	 * @param allTaskCompleted
	 * 		The time on which the order in the aseembly are done
	 * @return the amount of SingleTaskOrders can be done till the end of the day
	 */
	private int canDoSingleTaskOrders(AssemblyLineScheduler assemblyLineScheduler, GregorianCalendar allTaskCompleted) {
		int result = 0;
		GregorianCalendar time = (GregorianCalendar) allTaskCompleted.clone();
		while(time.get(GregorianCalendar.HOUR_OF_DAY) < AssemblyLineScheduler.END_OF_DAY){
			if(result ==0){
				time.add(GregorianCalendar.MINUTE, Math.max(0,  Math.max(getEstimateTime(assemblyLineScheduler.getAssemblyLine().getAllOrders().get(0),2), getEstimateTime(assemblyLineScheduler.getAssemblyLine().getAllOrders().get(1),3))));
			}
			if(result ==1){
				time.add(GregorianCalendar.MINUTE, Math.max(0,getEstimateTime(assemblyLineScheduler.getAssemblyLine().getAllOrders().get(0),3)  ));
			}
			if(result >1){
				time.add(GregorianCalendar.MINUTE, 60);
			}
			result++;
		}
		return result;
	}
	/**
	 * Gets an estimate time to finish a single workstation of an order
	 * @param order
	 * 		The order where the time will be retrieved from
	 * @param workstation
	 * 		The number of the workstation
	 * @return an estimate time to finish a single workstation of the order
	 */
	private int getEstimateTime(Order order, int workstation) {
		if(order == null) return 0;
		if(order instanceof SingleTaskOrder){
			if(getType((SingleTaskOrder) order) == OptionType.Color && workstation==1) return 60;
			if(getType((SingleTaskOrder) order) == OptionType.Seats && workstation==3) return 60;
			return 0;
		}
		return order.getConfiguration().getModel().getExpectedTaskTime();
	}

	/**
	 * Sorts SingleTaskOrders on their deadlines
	 * @param sTOrderList
	 * 		A list of SingleTaskOrders that will be sorted
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
	 * Extracts all SingleTaskOrders out a list
	 * @param orderList
	 *		The list where SingleTaskOrders will be extracted from
	 * @return A list with all the SingleTaskOrders out of orderList
	 * 		OrderList does not contain any SingleTaskOrders anymore
	 */
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

	/**
	 * Return the OptionType of the option in the configuration of the order
	 * @param order
	 * 		The order where the optionType will be retrieved from
	 * @return the OptionType of the option in the configuration of the order
	 * 		
	 */
	private OptionType getType(SingleTaskOrder order) {
		return order.getConfiguration().getAllOptions().get(0).getType();
	}


	/**
	 * retrieves all SingleTaskOrders with a specific type out a list
	 * @param list
	 *		The list where SingleTaskOrders with a specific type will be retrieved from
	 * @param type
	 * 		The type of the SingleTaskOrders that will be extracted
	 * @return A list with all the SingleTaskOrders out of orderList
	 */
	private ArrayList<SingleTaskOrder> combSingleTaskOrdersByType(
			ArrayList<SingleTaskOrder> list, OptionType type) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:list){
			if(getType(i) == type){
				result.add(i);
			}
		}
		return result;
	}

	public SchedulingAlgorithm getInnerAlgorithm() {
		return innerAlgorithm;
	}

	@Override
	public String toString(){
		return "Efficiency algorithm using " + this.getInnerAlgorithm().toString();
	}
}
