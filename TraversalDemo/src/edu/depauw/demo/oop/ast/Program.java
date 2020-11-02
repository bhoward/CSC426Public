package edu.depauw.demo.oop.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.Value;

public class Program implements ASTNode {
	private List<Statement> statements;

	public Program(List<Statement> statements) {
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void interpret() {
		Map<String, Value> symtab = new HashMap<>();

		for (Statement stmt : statements) {
			stmt.interpret(symtab);
		}
	}

	public void typecheck() {
		Map<String, Type> symtab = new HashMap<>();
		
		for (Statement stmt : statements) {
			stmt.typecheck(symtab);
		}
	}

	public List<ICode> generate() {
		Map<String, String> symtab = new HashMap<>();
		List<ICode> result = new ArrayList<>();

		for (Statement stmt : statements) {
			result.addAll(stmt.generate(symtab));
		}
		
		return result;
	}
}
