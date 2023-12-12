package io.scriptor.csaw.impl.interpreter.value;

import static io.scriptor.csaw.impl.Types.TYPE_STR;

public class StrValue extends Value {

    private final String mValue;

    public StrValue() {
        mValue = "";
    }

    public StrValue(String value) {
        mValue = value;
    }

    public String get() {
        return mValue;
    }

    @Override
    protected String value() {
        return mValue;
    }

    @Override
    protected String type() {
        return TYPE_STR;
    }

    @Override
    protected boolean bool() {
        return mValue != null && !mValue.isEmpty();
    }

    @Override
    protected String string() {
        return mValue;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof StrValue))
            return false;
        return mValue.equals(((StrValue) other).mValue);
    }

}
