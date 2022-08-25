package com.craftinginterpreters.lox2.ch22;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.craftinginterpreters.lox2.util.Util;

class VMTest {
    @Test
    void test() throws IOException {
        List<String> sourceDirs = List.of("src/test/lox/ch21", "src/test/lox/ch22");
        assertTrue(Util.runFiles(sourceDirs, s -> {
            VM vm = new VM();
            vm.interpret(s);
        }), "Not all files produced expected output");
    }
}
