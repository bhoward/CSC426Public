package com.craftinginterpreters.declan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Util {
    public static boolean runFiles(List<String> sourceDirs, Consumer<String> run)
            throws IOException, FileNotFoundException {
        List<File> sourceFiles = new ArrayList<>();
        List<File> successFiles = new ArrayList<>();

        for (String sourceDir : sourceDirs) {
            getFiles(new File(sourceDir), sourceFiles);
        }

        for (File sourceFile : sourceFiles) {
            File inputFile = new File(sourceFile.getPath().replace(".dcl", ".in"));
            File outputFile = new File(sourceFile.getPath().replace(".dcl", ".out"));

            byte[] bytes = Files.readAllBytes(sourceFile.toPath());
            String source = new String(bytes);

            // capture stdout and stderr
            PrintStream saveOut = System.out;
            PrintStream saveErr = System.err;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream capture = new PrintStream(baos);
            System.setOut(capture);
            System.setErr(capture);

            // redirect stdin if inputFile exists
            InputStream saveIn = System.in;
            if (inputFile.exists()) {
                InputStream input = new FileInputStream(inputFile);
                System.setIn(input);
            }

            run.accept(source);

            System.setIn(saveIn);
            System.setOut(saveOut);
            System.setErr(saveErr);
            capture.close();
            String actual = baos.toString();

            // look for corresponding .out file
            // -- if exists, compare contents with output; add to success list
            // -- if different, print source, expected, and actual output
            // -- if not, write output to new .out file; print source and output
            if (outputFile.exists()) {
                bytes = Files.readAllBytes(outputFile.toPath());
                String expected = new String(bytes);

                if (actual.equals(expected)) {
                    successFiles.add(sourceFile);
                } else {
                    System.err.println("====== " + sourceFile + "======");
                    System.err.println(source);
                    System.err.println("-- Expected --");
                    System.err.println(expected);
                    System.err.println("-- Actual --");
                    System.err.println(actual);
                }
            } else {
                System.err.println("====== " + sourceFile + "======");
                System.err.println(source);
                System.err.println("-- " + outputFile + " not found --");
                System.err.println("-- Saving actual output --");
                System.err.println(actual);

                PrintWriter out = new PrintWriter(outputFile);
                out.print(actual);
                out.close();
            }
        }

        return sourceFiles.size() == successFiles.size();
    }

    public static void getFiles(File sourceDir, List<File> sourceFiles) {
        if (!sourceDir.exists()) {
            return;
        }

        for (File file : sourceDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".dcl")) {
                sourceFiles.add(file);
            } else if (file.isDirectory()) {
                getFiles(file, sourceFiles);
            }
        }
    }
}
