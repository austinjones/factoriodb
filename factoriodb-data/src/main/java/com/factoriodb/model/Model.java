package com.factoriodb.model;

import com.factoriodb.graph.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Model {
	public Set<String> itemNames = new TreeSet<>();
//	public List<Item> items = new ArrayList<>();
//	public Map<String, Item> itemsByName = new HashMap<>();
//
	public Set<String> recipeNames = new TreeSet<>();
//	public List<Recipe> recipes = new ArrayList<>();
//	public Map<String, List<Recipe>> recipesByResult = new HashMap<>();

//	public Set<String> moduleNames = new TreeSet<>();
//	public List<Module> modules = new ArrayList<>();
//	public Map<String, Module> modulesByName = new HashMap<>();

    private Items items;
    private Recipes recipes;

	public Model(Items items, Recipes recipes) {
        this.items = items;
        this.recipes = recipes;

        for(Item item : items) {
            itemNames.add(item.name());
        }

        for(Recipe recipe : recipes) {
            recipeNames.add(recipe.name);
        }
//        for (Item item : inputItems) {
//            itemNames.add(item.name());
//            items.add(item);
//            this.itemsByName.put(item.name(), item);
//        }


//        for (Module m : moduleArray) {
//            if(m.name != null) {
//                moduleNames.add(m.name);
//                modules.add(m);
//                this.modulesByName.put(m.name, m);
//            }
//        }
    }

	

	public Set<String> getItemNames() {
		return itemNames;
	}

	public Items getItems() {
		return items;
	}

	public Set<String> getRecipeNames() {
		return recipeNames;
	}

	public Recipes getRecipes() {
		return recipes;
	}

//	public Set<String> getModuleNames() {
//		return moduleNames;
//	}
//
//	public List<Module> getModules() {
//		return modules;
//	}
//
//	public Map<String, Module> getModulesByName() {
//		return modulesByName;
//	}
	
	public Item getItemByName(String name) {
		return items.get(name);
	}

	public List<Recipe> getRecipeByResult(String itemName) {
		List<Recipe> r = recipes.getByResult(itemName);

        if (r == null) {
            return new ArrayList<>();
        }

        return r;
	}
	
//	public Module getModuleByName(String name) {
//		return modulesByName.get(name);
//	}

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
			
			if(r == null && !ignoreItems.contains(item)) {
				missingItems.add(item);
			}
		}

		return missingItems;
	}
}
