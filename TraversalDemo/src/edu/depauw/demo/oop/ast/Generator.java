package edu.depauw.demo.oop.ast;

public class Generator {
	private static int sequenceNumber = 0;
	
	public static String newvar() {
		return newvar("t");
	}
	
	public static String newvar(String prefix) {
		sequenceNumber++;
		return prefix + sequenceNumber;
	}
}
