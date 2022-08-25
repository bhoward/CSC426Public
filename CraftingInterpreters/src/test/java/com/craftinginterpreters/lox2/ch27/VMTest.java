package com.craftinginterpreters.lox2.ch27;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.craftinginterpreters.lox2.util.Util;

class VMTest {
    @Test
    void test() throws IOException {
        List<String> sourceDirs = List.of("src/test/lox/ch21", "src/test/lox/ch22", "src/test/lox/ch23",
                "src/test/lox/ch24", "src/test/lox/ch25", "src/test/lox/ch27");
        assertTrue(Util.runFiles(sourceDirs, s -> {
            VM vm = new VM();
//            vm.setDebugPrintCode(true);
            vm.interpret(s);
        }), "Not all files produced expected output");
    }
}
