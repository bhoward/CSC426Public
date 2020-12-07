package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: END
 * 
 * Halts execution of the program.
 * 
 * @author bhoward
 */
public class End implements ICode {
	@Override
	public String toString() {
		return "END";
	}

	@Override
	public void execute(State state) {
		state.pc = -1;
	}
}
