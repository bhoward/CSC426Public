package edu.depauw.declan.common.icode;

import java.util.List;

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
