package io.scriptor.csaw.impl.interpreter;

import static io.scriptor.csaw.impl.interpreter.Environment.createAlias;
import static io.scriptor.csaw.impl.interpreter.Environment.createFunction;
import static io.scriptor.csaw.impl.interpreter.Environment.createType;
import static io.scriptor.csaw.impl.interpreter.Environment.getFunction;
import static io.scriptor.csaw.impl.interpreter.Environment.getOrigin;
import static io.scriptor.csaw.impl.interpreter.Environment.isAssignable;

import java.io.File;
import java.io.FileInputStream;

import io.scriptor.csaw.impl.CSawException;
import io.scriptor.csaw.impl.Parser;
import io.scriptor.csaw.impl.expr.AssignExpr;
import io.scriptor.csaw.impl.expr.BinExpr;
import io.scriptor.csaw.impl.expr.CallExpr;
import io.scriptor.csaw.impl.expr.ChrExpr;
import io.scriptor.csaw.impl.expr.ConExpr;
import io.scriptor.csaw.impl.expr.Expr;
import io.scriptor.csaw.impl.expr.IdExpr;
import io.scriptor.csaw.impl.expr.MemExpr;
import io.scriptor.csaw.impl.expr.NumExpr;
import io.scriptor.csaw.impl.expr.StrExpr;
import io.scriptor.csaw.impl.expr.UnExpr;
import io.scriptor.csaw.impl.interpreter.value.ChrValue;
import io.scriptor.csaw.impl.interpreter.value.NumValue;
import io.scriptor.csaw.impl.interpreter.value.ObjValue;
import io.scriptor.csaw.impl.interpreter.value.StrValue;
import io.scriptor.csaw.impl.interpreter.value.Value;
import io.scriptor.csaw.impl.stmt.AliasStmt;
import io.scriptor.csaw.impl.stmt.EnclosedStmt;
import io.scriptor.csaw.impl.stmt.ForStmt;
import io.scriptor.csaw.impl.stmt.FunStmt;
import io.scriptor.csaw.impl.stmt.IfStmt;
import io.scriptor.csaw.impl.stmt.IncStmt;
import io.scriptor.csaw.impl.stmt.RetStmt;
import io.scriptor.csaw.impl.stmt.Stmt;
import io.scriptor.csaw.impl.stmt.ThingStmt;
import io.scriptor.csaw.impl.stmt.VarStmt;
import io.scriptor.csaw.impl.stmt.WhileStmt;
import io.scriptor.java.ErrorUtil;

public class Interpreter {

    private Interpreter() {
    }

    public static Value evaluate(Environment env, Stmt stmt) {
        if (stmt == null)
            return null;

        if (stmt instanceof EnclosedStmt)
            return evaluate(env, (EnclosedStmt) stmt);

        if (stmt instanceof AliasStmt)
            return evaluate(env, (AliasStmt) stmt);
        if (stmt instanceof ForStmt)
            return evaluate(env, (ForStmt) stmt);
        if (stmt instanceof FunStmt)
            return evaluate(env, (FunStmt) stmt);
        if (stmt instanceof IfStmt)
            return evaluate(env, (IfStmt) stmt);
        if (stmt instanceof IncStmt)
            return evaluate(env, (IncStmt) stmt);
        if (stmt instanceof RetStmt)
            return evaluate(env, (RetStmt) stmt);
        if (stmt instanceof ThingStmt)
            return evaluate(env, (ThingStmt) stmt);
        if (stmt instanceof VarStmt)
            return evaluate(env, (VarStmt) stmt);
        if (stmt instanceof WhileStmt)
            return evaluate(env, (WhileStmt) stmt);

        if (stmt instanceof Expr)
            return evaluate(env, (Expr) stmt);

        throw new CSawException();
    }

    public static Value evaluate(Environment env, EnclosedStmt stmt) {
        final var environment = new Environment(env);
        for (final var s : stmt.body) {
            final var value = evaluate(environment, s);
            if (value != null && value.isReturn())
                return value;
        }
        return null;
    }

    public static Value evaluate(Environment env, AliasStmt stmt) {
        createAlias(stmt.alias, stmt.origin);
        return null;
    }

    public static Value evaluate(Environment env, ForStmt stmt) {
        final var e = new Environment(env);
        for (evaluate(e, stmt.begin); evaluate(e, stmt.condition).asBoolean(); evaluate(e, stmt.loop)) {
            final var value = evaluate(e, stmt.body);
            if (value != null && value.isReturn())
                return value;
        }
        return null;
    }

    public static Value evaluate(Environment env, FunStmt stmt) {
        createFunction(
                stmt.constructor,
                stmt.name,
                stmt.type,
                stmt.parameters,
                stmt.vararg,
                stmt.member,
                stmt.body);
        return null;
    }

    public static Value evaluate(Environment env, IfStmt stmt) {

        final var condition = evaluate(env, stmt.condition);
        if (condition.asBoolean())
            return evaluate(env, stmt.thenBody);

        if (stmt.elseBody != null)
            return evaluate(env, stmt.elseBody);

        return null;
    }

    public static Value evaluate(Environment env, IncStmt stmt) {
        final var path = env.getPath();
        final var file = new File(path, stmt.path);
        Parser.parse(ErrorUtil.tryCatch(() -> new FileInputStream(file)), env.setPath(file.getParent()));
        env.setPath(path);

        return null;
    }

    public static Value evaluate(Environment env, RetStmt stmt) {
        return stmt.value == null ? null : evaluate(env, stmt.value).isReturn(true);
    }

