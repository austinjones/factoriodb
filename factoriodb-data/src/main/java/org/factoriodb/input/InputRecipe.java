package org.factoriodb.input;

import java.util.ArrayList;
import java.util.List;

public class InputRecipe {
	public String name;
	public String category;
	public boolean enabled;
	public double energy_required;
    public List<InputRecipeItem> ingredients = new ArrayList<>();
    public List<InputRecipeItem> results = new ArrayList<>();
	public String result;
	public int result_count = 1;
}