package com.factoriodb;

import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.GraphSolver;
import com.factoriodb.graph.TransportEdge;
import com.factoriodb.graph.TransportGraph;
import com.factoriodb.graph.TransportVertex;
import com.factoriodb.input.InputReader;
import com.factoriodb.model.Model;
import com.factoriodb.recipe.RecipeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Model m = InputReader.load();
        System.out.println("Missing items: " + m.getMissingItems());
		System.out.println("Missing recipes: " + m.getMissingRecipes());


        System.out.println("advanced-oil:");
        advancedOil(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("basic-oil:");
        basicOil(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("electronic-circuit:");
        circuits(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("iron-plate:");
        ironPlate(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("science-pack-1:");
		sci1(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("science-pack-2:");
		sci2(m);

		System.out.println("");
        System.out.println("");
        System.out.println("");
//
		System.out.println("iron-gear-wheel:");
		gear(m);
	}

    private static void basicOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver
                .bind("basic-oil-processing", "crude-oil", 10.0)
                .solve(ru.namedRecipe("basic-oil-processing"));
        print(graph);
    }

    private static void advancedOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.
                bind("output-petroleum-gas", 100.0)
                .solve(ru.namedRecipe("advanced-oil-processing"),
                ru.namedRecipe("heavy-oil-cracking"),
                ru.namedRecipe("light-oil-cracking"));

        print(graph);
    }

    private static void circuits(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.bind("output-electronic-circuit", 1).solve(
                ru.recipe("copper-cable"),
                ru.recipe("electronic-circuit"));

        print(graph);
    }

    private static void ironPlate(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver
                .bind("output-iron-plate", 10000.0)
                .solve(ru.recipe("iron-plate"));

        print(graph);
    }

    private static void sci1(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver
                .bind("output-science-pack-1", 1.0)
                .solve(ru.recipe("iron-gear-wheel"),
                ru.recipe("science-pack-1"));

        print(graph);
	}
	
	private static void sci2(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver
                .bind("output-science-pack-2", 1.0)
                .solve(ru.recipe("iron-gear-wheel"),
                ru.recipe("inserter"),
                ru.recipe("transport-belt"),
                ru.recipe("science-pack-2"));

        print(graph);
	}
	
	private static void gear(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver
                .bind("output-iron-gear-wheel", 1)
                .solve(ru.recipe("iron-gear-wheel"));

        print(graph);
	}

    public static void print(TransportGraph graph) {
        for (TransportVertex v : graph.vertexSet()) {
            print(v);
        }

        for (TransportEdge e : graph.edgeSet()) {
            print(graph, e);
        }
    }

    private static void print(TransportGraph g, TransportEdge e) {
        NumberFormat decimal = new DecimalFormat("0.00");
        NumberFormat percent = new DecimalFormat("00.00");

        String sourceName = g.getEdgeSource(e).name();
        String targetName = g.getEdgeTarget(e).name();
        double edgeFlow = g.getEdgeWeight(e);

        System.out.println("  " + decimal.format(edgeFlow) + " x " + e.getItem() + ": " + sourceName + " -> " + targetName);
        for (ConnectionOption o : e.getSolutions()) {
            double utilization = o.output() / o.maxOutput();
            System.out.println("     " + (long)Math.ceil(o.count()) + " x (" +
                    percent.format(100*utilization) + "%) " +
                    o.name());
        }
    }

    private static void print(TransportVertex v) {
        System.out.println("" + v.toString());
        for (RecipeOption o : v.getSolutions()) {
            System.out.println("     " + o.count() + " x " + o.name());
        }
    }
}
