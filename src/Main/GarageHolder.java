package Main;

public class GarageHolder extends User {

	/**
	 * Contructor of GarageHolder.
	 * 
	 * @param	id
	 * 			id of this garage holder
	 */
	public GarageHolder(int id) {
		super(id);
		initializeApprovedMethods();
	}

	/**
	 * Initializes all methods (represented by strings) that this garage holder can use.
	 */
	private void initializeApprovedMethods() {
		this.approvedMethods.add("getOrders");
		this.approvedMethods.add("getAllModels");
		this.approvedMethods.add("placeOrder");
		this.approvedMethods.add("getCompletionEstimate");
		this.approvedMethods.add("getOrderManager");
		this.approvedMethods.add("getPendingOrders");
		this.approvedMethods.add("getCompletedOrders");
		this.approvedMethods.add("getCatalog");
		this.approvedMethods.add("completionEstimate");
	}
}
