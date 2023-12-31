package io.scriptor.csaw.impl.frontend.stmt;

import io.scriptor.csaw.impl.frontend.expr.Expr;

public class WhileStmt extends Stmt {

    public final Expr condition;
    public final Stmt body;

    public WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("while (%s) %s", condition, body);
    }
}
