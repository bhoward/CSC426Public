package edu.depauw.declan.common.icode;

import java.util.List;

/**
 * Intermediate code statement: CALL pname(a1, a2, ..., an)
 * 
 * Calls the named procedure (which is either a label in a PROC statement or an
 * external library procedure) with the given locations as arguments.
 * 
 * @author bhoward
 */
public class Call implements ICode {
	private String pname;
	private List<String> args;

	public Call(String pname, List<String> args) {
		this.pname = pname;
		this.args = args;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CALL " + pname + "(");
		boolean first = true;
		for (String arg : args) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(arg);
		}
		sb.append(")");
		return sb.toString();
	}
}
