package edu.depauw.declan.common.ast;

/**
 * As part of the Visitor pattern, a DeclarationVisitor encapsulates an
 * algorithm that walks the Declaration nodes of an abstract syntax tree and
 * returns a result of type R. There is one overloaded version of the
 * visitResult() method for each type of Declaration. The visitor is responsible
 * for controlling the traversal of the tree by calling .acceptResult(this) on
 * each subnode at the appropriate time.
 * 
 * @author bhoward
 */
public interface DeclarationVisitor<R> {
	R visitResult(ConstDeclaration constDeclaration);

	R visitResult(VarDeclaration varDeclaration);
	
	R visitResult(ProcedureDeclaration procedureDeclaration);

}
