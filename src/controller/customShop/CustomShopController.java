package controller.customShop;

import controller.UIInterface;
import domain.Company;
import domain.user.CustomShopManager;

public class CustomShopController {
	
	private OrderSingleTaskHandler orderSingleTaskHandler;
	
	public CustomShopController() {
		this.orderSingleTaskHandler = new OrderSingleTaskHandler();
	}

	public void run(UIInterface ui, Company company, CustomShopManager customShopManager) {
		this.orderSingleTaskHandler.run(ui, company, customShopManager);
	}

}
