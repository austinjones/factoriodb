package com.factoriodb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import com.factoriodb.Solver.SolveNode;
import com.factoriodb.Solver.SolveOption;
import com.factoriodb.chain.Assembler;
import com.factoriodb.chain.Belt;
import com.factoriodb.model.ItemsFlow;
import com.factoriodb.recipe.RecipeUtils;
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Model m = Model.load();
		System.out.println("Missing items: " + m.getMissingItems());
		System.out.println("Missing recipes: " + m.getMissingRecipes());

		System.out.println("");
		System.out.println("");
		System.out.println("");

		System.out.println("Sci1:");
		sci1(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		System.out.println("Sci2:");
		sci2(m);

		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		System.out.println("Gears:");
		gear(m);
	}
	
	private static void sci1(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		Belt copper = ru.belt("copper-plate");

		iron.markInput(); copper.markInput();
		
		Assembler gears = ru.assembler("iron-gear-wheel");
		gears.insertFrom(iron);
		
		Belt gearBelt = ru.belt("iron-gear-wheel");
		gearBelt.insertFrom(gears);
		
		Assembler science_pack = ru.assembler("science-pack-1");
		science_pack.insertFrom(copper, gearBelt);

		Belt output = ru.belt("science-pack-1");
		output.insertFrom(science_pack);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsFlow outputRate = new ItemsFlow("science-pack-1", 1);
		
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	private static void sci2(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		Belt copper = ru.belt("copper-plate");
		Belt circuit = ru.belt("electronic-circuit");

		iron.markInput(); copper.markInput(); circuit.markInput();
		
		
		Assembler gears = ru.assembler("iron-gear-wheel");
		gears.insertFrom(iron);
		
		Belt gearBelt = ru.belt("iron-gear-wheel");
		gearBelt.insertFrom(gears);

		
		Assembler inserter = ru.assembler("inserter");
		inserter.insertFrom(circuit);
		inserter.insertFrom(iron);
		inserter.insertFrom(gearBelt);
		
		Belt inserterBelt = ru.belt("inserter");
		inserterBelt.insertFrom(inserter);
		
		
		Assembler transport = ru.assembler("transport-belt");
		transport.insertFrom(iron);
		transport.insertFrom(gearBelt);
		
		Belt transportBelt = ru.belt("transport-belt");
		transportBelt.insertFrom(transport);
		
		Assembler science_pack = ru.assembler("science-pack-2");
		science_pack.insertFrom(inserterBelt, transportBelt);

		Belt output = ru.belt("science-pack-2");
		output.insertFrom(science_pack);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsFlow outputRate = new ItemsFlow("science-pack-2", 1);
		
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	private static void gear(Model m) {
		RecipeUtils ru = new RecipeUtils(m);
		
		Belt iron = ru.belt("iron-plate");
		iron.markInput();
		
		Assembler gears = ru.assembler("iron-gear-wheel");
		gears.insertFrom(iron);

		Belt output = ru.belt("iron-gear-wheel");
		output.insertFrom(gears);
		
		Solver solver = new Solver(m, output);
//		System.out.println(solver.solveForInput(output));
		ItemsFlow outputRate = new ItemsFlow("iron-gear-wheel", 1);
		print(solver.solveForOutput(output, outputRate), 0);
	}
	
	private static void print(SolveNode node, int tab) {
		String tabStr = String.join("", Collections.nCopies(tab, " "));
//		System.out.println(tabStr + node.entity);
		for(SolveOption option : node.options) {
			System.out.println(tabStr + option);
		}
		
		for(SolveNode source : node.sources) {
			print(source, tab+2);
		}
	}
}
