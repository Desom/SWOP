package Main;
import java.util.ArrayList;

import OptionSubTypes.*;


public class CarModel {
	private final String Name;
	private final ArrayList<Option> PossibleOptions;
	private final Airco Default_Airco;
	private final Body Default_Body;
	private final Color Default_Color;
	private final Engine Default_Engine;
	private final Gearbox Default_Gearbox;
	private final Seats Default_Seats;
	private final Wheels Default_Wheels;
	public CarModel(String Name,ArrayList<Option> OptionList,Airco Default_Airco,Body Default_Body,Color Default_Color,Engine Default_Engine,Gearbox Default_Gearbox,Seats Default_Seats,Wheels Default_Wheels ) throws CarModelCatalogException{
		if(Name == null || OptionList == null || Default_Airco ==null  
				|| Default_Body ==null || Default_Color ==null 
				|| Default_Engine ==null || Default_Gearbox==null 
				|| Default_Seats ==null || Default_Wheels ==null ) throw new CarModelCatalogException("null in non null value of Model");
		PossibleOptions = OptionList;
		this.Name=Name;
		this.Default_Airco=Default_Airco;
		this.Default_Body=Default_Body;
		this.Default_Color=Default_Color;
		this.Default_Engine=Default_Engine;
		this.Default_Gearbox=Default_Gearbox;
		this.Default_Seats=Default_Seats;
		this.Default_Wheels=Default_Wheels;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) PossibleOptions.clone();
	}
	public Airco getDefault_Airco() {
		return Default_Airco;
	}
	public Body getDefault_Body() {
		return Default_Body;
	}
	public Color getDefault_Color() {
		return Default_Color;
	}
	public Engine getDefault_Engine() {
		return Default_Engine;
	}
	public Gearbox getDefault_Gearbox() {
		return Default_Gearbox;
	}
	public Seats getDefault_Seats() {
		return Default_Seats;
	}
	public Wheels getDefault_Wheels() {
		return Default_Wheels;
	}

	public String getName() {
		return Name;
	}
	
	public String toString(){
		return "Model : " + Name;
	}
	
}
