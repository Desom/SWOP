package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.SingleTaskOrder;

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


			//assembly represents the AssemblyLine with 3 workstations. Contains null if workstation would be empty.
			LinkedList<Order> assembly = new LinkedList<Order>(assemblyLine.getAllOrders());
			//kopieer de tijd naar movingTime zodat je later opnieuw kan beginnen.
			GregorianCalendar movingTime = (GregorianCalendar) allTasksCompletedTime.clone();

			scheduledList = new ArrayList<ScheduledOrder>();
			try{
				//Blijf dit doen zolang er orders zijn en zolang de assemblyLine niet leeg is
				while(!(sList.isEmpty() && STOrderListWorkStation3.isEmpty() && STOrderListWorkStation1.isEmpty() && this.isEmptyAssembly(assembly))){
					this.beginOfDay(assembly, movingTime, scheduledList, sList, STOrderListWorkStation3, STOrderListWorkStation1, assemblyLine, assemblyLineScheduler);
					this.middleOfDay(assembly, movingTime, scheduledList, sList, STOrderListWorkStation3, STOrderListWorkStation1, assemblyLine, assemblyLineScheduler);
					this.endOfDay(assembly, movingTime, scheduledList, sList, STOrderListWorkStation3, STOrderListWorkStation1, assemblyLine, assemblyLineScheduler);
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
	 * Option Seat to the assemblyLine until work has to be done on an order or
	 * there are no more.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param scheduledList
	 * @param sList
	 * @param sTOrderListWorkStation3
	 * @param sTOrderListWorkStation1
	 * @param assemblyLine
	 * @param assemblyLineScheduler
	 * @throws DeadlineErrorException
	 */
	private void beginOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<ScheduledOrder> scheduledList,
			LinkedList<Order> sList,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation3,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation1,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {
		
		while(this.singleTask3Possible(assembly, movingTime, sTOrderListWorkStation3, assemblyLineScheduler, assemblyLine)){
			Order order = sTOrderListWorkStation3.removeFirst();
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, true);
		}
		
	}

	/**
	 * Checks if it is still possible to add a SingleTaskOrder of
	 * sTOrderListWorkStation3 to assembly wthout having to update
	 * movingTime.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param sTOrderListWorkStation3
	 * @param assemblyLineScheduler
	 * @param assemblyLine
	 * @return
	 */
	private boolean singleTask3Possible(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation3,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		if(sTOrderListWorkStation3.isEmpty()){
			return false;
		}
		if(assemblyLine.calculateTimeTillAdvanceFor(assembly) > 0){
			return false;
		}
		
		LinkedList<Order> tempAssembly = (LinkedList<Order>) assembly.clone();
		tempAssembly.removeLast();
		tempAssembly.addFirst(sTOrderListWorkStation3.getFirst());
		int duration = assemblyLine.calculateTimeTillEmptyFor(tempAssembly);
		return this.checkEnoughTimeLeftFor(movingTime, duration, assemblyLineScheduler);
	}

	/**
	 * This method adds orders from sList to assembly as long as there is time
	 * enough during the day. When sList is empty, it starts adding orders form
	 * sTOrderListWorkStation3 and sTOrderListWorkStation1. If possible this
	 * happens in pairs as they are more efficient.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param scheduledList
	 * @param sList
	 * @param sTOrderListWorkStation3
	 * @param sTOrderListWorkStation1
	 * @param assemblyLine
	 * @param assemblyLineScheduler
	 * @throws DeadlineErrorException
	 */
	private void middleOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<ScheduledOrder> scheduledList,
			LinkedList<Order> sList,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation3,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation1,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {
		
		while(this.orderPossible(assembly, movingTime, sList, assemblyLineScheduler, assemblyLine)){
			Order order = sList.removeFirst();
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, false);
		}
		
		if(!sList.isEmpty()){
			return;
		}
		
		while(this.towSTOPossible(assembly, movingTime, sTOrderListWorkStation3, sTOrderListWorkStation1, assemblyLineScheduler, assemblyLine)){
			if(!sTOrderListWorkStation3.isEmpty()){
				this.advanceAssembly(assembly, sTOrderListWorkStation3.removeFirst(), movingTime, scheduledList, assemblyLine, true);
				if(!sTOrderListWorkStation1.isEmpty()){
					this.advanceAssembly(assembly, null, movingTime, scheduledList, assemblyLine, true);
				}
			}
			if(!sTOrderListWorkStation1.isEmpty()){
				this.advanceAssembly(assembly, sTOrderListWorkStation1.removeFirst(), movingTime, scheduledList, assemblyLine, true);
			}
		}
		
	}


	/**
	 * Checks if it is still possible to add more SingleTaskOrders from
	 * sTOrderListWorkStation3 and sTOrderListWorkStation1 to assembly.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param sTOrderListWorkStation3
	 * @param sTOrderListWorkStation1
	 * @param assemblyLineScheduler
	 * @param assemblyLine
	 * @return
	 */
	private boolean towSTOPossible(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation3,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation1,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		if(sTOrderListWorkStation3.isEmpty() && sTOrderListWorkStation1.isEmpty()){
			return false;
		}
		
		LinkedList<Order> tempAssembly = (LinkedList<Order>) assembly.clone();
		int duration = 0;
		if(!sTOrderListWorkStation3.isEmpty()){
			tempAssembly.removeLast();
			tempAssembly.addFirst(sTOrderListWorkStation3.getFirst());
			duration += assemblyLine.calculateTimeTillAdvanceFor(tempAssembly);
		}

		if(!sTOrderListWorkStation3.isEmpty() && !sTOrderListWorkStation1.isEmpty()){
		tempAssembly.removeLast();
		tempAssembly.addFirst(null);
		duration += assemblyLine.calculateTimeTillAdvanceFor(tempAssembly);
		}
		

		if(!sTOrderListWorkStation1.isEmpty()){
			tempAssembly.removeLast();
			tempAssembly.addFirst(sTOrderListWorkStation1.getFirst());
			duration += assemblyLine.calculateTimeTillEmptyFor(tempAssembly);
		}
		return this.checkEnoughTimeLeftFor(movingTime, duration, assemblyLineScheduler);
	}

	/**
	 * Checks if it is still possible to add an order from sList to assembly.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param sList
	 * @param assemblyLineScheduler
	 * @param assemblyLine
	 * @return
	 */
	private boolean orderPossible(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			LinkedList<Order> sList,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {

		if(sList.isEmpty()){
			return false;
		}
		
		LinkedList<Order> tempAssembly = (LinkedList<Order>) assembly.clone();
		tempAssembly.removeLast();
		tempAssembly.addFirst(sList.getFirst());
		int duration = assemblyLine.calculateTimeTillEmptyFor(tempAssembly);
		return this.checkEnoughTimeLeftFor(movingTime, duration, assemblyLineScheduler);
	}
	
	/**
	 * Advances assembly, adds a ScheduledOrder to scheduledList and updates the time.
	 * Throws an exception if there is a problem with a deadline.
	 * 
	 * @param assembly
	 * @param order
	 * @param movingTime
	 * @param scheduledList
	 * @param assemblyLine
	 * @param throwException
	 * 		This variable indicates if the exception should be thrown if there is a problem with a deadline.
	 * @throws DeadlineErrorException
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
	 * Keeps adding SingleTaskOrders with Option Color untill there isn't enough time left to finish them.
	 * Afterwards it empties assembly.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param scheduledList
	 * @param sList
	 * @param sTOrderListWorkStation3
	 * @param sTOrderListWorkStation1
	 * @param assemblyLine
	 * @param assemblyLineScheduler
	 * @throws DeadlineErrorException
	 */
	private void endOfDay(LinkedList<Order> assembly,
			GregorianCalendar movingTime, 
			ArrayList<ScheduledOrder> scheduledList,
			LinkedList<Order> sList,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation3,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation1,
			AssemblyLine assemblyLine,
			AssemblyLineScheduler assemblyLineScheduler) throws DeadlineErrorException {

		while(!this.isEmptyAssembly(assembly)){
			Order order = null;
			if(this.singleTask1Possible(assembly, movingTime, sTOrderListWorkStation1, assemblyLineScheduler, assemblyLine)){
				order = sTOrderListWorkStation1.removeFirst();
			}
			this.advanceAssembly(assembly, order, movingTime, scheduledList, assemblyLine, true);
		}
		

		while(!this.isEmptyAssembly(assembly)){
			this.advanceAssembly(assembly, null, movingTime, scheduledList, assemblyLine, true);
		}
	}

	/**
	 * Checks if it is still possible to add a SingleTaskOrder from
	 * sTOrderListWorkStation1 to assembly without creating overtime.
	 * 
	 * @param assembly
	 * @param movingTime
	 * @param sTOrderListWorkStation1
	 * @param assemblyLineScheduler
	 * @param assemblyLine
	 * @return
	 */
	private boolean singleTask1Possible(LinkedList<Order> assembly,
			GregorianCalendar movingTime,
			LinkedList<SingleTaskOrder> sTOrderListWorkStation1,
			AssemblyLineScheduler assemblyLineScheduler,
			AssemblyLine assemblyLine) {
		
		if(sTOrderListWorkStation1.isEmpty()){
			return false;
		}		
		LinkedList<Order> tempAssembly = (LinkedList<Order>) assembly.clone();
		tempAssembly.removeLast();
		tempAssembly.addFirst(sTOrderListWorkStation1.getFirst());
		int duration = assemblyLine.calculateTimeTillEmptyFor(tempAssembly);
		return this.checkEnoughTimeLeftFor(movingTime, duration, assemblyLineScheduler);
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
		return order.getConfiguration().getAllOptions().get(0).getType();
	}

	public String toString(){
		return "test";
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
