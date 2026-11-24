package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Bridges: the made layer's department of what carries a way over a gap ({@link Made}): over water, over another way,
 * over low ground. A span is {@link Transit}: it may be part of a network.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Bridges.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Bridges {

    private Bridges() {
    }

    /** The department's branch: a way carried over a gap. */
    public record Span() implements L1<Made>, Transit { public Made parent() { return new Made(); } }

    public record Bridge() implements Leaf<Span> { public Span parent() { return new Span(); } }
    public record Viaduct() implements Leaf<Span> { public Span parent() { return new Span(); } }
    public record Flyover() implements Leaf<Span> { public Span parent() { return new Span(); } }
}
