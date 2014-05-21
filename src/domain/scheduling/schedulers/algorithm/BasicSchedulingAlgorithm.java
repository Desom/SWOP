package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import domain.assembly.assemblyline.AssemblyLine;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.FactoryScheduler;
import domain.scheduling.schedulers.ScheduledOrder;

public class BasicSchedulingAlgorithm
	extends AbstractAssemblyLineSchedulingAlgorithm  
	implements FactorySchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;

	/**
	 * Constructor of BasicFactorySchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this basic factory algorithm.
	 */
	public BasicSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	/**
	 * Assigns all the given orders to one of the AssemblyLineSchedulers of factoryScheduler.
	 * 
	 * @param orders
	 * 		The orders which will be assigned to an AssemblyLineScheduler.
	 * @param factoryScheduler
	 * 		The FactoryScheduler who wants to assign these orders to the 
	 * @return
	 * 		A mapping of AssemblyLineSchedulers and their assigned orders.
	 */
	@Override
	public HashMap<AssemblyLineScheduler, ArrayList<Order>> assignOrders(
			ArrayList<Order> orders, FactoryScheduler factoryScheduler) {

		ArrayList<Order> orderedOrders = this.innerAlgorithm.scheduleToList(orders, factoryScheduler);

		HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> scheduleMapping = new HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>>();

		ArrayList<AssemblyLineScheduler> schedulers = factoryScheduler.getSchedulerList();
		for(AssemblyLineScheduler scheduler : schedulers){
			scheduleMapping.put(scheduler, new ArrayList<ScheduledOrder>());
		}
		
		
		
		for(Order order: orderedOrders){
			AssemblyLineScheduler chosenScheduler = null;
			GregorianCalendar timeWithChosen = null;
			ArrayList<ScheduledOrder> newScheduleOfChosen = null;
			for(AssemblyLineScheduler scheduler : schedulers){
				if(!scheduler.canScheduleOrder(order)){
					continue;
				}
				//addToSchedule voegt dan order toe aan het huidige schedule, mss niet de beste methode?
				ArrayList<ScheduledOrder> newSchedule = scheduler.addToSchedule(scheduleMapping.get(scheduler),order);
				GregorianCalendar time = this.findTimeOf(order, newSchedule);
				if(chosenScheduler == null || time.before(timeWithChosen))
					chosenScheduler = scheduler;
					timeWithChosen = time;
					newScheduleOfChosen = newSchedule;
			}
			if(chosenScheduler != null){
				scheduleMapping.put(chosenScheduler, newScheduleOfChosen);
			}
			else{
				//TODO iets doen? Ja, 2 exceptions voor de 2 situaties
			}
		}

		return convertToMapping(scheduleMapping);
	}


	/**
	 * Finds the time order will put on the assemblyLine according to schedule.
	 * 
	 * @param order
	 * 		The order whose time is wanted.
	 * @param schedule
	 * 		The schedule which contains a ScheduledOrder with order.
	 * @return The time order will be put on the assemblyLine according to schedule. Null if order not in schedule.
	 */
	private GregorianCalendar findTimeOf(Order order,
			ArrayList<ScheduledOrder> schedule) {
		for(int i = schedule.size() - 1; i >= 0; i--){
			if (schedule.get(i).getScheduledOrder().equals(order)){
				return schedule.get(i).getScheduledTime();
			}
		}
		return null; //TODO goed?
	}

	/**
	 * Convert HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> 
	 * to HashMap<AssemblyLineScheduler, ArrayList<Order>>
	 * by replacing all ScheduledOrders with their Order.
	 * 
	 * @param scheduleMapping
	 * 		The mapping which has to be converted.
	 * @return A HashMap derived from scheduleMapping.
	 */
	private HashMap<AssemblyLineScheduler, ArrayList<Order>> convertToMapping(
			HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> scheduleMapping) {
		
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = new HashMap<AssemblyLineScheduler, ArrayList<Order>>();

		for(Entry<AssemblyLineScheduler, ArrayList<ScheduledOrder>> entry : scheduleMapping.entrySet()){
			ArrayList<Order> newList = new ArrayList<Order>();
			for(ScheduledOrder scheduledOrder : entry.getValue()){
				newList.add(scheduledOrder.getScheduledOrder());
			}
			mapping.put(entry.getKey(), newList);
		}
		
		return mapping;
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
			AssemblyLine assemblyLine) {

		
		//assembly represents the AssemblyLine with 3 workstations. Contains null if workstation would be empty.
		@SuppressWarnings("unchecked")
		LinkedList<Order> assembly = (LinkedList<Order>) assemblyLine.stateWhenAcceptingOrders().clone();
		ArrayList<Order> sList = this.innerAlgorithm.scheduleToList(orderList, assemblyLine.getAssemblyLineScheduler());
		GregorianCalendar movingTime = (GregorianCalendar) assemblyLine.timeWhenAcceptingOrders().clone();

		ArrayList<ScheduledOrder> scheduledList = new ArrayList<ScheduledOrder>();

		//Simuleer heel het toekomstig proces, waarbij aan het begin van de loop alle tasks completed zijn.
		for(Order order : sList){

			//haal de laatste order van de assemblyLine
			assembly.removeLast();
			//Zet volgende op assembly
			assembly.addFirst(order);
			//zoek hoelang het minimaal zal duren om deze order af te maken. hier wordt veronderstelt dat het een Order is.
			int totalDuration = assemblyLine.calculateTimeTillEmptyFor(assembly);
			//Controleer ofdat er nog genoeg tijd is om deze order af te maken.
			if(!this.checkEnoughTimeLeftFor(movingTime, totalDuration, assemblyLine.getAssemblyLineScheduler())){
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
	 * Returns the inner algorithm of this efficiency scheduling algorithm.
	 * 
	 * @return The inner algorithm of this efficiency scheduling algorithm.
	 */
	public SchedulingAlgorithm getInnerAlgorithm() {
		return innerAlgorithm;
	}
	
	public String toString(){
		return "Basic scheduling algorithm using " + this.innerAlgorithm.toString();
	}
}