    public static Value evaluate(Environment env, ThingStmt stmt) {
        createType(stmt.group, stmt.name, stmt.fields);
        return null;
    }

    public static Value evaluate(Environment env, VarStmt stmt) {
        var value = stmt.value == null ? null : evaluate(env, stmt.value);

        if (value != null && !isAssignable(value.getType(), stmt.type))
            throw new CSawException(
                    String.format("cannot assign value of type '%s' to type '%s'", value.getType(), stmt.type));
        else if (value == null)
            value = Value.makeValue(env, stmt.type, false, false);

        env.createVariable(stmt.name, stmt.type, value);
        return null;
    }

    public static Value evaluate(Environment env, WhileStmt stmt) {
        final var e = new Environment(env);
        while (evaluate(e, stmt.condition).asBoolean()) {
            final var value = evaluate(e, stmt.body);
            if (value != null && value.isReturn())
                return value;
        }

        return null;
    }

    public static Value evaluate(Environment env, Expr expr) {
        if (expr instanceof AssignExpr)
            return evaluate(env, (AssignExpr) expr);
        if (expr instanceof BinExpr)
            return evaluate(env, (BinExpr) expr);
        if (expr instanceof CallExpr)
            return evaluate(env, (CallExpr) expr);
        if (expr instanceof ChrExpr)
            return evaluate(env, (ChrExpr) expr);
        if (expr instanceof ConExpr)
            return evaluate(env, (ConExpr) expr);
        if (expr instanceof IdExpr)
            return evaluate(env, (IdExpr) expr);
        if (expr instanceof MemExpr)
            return evaluate(env, (MemExpr) expr);
        if (expr instanceof NumExpr)
            return evaluate(env, (NumExpr) expr);
        if (expr instanceof StrExpr)
            return evaluate(env, (StrExpr) expr);
        if (expr instanceof UnExpr)
            return evaluate(env, (UnExpr) expr);

        throw new CSawException();
    }

    public static Value evaluate(Environment env, AssignExpr expr) {
        if (expr.object instanceof IdExpr)
            return env.setVariable(((IdExpr) expr.object).name, evaluate(env, expr.value));
        if (expr.object instanceof MemExpr) {
            final var object = (ObjValue) evaluate(env, ((MemExpr) expr.object).object);
            return object.setField(((MemExpr) expr.object).member, evaluate(env, expr.value));
        }

        throw new CSawException("unsupported assign operation %s", expr);
    }

    public static Value evaluate(Environment env, BinExpr expr) {
        final var left = evaluate(env, expr.left);
        final var right = evaluate(env, expr.right);

        return switch (expr.operator) {

            case "&" -> Value.binAnd(env, left, right);
            case "&&" -> Value.and(env, left, right);
            case "|" -> Value.binOr(env, left, right);
            case "||" -> Value.or(env, left, right);
            case "==" -> Value.cmpe(env, left, right);
            case "!=" -> Value.cmpne(env, left, right);
            case "<" -> Value.cmpl(env, left, right);
            case "<=" -> Value.cmple(env, left, right);
            case ">" -> Value.cmpg(env, left, right);
            case ">=" -> Value.cmpge(env, left, right);
            case "+" -> Value.add(env, left, right);
            case "-" -> Value.sub(env, left, right);
            case "*" -> Value.mul(env, left, right);
            case "/" -> Value.div(env, left, right);
            case "%" -> Value.mod(env, left, right);
            case "^" -> Value.xor(env, left, right);

            default -> throw new CSawException("unsupported operator '%s'", expr.operator);
        };
    }

    public static Value evaluate(Environment env, CallExpr expr) {
        final var args = new Value[expr.arguments.length];
        final var argTypes = new String[expr.arguments.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = evaluate(env, expr.arguments[i]);
            argTypes[i] = args[i].getType();
        }

        String name = null;
        Value member = null;

        if (expr.function instanceof IdExpr)
            name = ((IdExpr) expr.function).name;
        else if (expr.function instanceof MemExpr) {
            member = evaluate(env, ((MemExpr) expr.function).object);
            name = ((MemExpr) expr.function).member;
        }

        final var fun = getFunction(member != null ? getOrigin(member.getType()) : null, name, argTypes);
        return fun.invoke(member, args);
    }

    public static Value evaluate(Environment env, ChrExpr expr) {
        return new ChrValue(expr.value);
    }

    public static Value evaluate(Environment env, ConExpr expr) {
        return evaluate(env, expr.condition).asBoolean()
                ? evaluate(env, expr.thenExpr)
                : evaluate(env, expr.elseExpr);
    }

    public static Value evaluate(Environment env, IdExpr expr) {
        return env.getVariable(expr.name);
    }

    public static Value evaluate(Environment env, MemExpr expr) {
        return ((ObjValue) evaluate(env, expr.object)).getField(expr.member);
    }

    public static Value evaluate(Environment env, NumExpr expr) {
        return new NumValue(expr.value);
    }

    public static Value evaluate(Environment env, StrExpr expr) {
        return new StrValue(expr.value);
    }

    public static Value evaluate(Environment env, UnExpr expr) {
        final var value = evaluate(env, expr.value);

        return switch (expr.operator) {

            case "-" -> Value.neg(env, value);
            case "!" -> Value.not(env, value);
            case "~" -> Value.inv(env, value);

            default -> throw new CSawException("unsupported operator '%s'", expr.operator);
        };
    }
}