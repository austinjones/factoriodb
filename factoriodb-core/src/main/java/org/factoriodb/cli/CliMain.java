package org.factoriodb.cli;

import org.factoriodb.Main;
import org.factoriodb.PrintOption;
import org.factoriodb.graph.GraphSolver;
import org.factoriodb.graph.TransportGraph;
import org.factoriodb.input.InputReader;
import org.factoriodb.model.Model;
import org.factoriodb.recipe.Recipe;
import org.factoriodb.recipe.RecipeUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CliMain {
    public static void main(String[] args) throws IOException {
        String input = args[0];

        Constructor constructor = new Constructor(RootYaml.class);
        Yaml yaml = new Yaml(constructor);
        RootYaml in = yaml.load(new FileReader(new File(input)));

        Model m = InputReader.load();
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        List<Recipe> recipes = new ArrayList<>();
        for (RecipeYaml recipe : in.getRecipes()) {
//            List<String> inputSections = section.getInput();

            recipes.add(ru.namedRecipe(recipe.getName()));

            if (recipe.getRate() != null) {
                solver.bind(recipe.getName(), null, recipe.getRate(), GraphSolver.ConstraintType.OUTPUT);
            }

            for(Map.Entry<String, RecipeItemYaml> entry : recipe.getInput().entrySet()) {
                if (entry.getValue().getRate() != null) {
                    solver.bind(recipe.getName(), entry.getKey(), entry.getValue().getRate(),
                            GraphSolver.ConstraintType.INPUT);
                }
            }

            for(Map.Entry<String, RecipeItemYaml> entry : recipe.getOutput().entrySet()) {
                if (entry.getValue().getRate() != null) {
                    solver.bind(recipe.getName(), entry.getKey(), entry.getValue().getRate(),
                            GraphSolver.ConstraintType.OUTPUT);
                }
            }
        }

        TransportGraph graph = solver.solve(recipes.toArray(new Recipe[]{}));
        Main.print_small(graph, new PrintOption());
    }
}
