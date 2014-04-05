package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelCreator {
	String path;
	List<Option> options;
	private HashMap<String, CarModel> allCarModels;
	public ModelCreator(List<Option> options, String path){
		this.options = options;
		this.path = path;

	}

	public ModelCreator(List<Option> options){
		this(options, "data/models.txt");
	}

	/**
	 * Create the models from a file
	 * @param path of the file 
	 * @return 
	 * @return 
	 * @throws IOException Problems with accessing file
	 * @throws CarModelCatalogException an modelline is not in the right format
	 */
	public ArrayList<CarModel> createModels() throws IOException, CarModelCatalogException{
		this.allCarModels = new HashMap<String,CarModel>();
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while( inputline!=null){
			processModelLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		return new ArrayList<CarModel>(allCarModels.values());
	}
	/**
	 * processes a line of the model file
	 * @param inputline a line of the model file
	 * @throws CarModelCatalogException the line is in the wrong format, he modelname already exists,
	 * 									or an option type is missing.	 * 
	 */
	private void processModelLine(String inputline) throws CarModelCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 2) throw new CarModelCatalogException("Model: wrong input format: " + inputline);
		if(allCarModels.containsKey(input[0])) throw new CarModelCatalogException("Model name already exists: "+input[0] );
		try{
			ArrayList<String> a = new ArrayList<String>();
			addAll(a,input[1].split(","));
			allCarModels.put(input[0], new CarModel(input[0], collectOption(a)));
		}catch(ClassCastException e){
			throw new CarModelCatalogException("Wrong Option Type in form: " + inputline);
		}
	}

	private void addAll(ArrayList<String> incomp, String[] split) {
		for(String i: split) incomp.add(i);

	}

	/**
	 *  makes a list of option made of a list of descriptions
	 * @param comp the list of descriptions of options
	 * @return a list of options which corresponds with comp
	 * @throws CarModelCatalogException 
	 */
	private ArrayList<Option> collectOption(ArrayList<String> comp) throws CarModelCatalogException {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i: comp){
			Option e = getOption(i);
			if(e !=null)result.add(e);
			else {
				throw new CarModelCatalogException("Option does not exists: "+ i);
			}
		}
		return result;
	}

	private Option getOption( String i) {
		for(Option possibility:options){
			if(possibility.getDescription().equals(i))return possibility;

		}
		return null;
	}
}
