package edu.depauw.declan;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Token;

/**
 * Main class for Project 1 -- Scanner for DeCLan (Fall 2020). Scans tokens from
 * standard input and prints the token stream to standard output.
 * 
 * @author bhoward
 */
public class Project1 {
	public static void main(String[] args) {
		Config config = new Config(args);
		
		try (Lexer lexer = config.getLexer()) {
			while (lexer.hasNext()) {
				Token token = lexer.next();
				System.out.println(token);
			}
		}
		
		System.out.println("DONE");
	}
}
