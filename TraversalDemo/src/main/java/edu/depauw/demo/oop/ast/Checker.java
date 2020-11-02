package edu.depauw.demo.oop.ast;

public class Checker {
	public static void check(Type expected, Type actual) {
		if (expected != actual) {
			throw new RuntimeException("Type mismatch: expected " + expected + ", found " + actual);
		}
	}
}
