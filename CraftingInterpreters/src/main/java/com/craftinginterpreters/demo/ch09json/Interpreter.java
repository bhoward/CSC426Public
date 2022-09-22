package com.craftinginterpreters.demo.ch09json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.craftinginterpreters.demo.ch09json.Expr.Array;
import com.craftinginterpreters.demo.ch09json.Expr.Binary;
import com.craftinginterpreters.demo.ch09json.Expr.Comprehension;
import com.craftinginterpreters.demo.ch09json.Expr.Conditional;
import com.craftinginterpreters.demo.ch09json.Expr.Hash;
import com.craftinginterpreters.demo.ch09json.Expr.Let;
import com.craftinginterpreters.demo.ch09json.Expr.Literal;
import com.craftinginterpreters.demo.ch09json.Expr.Unary;
import com.craftinginterpreters.demo.ch09json.Expr.Variable;
import com.craftinginterpreters.demo.ch09json.Expr.Visitor;

public class Interpreter implements Visitor<Object> {
    private Environment environment;

    public Interpreter() {
        this.environment = new Environment(null);
    }

    public Object visitLiteral(Literal expr) {
        return expr.value();
    }

    public Object visitVariable(Variable expr) {
        Object value = environment.get(expr.name());

        // Lazy evaluation of recursive binding.
        if (value instanceof Thunk t) {
            Environment save = environment;
            environment = t.closure();
            value = interpret(t.value());
            environment = save;
            environment.update(expr.name(), value);
        }

        return value;
    }

    public Object visitBinary(Binary expr) {
        Object left = interpret(expr.left());
        Object right = interpret(expr.right());

        switch (expr.operator().type) {
        case BANG_EQUAL:
            return !Objects.equals(left, right);

        case DOT: {
            if (left instanceof Map<?, ?> m) {
                return m.get(right.toString());
            } else if (left instanceof List<?> l && right instanceof Double r) {
                return l.get(r.intValue());
            } else
                throw new RuntimeError(expr.operator(), "Unable to perform selection.");
        }

        case EQUAL_EQUAL:
            return Objects.equals(left, right);

        case GREATER:
            return compare(expr.operator(), left, right) > 0;

        case GREATER_EQUAL:
            return compare(expr.operator(), left, right) >= 0;

        case LESS:
            return compare(expr.operator(), left, right) < 0;

        case LESS_EQUAL:
            return compare(expr.operator(), left, right) <= 0;

        case MINUS:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left - (double) right;

        case PLUS: {
            if (left instanceof Double l && right instanceof Double r) {
                return l + r;
            } else if (left instanceof Boolean l && right instanceof Boolean r) {
                return l || r;
            } else {
                return String.valueOf(left) + String.valueOf(right);
            }
        }

        case RANGE: {
            checkNumberOperands(expr.operator(), left, right);
            int start = ((Double) left).intValue();
            int end = ((Double) right).intValue();
            return IntStream.rangeClosed(start, end).mapToObj(Double::valueOf).collect(Collectors.toList());
        }

        case SLASH:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left / (double) right;

        case STAR: {
            if (left instanceof Double l && right instanceof Double r) {
                return l * r;
            } else if (left instanceof Boolean l && right instanceof Boolean r) {
                return l && r;
            } else {
                throw new RuntimeError(expr.operator(), "Operands must be numbers or booleans.");
            }
        }

        default:
            // shouldn't happen
            break;
        }
        return null;
    }

    public Object visitUnary(Unary expr) {
        Object right = interpret(expr.right());

        switch (expr.operator().type) {
        case BANG:
            return !truthy(right);

        case MINUS:
            checkNumberOperand(expr.operator(), right);
            return -(double) right;

        default:
            // should not happen.
            break;
        }
        return null;
    }

    public Object visitArray(Array expr) {
        List<Object> result = new ArrayList<>();
        for (Expr e : expr.elements()) {
            result.add(interpret(e));
        }
        return result;
    }

    public Object visitHash(Hash expr) {
        Map<String, Object> result = new HashMap<>();
        for (var entry : expr.members().entrySet()) {
            result.put(entry.getKey(), interpret(entry.getValue()));
        }
        return result;
    }

    public Object visitConditional(Conditional expr) {
        Object test = interpret(expr.test());

        if (truthy(test)) {
            return interpret(expr.ifTrue());
        } else {
            return interpret(expr.ifFalse());
        }
    }

    public Object visitComprehension(Comprehension expr) {
        Object source = interpret(expr.source());

        if (source instanceof List<?> array) {
            List<Object> result = new ArrayList<>();

            for (Object element : array) {
                Environment outer = environment;
                environment = new Environment(outer);

                environment.define(expr.name().lexeme, element);
                result.add(interpret(expr.result()));

                environment = outer;
            }

            return result;
        } else {
            throw new RuntimeError(expr.name(), "Comprehension source must be an array.");
        }
    }

    public Object visitLet(Let expr) {
        Environment outer = environment;
        Environment inner = new Environment(outer);

        for (Binding binding : expr.bindings()) {
            if (binding instanceof Binding.Simple b) {
                inner.define(b.name().lexeme, interpret(b.value()));
            } else if (binding instanceof Binding.Recursive b) {
                inner.define(b.name().lexeme, new Thunk(b.value(), inner));
            } else {
                // Should not happen
            }
        }

        environment = inner;
        Object result = interpret(expr.body());
        environment = outer;

        return result;
    }

    private int compare(Token t, Object left, Object right) {
        if (left == right) {
            return 0;
        } else if (left == null) {
            return -1;
        } else if (right == null) {
            return 1;
        } else if (left instanceof Double l && right instanceof Double r) {
            return l.compareTo(r);
        } else if (left instanceof String l && right instanceof String r) {
            return l.compareTo(r);
        } else if (left instanceof Boolean l && right instanceof Boolean r) {
            return l.compareTo(r);
        } else {
            throw new RuntimeError(t, "Incomparable values.");
        }
    }

    private boolean truthy(Object test) {
        if (test instanceof Boolean b) {
            return b;
        } else {
            return test != null;
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object interpret(Expr expr) {
        return expr.accept(this);
    }
}
