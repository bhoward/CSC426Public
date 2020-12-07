package edu.depauw.declan.common.icode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Maintains the state of a machine that can execute ICode. Contains a loaded
 * program (list of ICode objects), a map from label to instruction index, a map
 * from location name to value, a stack of return addresses, and a program
 * counter.
 * 
 * @author bhoward
 */
public class State {
	List<ICode> program;
	Map<String, Integer> label;
	Map<String, Object> store;
	Stack<Integer> stack;
	int pc;

	public State(List<ICode> program) {
		this.program = program;
		this.label = new HashMap<>();
		this.store = new HashMap<>();
		this.stack = new Stack<>();
		this.pc = 0;

		for (int i = 0; i < program.size(); i++) {
			ICode instr = program.get(i);
			if (instr instanceof Label) {
				Label lbl = (Label) instr;
				label.put(lbl.getName(), i);
			} else if (instr instanceof Proc) {
				Proc proc = (Proc) instr;
				label.put(proc.getName(), i);
			}
		}
	}

	public void run() {
		pc = 0;
		while (pc >= 0) {
			ICode instr = program.get(pc);
			pc += 1;
			instr.execute(this);
		}
	}

	public void callExternal(String pname, List<String> args) {
		switch (pname) {
		case "PrintLn":
			System.out.println();
			break;
			
		case "PrintInt": case "PrintReal": case "PrintString":
			System.out.print(store.get(args.get(0)));
			break;
			
		case "ASSERT":
			int test = (int) store.get(args.get(0));
			String message = (String) store.get(args.get(1));
			if (test != 1) {
				System.err.println(message);
				System.exit(1);
			}
			break;
			
		default:
			System.err.println("Unknown external procedure: " + pname);
			System.exit(1);
		}
	}
}
