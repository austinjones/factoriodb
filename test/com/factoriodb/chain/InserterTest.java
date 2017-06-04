package com.factoriodb.chain;

import com.factoriodb.model.Model;
import com.factoriodb.chain.option.InserterOption;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.ModelUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author austinjones
 */
public class InserterTest {
    public static Inserter testInserter(Model m) {
        return new Inserter(BeltTest.testBelt(m), CrafterTest.testAssembler(m));
    }

    public static InserterOption testInserterOption(Inserter b) {
        return new InserterOption(b, "Inserter-a", 3, 2);
    }

    @Test
    public void testAvailableOutput() {
        Model m = ModelUtils.getTestModel();
        Inserter testInserter = testInserter(m);
        InserterOption option = testInserterOption(testInserter);

        ItemsStack someOre = new ItemsStack("test-ore", 16);
        ItemsStack littleOre = new ItemsStack("test-ore", 4);

        assertEquals(new ItemsStack("test-ore", 6),
                option.availableOutputLimited(someOre));


        assertEquals(littleOre,
                option.availableOutputLimited(littleOre));

        assertEquals(littleOre,
                option.availableOutputLimited(someOre, littleOre));

        assertEquals(littleOre,
                option.availableOutputLimited(littleOre, someOre));
    }

    @Test
    public void testRequestedInput() {
        Model m = ModelUtils.getTestModel();
        Inserter testInserter = testInserter(m);
        InserterOption option = testInserterOption(testInserter);

        ItemsStack someOre = new ItemsStack("test-ore", 16);
        ItemsStack littleOre = new ItemsStack("test-ore", 4);

        assertEquals(new ItemsStack("test-ore", 6),
                option.requestedInputLimited(someOre));

        assertEquals(littleOre,
                option.requestedInputLimited(littleOre));
    }


    @Test
    public void testMaxIO() {
        Model m = ModelUtils.getTestModel();
        Inserter testInserter = testInserter(m);
        InserterOption option = testInserterOption(testInserter);

        assertEquals(6, option.maxInput(), 0);
        assertEquals(6, option.maxOutput(), 0);
    }

    @Test
    public void testCost() {
        Model m = ModelUtils.getTestModel();
        Inserter testInserter = testInserter(m);
        InserterOption option = testInserterOption(testInserter);

        assertEquals(6, option.constructionCost(), 0);
        assertEquals(1.0, option.placementCost(), 0);
    }

    @Test
    public void testIsCrafterConnection() {
        Model m = ModelUtils.getTestModel();
        Inserter testInserter = testInserter(m);
        InserterOption option = testInserterOption(testInserter);

        assertFalse(option.isCrafter());
        assertTrue(option.isConnection());
    }
}
