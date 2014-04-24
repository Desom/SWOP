package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.Configuration;
import domain.order.Order;

public class SpecificationBatchSchedulingAlgorithm implements
		SchedulingAlgorithm {

	private Configuration batchConfiguration;
	private SchedulingAlgorithm innerAlgorithm;
	
	/**
	 * Constructor of SpecificationBatchSchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this specification batch algorithm.
	 */
	public SpecificationBatchSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.batchConfiguration = null;
		this.innerAlgorithm = innerAlgorithm;
	}
	
	/**
	 * Sets the configuration of this specification batch scheduling algorithm.
	 * 
	 * @param configuration
	 * 		The configuration which will have priority.
	 */
	public void setConfiguration(Configuration configuration){
		this.batchConfiguration = configuration;
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

		ArrayList<Order> batchList = new ArrayList<Order>();
		ArrayList<Order> standardList = new ArrayList<Order>();
		
		for(Order order : orderList){
			if(this.batchConfiguration != null && this.batchConfiguration.equals(order.getConfiguration())){
				batchList.add(order);
			}
			else{
				standardList.add(order);
			}
		}
		
		if(batchList.isEmpty()){
			assemblyLineScheduler.setSchedulingAlgorithmToDefault();
		}

		ArrayList<Order> orderedList = this.innerAlgorithm.scheduleToList(batchList, assemblyLineScheduler);
		
		orderedList.addAll(this.innerAlgorithm.scheduleToList(standardList, assemblyLineScheduler));
		return orderedList;
	}
	
	/**
	 * Schedules the given list of orders and returns a scheduled list of ScheduledOrder objects.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param allTasksCompletedTime
	 * 		The time by which all tasks have to be completed.
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled list of ScheduledOrder objects.
	 */
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			GregorianCalendar allTasksCompletedTime,
			AssemblyLineScheduler assemblyLineScheduler) {

		AssemblyLine assemblyLine = assemblyLineScheduler.getAssemblyLine();
		//assembly represents the AssemblyLine with 3 workstations. Contains null if workstation would be empty.
		LinkedList<Order> assembly = new LinkedList<Order>(assemblyLine.getAllOrders());
		ArrayList<Order> sList = this.scheduleToList(orderList, assemblyLineScheduler);
		GregorianCalendar movingTime = (GregorianCalendar) allTasksCompletedTime.clone();

		ArrayList<ScheduledOrder> scheduledList = new ArrayList<ScheduledOrder>();

		//Simuleer heel het toekomstig proces, waarbij aan het begin van de loop alle tasks completed zijn.
		for(Order order : sList){

			//haal de laatste order van de assemblyLine
			assembly.removeLast();
			//Zet volgende op assembly
			assembly.addFirst(order);
			//zoek hoelang het minimaal zal duren om deze order af te maken. hier wordt veronderstelt dat het een CarOrder is.
			int totalDuration = assemblyLine.calculateTimeTillEmptyFor(assembly);
			//Controleer ofdat er nog genoeg tijd is om deze order af te maken.
			if(!this.checkEnoughTimeLeftFor(movingTime, totalDuration, assemblyLineScheduler)){
				// haal order er weer af, omdat het toch niet gaat.
				assembly.removeFirst();
				// zet een null in de plaats
				assembly.addFirst(null);
				scheduledList.add(new ScheduledOrder(movingTime,null));
				movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
				
				//Simuleer leeg maken aan het einde van de dag.
				while(!this.isEmptyAssembly(assembly)){
					assembly.removeLast();
					assembly.addFirst(null);
					scheduledList.add(new ScheduledOrder(movingTime,null));
					movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
				}
				// Voeg de order voorraan toe en zet de time op het begin van de volgende dag.
				assembly.removeLast();
				assembly.addFirst(order);
				movingTime = this.nextDay(movingTime);
			}

			// voeg een scheduledOrder toe, movingTime is het moment dat de order op de AssemblyLine gaat.
			scheduledList.add(new ScheduledOrder(movingTime,order));
			//verschuif tijd totdat alle workstations klaar zijn.
			movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
		}
		
		//maak leeg wanneer er niets meer moet worden gescheduled.
		while(!this.isEmptyAssembly(assembly)){
			assembly.removeLast();
			assembly.addFirst(null);
			scheduledList.add(new ScheduledOrder(movingTime,null));
			movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
		}

		return scheduledList;
	}

	/**
	 * Checks if the given list of Orders only contains null.
	 * 
	 * @param assemblyLineOrders
	 * 		The list that needs to be checked.
	 * @return True if assembly only contains null, false otherwise.
	 */
	private boolean isEmptyAssembly(LinkedList<Order> assemblyLineOrders) {
		for(Order order : assemblyLineOrders){
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
	 * @return True if there are enough minutes left after the given calendar before the end of the workday, false otherwise.
	 */
	private boolean checkEnoughTimeLeftFor(
			GregorianCalendar currentTime, int minutes, AssemblyLineScheduler assemblyLineScheduler) {
		//TODO hier moet ergens overtime worden behandeld...
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
	private boolean sameDayAsScheduler(GregorianCalendar currentTime,
			AssemblyLineScheduler assemblyLineScheduler) {
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
	 * Searches for Configurations that can be executed in batch. 
	 * These Configurations must be used by more than 3 Orders.
	 * 
	 * @param assemblyLineScheduler
	 * 		The AssemblyLineScheduler from which the orders will be fetched.
	 * @return A list of Configuration which are all used by more than 3 orders.
	 */
	public ArrayList<Configuration> searchForBatchConfiguration(AssemblyLineScheduler assemblyLineScheduler){
		ArrayList<Configuration> batchList = new ArrayList<Configuration>();
		
		ArrayList<Order> orders = assemblyLineScheduler.getOrdersToBeScheduled();
		for(int i = 0; i < orders.size() - 2; i++){
			if(batchList.contains(orders.get(i).getConfiguration())){
				continue;
			}
			for(int j = i+1; j < orders.size() - 1; j++){
				if(!orders.get(i).getConfiguration().equals(orders.get(j).getConfiguration())){
					continue;
				}
				for(int k = j + 1; k < orders.size(); k++){
					if(orders.get(i).getConfiguration().equals(orders.get(k).getConfiguration())){
						if(!batchList.contains(orders.get(i).getConfiguration())){
							batchList.add(orders.get(i).getConfiguration());
						}
					}
				}
			}
		}
		
		return batchList;
	}
	
	@Override
	public String toString(){
		return "Specification Batch with " + this.innerAlgorithm.toString();
	}
}
