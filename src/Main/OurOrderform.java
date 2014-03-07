package Main;

import java.util.ArrayList;

public class OurOrderform implements OrderForm{
	ArrayList<Option> options;
	CarModel model;
	public OurOrderform(CarModel model, CarModelCatalog catalog,UI ui){

		for(String i: catalog.getAllOptionTypes()){
			ArrayList<String> optionOfType = filterOptiontype(i,model.getOptions());
			String antwoord = null;
			while(antwoord != null && optionOfType.contains(antwoord)){
				String vraag = "Welke "+ i+" moet uw wagen hebben?\nDit zijn de mogelijkheden:\n";
				for(String j: optionOfType){
					vraag += j+"\n";
				}
				ui.display(vraag);
				antwoord = ui.vraag();
			}
			options.add(catalog.getOption(antwoord));
		}
	}

	private ArrayList<String> filterOptiontype(String type, ArrayList<Option> optionlist) {
		ArrayList<String> result = new ArrayList<String>();
		for(Option i: optionlist){
			if(i.getType().equals(type)){
				Boolean incompatible = false;
				for(Option j: options){
					incompatible=	incompatible || j.conflictsWith(i);
				}
				if(!incompatible) result.add(i.getdescription());
			}
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) options.clone();
	}

	@Override
	public CarModel getModel() {
		return model;
	}

}
