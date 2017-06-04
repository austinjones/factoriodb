package com.factoriodb;

import com.factoriodb.Solver.SolveNode;
import com.factoriodb.Solver.SolveOption;
import com.factoriodb.chain.Belt;
import com.factoriodb.chain.Crafter;
import com.factoriodb.chain.Pipe;
import com.factoriodb.com.factoriodb.input.InputReader;
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
		
		System.out.println("iron-gear-wheel:");
		gear(m);
	}

    private static void basicOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);

        Pipe crude = ru.pipe("crude-oil");
        crude.markInput();

        Crafter refinery = ru.craftRecipe("basic-oil-processing");
        refinery.connectFrom(crude);

        Pipe output = ru.pipe("petroleum-gas");
        output.connectFrom(refinery);

        Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
        ItemsStack outputRate = new ItemsStack("petroleum-gas", 10);
        print(solver.solveForOutput(output, outputRate), 0);
    }

    private static void advancedOil(Model m) {
        RecipeUtils ru = new RecipeUtils(m);

        Pipe crude = ru.pipe("crude-oil");
        Pipe water = ru.pipe("water");
        crude.markInput(); water.markInput();

        Crafter refinery = ru.craftRecipe("advanced-oil-processing");
        refinery.connectFrom(crude);
        refinery.connectFrom(water);

        Pipe light = ru.pipe("light-oil");
        light.connectFrom(refinery);

        Pipe heavy = ru.pipe("heavy-oil");
        heavy.connectFrom(refinery);

        Crafter heavyCracking = ru.craftRecipe("heavy-oil-cracking");
        heavyCracking.connectFrom(heavy, water);
        light.connectFrom(heavyCracking);

        Crafter lightCracking = ru.craftRecipe("light-oil-cracking");
        lightCracking.connectFrom(light, water);

        Pipe petroleum = ru.pipe("petroleum-gas");
        petroleum.connectFrom(lightCracking);
        petroleum.connectFrom(refinery);

        Solver solver = new Solver(m, petroleum);
//		System.out.println(solver.solveForInput(output));
        ItemsStack outputRate = new ItemsStack("petroleum-gas", 200);
        print(solver.solveForOutput(petroleum, outputRate), 0);
    }

    private static void circuits(Model m) {
        RecipeUtils ru = new RecipeUtils(m);

        Belt iron = ru.belt("iron-plate");
        Belt copper = ru.belt("copper-plate");
        iron.markInput(); copper.markInput();

        Crafter cableAssembler = ru.craftItem("copper-cable");
        cableAssembler.insertFrom(copper);

        Crafter circuitAssembler = ru.craftItem("electronic-circuit");
        circuitAssembler.insertFrom(iron, cableAssembler);

        Belt output = ru.belt("electronic-circuit");
        output.insertFrom(circuitAssembler);

        Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
        ItemsStack outputRate = new ItemsStack("electronic-circuit", Belt.SPEED_YELLOW);
        print(solver.solveForOutput(output, outputRate), 0);
    }

    private static void ironPlate(Model m) {
        RecipeUtils ru = new RecipeUtils(m);

        Belt iron = ru.belt("iron-ore");
        iron.markInput();

        Crafter smelter = ru.craftItem("iron-plate");
        smelter.insertFrom(iron);

        Belt output = ru.belt("iron-plate");
        output.insertFrom(smelter);

        Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
        ItemsStack outputRate = new ItemsStack("iron-plate", 12);
        print(solver.solveForOutput(output, outputRate), 0);
    }

    private static void sci1(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		Belt copper = ru.belt("copper-plate");

		iron.markInput(); copper.markInput();
		
		Crafter gears = ru.craftItem("iron-gear-wheel");
		gears.insertFrom(iron);
		
		Belt gearBelt = ru.belt("iron-gear-wheel");
		gearBelt.insertFrom(gears);
		
		Crafter science_pack = ru.craftItem("science-pack-1");
		science_pack.insertFrom(copper, gearBelt);

		Belt output = ru.belt("science-pack-1");
		output.insertFrom(science_pack);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsStack outputRate = new ItemsStack("science-pack-1", 1);
		
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	private static void sci2(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		Belt copper = ru.belt("copper-plate");
		Belt circuit = ru.belt("electronic-circuit");

		iron.markInput(); copper.markInput(); circuit.markInput();
		
		
		Crafter gears = ru.craftItem("iron-gear-wheel");
		gears.insertFrom(iron);
		
		Belt gearBelt = ru.belt("iron-gear-wheel");
		gearBelt.insertFrom(gears);

		
		Crafter inserter = ru.craftItem("inserter");
		inserter.insertFrom(circuit);
		inserter.insertFrom(iron);
		inserter.insertFrom(gearBelt);
		
		Belt inserterBelt = ru.belt("inserter");
		inserterBelt.insertFrom(inserter);
		
		
		Crafter transport = ru.craftItem("transport-belt");
		transport.insertFrom(iron);
		transport.insertFrom(gearBelt);
		
		Belt transportBelt = ru.belt("transport-belt");
		transportBelt.insertFrom(transport);
		
		Crafter science_pack = ru.craftItem("science-pack-2");
		science_pack.insertFrom(inserterBelt, transportBelt);

		Belt output = ru.belt("science-pack-2");
		output.insertFrom(science_pack);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsStack outputRate = new ItemsStack("science-pack-2", 1);
		
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	private static void gear(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		iron.markInput();
		
		Crafter gears = ru.craftItem("iron-gear-wheel");
		gears.insertFrom(iron);

		Belt output = ru.belt("iron-gear-wheel");
		output.insertFrom(gears);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsStack outputRate = new ItemsStack("iron-gear-wheel", 1);
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	public static void print(SolveNode node, int tab) {
		String tabStr = String.join("", Collections.nCopies(tab, " "));
//		System.out.println(tabStr + node.entity);
		for(SolveOption option : node.options) {
            if(option.unnecessary) {
                continue;
            }
			System.out.println(tabStr + option);
		}
		
		for(SolveNode source : node.sources) {
			print(source, tab+2);
		}
	}
}
