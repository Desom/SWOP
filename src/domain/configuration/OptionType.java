package domain.configuration;

import java.util.ArrayList;

public enum OptionType {
	Body, 
	Color(true,true), 
	Engine, 
	Gearbox,
	Seats(true,true),
	Wheels,
	Airco(false,false),
	Spoiler(false,false);

	private boolean singleTaskPossible;
	private boolean mandatory;

	private OptionType(boolean singleTaskPossible, boolean mandatory){
		this.singleTaskPossible = singleTaskPossible;
		this.mandatory =mandatory;
	}

	private OptionType(){
		this.singleTaskPossible = false;
		this.mandatory =true;
	}

	public boolean isSingleTaskPossible() {
		return singleTaskPossible;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public static ArrayList<OptionType> getAllSingleTaskPossibleTypes() {
		ArrayList<OptionType> singleTaskPossibleTypes = new ArrayList<OptionType>();
		for (OptionType optionType : OptionType.values())
			if (optionType.isSingleTaskPossible())
				singleTaskPossibleTypes.add(optionType);
		return singleTaskPossibleTypes;
	}
}
