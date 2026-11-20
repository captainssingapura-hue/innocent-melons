package com.example.fxsuite.taxonomy;

/**
 * A node under another: its parent its type's argument, so a node in the wrong place does not compile, and, a class
 * implementing a generic interface once only, no node has two parents. It gives its parent too, to be walked.
 *
 * @param <P> its parent's kind
 */
public interface Child<P extends Branch> extends Node {

    /** Its parent. */
    P parent();
}
