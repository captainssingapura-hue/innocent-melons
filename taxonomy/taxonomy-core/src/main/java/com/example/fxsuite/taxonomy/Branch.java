package com.example.fxsuite.taxonomy;

/**
 * A node that groups others: not a thing itself, but what the things under it share. A rule or an answer said on a
 * branch holds for every leaf under it ({@link Taxonomy#answer}).
 */
public interface Branch extends Node {
}
