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

The [original implementation](src/main/scala) is written in the Java-compatible hybrid object-functional language [Scala](https://www.scala-lang.org/).

* [`ASTNode.scala`](src/main/scala/ASTNode.scala) defines the AST nodes as a series of case classes. The four types of statement extend the `Statement` trait, and the three types of expression extend the `Expression` trait. Scala case classes hide most of the implementation details of creating immutable abstract data types, automatically creating accessors for their fields as well as implementations of methods such as `toString` and `equals`. In addition, they enable pattern matching as a way to simultaneously select an appropriate branch of code for each type of statement or expression, and also bind the relevant field values to local variables in the branch. Note that the `Expression` nodes also contain a mutable field to represent the type, which is not known until after the typechecker has traversed the tree (there are more purely-functional ways to do this, but a little bit of mutability is convenient here).

* [Interpreter.scala](src/main/scala/Interpreter.scala) 