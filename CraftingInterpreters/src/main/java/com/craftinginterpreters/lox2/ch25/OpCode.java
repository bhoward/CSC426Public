package com.craftinginterpreters.lox2.ch25;

public class OpCode {
    public static final byte OP_CONSTANT = 0;
    public static final byte OP_NIL = 1;
    public static final byte OP_TRUE = 2;
    public static final byte OP_FALSE = 3;
    public static final byte OP_POP = 4;
    public static final byte OP_GET_LOCAL = 5;
    public static final byte OP_SET_LOCAL = 6;
    public static final byte OP_GET_GLOBAL = 7;
    public static final byte OP_DEFINE_GLOBAL = 8;
    public static final byte OP_SET_GLOBAL = 9;
    public static final byte OP_GET_UPVALUE = 10;
    public static final byte OP_SET_UPVALUE = 11;
    public static final byte OP_EQUAL = 12;
    public static final byte OP_GREATER = 13;
    public static final byte OP_LESS = 14;
    public static final byte OP_ADD = 15;
    public static final byte OP_SUBTRACT = 16;
    public static final byte OP_MULTIPLY = 17;
    public static final byte OP_DIVIDE = 18;
    public static final byte OP_NOT = 19;
    public static final byte OP_NEGATE = 20;
    public static final byte OP_PRINT = 21;
    public static final byte OP_JUMP = 22;
    public static final byte OP_JUMP_IF_FALSE = 23;
    public static final byte OP_LOOP = 24;
    public static final byte OP_CALL = 25;
    public static final byte OP_CLOSURE = 26;
    public static final byte OP_CLOSE_UPVALUE = 27;
    public static final byte OP_RETURN = 28;
}
