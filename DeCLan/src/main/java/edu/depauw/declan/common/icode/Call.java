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

	@Override
	public void execute(State state) {
		int p = state.label.getOrDefault(pname, -1);
		if (p == -1) {
			state.callExternal(pname, args);
		} else {
			state.stack.push(state.pc);
			state.pc = p;
			Proc proc = (Proc) state.program.get(p);
			List<String> params = proc.getParams();
			for (int i = 0; i < args.size(); i++) {
				String arg = args.get(i);
				String param = params.get(i);
				state.store.put(param, state.store.get(arg));
			}
		}
	}
}
