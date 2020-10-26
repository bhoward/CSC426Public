package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a string literal. There are no operations on strings
 * in DeCLan, so the only thing that can be done with a StringValue is to send
 * its contents to a library method such as "PrintString".
 * 
 * @author bhoward
 */
public class StringValue extends AbstractASTNode implements Expression {
	private final String contents;

	/**
	 * Construct a StringValue ast node starting at the specified Position, with the
	 * given String contents.
	 * 
	 * @param start
	 * @param contents
	 */
	public StringValue(Position start, String contents) {
		super(start);
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(ExpressionVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
