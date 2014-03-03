package Main;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;



public class OrderManager {

	//	TODO or not TODO ? private Company company:
	private ProductionSchedule productionSchedule;
	private HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;

	
	public OrderManager() {
		ArrayList<CarOrder> allCarOrders = this.createOrderList();
		this.setCarOrdersPerId(new HashMap<Integer,ArrayList<CarOrder>>());
		for(CarOrder order : allCarOrders) {
			this.addCarOrder(order);
		}
		
		this.createProductionSchedule(allCarOrders);
	}
	
	
	public ArrayList<CarOrder> getOrders(User user){
		if(user.canPerform("getOrders"))
		{
			return (ArrayList<CarOrder>) this.getCarOrdersPerId().get(user.getId()).clone();
		}
		else
		{
			return null;
		}
	}

	public void placeOrder(User user, CarModel model, Option[] options) throws Exception{
		if(user.canPerform("placeOrder"))
		{
			CarOrder newOrder = new CarOrder(user,model,options);
			this.addCarOrder(newOrder);
			this.getProductionSchedule().addOrder(newOrder);
		}
		else
		{
			throw new Exception();
		}
	}

	// TODO kies het type voor tijd
	public GregorianCalendar completionEstimate(User user, CarOrder order) throws Exception{
		if(user.canPerform("completionEstimate"))
		{
			return this.getProductionSchedule().completionEstimateCarOrder(order);
		}
		else
		{
			throw new Exception();
		}
	}

	private void createProductionSchedule(ArrayList<CarOrder> orderList){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(orderList);
		this.setProductionSchedule(newProductionSchedule);
		// TODO zorg dat de productionSchedule en een Assembly line gekoppelt worden.
		// Vraag: waar halen we de AssemblyLine vandaan?
	}

	private ArrayList<CarOrder> createOrderList(){
		ArrayList<CarOrder> allCarOrders = new ArrayList<CarOrder>();
		ArrayList<String> allCarOrderInfo = new ArrayList<String>();
		try {
			FileInputStream fStream = new FileInputStream("carOrderData.txt");
			DataInputStream dinStream = new DataInputStream(fStream);
			InputStreamReader insReader = new InputStreamReader(dinStream);
			BufferedReader bReader = new BufferedReader(insReader);
			String firstLine = bReader.readLine();
			String otherLine = bReader.readLine();
			while(!otherLine.startsWith("End")){
				allCarOrderInfo.add(otherLine);
				otherLine = bReader.readLine();
			}
			//TODO is multiple exceptions in 1 catcher possible?
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split("  ");
			// TODO string omvormen naar objecten
			// 0 : orderId
			// 1 : garageHolderId
			// 2 : isDelivered -> Boolean
			// 3 : orderedTime -> GregorianCalendar
			// 4 : deliveryTime -> GregorianCalendar
			// 5 : modelId -> CarModel (we hebben hiervoor de Catalog nodig, hoe komen we daar aan?)
			// 6 : options -> Options[] of List (ook Catalog nodig)
		}
		
		return allCarOrders;
	}

	private void addCarOrder(CarOrder newOrder) {
		if(!this.getCarOrdersPerId().containsKey(newOrder.getUserId()))
		{
			this.getCarOrdersPerId().put(newOrder.getUserId(), new ArrayList<CarOrder>());
		}
		this.getCarOrdersPerId().get(newOrder.getUserId()).add(newOrder);
	}

	private ProductionSchedule getProductionSchedule() {
		return productionSchedule;
	}

	private void setProductionSchedule(ProductionSchedule productionSchedule) {
		this.productionSchedule = productionSchedule;
	}

	private HashMap<Integer, ArrayList<CarOrder>> getCarOrdersPerId() {
		return carOrdersPerId;
	}

	private void setCarOrdersPerId(
		HashMap<Integer, ArrayList<CarOrder>> carOrdersPerId) {
		this.carOrdersPerId = carOrdersPerId;
	}
}
//TODO getter