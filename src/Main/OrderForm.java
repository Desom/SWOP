package Main;

import java.util.ArrayList;
import java.util.List;

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
