package io.scriptor.value;

public class ChrValue extends Value {

    private char mValue;

    public ChrValue(char value) {
        mValue = value;
    }

    @Override
    public Character getValue() {
        return mValue;
    }

    @Override
    public String getType() {
        return Value.TYPE_CHR;
    }

    @Override
    public boolean asBoolean() {
        return mValue > 0;
    }

}
