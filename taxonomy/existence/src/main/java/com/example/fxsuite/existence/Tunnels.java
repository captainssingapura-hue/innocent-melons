package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Tunnels: the made layer's department of what carries a way through the ground ({@link Made}): through a hill, under
 * water. A bore is {@link Transit}: it may be part of a network.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Tunnels.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Tunnels {

    private Tunnels() {
    }

    /** The department's branch: a way carried through the ground. */
    public record Bore() implements L1<Made>, Transit { public Made parent() { return new Made(); } }

    public record Tunnel() implements Leaf<Bore> { public Bore parent() { return new Bore(); } }
}
