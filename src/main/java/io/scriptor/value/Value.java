package io.scriptor.value;

import io.scriptor.Environment;

public abstract class Value {

    public static final String TYPE_ANY = "any";
    public static final String TYPE_NUM = "num";
    public static final String TYPE_STR = "str";
    public static final String TYPE_CHR = "chr";
    public static final String TYPE_LIST = "list";

    private boolean mReturn = false;

    public boolean isReturn() {
        return mReturn;
    }

    public Value isReturn(boolean ret) {
        mReturn = ret;
        return this;
    }

    public boolean isNum() {
        return this instanceof NumValue;
    }

    public boolean isStr() {
        return this instanceof StrValue;
    }

    public boolean sameType(Value value) {
        return getClass().isInstance(value);
    }

    public abstract Object getValue();

    public abstract String getType();

    public abstract boolean asBoolean();

    public abstract String toString();

    public static Value makeValue(Environment env, String type, boolean primitives) {
        switch (env.getOrigin(type)) {
            case TYPE_NUM:
                return new NumValue(0);
            case TYPE_STR:
                return new StrValue();
            case TYPE_CHR:
                return new ChrValue((char) 0);
            case TYPE_LIST:
                return new ListValue();
            default:
                break;
        }

        if (primitives || !env.existsType(env.getOrigin(type)))
            return new NullValue(type);

        return new ObjValue(env, type);
    }

    public static Value binAnd(Environment env, Value left, Value right) {
        return env.getAndInvoke(null, "&", left, right);
    }

    public static Value and(Environment env, Value left, Value right) {
        return new NumValue(left.asBoolean() && right.asBoolean());
    }

    public static Value binOr(Environment env, Value left, Value right) {
        return env.getAndInvoke(null, "|", left, right);
    }

    public static Value or(Environment env, Value left, Value right) {
        return new NumValue(left.asBoolean() || right.asBoolean());
    }

    public static Value cmpe(Environment env, Value left, Value right) {
        return new NumValue(left.equals(right));
    }

    public static Value cmpne(Environment env, Value left, Value right) {
        return new NumValue(!left.equals(right));
    }

    public static Value cmpl(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() < ((NumValue) right).getValue());

        return env.getAndInvoke(null, "<", left, right);
    }

    public static Value cmple(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() <= ((NumValue) right).getValue());

        return env.getAndInvoke(null, "<=", left, right);
    }

    public static Value cmpg(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() > ((NumValue) right).getValue());

        return env.getAndInvoke(null, ">", left, right);
    }

    public static Value cmpge(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() >= ((NumValue) right).getValue());

        return env.getAndInvoke(null, ">=", left, right);
    }

    public static Value add(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() + ((NumValue) right).getValue());

        if (left.isStr())
            return new StrValue(((StrValue) left).getValue() + right.toString());

        if (right.isStr())
            return new StrValue(left.toString() + ((StrValue) right).getValue());

        return env.getAndInvoke(null, "+", left, right);
    }

    public static Value sub(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() - ((NumValue) right).getValue());

        return env.getAndInvoke(null, "-", left, right);
    }

    public static Value mul(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() * ((NumValue) right).getValue());

        return env.getAndInvoke(null, "*", left, right);
    }

    public static Value div(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() / ((NumValue) right).getValue());

        return env.getAndInvoke(null, "/", left, right);
    }

    public static Value mod(Environment env, Value left, Value right) {
        if (left.isNum() && right.isNum())
            return new NumValue(((NumValue) left).getValue() % ((NumValue) right).getValue());

        return env.getAndInvoke(null, "%", left, right);
    }

    public static Value neg(Environment env, Value value) {
        if (value.isNum())
            return new NumValue(-((NumValue) value).getValue());

        return env.getAndInvoke(value, "-");
    }

    public static Value not(Environment env, Value value) {
        return new NumValue(!value.asBoolean());
    }

    public static Value inv(Environment env, Value value) {
        if (value.isNum())
            return new NumValue(~(int) (double) ((NumValue) value).getValue());

        return env.getAndInvoke(value, "~");
    }
}
