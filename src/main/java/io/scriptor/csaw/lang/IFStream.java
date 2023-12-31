package io.scriptor.csaw.lang;

import static io.scriptor.java.ErrorUtil.handle;
import static io.scriptor.java.ErrorUtil.handleVoid;

import java.io.BufferedReader;
import java.io.FileReader;

import io.scriptor.csaw.impl.interpreter.Type;
import io.scriptor.csaw.impl.interpreter.value.ConstNum;
import io.scriptor.csaw.impl.interpreter.value.ConstStr;
import io.scriptor.csaw.impl.interpreter.value.Value;
import io.scriptor.java.CSawNative;

@CSawNative("ifstream")
public class IFStream extends Value {

    private final String mFilename;
    private final BufferedReader mReader;

    public IFStream(ConstStr filename) {
        mFilename = filename.get();
        mReader = new BufferedReader(handle(() -> new FileReader(mFilename)));
    }

    @Override
    public ConstNum asNum() {
        return new ConstNum(mReader != null);
    }

    public ConstStr readLine() {
        return new ConstStr(handle(mReader::readLine));
    }

    public void close() {
        handleVoid(mReader::close);
    }

    @Override
    protected Type type() {
        return Type.get("ifstream");
    }

    @Override
    protected Object object() {
        return mReader;
    }

    @Override
    protected String string() {
        return String.format("{ ifstream %s }", mFilename);
    }
}
