package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * Something that can be done to numbers, said three ways that must agree: what shape comes of what shapes, what Java
 * makes of it, and how the shader's language writes it.
 */
public interface Op {

    /** How many numbers come of it, done to things of {@code of} numbers each; shapes it cannot be done to refused. */
    int size(List<Integer> of);

    /** What comes of it, done to {@code of}. */
    double[] worked(List<double[]> of);

    /** Its code, done to what goes by the names {@code of}. */
    String written(List<String> of);

    /**
     * What a shader has to declare, once, before this can be written in it: the lines of a function of its own, for
     * what the shader's language has no word for. Nothing, for most.
     */
    default List<String> declared() {
        return List.of();
    }
}
