package com.factoriodb.chain;

import com.factoriodb.model.Model;
import com.factoriodb.chain.option.BeltOption;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.ModelUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author austinjones
 */
public class BeltTest {
    public static Belt testBelt(Model m) {
        return new Belt(m, "test-ore");
    }

    public static BeltOption testBeltOption(Belt b) {
        return new BeltOption(b, "belt-a", 8);
    }

    @Test
    public void testAvailableOutput() {
        Model m = ModelUtils.getTestModel();
        Belt testBelt = testBelt(m);
        BeltOption option = testBeltOption(testBelt);

        ItemsStack someOre = new ItemsStack("test-ore", 16);
        ItemsStack littleOre = new ItemsStack("test-ore", 4);

        assertEquals(new ItemsStack("test-ore", 8),
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
        Belt testBelt = testBelt(m);
        BeltOption option = testBeltOption(testBelt);

        ItemsStack someOre = new ItemsStack("test-ore", 16);
        ItemsStack littleOre = new ItemsStack("test-ore", 4);

        assertEquals(new ItemsStack("test-ore", 8),
                option.requestedInputLimited(someOre));

        assertEquals(littleOre,
                option.requestedInputLimited(littleOre));
    }


    @Test
    public void testMaxIO() {
        Model m = ModelUtils.getTestModel();
        Belt testBelt = testBelt(m);
        BeltOption option = testBeltOption(testBelt);

        assertEquals(8, option.maxInput(), 0);
        assertEquals(8, option.maxOutput(), 0);
    }

    @Test
    public void testCost() {
        Model m = ModelUtils.getTestModel();
        Belt testBelt = testBelt(m);
        BeltOption option = testBeltOption(testBelt);

        assertEquals(8.0, option.constructionCost(), 0);
        assertEquals(1.0, option.placementCost(), 0);
    }

    @Test
    public void testIsCrafterConnection() {
        Model m = ModelUtils.getTestModel();
        Belt testBelt = testBelt(m);
        BeltOption option = testBeltOption(testBelt);

        assertFalse(option.isCrafter());
        assertTrue(option.isConnection());
    }
}
