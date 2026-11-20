package com.example.fxsuite.taxonomy;

/**
 * A leaf: a concrete kind, the only nodes that are things. Under a branch at any level, never under another leaf.
 *
 * @param <P> the branch it is under
 */
public interface Leaf<P extends Branch> extends Child<P> {
}
