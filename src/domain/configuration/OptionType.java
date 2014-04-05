package domain.configuration;

public enum OptionType {
	Body, 
	Color(true), 
	Engine, 
	Gearbox,
	Seats(true),
	Wheels,
	Airco,
	Spoiler;

	private boolean singleTaskPossible;

	private OptionType(boolean singleTaskPossible){
		this.singleTaskPossible = singleTaskPossible;
	}

	private OptionType(){
		this.singleTaskPossible = false;
	}

	public boolean isSingleTaskPossible() {
		return singleTaskPossible;
	}
}
