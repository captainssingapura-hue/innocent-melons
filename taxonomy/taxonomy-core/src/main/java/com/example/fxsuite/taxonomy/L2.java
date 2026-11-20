package com.example.fxsuite.taxonomy;

/**
 * A branch at the second level, under one at the first.
 *
 * @param <P> the branch it is under
 */
public interface L2<P extends L1<?>> extends Branch, Child<P> {
}
