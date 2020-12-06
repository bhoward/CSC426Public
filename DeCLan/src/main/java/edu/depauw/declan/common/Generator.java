package edu.depauw.declan.common;

import java.util.List;

import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.icode.ICode;

/**
 * A Generator is used to generate intermediate code from a DeCLan program.
 * 
 * @author bhoward
 */
public interface Generator {
	List<ICode> generate(Program program);
}
