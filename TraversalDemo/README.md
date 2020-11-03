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

* [`Interpreter.scala`](src/main/scala/Interpreter.scala) defines an interpreter via pattern-matching. It uses a symbol table mapping variables to values (see [`Value.scala`](src/main/scala/Value.scala)). To interpret a program, it creates an empty symbol table and then interprets each statement in order. The overloaded version of `interpret` that takes a `Statement` selects the appropriate branch that matches the statement (note that there are three branches for the `Decl` case, depending on whether the type is `INT`, `REAL`, or `UNKNOWN`&mdash;this last one shouldn't happen, but Scala checks to see that all possible cases are covered in a match expression). If the statement contains an expression, the overloaded `Expression` version of `interpret` is called to evaluate the expression and return a `Value`. Only enough typechecking is done to be sure that addition is performed between two values of the same type, and that variables have been declared before use.

* [`Checker.scala`](src/main/scala/Checker.scala) performs a pass over the program that is very similar to the interpreter (indeed, typechecking is a form of "abstract interpretation", where the values are types instead of numbers). The structure of the code is almost the same, except the symbol table maps variables to types (see [`Type.scala`](src/main/scala/Type.scala)), and typechecking an expression results in the AST nodes for the parts of the expression being annotated with their types (stored in the mutable `typ` field&mdash;the field is named `typ` instead of `type` because the latter is a keyword in Scala). If any type errors are found, the `sys.error` method is called to immediately print out an error message and halt the program.

* [`Generator.scala`](src/main/scala/Generator.scala) is another interpreter-like function that performs a pattern-matching traversal of the program and returns a list of intermediate code statements (see [`ICode.scala`](src/main/scala/ICode.scala)). The intermediate code is based on the code from [Section 7.3 of the Mogensen text](http://hjemmesider.diku.dk/~torbenm/Basics/basics_lulu2.pdf#section.7.3). The symbol table maps each program variable to the corresponding intermediate code variable (which are given names of the form `vn`, where `n` is a positive integer). The `Generator.newvar()` function is also used to create names for temporary storage places (which are given names of the form `tn`, just as in Mogensen).

* [`Demo.scala`](src/main/scala/Demo.scala) is a sample program that creates an AST, then calls the interpreter, typechecker, and code generator in turn, and finally prints out the lines of the generated code.

## Pattern-Matching Java Version

The [`edu.depauw.demo.patmat`](src/main/java/edu/depauw/demo/patmat) and [`edu.depauw.demo.patmat.ast`](src/main/java/edu/depauw/demo/patmat/ast) packages contain a straightforward translation of the Scala version into Java.
The AST nodes are defined as ordinary classes implementing the `ASTNode`, `Statement`, and `Expression` interfaces, and almost all of the code is devoted to the necessary fields, constructors, and getters.
The `Expression` classes implement a common `AbstractExpression` base class, which provides the `type` field present in every expression class.

Pattern matching is handled as a series of `if (... instanceof ...)` statements. When a matching branch is found, the node is downcast to the matching type and its fields are extracted into local variables. Beyond that, the code is almost identical to the Scala version.

## Visitor Pattern Java Version

The [`edu.depauw.demo.visitor`](src/main/java/edu/depauw/demo/visitor) and [`edu.depauw.demo.visitor.ast`](src/main/java/edu/depauw/demo/visitor/ast) packages give a corresponding implementation in Java using the Visitor pattern.
This is actually a slight extension of the Visitor pattern that we have seen before, where each visitor is parameterized by two types: a result type `R` returned by each `visit` method, along with an argument type `T` that specifies an extra argument that is passed in to `visit` (in the terminology of the text, the `T` values are "inherited" attributes being passed down the tree, while the `R` values are "synthesized" attributes being passed back up).
If either of these type parameters is not needed for a particular visitor, it is specified as `Void`&mdash;this is a Java library class with no implementation, so the only legal value is `null`.

Note that each branch of the pattern-matching version becomes one `visit` method in an appropriate visitor object.
Because the types of attributes passed in and out of statement nodes and expression nodes differ, there are separate interfaces to define [`StatementVisitor`](src/main/java/edu/depauw/demo/visitor/ast/StatementVisitor.java) and [`ExpressionVisitor`](src/main/java/edu/depauw/demo/visitor/ast/ExpressionVisitor.java) methods, although in each of the interpreter, typechecker, and code generator cases the same object is used for both kinds of visitor (partly because the visitor is also used to carry around the symbol table).

## Plain Object-Oriented Java Version

## Comparison of Advantages and Disadvantages