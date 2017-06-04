package com.factoriodb.chain;

import com.factoriodb.chain.assembler.Assembler;
import com.factoriodb.model.Model;
import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.ModelUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author austinjones
 */
public class CrafterTest {
    public static Crafter testAssembler(Model m) {
        return new Crafter(m, "test-plate");
    }

    public static AssemblerOption testAssemblerOption(Crafter a) {
        return new AssemblerOption(a, "assembler-a", 2);
    }

    @Test
    public void testAvailableOutput() {
        Model m = ModelUtils.getTestModel();
        Crafter assembler = testAssembler(m);
        AssemblerOption option = testAssemblerOption(assembler);

        ItemsStack somePlate = new ItemsStack("test-plate", 1);
        ItemsStack someOre = new ItemsStack("test-ore", 5);
        ItemsStack littleOre = new ItemsStack("test-ore", 0.2);

        assertEquals(new ItemsStack("test-plate", 8),
                option.availableOutputLimited(somePlate));

        assertEquals(new ItemsStack("test-plate", 8),
                option.availableOutputLimited(somePlate, someOre));

        assertEquals(new ItemsStack("test-plate", 0.4),
                option.availableOutputLimited(somePlate, littleOre));
    }

    @Test
    public void testRequestedInput() {
        Model m = ModelUtils.getTestModel();
        Crafter assembler = testAssembler(m);
        AssemblerOption option = testAssemblerOption(assembler);


        ItemsStack somePlate = new ItemsStack("test-plate", 1);

        assertEquals(new ItemsStack("test-ore", 0.5),
                option.requestedInputLimited(somePlate));
    }

    @Test
    public void testMaxIO() {
        Model m = ModelUtils.getTestModel();
        Crafter assembler = testAssembler(m);
        AssemblerOption option = testAssemblerOption(assembler);

        assertEquals(8.0, option.maxOutput(), 0);
        assertEquals(4.0, option.maxInput(), 0);
    }

    @Test
    public void testCost() {
        Model m = ModelUtils.getTestModel();
        Crafter assembler = testAssembler(m);
        AssemblerOption option = testAssemblerOption(assembler);

        assertEquals(2.0, option.constructionCost(), 0);
        assertEquals(1.0, option.placementCost(), 0);
    }

    @Test
    public void testIsCrafterConnection() {
        Model m = ModelUtils.getTestModel();
        Crafter assembler = testAssembler(m);
        AssemblerOption option = testAssemblerOption(assembler);

        assertTrue(option.isCrafter());
        assertFalse(option.isConnection());
    }

}
