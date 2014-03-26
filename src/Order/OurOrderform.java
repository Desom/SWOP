package Order;

import java.util.ArrayList;
import java.util.List;

import Main.Communcatietool;

public class OurOrderform implements OrderForm{
	private ArrayList<String> options;
	private String model;
	private Communcatietool controller;
	public OurOrderform(String model, Communcatietool controller) {
		options= new ArrayList<String>();
		this.model = model;
		this.controller=controller;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> getOptions() {
		return  (ArrayList<String>) options.clone();
	}

	@Override
	public String getModel() {
		return model;
	}
	
	@Override
	public void setOption(String description) {
		this.options.add(description);
	}

	@Override
	public List<String> getPossibleOptionsOfType( String type) {
 		return this.controller.getPossibleOptionsOfType(this, type);
	}

	@Override
	public boolean canPlaceType(String Type) {
		return this.controller.canPlaceType(this, Type);
	}

	@Override
	public List<String> getOptionTypes() {
		return this.controller.getOptionTypes();
	}
}
