package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.CarOrder;
import domain.order.Order;

public class FIFOSchedulingAlgorithm implements SchedulingAlgorithm {
	
	private Comparator<Order> comparator;
	
	public FIFOSchedulingAlgorithm() {
		comparator = new Comparator<Order>(){
			@Override
			public int compare(Order order1, Order order2){
				return order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
	}


	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		//TODO hier mss de gekregen list eerst kopieren?
				Collections.sort(orderList, this.comparator);
				return orderList;
	}

	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineScheduler) {
		
		//assembly represents the AssemblyLine with 3 workstations. Contains null if workstation would be empty.
		LinkedList<Order> assembly = new LinkedList<Order>(assemblyLineScheduler.getAssemblyLine().getAllOrders());
		ArrayList<Order> sList = this.scheduleToList(orderList, assemblyLineScheduler);
		
		GregorianCalendar movingTime = assemblyLineScheduler.getCurrentTime();

		ArrayList<ScheduledOrder> scheduledList = new ArrayList<ScheduledOrder>();
		
		//Simuleer heel het toekomstig proces.
		for(Order order : sList){
			//verschuif tijd totdat alle workstations klaar zijn.
			movingTime.add(GregorianCalendar.MINUTE, this.findDuration(assembly));
			//haal de laatste order uit de lijst als er een achteraan staat.
			if(assembly.size() == 3){
				assembly.remove(2);
			}
			//zoek hoelang het minimaal zal duren om deze order af te maken. hier wordt veronderstelt dat het een CarOrder is.
			//TODO voor alle orders bruikbaar maken?
			int totalDuration = this.findTotalDurationFor(order,assembly);
			//Controleer ofdat er nog genoeg tijd is om deze order af te maken.
			if(this.checkEnoughTimeLeftFor(movingTime,totalDuration)){
				//Ja, genoeg tijd. Voeg de order vooraan toe.
				assembly.addFirst(order);
			}
			else{
				//Nee, tijd te kort. Simuleer leeg maken aan het einde van de dag.
				while(assembly.size()!= 0){
					assembly.remove();
					assembly.addFirst(null);
				}
				// Voeg de order voorraan toe en zet de time op het begin van de volgende dag.
				assembly.addFirst(order);
				movingTime = this.nextDay(movingTime);
			}

			// voeg een scheduledOrder toe, movingTime is het moment dat de order op de AssemblyLine gaat.
			scheduledList.add(new ScheduledOrder(movingTime,order));
		}
		
		return scheduledList;
	}


	
	/**
	 * Calculates the minimum time necessary to do the given Order, based on the given assemblyLine.
	 * 
	 * @param order
	 * 		The Order that will go on the assemblyLine.
	 * @param assembly
	 * 		A list of Orders containing the 2 orders that went on the assemblyLine before it. ( null if there were none before order)
	 * @return The calculated amount of minutes it will take to finish the given Order while considering the other Orders in assembly. 
	 */
	private int findTotalDurationFor(Order order, LinkedList<Order> assembly) {
		int duration = 0;
		int timeForOrder = order.getConfiguration().getExpectedWorkingTime();
		
		duration += Math.max(timeForOrder, this.findDuration(assembly));
		if(assembly.getFirst() != null){
			duration += Math.max(timeForOrder, assembly.getFirst().getConfiguration().getExpectedWorkingTime());		
		}
		duration += timeForOrder;
		return duration;
	}


	/**
	 * Makes a GregorianCalendar which represents the beginning of the first workday after the given calendar.
	 * 
	 * @param calendar
	 * 		The current time and date.
	 * @return The GregorianCalendar representing the beginning of the first workday after the day in calendar. 
	 */
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

	/**
	 * Checks if there is enough minutes left in the workday after the given calendar before the workday ends.
	 * 
	 * @param calendar
	 * 		The time it currently is.
	 * @param minutes
	 * 		The amount of minutes for which will be checked.
	 * @return True if there are enough minutes left after the given calendar before the end of the workday, false otherwise.
	 */
	private boolean checkEnoughTimeLeftFor(
			GregorianCalendar calendar, int minutes) {
		//TODO hier moet ergens overtime worden behandeld...
		
		GregorianCalendar endOfDay = new GregorianCalendar(
				calendar.get(GregorianCalendar.YEAR),
				calendar.get(GregorianCalendar.MONTH),
				calendar.get(GregorianCalendar.DAY_OF_MONTH),
				AssemblyLineScheduler.END_OF_DAY,
				0,
				0);

		GregorianCalendar time = (GregorianCalendar) calendar.clone();
		time.add(GregorianCalendar.MINUTE, minutes);
		
		return time.before(endOfDay);
	}


	/**
	 * Calculates the estimated amount of minutes it will take to complete all
	 * tasks on all workstations if the given Orders are on the assembleLine.
	 * 
	 * @param assembly
	 *            The Orders that will be on the assemblyLine.
	 * @return The amount of minutes it will take to comlete all tasks on all workstations.
	 */
	private int findDuration(LinkedList<Order> assembly) {
		int duration = 0;
		for(Order order : assembly){
			if(order != null){
				int temp = order.getConfiguration().getExpectedWorkingTime();
				if(temp > duration){
					duration = temp;
				}
			}
		}
		return duration;
	}

}
