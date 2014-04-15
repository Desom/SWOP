package test.order;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Option;
import domain.order.Car;
import domain.order.CarOrder;
import domain.user.GarageHolder;

public class CarTest {

	@Test
	public void testCreate() throws IOException, CarModelCatalogException {
		GarageHolder holder = new GarageHolder(1);
		CarModelCatalog catalog = new CarModelCatalog();
		CarModel model= catalog.getCarModel("Ford");
		ArrayList<Option> allOptions = model.getOptions();
		ArrayList<Option> selectedOptions = new ArrayList<Option>();
		selectedOptions.add(allOptions.get(0));
		selectedOptions.add(allOptions.get(1));
		selectedOptions.add(allOptions.get(2));
		selectedOptions.add(allOptions.get(3));
		selectedOptions.add(allOptions.get(4));
		selectedOptions.add(allOptions.get(5));
		selectedOptions.add(allOptions.get(6));
		CarOrder order = new CarOrder(1, holder, model, selectedOptions);
		Car car = order.getCar();
		
		assertEquals(order, car.getOrder());
		assertEquals(false, car.isCompleted());
	}

}
