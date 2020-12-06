package edu.depauw.declan.common.icode;

import java.util.List;

/**
 * Intermediate code statement: PROC pname(p1, p2, ..., pn)
 * 
 * Marks the entry point to a procedure with the given label name. When the
 * procedure is called with a list of arguments, their values will be stored in
 * the locations p1 through pn.
 *
 * @author bhoward
 */
public class Proc implements ICode {
	private String pname;
	private List<String> params;

	public Proc(String pname, List<String> params) {
		this.pname = pname;
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PROC " + pname + "(");
		boolean first = true;
		for (String param : params) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(param);
		}
		sb.append(")");
		return sb.toString();
	}
}
