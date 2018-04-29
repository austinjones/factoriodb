package com.factoriodb;

import com.factoriodb.chain.Belt;
import com.factoriodb.chain.Pipe;
import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.GraphSolver;
import com.factoriodb.graph.RecipeGraph;
import com.factoriodb.graph.TransportEdge;
import com.factoriodb.graph.TransportGraph;
import com.factoriodb.graph.TransportVertex;
import com.factoriodb.input.InputReader;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;
import com.factoriodb.recipe.RecipeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Model m = InputReader.load();
        System.out.println("Missing items: " + m.getMissingItems());
		System.out.println("Missing recipes: " + m.getMissingRecipes());


//        System.out.println("advanced-oil:");
//        advancedOil(m);
//
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//
//        System.out.println("basic-oil:");
//        basicOil(m);
//
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//
//        System.out.println("electronic-circuit:");
//        circuits(m);
//
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//
//        System.out.println("iron-plate:");
//        ironPlate(m);
//
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//
//		System.out.println("science-pack-1:");
//		sci1(m);
//
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//
//		System.out.println("science-pack-2:");
//		sci2(m);
//
//		System.out.println("");
//        System.out.println("");
//        System.out.println("");
//
		System.out.println("iron-gear-wheel:");
		gear(m);
	}

    private static void basicOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve(ru.namedRecipe("basic-oil-processing"));
        print(graph);
    }

    private static void advancedOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve(
                ru.namedRecipe("advanced-oil-processing"),
                ru.namedRecipe("heavy-oil-cracking"),
                ru.namedRecipe("light-oil-cracking"));

        print(graph);
    }

    private static void circuits(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve("output-electronic-circuit", 1,
                ru.recipe("copper-cable"),
                ru.recipe("electronic-circuit"));

        print(graph);
    }

    private static void ironPlate(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve(ru.recipe("iron-plate"));

        print(graph);
    }

    private static void sci1(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve(ru.recipe("iron-gear-wheel"),
                ru.recipe("science-pack-1"));

        print(graph);
	}
	
	private static void sci2(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve(ru.recipe("iron-gear-wheel"),
                ru.recipe("inserter"),
                ru.recipe("transport-belt"),
                ru.recipe("science-pack-2"));

        print(graph);
	}
	
	private static void gear(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.solve("output-iron-gear-wheel", 1, ru.recipe("iron-gear-wheel"));

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
        String sourceName = g.getEdgeSource(e).getRecipe().toString();
        String targetName = g.getEdgeTarget(e).getRecipe().toString();
        System.out.println("  " + e.getItem() + ": " + sourceName + " -> " + targetName);
        for (ConnectionOption o : e.getSolutions()) {
            System.out.println("     " + o.name() + ": " + o.output() + " / " + o.maxOutput());
        }
    }

    private static void print(TransportVertex v) {
        System.out.println("" + v.toString());
        for (RecipeOption o : v.getSolutions()) {
            System.out.println("     " + o.name() + " x " + o.count());
        }
    }
}
