package domain.configuration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.policies.SingleTaskOrderTaskTypePolicy;

public class PolicyTest {

	VehicleModelCatalog cmc;
	Policy policyChainCarOrders;
	Policy policyChainSTOrders;

	@Before
	public void makePolicies() throws IOException, VehicleModelCatalogException {
		this.cmc = new VehicleModelCatalog();

		//Creates the car order policy chain.
		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		this.policyChainCarOrders = pol4;

		//Creates the single task order policy chain.
		Policy STpol1 = new SingleTaskOrderTaskTypePolicy(null);
		Policy STpol2 = new SingleTaskOrderNumbersOfTasksPolicy(STpol1);
		this.policyChainSTOrders = STpol2;


	}

	/**
	 * 
	 * 
	 * NIET-SingleTask Tests
	 * 
	 * 
	 */


	@Test
	public void testSucces() {
		Configuration validConfiguration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				try {
					validConfiguration.addOption(option);
				} catch (InvalidConfigurationException e) {
					System.out.println(e.getMessage());
					fail("An invalidConfigurationException was thrown while building the configuration");
				} 
			}
		}
		try {
			validConfiguration.complete();
		} catch (InvalidConfigurationException e) {
			System.out.println(e.getMessage());
			fail("An invalidConfigurationException was thrown for a valid configuration");
		}
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testFailCompetion() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}
	
	
	@Test(expected = InvalidConfigurationException.class)
	public void testFailCompatibility() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void testFailCompatibility2() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null, new ArrayList<OptionType>()));
		Policy STpol = new ModelCompatibilityPolicy(null);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				configuration.addOption(option);
			}
		}
		STpol.checkComplete(configuration);;
	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void testConflict() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("white")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void testConflict2() throws InvalidConfigurationException {
		// Model C
		Configuration configuration = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null, new ArrayList<OptionType>()));
		Policy STpol = new ConflictPolicy(null);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("white")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				configuration.addOption(option);
			}
		}
		STpol.checkComplete(configuration);
	}

	
	@Test(expected = InvalidConfigurationException.class)
	public void testDepenency() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}

	
	@Test
	public void testmultipleFaults(){
		Configuration configuration = new Configuration(cmc.getAllModels().get(0),policyChainCarOrders);
		// Model C
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sport")
					||option.getDescription().equals("black")
					||option.getDescription().equals("red")
					||option.getDescription().equals("performance 2.5l v6")
					||option.getDescription().equals("6 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("sports")
					||option.getDescription().equals("low")
					){
				try {
					configuration.addOption(option);
				} catch (InvalidConfigurationException e) {
					//do nothing
				}
			}
		}
		try {
			configuration.complete();
		} catch (InvalidConfigurationException e) {
			assertTrue(e.getMessages().size() == 2);
		}
	}
	
	
	/**
	 * 
	 * 
	 * SingleTask Tests
	 * 
	 * 
	 */


	@Test
	public void testSuccesST() {
		Configuration validConfiguration = new Configuration(null,policyChainSTOrders);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("black")){
				try {
					validConfiguration.addOption(option);
				} catch (InvalidConfigurationException e) {
					System.out.println(e.getMessage());
					fail("An invalidConfigurationException was thrown while building the configuration");
				} 
			}
		}
		try {
			validConfiguration.complete();
		} catch (InvalidConfigurationException e) {
			System.out.println(e.getMessage());
			fail("An invalidConfigurationException was thrown for a valid configuration");
		}
	}

	@Test(expected = InvalidConfigurationException.class)
	public void testinvalidSTOrderTaskType() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(null,policyChainSTOrders);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void testinvalidSTOrderTaskType2() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(null, new CompletionPolicy(null, new ArrayList<OptionType>()));
		Policy STpol = new SingleTaskOrderTaskTypePolicy(null);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")){
				configuration.addOption(option);
			}
		}
		STpol.checkComplete(configuration);;
	}


	@Test(expected = InvalidConfigurationException.class)
	public void testinvalidSTNumberOfTasks() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(null,policyChainSTOrders);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("black") || option.getDescription().equals("vinyl grey")){
				configuration.addOption(option);
			}
		}
		configuration.complete();
	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void testinvalidSTNumberOfTasks2() throws InvalidConfigurationException {
		Configuration configuration = new Configuration(null, new CompletionPolicy(null, new ArrayList<OptionType>()));
		Policy STpol = new SingleTaskOrderNumbersOfTasksPolicy(null);
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("black") || option.getDescription().equals("vinyl grey")){
				configuration.addOption(option);
			}
		}
		STpol.checkComplete(configuration);;
	}

}
