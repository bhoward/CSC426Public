package edu.depauw.declan.common.icode;

/**
 * Marker interface that serves as the parent of all intermediate code
 * instructions.
 * 
 * @author bhoward
 */
public interface ICode {
	/**
	 * Perform the run-time action of this instruction.
	 * 
	 * @param state
	 */
	void execute(State state);
}
