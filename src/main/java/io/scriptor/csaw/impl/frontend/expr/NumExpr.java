package io.scriptor.csaw.impl.frontend.expr;

public class NumExpr extends Expr {

    private final double mValue;

    public NumExpr(String value, int radix) {
        if (radix == 10)
            this.mValue = Double.parseDouble(value);
        else
            this.mValue = Long.parseLong(value, radix);
    }

    public NumExpr(double value) {
        this.mValue = value;
    }

    public synchronized double value() {
        return mValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Expr makeConstant() {
        return new ConstExpr(this);
    }

    @Override
    public String toString() {
        return Double.toString(mValue);
    }
}