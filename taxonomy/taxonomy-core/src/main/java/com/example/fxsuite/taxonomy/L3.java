package com.example.fxsuite.taxonomy;

/**
 * A branch at the third level, under one at the second.
 *
 * @param <P> the branch it is under
 */
public interface L3<P extends L2<?>> extends Branch, Child<P> {
}
