package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.configuration.Taskables.OptionType;
import domain.scheduling.order.Order;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;

public class NewEfficiency implements SchedulingAlgorithm{

	private SchedulingAlgorithm innerAlgorithm;
	private Comparator<SingleTaskOrder> deadlineComparator = new Comparator<SingleTaskOrder>(){
		@Override
		public int compare(SingleTaskOrder order1, SingleTaskOrder order2){
			return order1.getDeadLine().compareTo(order2.getDeadLine());
		}
	};
	
	public NewEfficiency(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	/**
	 * Schedules the given list of orders and returns it.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled version of the given list of orders.
	 */

	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineScheduler) {
		GregorianCalendar allTasksCompletedTime = assemblyLineScheduler.getCurrentTime();
		return convert(this.scheduleToScheduledOrderList(orderList, allTasksCompletedTime , assemblyLineScheduler));
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
	 * Schedules the given list of orders and returns a scheduled list of ScheduledOrder objects.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param allTasksCompletedTime
	 * 		The time by which all tasks have to be completed.
	 * @param assemblyLineScheduler
	 * @return A scheduled list of ScheduledOrder objects.
	 */
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			GregorianCalendar allTasksCompletedTime,
			AssemblyLineScheduler assemblyLineScheduler) {

		
		//verdeel de orders over de verschillende lijsten.
		orderList = (ArrayList<Order>) orderList.clone();
		ArrayList<SingleTaskOrder> STOrderList = combSingleTaskOrders(orderList);
		LinkedList<Order> sList = new LinkedList<Order>(this.innerAlgorithm.scheduleToList(orderList, assemblyLineScheduler));
		LinkedList<SingleTaskOrder> STOrderListWorkStation3 = new LinkedList<SingleTaskOrder>(combSingleTaskOrdersByType( STOrderList,OptionType.Seats));
		LinkedList<SingleTaskOrder> STOrderListWorkStation1 = new LinkedList<SingleTaskOrder>(combSingleTaskOrdersByType( STOrderList,OptionType.Color));

		//sorteer alle singleTasks volgens hun deadline
		Collections.sort(STOrderListWorkStation3, this.deadlineComparator);
		Collections.sort(STOrderListWorkStation3, this.deadlineComparator);

		//initialiseer de lijst met STOrders die niet volgens het standaard patroon kunnen.
		ArrayList<SingleTaskOrder> endangeredSTO = new ArrayList<SingleTaskOrder>();
				
		//zet kopieën klaar zodat je bij een deadlineErrorException opnieuw kan beginnen.
		ArrayList<SingleTaskOrder> copy_of_endangeredSTO =  endangeredSTO;
		LinkedList<SingleTaskOrder> copy_of_STOrderListWorkStation3 = STOrderListWorkStation3;
		LinkedList<SingleTaskOrder> copy_of_STOrderListWorkStation1 =  STOrderListWorkStation1;
		LinkedList<Order> copy_of_sList =  sList;

		//initialiseer enkele dingen.
		ArrayList<ScheduledOrder> scheduledList = new ArrayList<ScheduledOrder>();
		AssemblyLine assemblyLine = assemblyLineScheduler.getAssemblyLine();
		
		boolean isScheduled = false;
		//zolang het schedulen niet is gelukt, blijf het herhalen.
		while(!isScheduled){
			//kopieer alles uit de lijst zodat je later opnieuw kan beginnen.
			endangeredSTO = (ArrayList<SingleTaskOrder>) copy_of_endangeredSTO.clone();
			STOrderListWorkStation3 = (LinkedList<SingleTaskOrder>) copy_of_STOrderListWorkStation3.clone();
			STOrderListWorkStation1 = (LinkedList<SingleTaskOrder>) copy_of_STOrderListWorkStation1.clone();
			sList = (LinkedList<Order>) copy_of_sList.clone();
			//plaats de STO's die ingevaar zijn vooraan in de queue van orders
			sList.addAll(0, endangeredSTO);
			
			//lijst met alle singleTaskOrders verdeeld over de verschillende workstations
			//aanmaak zal later afhankelijk van workstation gebeuren.
			ArrayList<LinkedList<SingleTaskOrder>> singleTasks = new ArrayList<LinkedList<SingleTaskOrder>>();
			singleTasks.add(STOrderListWorkStation1);
			singleTasks.add(new LinkedList<SingleTaskOrder>());
			singleTasks.add(STOrderListWorkStation3);

			//assembly represents the AssemblyLine with 3 workstations. Contains null if workstation would be empty.
			LinkedList<Order> assembly = new LinkedList<Order>(assemblyLine.getAllOrders());
			//kopieer de tijd naar movingTime zodat je later opnieuw kan beginnen.
			GregorianCalendar movingTime = (GregorianCalendar) allTasksCompletedTime.clone();

			scheduledList = new ArrayList<ScheduledOrder>();
			try{
				//Blijf dit doen zolang er orders zijn en zolang de assemblyLine niet leeg is
				while(!(sList.isEmpty() && STOrderListWorkStation3.isEmpty() && STOrderListWorkStation1.isEmpty() && this.isEmptyAssembly(assembly))){
					
					if(!sList.isEmpty()){
						ArrayList<Order> orderForToday = new ArrayList<Order>();
						this.beginOfDay(assembly, movingTime, scheduledList, sList, singleTasks, assemblyLine, assemblyLineScheduler);
						this.prepareEndOfDay(assembly, orderForToday, singleTasks);
						this.middleOfDay(assembly, movingTime, orderForToday, scheduledList, sList, assemblyLine, assemblyLineScheduler);
						this.endOfDay(assembly, movingTime, orderForToday, scheduledList, singleTasks, assemblyLine, assemblyLineScheduler);
					}
					if(sList.isEmpty()){
						this.onlySingleTasks(assembly, movingTime, scheduledList, singleTasks, assemblyLine, assemblyLineScheduler);
					}
					movingTime = this.nextDay(movingTime);
				}
			}
			//wanneer er een order voorbij zijn deadline gaat
			catch(DeadlineErrorException exc){
				//sla het order op bij de orders die in gevaar zijn.
				copy_of_endangeredSTO.add(exc.getSingleTaskOrder());
				//verwijder het order in gevaar uit de lijst, (beide omdat we niet weten uit welke hij komt.
				copy_of_STOrderListWorkStation3.remove(exc.getSingleTaskOrder());
				copy_of_STOrderListWorkStation1.remove(exc.getSingleTaskOrder());
				//begin opnieuw
				continue;
			}
			//als je hier komt, is het schedulen gelukt!
			isScheduled = true;
		}
		return scheduledList;
	}


