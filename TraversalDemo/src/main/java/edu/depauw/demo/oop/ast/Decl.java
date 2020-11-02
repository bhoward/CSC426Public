package edu.depauw.demo.oop.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.IntValue;
import edu.depauw.demo.common.RealValue;
import edu.depauw.demo.common.Value;

public class Decl implements Statement {
	private Var id;
	private Type type;
	
	public Decl(Var id, Type type) {
		this.id = id;
		this.type = type;
	}

	public Var getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	@Override
	public void interpret(Map<String, Value> symtab) {
		String lexeme = id.getLexeme();
		
		if (type == Type.INT) {
			symtab.put(lexeme, new IntValue(0));
		} else {
			symtab.put(lexeme, new RealValue(0.0));
		}
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		String lexeme = id.getLexeme();
		
		if (symtab.containsKey(lexeme)) {
			throw new RuntimeException("Variable " + lexeme + " already declared");
		} else {
			symtab.put(lexeme, type);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab) {
		String lexeme = id.getLexeme();
		
		symtab.put(lexeme, Generator.newvar("v"));
		return Arrays.asList();
	}
}
