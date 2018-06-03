package org.factoriodb;

import org.factoriodb.chain.Belt;
import org.factoriodb.chain.Connection;
import org.factoriodb.chain.OptionFactory;
import org.factoriodb.chain.option.ConnectionOption;
import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.graph.GraphSolver;
import org.factoriodb.graph.TransportEdge;
import org.factoriodb.graph.TransportGraph;
import org.factoriodb.graph.TransportVertex;
import org.factoriodb.input.InputReader;
import org.factoriodb.model.CrafterType;
import org.factoriodb.model.Model;
import org.factoriodb.recipe.RecipeUtils;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Model m = InputReader.load();
        System.out.println("Missing items: " + m.getMissingItems());
		System.out.println("Missing recipes: " + m.getMissingRecipes());


        System.out.println("---- advanced-oil:");
        advancedOil(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("---- basic-oil:");
        basicOil(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("---- electronic-circuit:");
        circuits(m);

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("---- iron-plate:");
        ironPlate(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("---- science-pack-1:");
		sci1(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("---- science-pack-2:");
		sci2(m);

		System.out.println("");
        System.out.println("");
        System.out.println("");
//
		System.out.println("---- iron-gear-wheel:");
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

        print_small(graph, new PrintOption());
    }

    private static void circuits(Model m) {
        RecipeUtils ru = new RecipeUtils(m);
        GraphSolver solver = new GraphSolver();

        TransportGraph graph = solver.bind("output-electronic-circuit", 10).solve(
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

    public static void print_small(TransportGraph graph, PrintOption option) {
        TopologicalOrderIterator<TransportVertex, TransportEdge> iter = new TopologicalOrderIterator(graph);
        while (iter.hasNext()) {
            TransportVertex v = iter.next();
            print_small(graph, v, option);
        }
    }

    private static String format(String item, double rate) {
        NumberFormat decimal = new DecimalFormat("0.00");
	    return item + " @ " + decimal.format(rate) + "/s";
    }


    private static String format_nobelt(Collection<? extends ConnectionOption> solutions, int distributeAcross) {
        List<? extends ConnectionOption> options = solutions.stream()
                .filter( e -> !(e.name().contains("belt")))
                .collect(Collectors.toList());

        return format(options, distributeAcross);
    }

    private static Map<String, String> displayNames = new HashMap<String, String>() {{
        put("inserter", "Inserter");
        put("long-handed-inserter", "Long");
        put("fast-inserter", "Fast");
        put("stack-inserter", "Stack");
        put("assembling-machine-1", "Assembler");
        put("assembling-machine-2", "Assembler 2");
        put("assembling-machine-3", "Assembler 3");
        put("transport-belt", "Belt");
        put("fast-transport-belt", "Fast Belt");
        put("express-transport-belt", "Express Belt");
        put("pipe", "Pipe");
    }};

	private static String render(String input) {
	    String display = displayNames.get(input);

	    if (display != null) {
	        return display;
        }

        return input;
    }

    private static String format(Collection<? extends ConnectionOption> solutions) {
        NumberFormat decimal = new DecimalFormat("0.00");
        List<String> options = new ArrayList<>();

        int lastAmount = Integer.MAX_VALUE;
        for(ConnectionOption o : solutions) {
            int intCount = (int)Math.ceil(o.count());
            if (intCount == lastAmount) {
                continue;
            }
            options.add(decimal.format(o.count()) + "x " + render(o.name()));
            lastAmount = intCount;
        }

        return String.join(" | ", options);
    }

    private static String format(Collection<? extends ConnectionOption> solutions, int distributeAcross) {
	    List<String> options = new ArrayList<>();

	    int lastAmount = Integer.MAX_VALUE;
	    for(ConnectionOption o : solutions) {
            int count = (int)Math.ceil(1.0 * o.count() / distributeAcross);
            if (count == lastAmount) {
                continue;
            }
	        options.add(count + "x " + render(o.name()));
            lastAmount = count;
        }

        return String.join(" | ", options);
    }

    private static String rpad(String str, int len) {
	    while (str.length() < len) {
	        str += " ";
        }

        return str;
    }

    private static void print_small(TransportGraph g, TransportVertex v, PrintOption options) {
	    int col0 = 30;

        NumberFormat decimal = new DecimalFormat("0.00");

	    System.out.println("------- " + v.getRecipe().getRecipe().name + " -------");

        CrafterType crafter = v.getRecipe().getRecipe().crafterType;

	    if (crafter == CrafterType.INPUT) {
	        // wtf java.
	        String item = v.getRecipe().getRecipe().outputItems.keySet().stream().findFirst().get();
	        RecipeOption option = v.getSolutions().stream().findFirst().get();
	        double rate = option.output(item);
            String input = format(item, rate);

            Collection<? extends ConnectionOption> transitOption = OptionFactory.transitOptions(item, rate);
            String output = format(transitOption);

            System.out.println(rpad(input, col0) + " -> " + output);
            System.out.println();
            return;
        } else if (crafter == CrafterType.OUTPUT) {
            String item = v.getRecipe().getRecipe().inputItems.keySet().stream().findFirst().get();
            RecipeOption option = v.getSolutions().stream().findFirst().get();
            double rate = option.input(item);
            String input = format(item, rate);

            Collection<? extends ConnectionOption> transitOption = OptionFactory.transitOptions(item, rate);
            String output = format(transitOption);

            System.out.println(output + " -> " + rpad(input, col0));
            System.out.println();
            return;
        }

        Optional<? extends RecipeOption> recipeOptional = Optional.empty();
	    if (crafter == CrafterType.ASSEMBLER || crafter == CrafterType.ADVANCED_ASSEMBLER) {
            recipeOptional = v.getSolutions().stream()
                    .filter(e -> e.name().equals(options.assemblerPreference)).findFirst();
        } else if (crafter == CrafterType.SMELTER) {
            recipeOptional = v.getSolutions().stream()
                    .filter(e -> e.name().equals(options.furnacePreference)).findFirst();
        }

        if (!recipeOptional.isPresent()) {
            recipeOptional = v.getSolutions().stream().findFirst();
        }

        if (!recipeOptional.isPresent()) {
            throw new IllegalStateException(v.toString() + " / " + v.getSolutions().toString());
        }

        RecipeOption option = recipeOptional.get();

        for (TransportEdge edge : g.sourcesOf(v)) {
            String edge_resource = format(edge.getItem(), g.getEdgeWeight(edge));
            String edge_format = format_nobelt(edge.getSolutions(), option.count());

            System.out.println(rpad(edge_resource, col0) + " -> " + edge_format);
        }

        List<String> outputs = new ArrayList<>();
	    for (String item : option.getRecipe().outputItems.keySet()) {
	        outputs.add(format(item, option.output(item)));
        }
	    String output = String.join(", ", outputs);
        System.out.println(rpad(option.count() + "x " + render(option.name()), col0) + " -> " + output);

        for(String outputItem : option.getRecipe().outputItems.keySet()) {
            double rate = option.output(outputItem);

            Collection<? extends ConnectionOption> allOptions = OptionFactory.connectionOptions(outputItem, rate);
            Collection<? extends ConnectionOption> transitOptions = OptionFactory.transitOptions(outputItem, rate);

            Optional<? extends ConnectionOption> transitOptional = transitOptions.stream()
                    .filter(e -> e.name().equals(options.beltPreference)).findFirst();

            if (!transitOptional.isPresent()) {
                transitOptional = transitOptions.stream().findFirst();
            }

            if (!transitOptional.isPresent()) {
                throw new IllegalStateException();
            }

            ConnectionOption transit = transitOptional.get();

            String others = format_nobelt(allOptions, option.count());

            String draw = OptionFactory.isFluid(outputItem) ? format(outputItem, rate) : others;
            System.out.println(rpad(decimal.format(transit.count()) + "x " + render(transit.name()) + "[" + outputItem +"]", col0) + " <- " + draw);
        }

        System.out.println("");
    }

    public static void print(TransportGraph graph) {
        TopologicalOrderIterator<TransportVertex, TransportEdge> iter = new TopologicalOrderIterator(graph);
        while (iter.hasNext()) {
            TransportVertex v = iter.next();
            print(graph, v);
        }
    }

    enum EdgeContext {
	    INPUT, OUTPUT
    }

    private static void print(TransportGraph g, TransportEdge e, EdgeContext context) {
        NumberFormat decimal = new DecimalFormat("0.00");
        NumberFormat percent = new DecimalFormat("00.00");

        String sourceName = g.getEdgeSource(e).name();
        String targetName = g.getEdgeTarget(e).name();
        double edgeFlow = g.getEdgeWeight(e);

        if (context == EdgeContext.INPUT) {
            System.out.println("  input from " + sourceName + ":");

        } else if (context == EdgeContext.OUTPUT) {
            System.out.println("  output to " + targetName + ":");
        }
        System.out.println("    " + decimal.format(edgeFlow) + " x " + e.getItem() + ":");

        for (ConnectionOption o : e.getSolutions()) {
            double utilization = o.output() / o.maxOutput();
            System.out.println("    - " + (long)Math.ceil(o.count()) + " x (" +
                    percent.format(100*utilization) + "%) " +
                    o.name());
        }
        System.out.println();
    }

    private static void print(TransportGraph g, TransportVertex v) {
        System.out.println("" + v.toString());
        for (RecipeOption o : v.getSolutions()) {
            System.out.println("  - " + o.count() + " x " + o.name());
        }

        for (TransportEdge e : g.sourcesOf(v)) {
            print(g, e, EdgeContext.INPUT);
        }

        for (TransportEdge e : g.targetsOf(v)) {
            print(g, e, EdgeContext.OUTPUT);
        }

        System.out.println("");
    }
}
