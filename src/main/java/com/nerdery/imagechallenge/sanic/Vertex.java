package com.nerdery.imagechallenge.sanic;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Vertex {
    public final int x;
    public final int y;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof Vertex) {
            return x == ((Vertex)other).x && y == ((Vertex)other).y;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(x).append(y).toHashCode();
    }
}