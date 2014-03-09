package Main;

import java.util.ArrayList;

public class OurOrderform implements OrderForm{
	ArrayList<Option> options;
	CarModel model;
	User user;
	public OurOrderform(User user, CarModelCatalog catalog,UI ui) throws UserAccessException{
		String Modelnaam = null;
		while(Modelnaam == null || catalog.getCarModel(Modelnaam) == null){
			String vraag = "Welke model moet uw wagen hebben?\nDit zijn de mogelijkheden:\n";
			for(String j: catalog.getAllModelnames(user)){
				vraag += j+"\n";
			}
			ui.display(vraag);
			Modelnaam = ui.vraag();
		}
		model = catalog.getCarModel(Modelnaam);
		for(String i: catalog.getAllOptionTypes()){
			ArrayList<String> optionOfType = catalog.filterOptiontype(i,options,model);
			String antwoord = null;
			while(antwoord == null || !optionOfType.contains(antwoord)){
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

	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) options.clone();
	}

	@Override
	public CarModel getModel() {
		return model;
	}
	@Override
	public User getUser() {
		return user;
	}
}
