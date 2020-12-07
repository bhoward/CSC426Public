package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: RETURN
 * 
 * End execution of a procedure, and branch back to the instruction following
 * the CALL instruction that started the procedure.
 *
 * @author bhoward
 */
public class Return implements ICode {
	@Override
	public String toString() {
		return "RETURN";
	}

	@Override
	public void execute(State state) {
		state.pc = state.stack.pop();
	}
}
