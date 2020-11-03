# Abstract Syntax Tree Traversal Demo

This project contains four implementations of an interpreter, typechecker, and intermediate code generator for a (very) simple language.
Programs in this language consist of variable declarations, assignments, and print statements.
Expressions are made up of numbers, variables, and addition.
Variables may be declared as `INT` or `REAL`; numeric literals are of type `REAL` if they contain a decimal point and `INT` if they are entirely composed of decimal digits.
In addition and assignment, all of the types involved must match&mdash;there are no conversions between types.

## AST Nodes

* A `Program` contains a list of statements to be executed in order.
* A `Decl` is a statement that declares a new variable and its type.
* An `Assign` is a statement that assigns the result of an expression (the "right-hand-side") to a variable (the "left-hand-side").
* The `PrintInt` and `PrintReal` statements evaluate an expression of the appropriate type and print the result to the console.
* A `Num` is an expression containing the lexeme of a numeric literal.
* A `Var` is an expression containing the lexeme of its variable name.
* An `Add` expression contains two subexpressions to be evaluated and added.

## Scala Version

The original implementation is written in the hybrid object-functional language [Scala](https://www.scala-lang.org/).