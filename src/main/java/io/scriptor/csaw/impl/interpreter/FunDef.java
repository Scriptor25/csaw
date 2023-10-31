package io.scriptor.csaw.impl.interpreter;

import static io.scriptor.csaw.impl.interpreter.Environment.getGlobal;
import static io.scriptor.csaw.impl.interpreter.Environment.isAssignable;

import io.scriptor.csaw.impl.CSawException;
import io.scriptor.csaw.impl.interpreter.value.Value;
import io.scriptor.csaw.impl.stmt.EnclosedStmt;

public class FunDef {

    public static class FunBody implements IFunBody {

        public FunDef definition;
        public String[] parameters;
        public EnclosedStmt implementation;

        public FunBody(FunDef def, EnclosedStmt impl) {
            definition = def;
            implementation = impl;
        }

        public FunBody(String[] params, EnclosedStmt impl) {
            parameters = params;
            implementation = impl;
        }

        @Override
        public Value invoke(Value member, Value... args) {
            final var env = new Environment(getGlobal());
            for (int i = 0; i < args.length; i++)
                env.createVariable(parameters[i], definition.parameters[i], args[i]);

            if (definition.constructor)
                env.createVariable("my", definition.type, Value.makeValue(env, definition.type, false, true));
            if (definition.member != null)
                env.createVariable("my", definition.member, member);

            final var value = Interpreter.evaluate(env, implementation);
            if (value != null)
                value.isReturn(false);

            if (definition.constructor) {
                if (value != null)
                    throw new CSawException("a constructor must not return anything");
                return env.getVariable("my");
            }

            if (value == null && definition.type != null)
                throw new CSawException(
                        "invalid return value: value is null, but function has to provide type '%s'", definition.type);

            if (value != null && !isAssignable(value.getType(), definition.type))
                throw new CSawException(
                        "invalid return value: value type is '%s', but function has to provide type '%s'",
                        value.getType(), definition.type);

            return value;
        }
    }

    public static class Builder {

        public boolean constructor;
        public String type;
        public String[] parameters;
        public boolean vararg;
        public String member;
        public IFunBody body;

        public Builder constructor(boolean constructor) {
            this.constructor = constructor;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder parameters(String[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder vararg(boolean vararg) {
            this.vararg = vararg;
            return this;
        }

        public Builder member(String member) {
            this.member = member;
            return this;
        }

        public Builder body(IFunBody body) {
            this.body = body;
            return this;
        }

        public FunDef build() {
            return new FunDef(constructor, type, parameters, vararg, member, body);
        }
    }

    public final boolean constructor;
    public final String type;
    public final String[] parameters;
    public final boolean vararg;
    public final String member;
    public final IFunBody body;

    public FunDef(boolean constructor, String type, String[] parameters, boolean vararg, String member, IFunBody body) {
        this.constructor = constructor;
        this.type = type;
        this.parameters = parameters;
        this.vararg = vararg;
        this.member = member;
        this.body = body;

        if (body instanceof FunBody)
            ((FunBody) body).definition = this;
    }

    public Value invoke(Value member, Value... args) {
        return body.invoke(member, args);
    }
}