	/**
	 * If it's the begin of the day, this method will add SingleTaskOrders with
	 * to the assemblyLine until there is only one spot left for an Order.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param scheduledList
	 * 
	 * @param sList
	 * 
	 * @param sTOrderListWorkStation3
	 * 
	 * @param sTOrderListWorkStation1
	 * 
	 * @param assemblyLine
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @throws DeadlineErrorException
	 * 
	 */
	private void beginOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<ScheduledOrder> scheduledList,
			LinkedList<Order> sList,
			ArrayList<LinkedList<SingleTaskOrder>> singleTasks,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {
		
		if(!isBeginOfDay(movingTime)){
			return;
		}
		
		//zoek hoeveel singles er al zijn
		int count = 0;
		for(; count < assembly.size(); count++){
			if(assembly.get(count) == null){
				break;
			}
		}
		
		//voeg de singleTaskOrders toe tot er nog 1 plaats over is.
		count = assembly.size() -1 - count;
		int number = findNotEmptyList(singleTasks, count, 1);
		while(count > 0 && number > 0 && this.singleTask3Possible(assembly, movingTime, singleTasks.get(number), assemblyLineScheduler, assemblyLine)){
			Order order = null;
			if(number >= 0){
				order = singleTasks.get(number).removeFirst();
			}
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, true);
			count--;
			number = findNotEmptyList(singleTasks, count, 1);
		}
		
	}

	/**
	 * Find the index of the first list in listOfLists that isn't empty.
	 * Start searching at index 'number' and change the index using the 'changeAmount'.
	 * 
	 * @param listOfLists
	 * 
	 * @param number
	 * 
	 * @param movingAmount
	 * 
	 * @return
	 */
	private int findNotEmptyList(
			ArrayList<LinkedList<SingleTaskOrder>> listOfLists, int number,
			int movingAmount) {
		while(number >=0 && number < listOfLists.size()){
			if(!listOfLists.get(number).isEmpty()){
				return number;
			}
			number += movingAmount;
		}
		return -1;
	}

	/**
	 * Checks if the given time is the begin of a day.
	 * 
	 * @param time
	 * 
	 * @return True if movingTime is at 6h00.
	 */
	private boolean isBeginOfDay(GregorianCalendar time) {

		if(time.get(GregorianCalendar.HOUR_OF_DAY) != 6){
			return false;
		}
		if(time.get(GregorianCalendar.MINUTE) != 0){
			return false;
		}
		return true;
	}

	/**
	 * Checks if it is still possible to add a SingleTaskOrder of
	 * sTOrderListWorkStation to assembly wthout having to update
	 * movingTime.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param sTOrderListWorkstation
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @param assemblyLine
	 * 
	 * @return
	 */
	private boolean singleTask3Possible(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			LinkedList<SingleTaskOrder> sTOrderListWorkstation,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		if(sTOrderListWorkstation.isEmpty()){
			return false;
		}
		if(assemblyLine.calculateTimeTillAdvanceFor(assembly) > 0){
			return false;
		}
		
		return this.orderPossible(assembly, movingTime, sTOrderListWorkstation.getFirst(), assemblyLineScheduler, assemblyLine);
	}
	
	/**
	 * Fill orderForToday with the SingleTaskOrders that will be put on the assemblyLine at the end of the day.
	 * 
	 * @param assembly
	 * 
	 * @param orderForToday
	 * 
	 * @param singleTasks
	 * 
	 */
	private void prepareEndOfDay(LinkedList<Order> assembly,
			ArrayList<Order> orderForToday,
			ArrayList<LinkedList<SingleTaskOrder>> singleTasks) {
		
		for(int number = assembly.size() - 2; number >= 0 ; number--){
			int position = this.findNotEmptyList(singleTasks, number, -1);
			if(position < 0){
				break;
			}
			SingleTaskOrder order = null;
			for(int i = position; order == null && i >= 0;i--){
				for(SingleTaskOrder sto : singleTasks.get(i)){
					if(!orderForToday.contains(sto)){
						order = sto;
						break;
					}
				}
			}
			orderForToday.add(order);
		}
	}

	/**
	 * This method adds orders from sList to assembly as long as there is time left for the orders in orderForToday.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param orderForToday
	 * 
	 * @param scheduledList
	 * 
	 * @param sList
	 * 
	 * @param assemblyLine
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @throws DeadlineErrorException
	 * 
	 */
	private void middleOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<Order> orderForToday, 
			ArrayList<ScheduledOrder> scheduledList,
			LinkedList<Order> sList,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {
		if(sList.isEmpty()){
			return;
		}
		
		orderForToday.add(0,sList.getFirst());
		while(this.ordersPossible(assembly, movingTime, orderForToday, assemblyLineScheduler, assemblyLine)){
			Order order = sList.removeFirst();
			orderForToday.remove(0);
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, false);
			if(sList.isEmpty()){
				return;
			}
			orderForToday.add(0,sList.getFirst());
		}
		orderForToday.remove(0);
	}

	/**
	 * Checks if it is still possible to add all the orders in orderForToday to assembly.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param orderForToday
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @param assemblyLine
	 * 
	 * @return
	 */
	private boolean ordersPossible(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<Order> orderForToday,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		
		ArrayList<Order> ordersToBeScheduled = (ArrayList<Order>) orderForToday.clone();
		LinkedList<Order> tempAssembly = (LinkedList<Order>) assembly.clone();
		GregorianCalendar tempTime = (GregorianCalendar) movingTime.clone();
		
		while(!ordersToBeScheduled.isEmpty()){
			tempAssembly.removeLast();
			tempAssembly.addFirst(ordersToBeScheduled.remove(0));
			int duration = assemblyLine.calculateTimeTillEmptyFor(tempAssembly);
			if(!this.checkEnoughTimeLeftFor(tempTime, duration, assemblyLineScheduler))
				return false;
			tempTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(tempAssembly));
		}
		return true;
	}
	
	/**
	 * Advances assembly, adds a ScheduledOrder to scheduledList and updates the time.
	 * Throws an exception if there is a problem with a deadline.
	 * 
	 * @param assembly
	 * 
	 * @param order
	 * 
	 * @param movingTime
	 * 
	 * @param scheduledList
	 * 
	 * @param assemblyLine
	 * 
	 * @param throwException
	 * 		This variable indicates if the exception should be thrown if there is a problem with a deadline.
	 * @throws DeadlineErrorException
	 * 
	 */
	private void advanceAssembly(LinkedList<Order> assembly, Order order,
			GregorianCalendar movingTime,
			ArrayList<ScheduledOrder> scheduledList,
			AssemblyLine assemblyLine,
			boolean throwException) throws DeadlineErrorException {
		//haal de laatste order van de assemblyLine
		Order doneOrder = assembly.removeLast();
		
		if(throwException && doneOrder instanceof SingleTaskOrder){
			SingleTaskOrder singleTask = (SingleTaskOrder) doneOrder;
			if(movingTime.after(singleTask.getDeadLine())){
				throw new DeadlineErrorException(singleTask);
			}
		}
		
		//Zet volgende op assembly
		assembly.addFirst(order);
		
		// voeg een scheduledOrder toe, movingTime is het moment dat de order op de AssemblyLine gaat.
		scheduledList.add(new ScheduledOrder(movingTime,order));
		//verschuif tijd totdat alle workstations klaar zijn.
		movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
	}

	/**
	 * Add all the orders in orderForToday to assembly if they can be done today. 
	 * Afterwards it empties assembly.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param orderForToday
	 * 
	 * @param scheduledList
	 * 
	 * @param singleTasks
	 * 
	 * @param assemblyLine
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @throws DeadlineErrorException
	 * 
	 */
	private void endOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<Order> orderForToday,
			ArrayList<ScheduledOrder> scheduledList,
			ArrayList<LinkedList<SingleTaskOrder>> singleTasks,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {

		while(!orderForToday.isEmpty()){
			Order order = orderForToday.remove(0);
			
			if(!this.orderPossible(assembly, movingTime, order, assemblyLineScheduler, assemblyLine)){
				continue;
			}
			else{
				for(LinkedList<SingleTaskOrder> list : singleTasks){
					if(list.remove(order)){
						break;
					}
				}
			}
			
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, true);
		}
		

		while(!this.isEmptyAssembly(assembly)){
			this.advanceAssembly(assembly, null, movingTime, scheduledList, assemblyLine, true);
		}
	}

	/**
	 * Checks if it is still possible to add the given Order to assembly without creating overtime.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param sTOrderListWorkStation1
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @param assemblyLine
	 * 
	 * @return
	 */
	private boolean orderPossible(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			Order order,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		
		
		ArrayList<Order> oneOrderList = new ArrayList<Order>();
		oneOrderList.add(order);
		return this.ordersPossible(assembly, movingTime, oneOrderList, assemblyLineScheduler, assemblyLine);
	}

	/**
	 * Adds SingleTaskOrders from singleTasks to assembly as long as they can be done today.
	 * Always takes the first order from each list in singleTasks while traversing the lists of lists in reverse order.
	 * 
	 * @param assembly
	 * 
	 * @param movingTime
	 * 
	 * @param scheduledList
	 * 
	 * @param singleTasks
	 * 
	 * @param assemblyLine
	 * 
	 * @param assemblyLineScheduler
	 * 
	 * @throws DeadlineErrorException
	 * 
	 */
	private void onlySingleTasks(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			ArrayList<ScheduledOrder> scheduledList,
			ArrayList<LinkedList<SingleTaskOrder>> singleTasks,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {
		
		int noAddition = 0;
		int workstation = assembly.size()-1;
		while(noAddition < assembly.size()){
			LinkedList<SingleTaskOrder> list = singleTasks.get(workstation);
			if(!list.isEmpty() && this.orderPossible(assembly, movingTime, list.getFirst(), assemblyLineScheduler, assemblyLine)){
				Order order = list.removeFirst();
				this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, true);
			}
			else{
				noAddition++;
			}
			workstation--;
			if(workstation < 0){
				workstation = assembly.size() - 1;
			}
		}
		
		while(!this.isEmptyAssembly(assembly)){
			this.advanceAssembly(assembly, null, movingTime, scheduledList, assemblyLine, true);
		}
		
	}
	
	/**
	 * Checks if the given list of Orders only contains null.
	 * 
	 * @param assembly
	 * 		The list that needs to be checked.
	 * @return True if assembly only contains null, false otherwise.
	 */
	private boolean isEmptyAssembly(LinkedList<Order> assembly) {
		for(Order order : assembly){
			if(order != null){
				return false;
			}
		}
		return true;
	}


	/**
	 * Makes a GregorianCalendar which represents the beginning of the first workday after the given calendar.
	 * 
	 * @param currentTime
	 * 		The current time and date.
	 * @return The GregorianCalendar representing the beginning of the first workday after the day in currentTime. 
	 */
	private GregorianCalendar nextDay(GregorianCalendar currentTime) {
		GregorianCalendar nextDay = (GregorianCalendar) currentTime.clone();
		if (currentTime.get(GregorianCalendar.HOUR_OF_DAY) > 6)
			nextDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
		nextDay.set(GregorianCalendar.HOUR_OF_DAY, AssemblyLineScheduler.BEGIN_OF_DAY);
		nextDay.set(GregorianCalendar.MINUTE, 0);
		nextDay.set(GregorianCalendar.SECOND, 0);
		return nextDay;
	}

	/**
	 * Checks if there is enough minutes left in the workday after the given calendar before the workday ends.
	 * 
	 * @param currentTime
	 * 		The time it currently is.
	 * @param minutes
	 * 		The amount of minutes for which will be checked.
	 * @param assemblyLineScheduler
	 * 		The assembly line scheduler used to check the real end of day time (overtime taken into account).
	 * @return True if there are enough minutes left after the given calendar before the end of the workday, false otherwise.
	 */
	private boolean checkEnoughTimeLeftFor(
			GregorianCalendar currentTime, int minutes, AssemblyLineScheduler assemblyLineScheduler) {
		GregorianCalendar endOfDay;
		if(this.sameDayAsScheduler(currentTime, assemblyLineScheduler)){
			endOfDay = assemblyLineScheduler.getRealEndOfDay();
		}
		else{
			endOfDay = new GregorianCalendar(
					currentTime.get(GregorianCalendar.YEAR),
					currentTime.get(GregorianCalendar.MONTH),
					currentTime.get(GregorianCalendar.DAY_OF_MONTH),
					AssemblyLineScheduler.END_OF_DAY,
					0,
					0);
		}
		GregorianCalendar time = (GregorianCalendar) currentTime.clone();
		time.add(GregorianCalendar.MINUTE, minutes);

		return !endOfDay.before(time);
	}

	/**
	 * Checks whether the given current time is on the same work day as the assembly line scheduler's current time.
	 * 
	 * @param currentTime
	 * 		The current time to be checked.
	 * @param assemblyLineScheduler
	 * 		The assembly line scheduler to be checked.
	 * @return True if the given current time is on the same work day as the assembly line scheduler's current time, otherwise false.
	 */
	private boolean sameDayAsScheduler(GregorianCalendar currentTime, AssemblyLineScheduler assemblyLineScheduler) {
		GregorianCalendar schedulerTime = assemblyLineScheduler.getCurrentTime();
		if(schedulerTime.get(GregorianCalendar.YEAR) == currentTime.get(GregorianCalendar.YEAR)
				&& schedulerTime.get(GregorianCalendar.MONTH) == currentTime.get(GregorianCalendar.MONTH)
				&& schedulerTime.get(GregorianCalendar.DAY_OF_MONTH) == currentTime.get(GregorianCalendar.DAY_OF_MONTH)){
			return true;
		}

		GregorianCalendar nextDayTime = this.nextDay(schedulerTime);

		if(currentTime.before(nextDayTime)
				&& nextDayTime.get(GregorianCalendar.YEAR) == currentTime.get(GregorianCalendar.YEAR)
				&& nextDayTime.get(GregorianCalendar.MONTH) == currentTime.get(GregorianCalendar.MONTH)
				&& nextDayTime.get(GregorianCalendar.DAY_OF_MONTH) == currentTime.get(GregorianCalendar.DAY_OF_MONTH)){
			return true;
		}

		return false;
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

	private class DeadlineErrorException extends Exception{
		private static final long serialVersionUID = 1L;
		private SingleTaskOrder singleTaskOrder;
		
		private DeadlineErrorException(SingleTaskOrder singleTask){
			this.singleTaskOrder = singleTask;
		}
		
		private SingleTaskOrder getSingleTaskOrder(){
			return this.singleTaskOrder;
		}
		
	}

}
