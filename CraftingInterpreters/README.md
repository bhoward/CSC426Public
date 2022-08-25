This repository contains the code from Robert Nystrom's "[Crafting Interpreters](http://craftinginterpreters.com/)" ([GitHub](https://github.com/munificent/craftinginterpreters)), adapted and arranged for use in the DePauw University course CSC 426, Compilers, by Brian Howard.

The src/main/java directory contains two collections of packages:

* com.craftinginterpreters.lox.* is the original code for jlox, split out into individual versions for each chapter (4 through 13), as well as a package labeled "preview" which uses pattern matching on records (released as a preview feature in Java 17) instead of the Visitor pattern.

* com.craftinginterpreters.lox2.* is a Java port of the chapter-by-chapter code for clox (chapters 14 through 30); it makes use of Java's Strings, HashMaps, and garbage collection instead of implementing these from scratch.

In src/test/java are JUnit tests for each of the chapters, which work by running .lox files from src/test/lox through the corresponding interpreters and checking that the output matches the expected output in the .out or .out2 files. The first time a .lox file is encountered (with no matching output file), the test will fail but the actual output will be saved as a new expected output file. These should be inspected to verify that the output is correct, and from then on the test will pass as long as no regression (different output) is detected.