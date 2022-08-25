package com.craftinginterpreters.lox2.ch17a;

import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch17.Precedence;
import com.craftinginterpreters.lox2.ch17a.Parser.InfixParselet;
import com.craftinginterpreters.lox2.ch17a.Parser.PrefixParselet;

public interface Parselets {
    PrefixParselet getPrefix(TokenType type);

    InfixParselet getInfix(TokenType type);

    Precedence getPrecedence(TokenType type);
}
