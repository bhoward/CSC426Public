package edu.depauw.declan.common.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Describes the various types that can be encountered while typechecking a
 * DeCLan program. Types include:
 * 
 * BaseType: BOOLEAN, INTEGER, REAL, STRING
 * 
 * Proc(params): params is a list of Val(T) (in full DeCLan, may also be Var(T))
 * 
 * Const(T), Val(T), Var(T), where T is a BaseType:
 * 
 * -- Const(T) is a CONST identifier or constant expression
 * 
 * -- Val(T) is a value parameter or non-constant expression
 * 
 * -- Var(T) is a variable
 * 
 * @author bhoward
 */
public interface Type {
	default boolean isBaseType() {
		return false;
	}

	class BaseType implements Type {
		private String name;

		private BaseType(String name) {
			this.name = name;
		}

		public static final Type.BaseType BOOLEAN = new BaseType("BOOLEAN");
		public static final Type.BaseType INTEGER = new BaseType("INTEGER");
		public static final Type.BaseType REAL = new BaseType("REAL");
		public static final Type.BaseType STRING = new BaseType("STRING");

		@Override
		public boolean isBaseType() {
			return true;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	class ProcType implements Type {
		private List<Type.ExprType> params;

		public ProcType(List<Type.ExprType> params) {
			this.params = params;
		}

		public ProcType(Type.ExprType... params) {
			this(Collections.unmodifiableList(Arrays.asList(params)));
		}

		public List<Type.ExprType> getParams() {
			return params;
		}
	}

	interface ExprType extends Type {
		default boolean isConst() {
			return false;
		}

		default boolean isVar() {
			return false;
		}

		Type.BaseType getType();
	}

	class Val implements Type.ExprType {
		private Type.BaseType type;

		public Val(Type.BaseType type) {
			this.type = type;
		}

		public Type.BaseType getType() {
			return type;
		}
	}

	class Var implements Type.ExprType {
		private Type.BaseType type;

		public Var(Type.BaseType type) {
			this.type = type;
		}

		public Type.BaseType getType() {
			return type;
		}

		public boolean isVar() {
			return true;
		}
	}

	class Const implements Type.ExprType {
		private Type.BaseType type;

		public Const(Type.BaseType type) {
			this.type = type;
		}

		@Override
		public boolean isConst() {
			return true;
		}

		public Type.BaseType getType() {
			return type;
		}
	}
}