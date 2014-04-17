package domain.configuration;

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
}
