package io.scriptor.csaw.impl.frontend.stmt;

import io.scriptor.csaw.impl.frontend.expr.Expr;

public class RetStmt extends Stmt {

    public final Expr value;

    public RetStmt(Expr value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ret %s", value);
    }
}
