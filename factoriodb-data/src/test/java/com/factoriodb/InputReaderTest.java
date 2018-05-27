package com.factoriodb;

import com.factoriodb.input.InputReader;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * @author austinjones
 */
public class InputReaderTest {
    @Test
    public void testInputReader() {
        try {
            InputReader.load();
        } catch (IOException e) {
            e.printStackTrace();
            fail("InputReader threw exception");
        }
    }
}
