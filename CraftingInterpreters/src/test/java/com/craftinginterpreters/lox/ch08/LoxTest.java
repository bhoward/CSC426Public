package com.craftinginterpreters.lox.ch08;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.craftinginterpreters.lox.util.Util;

public class LoxTest {
    @Test
    void test() throws IOException {
        List<String> sourceDirs = List.of("src/test/lox/ch08");
        assertTrue(Util.runFiles(sourceDirs, s -> {
            Lox.reset();
            Lox.run(s);
        }), "Not all files produced expected output");
    }
}
