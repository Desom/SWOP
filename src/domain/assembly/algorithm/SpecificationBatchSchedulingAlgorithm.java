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
	
	public SpecificationBatchSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm, Configuration batchConfiguration) {
		this.batchConfiguration = batchConfiguration;
		this.innerAlgorithm = innerAlgorithm;
	}


	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {

		ArrayList<Order> batchList = new ArrayList<Order>();
		ArrayList<Order> standardList = new ArrayList<Order>();
		
		for(Order order : orderList){
			if(this.batchConfiguration.equals(order.getConfiguration())){
				batchList.add(order);
			}
			else{
				standardList.add(order);
			}
		}
		

		ArrayList<Order> orderedList = this.innerAlgorithm.scheduleToList(batchList, assemblyLineSchedule);
		
		orderedList.addAll(this.innerAlgorithm.scheduleToList(standardList, assemblyLineSchedule));
		return orderedList;
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
			if(!this.checkEnoughTimeLeftFor(movingTime, totalDuration, assemblyLineScheduler)){
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



	/**
	 * Checks if the given list of Orders only contains null.
	 * 
	 * @param assembly
	 * 		The list that needs to be checked.
	 * @return True if assembly only contains null, false otherwise.
	 */
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
	 * @param currentTime
	 * 		The current time and date.
	 * @return The GregorianCalendar representing the beginning of the first workday after the day in currentTime. 
	 */
	private GregorianCalendar nextDay(GregorianCalendar currentTime) {
		//TODO zijn er nog uitzonderlijke gevallen?
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

		return time.before(endOfDay);
	}


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
}
