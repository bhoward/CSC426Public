package com.craftinginterpreters.declan;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DeCLanTest {
    @Test
    void test() throws IOException {
        List<String> sourceDirs = List.of("src/test/declan");
        assertTrue(Util.runFiles(sourceDirs, s -> {
            DeCLan.reset();
            try {
                DeCLan.run(s);
            } catch (Exception e) {
                System.err.println(e);
            }
        }), "Not all files produced expected output");
    }
}
