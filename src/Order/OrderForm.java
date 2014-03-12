package Order;

import java.util.ArrayList;
import java.util.List;

import Car.CarModel;
import Car.Option;
import User.User;

public interface OrderForm {
	public List<String> getAllModels();
	public ArrayList<Option> getOptions();
	public CarModel getModel();
	public User getUser();
	public boolean setOption(String option);
	public List<String> getPossibleOptionsOfType(String type);
	public boolean SetModel(String Model);
	public boolean CanPlaceType(String Type);
	public List<String> getOptionTypes();
}
