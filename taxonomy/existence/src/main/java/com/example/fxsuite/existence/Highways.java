package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Highways: the made layer's department of ways along the ground ({@link Made}): the ways people and goods go along,
 * and what they are paved with. A way is {@link Transit}: it may be part of a network.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Highways.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Highways {

    private Highways() {
    }

    /** The department's branch: a highway, a way and its pavement. */
    public record Highway() implements L1<Made> { public Made parent() { return new Made(); } }

    // Way: along the ground
    public record Way() implements L2<Highway>, Transit { public Highway parent() { return new Highway(); } }
    public record Road() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Expressway() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Street() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Lane() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Path() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Towpath() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Ford() implements Leaf<Way> { public Way parent() { return new Way(); } }

    // Pavement: what a way is surfaced with
    public record Pavement() implements L2<Highway> { public Highway parent() { return new Highway(); } }
    public record Paving() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Setts() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Asphalt() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Gravel() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
}
