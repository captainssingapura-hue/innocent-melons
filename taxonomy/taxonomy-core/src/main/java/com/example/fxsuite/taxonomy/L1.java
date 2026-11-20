package com.example.fxsuite.taxonomy;

/**
 * A branch at the first level, under a root.
 *
 * @param <P> the root it is under
 */
public interface L1<P extends L0> extends Branch, Child<P> {
}
