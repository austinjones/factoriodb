package com.factoriodb.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
	public String name;
	public String category;
	public boolean enabled;
	public double energy_required;
	public List<RecipeIngredient> ingredients = new ArrayList<>();
	public String result;
	public int result_count = 1;
}