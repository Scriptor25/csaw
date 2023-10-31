package io.scriptor.csaw.impl.stmt;

public class EnclosedStmt extends Stmt {

    public Stmt[] body;

    public EnclosedStmt(Stmt[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder().append("{\n");
        for (final var stmt : body)
            builder.append("\t").append(stmt).append(";\n");
        builder.append("}");

        return builder.toString();
    }

}
