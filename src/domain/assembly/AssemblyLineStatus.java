package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.order.Order;

public abstract class AssemblyLineStatus {

	protected StatusCreator creator;
	
	/**
	 * Constructor of AssemblyLineStatus
	 * 
	 * @param creator
	 * 		The assembly line status creator used to get other statuses.
	 */
	public AssemblyLineStatus(StatusCreator creator) {
		this.creator = creator;
	}
	
	/**
	 * Advances the assembly depending on its status.
	 * @param assemblyLine
	 * 		The assembly line to be advanced.
	 * @throws CannotAdvanceException
	 * 		If the assembly line cannot advance.
	 */
	public abstract void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException;
	
	/**
	 * Checks whether the assembly line can advance.
	 * 
	 * @param assemblyLine
	 * 		The assembly line to be checked.
	 * @return True if the assembly line can be advanced, otherwise false.
	 */
	public abstract Boolean canAdvanceLine(AssemblyLine assemblyLine);
	
	/**
	 * Checks whether the assembly line can accept new orders.
	 * 
	 * @return True if the assembly line can accept new orders, otherwise false.
	 */
	public abstract Boolean canAcceptNewOrders();
	
	/**
	 * Returns the state of the assembly line when it is accepting orders again.
	 * 
	 * @param assemblyLine
	 * 		The assembly line of which the state is requested.
	 * @return The state of the assembly line when it is accepting orders again.
	 */
	public abstract LinkedList<Order> stateWhenAcceptingOrders(AssemblyLine assemblyLine);
	
	/**
	 * Returns the time when the assembly line will accept new orders again.
	 * 
	 * @param assemblyLine
	 * 		The assembly line to be checked.
	 * @return The time when the assembly line will accept new orders again.
	 */
	public abstract GregorianCalendar timeWhenAcceptingOrders(AssemblyLine assemblyLine);
	
	/**
	 * Returns the time when the assembly line will be empty with the given state of orders.
	 * 
	 * @param assemblyLine
	 * 		The assembly line to be checked.
	 * @param assembly
	 * 		The state of orders of the assembly line to be checked.
	 * @return The time when the assembly line will be empty.
	 */
	public abstract int calculateTimeTillEmptyFor(AssemblyLine assemblyLine, LinkedList<Order> assembly);
}
