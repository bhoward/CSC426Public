package com.craftinginterpreters.lox2.ch17a;

import java.util.HashMap;
import java.util.Map;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch15.OpCode;
import com.craftinginterpreters.lox2.ch17.Precedence;
import com.craftinginterpreters.lox2.ch17a.Parser.InfixParselet;
import com.craftinginterpreters.lox2.ch17a.Parser.PrefixParselet;

public class LoxParselets implements Parselets {
    private Map<TokenType, PrefixParselet> prefix;
    private Map<TokenType, InfixParselet> infix;

    public LoxParselets(Compiler compiler) {
        prefix = new HashMap<>();
        infix = new HashMap<>();

        prefix.put(TokenType.LEFT_PAREN, new PrefixParselet() {
            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.ASSIGNMENT);
                parser.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            }
        });

        prefix.put(TokenType.MINUS, new PrefixParselet() {
            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.UNARY);
                compiler.emitByte(OpCode.OP_NEGATE);
            }
        });

        prefix.put(TokenType.NUMBER, new PrefixParselet() {
            public void parse(Parser parser, Token token) {
                double value = Double.parseDouble(token.lexeme);
                compiler.emitConstant(value);
            }
        });

        infix.put(TokenType.PLUS, new InfixParselet() {
            public Precedence precedence() {
                return Precedence.TERM;
            }

            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.FACTOR);
                compiler.emitByte(OpCode.OP_ADD);
            }
        });

        infix.put(TokenType.MINUS, new InfixParselet() {
            public Precedence precedence() {
                return Precedence.TERM;
            }

            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.FACTOR);
                compiler.emitByte(OpCode.OP_SUBTRACT);
            }
        });

        infix.put(TokenType.STAR, new InfixParselet() {
            public Precedence precedence() {
                return Precedence.FACTOR;
            }

            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.UNARY);
                compiler.emitByte(OpCode.OP_MULTIPLY);
            }
        });

        infix.put(TokenType.SLASH, new InfixParselet() {
            public Precedence precedence() {
                return Precedence.FACTOR;
            }

            public void parse(Parser parser, Token token) {
                parser.parsePrecedence(Precedence.UNARY);
                compiler.emitByte(OpCode.OP_DIVIDE);
            }
        });
    }

    public PrefixParselet getPrefix(TokenType type) {
        return prefix.get(type);
    }

    public InfixParselet getInfix(TokenType type) {
        return infix.get(type);
    }

    public Precedence getPrecedence(TokenType type) {
        if (infix.containsKey(type)) {
            return infix.get(type).precedence();
        } else {
            return Precedence.NONE;
        }
    }
}
