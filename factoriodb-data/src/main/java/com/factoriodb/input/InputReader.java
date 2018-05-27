package com.factoriodb.input;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.factoriodb.graph.Recipe;
import com.factoriodb.model.CrafterType;
import com.factoriodb.model.Item;
import com.factoriodb.model.ItemType;
import com.factoriodb.model.Items;
import com.factoriodb.model.Model;
import com.factoriodb.model.Recipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reads the input data, and isolates the Model class from the input data format.
 *
 * @author austinjones
 */
public class InputReader {
    // TODO: get rid of static, use dynamic loading/caching
    private static String FACTORIO_VERSION = "0.16.45";
    private static Model model = null;
    public static Model load() throws FileNotFoundException, IOException {
        if (model != null) {
            return model;
        }

        Items items = renderItems(loadItems(), loadFluids());
        Recipes recipes = renderRecipes(items, loadRecipes());

        return new Model(items, recipes);
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static <T> T[] read(T[] proto, Class<T[]> clz, File... files) throws IOException {
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

    private static <T> T[] read(T[] proto, Class<T[]> clz, String... files) throws IOException {
        Gson gson = new GsonBuilder().create();

        T[] working = Arrays.copyOf(proto, 0);
        for(String file : files) {
            try(InputStream input = InputReader.class.getResourceAsStream(file)) {
                if (input == null) {
                    throw new IOException("Failed to find resource at path: " + file);
                }
                T[] fileData = gson.fromJson(new InputStreamReader(input), clz);
                working = concat(working, fileData);
            }
        }

        return working;
    }

    private static String custom(String file) {
        return "/custom/custom-" + file;
    }
    private static String generated(String file) {
        return "/generated/factorio-parsed-" + FACTORIO_VERSION + "/" + file;
    }

    public static InputItem[] loadItems() throws IOException {
        return read(new InputItem[0],
                InputItem[].class,
                custom("items.json"),
                generated("items.json"));
    }

    public static InputRecipe[] loadRecipes() throws IOException {
        return read(new InputRecipe[0],
                InputRecipe[].class,
                custom("recipes.json"),
                generated("recipes.json"));
    }

    public static InputModule[] loadModules() throws IOException {
        return read(new InputModule[0],
                InputModule[].class,
                generated("modules.json"));
    }

    public static InputFluid[] loadFluids() throws IOException {
        return read(new InputFluid[0],
                InputFluid[].class,
                custom("fluids.json"),
                generated("fluids.json"));
    }

    public static Items renderItems(InputItem[] items, InputFluid[] fluids) {
        List<Item> result = new ArrayList<>();
        for (InputItem input : items) {
            Item item = new Item(input.name, ItemType.ITEM);
            result.add(item);
        }

        for (InputFluid input : fluids) {
            Item item = new Item(input.name, ItemType.FLUID);
            result.add(item);
        }

        return new Items(result);
    }


    private static Map<String, Double> getStack(Collection<InputRecipeItem> input) {
        if (input == null) {
            return new HashMap<>();
        }

        Map<String, List<InputRecipeItem>> items = input.stream()
                .collect(Collectors.groupingBy((e) -> e.name));

        return items.entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue().stream().mapToDouble(i -> i.amount).sum()
        ));
    }

    public static Recipes renderRecipes(Items items, InputRecipe[] inputRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        for (InputRecipe r : inputRecipes) {
            Map<String, Double> input = getStack(r.ingredients);
            Map<String, Double> output = getStack(r.results);

            if(r.result != null) {
                double val = output.getOrDefault(r.result, 0.0);
                val += r.result_count;
                output.put(r.result, val);
            }

            Recipe recipe = new Recipe();
            recipe.name = r.name;
            recipe.inputItems = input;
            recipe.outputItems = output;
            recipe.crafterType = CrafterType.fromInput(r.category);
            recipe.time = r.energy_required;

            recipes.add(recipe);
        }

        return new Recipes(recipes);
    }
}
