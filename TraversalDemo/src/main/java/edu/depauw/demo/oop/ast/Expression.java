package edu.depauw.demo.oop.ast;

import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.Value;

public interface Expression extends ASTNode {
	Type getType();
	
	void setType(Type type);
	
	Value interpret(Map<String, Value> symtab);

	void typecheck(Map<String, Type> symtab);

	List<ICode> generate(Map<String, String> symtab, String place);
}
