package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
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
			if(!this.checkEnoughTimeLeftFor(movingTime,totalDuration)){
				// haal order er weer af, omdat het toch niet gaat.
				assembly.removeFirst();
				// zet een null in de plaats
				assembly.addFirst(null);
				scheduledList.add(new ScheduledOrder(movingTime,null));

				//Simuleer leeg maken aan het einde van de dag.
				while(!this.isEmptyAssembly(assembly)){
					assembly.removeLast();
					assembly.addFirst(null);
					movingTime.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assembly));
					scheduledList.add(new ScheduledOrder(movingTime,null));
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
		
		return scheduledList;
	}



	private boolean isEmptyAssembly(LinkedList<Order> assembly) {
		// TODO Auto-generated method stub
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
}
