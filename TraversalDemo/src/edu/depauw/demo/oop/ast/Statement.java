package edu.depauw.demo.oop.ast;

import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.Value;

public interface Statement extends ASTNode {
	void interpret(Map<String, Value> symtab);

	void typecheck(Map<String, Type> symtab);

	List<ICode> generate(Map<String, String> symtab);
}
