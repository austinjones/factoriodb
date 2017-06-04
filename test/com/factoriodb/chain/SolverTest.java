package com.factoriodb.chain;

import com.factoriodb.Main;
import com.factoriodb.chain.assembler.Assembler;
import com.factoriodb.model.Model;
import com.factoriodb.Solver;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.ModelUtils;
import com.factoriodb.recipe.RecipeUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author austinjones
 */
public class SolverTest {
    @Test
    public void testTransitive() {
        Model m = ModelUtils.getTestModel();
        RecipeUtils ru = new RecipeUtils(m);

        Pipe raw = ru.pipe("raw-fluid");
        raw.markInput();

        Crafter refinery = ru.craftRecipe("fluid-processing");
        refinery.connectFrom(raw);

        Pipe spaceFluid = ru.pipe("space-fluid");
        spaceFluid.connectFrom(refinery);

        Pipe badFluid = ru.pipe("bad-fluid");
        badFluid.connectFrom(refinery);

        Crafter cracking = ru.craftRecipe("bad-fluid-cracking");
        cracking.connectFrom(badFluid);
        spaceFluid.connectFrom(cracking);

        Solver solver = new Solver(m, spaceFluid);
        Solver.SolveNode node = solver.solveForOutput(spaceFluid, new ItemsStack("space-fluid", 4));

        Main.print(node, 0);
    }

    @Test
    public void testSolver() {
        Model m = ModelUtils.getTestModel();

        Belt input = new Belt(m, "test-ore");
        input.markInput();

        Crafter a = CrafterTest.testAssembler(m);
        a.insertFrom(input);

        Belt output = new Belt(m, "test-plate");
        output.insertFrom(a);

        Solver solver = new Solver(m, output);
        Solver.SolveNode node = solver.solveForOutput(output, new ItemsStack("test-plate", 4));

        Main.print(node, 0);
        Solver.SolveOption transportBelt = node.options.get(0);
        assertEquals("1x transport-belt-1", transportBelt.option.name());
        assertEquals(new ItemsStack("test-plate", 4), transportBelt.input);
        assertEquals(new ItemsStack("test-plate", 4), transportBelt.inputRequest);
        assertEquals(new ItemsStack("test-plate", 4), transportBelt.output);
        assertEquals(0.3, transportBelt.usageRatio, 0.01);

        assertEquals(1, node.sources.size());
        Solver.SolveNode inserterSource = node.sources.get(0);
        Solver.SolveOption inserterOption = inserterSource.options.get(0);
        assertEquals("5x inserter", inserterOption.option.name());
        assertEquals(0.96, inserterOption.usageRatio, 0.01);

        inserterOption = inserterSource.options.get(1);
        assertEquals("4x long-handed-inserter", inserterOption.option.name());
        assertEquals(0.87, inserterOption.usageRatio, 0.01);
    }
}
