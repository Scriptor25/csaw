package io.scriptor.expr;

public class ChrExpr extends Expr {

    public char value;

    public ChrExpr(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Character.toString(value);
    }

}
