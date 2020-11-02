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
		String lex = id.getLexeme();
		
		if (type == Type.INT) {
			symtab.put(lex, new IntValue(0));
		} else {
			symtab.put(lex, new RealValue(0.0));
		}
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		String lex = id.getLexeme();
		
		if (symtab.containsKey(lex)) {
			throw new RuntimeException("Variable " + lex + " already declared");
		} else {
			symtab.put(lex, type);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab) {
		String lex = id.getLexeme();
		
		symtab.put(lex, Generator.newvar("v"));
		return Arrays.asList();
	}
}
