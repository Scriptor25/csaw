package io.scriptor.csaw.impl;

public class Pair<F, S> {

    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return String.format("{ %s %s }", first, second);
    }
}
