package com.factoriodb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.factoriodb.model.Fluid;
import com.factoriodb.model.Item;
import com.factoriodb.model.Module;
import com.factoriodb.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Model {	
	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private static <T> T[] read(T[] proto, Class<T[]> clz, File... files) throws FileNotFoundException, IOException {
		Gson gson = new GsonBuilder().create();

		T[] working = Arrays.copyOf(proto, 0);
		for(File f : files) {
			try(FileReader reader = new FileReader(f)) {
				T[] fileData = gson.fromJson(reader, clz);
				working = concat(working, fileData);
			}
		}

		return working;
	}
	
	private static Model model = null;
	public static Model load() throws FileNotFoundException, IOException {
		if (model != null) {
			return model;
		}

		model = new Model();
		return model;
	}

	public Set<String> itemNames = new TreeSet<>();
	public List<Item> items = new ArrayList<>();
	public Map<String, Item> itemsByName = new HashMap<>();

	public Set<String> recipeNames = new TreeSet<>();
	public List<Recipe> recipes = new ArrayList<>();
	public Map<String, List<Recipe>> recipesByResult = new HashMap<>();

	public Set<String> moduleNames = new TreeSet<>();
	public List<Module> modules = new ArrayList<>();
	public Map<String, Module> modulesByName = new HashMap<>();	

	public Set<String> fluidNames = new TreeSet<>();
	public List<Fluid> fluids = new ArrayList<>();
	public Map<String, Fluid> fluidsByName = new HashMap<>();

	private Model() throws FileNotFoundException, IOException {
		Item[] itemArray = read(new Item[0], 
				Item[].class, 
				new File("json/items.json"), 
				new File("json/custom-items.json"));
		for( Item item : itemArray ) {
			if(item.name != null) {
				itemNames.add(item.name);
				items.add(item);
				this.itemsByName.put(item.name, item);
			}
		}

		Recipe[] recipeArray = read(new Recipe[0], 
				Recipe[].class, 
				new File("json/recipes.json"), 
				new File("json/custom-recipes.json"));
		for( Recipe r : recipeArray ) {
			if(r.result != null) {
				itemNames.add(r.result);
				recipeNames.add(r.name);
				recipes.add(r);
				List<Recipe> rlist = recipesByResult.get(r.result);
				if(rlist == null) {
					rlist = new ArrayList<>();
					recipesByResult.put(r.result, rlist);
				}
				rlist.add(r);
			}
		}

		Module[] moduleArray = read(new Module[0], 
				Module[].class, 
				new File("json/modules.json"));
		for( Module m : moduleArray ) {
			if(m.name != null) {
				moduleNames.add(m.name);
				modules.add(m);
				this.modulesByName.put(m.name, m);
			}
		}

		Fluid[] fluidArray = read(new Fluid[0], 
				Fluid[].class, 
				new File("json/fluids.json"),
				new File("json/custom-fluids.json"));
		for( Fluid f : fluidArray ) {
			if(f.name != null) {
				fluidNames.add(f.name);
				fluids.add(f);
				this.fluidsByName.put(f.name, f);
			}
		}
	}

	

	public Set<String> getItemNames() {
		return itemNames;
	}

	public List<Item> getItems() {
		return items;
	}

	public Set<String> getRecipeNames() {
		return recipeNames;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public Set<String> getModuleNames() {
		return moduleNames;
	}

	public List<Module> getModules() {
		return modules;
	}

	public Map<String, Module> getModulesByName() {
		return modulesByName;
	}

	public Set<String> getFluidNames() {
		return fluidNames;
	}

	public List<Fluid> getFluids() {
		return fluids;
	}
	
	public Item getItemByName(String name) {
		return itemsByName.get(name);
	}	
	
	public List<Recipe> getRecipeByResult(String itemName) {
		List<Recipe> recipes = recipesByResult.get(itemName);
		if (recipes== null) {
			recipes =  new ArrayList<>();
		}
		
		return recipes;
	}
	
	public Fluid getFluidByName(String name) {
		return fluidsByName.get(name);
	}	
	
	public Module getModuleByName(String name) {
		return modulesByName.get(name);
	}

	public List<String> getMissingRecipes() {
		List<String> missingRecipes = new ArrayList<>();

		Set<String> ignoreRecipe = this.getIgnoreRecipes();
		Set<String> ignoreItem = this.getIgnoreItems();
		for(String item : itemNames) {
			List<Recipe> r = getRecipeByResult(item);

			if(r.isEmpty() && !ignoreRecipe.contains(item) && !ignoreItem.contains(item)) {
				missingRecipes.add(item);
			}
		}

		return missingRecipes;
	}

	private Set<String> getRawResources() {
		Set<String> raw = new HashSet<String>();
		raw.add("coal");
		raw.add("crude-oil");
		raw.add("stone");
		raw.add("copper-ore");
		raw.add("iron-ore");
		raw.add("water");
		raw.add("raw-wood");
		return raw;
	}

	private Set<String> getIgnoreRecipes() {
		Set<String> ignoreRecipe = new HashSet<String>();
		ignoreRecipe.add("belt-immunity-equipment");
		ignoreRecipe.add("coin");
		ignoreRecipe.add("simple-entity-with-force");
		ignoreRecipe.add("simple-entity-with-owner");
		return ignoreRecipe;
	}
	
	private Set<String> getIgnoreItems() {
		Set<String> ignoreItem = new HashSet<String>();
		ignoreItem.add("railgun");
		ignoreItem.add("small-plane");
		ignoreItem.addAll(getRawResources());
		return ignoreItem;
	}

	public List<String> getMissingItems() {
		List<String> missingItems = new ArrayList<String>();
		Set<String> ignoreItems = getIgnoreItems();
		
		for(String item : itemNames) {
			
			Item r = getItemByName(item);
			Fluid f = getFluidByName(item);
			
			if(r == null && f == null && !ignoreItems.contains(item)) {
				missingItems.add(item);
			}
		}

		return missingItems;
	}
}
