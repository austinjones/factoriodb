package com.factoriodb.model;

import com.factoriodb.graph.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author austinjones
 */
public class Recipes implements Iterable<Recipe> {
    private Map<String, Recipe> map;
    private Map<String, List<Recipe>> mapByResult = new HashMap<>();
    private Collection<Recipe> recipes;

    public Recipes(Collection<Recipe> recipes) {
        this.recipes = new ArrayList<>(recipes);
        this.map = recipes.stream().collect(
                Collectors.toMap(
                        (e) -> e.name,
                        (e) -> e
                )
        );

        for (Recipe r : recipes) {
            this.recipes.add(r);

            for(String item : r.outputs()) {
//                itemNames.add(item.name());

                List<Recipe> rlist = mapByResult.get(item);
                if(rlist == null) {
                    rlist = new ArrayList<>();
                    mapByResult.put(item, rlist);
                }
                rlist.add(r);
            }

//            recipeNames.add(r.getName());
        }
    }

    public Recipe get(String name) {
        return map.get(name);
    }

    public List<Recipe> getByResult(String name) {
        return mapByResult.get(name);
    }

//    public List<Recipe> getRecipesByInput() {
//
//    }
//
//    public List<Recipe> getRecipesByResult() {
//
//    }

    @Override
    public Iterator<Recipe> iterator() {
        return recipes.iterator();
    }
}
