package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Features: the layer of the land's features, laid over the base terrain ({@link Terrains}): its waters, cut into it or
 * lying in it, and their edges, however they came there: a canal is a river people dug, and none of them is built by a
 * civilization. Their layer may be a stack of its own, each feature laid over those before it: a river, a sandbar in
 * it, a marsh along it. Water is {@link Wet}; where boats go, it is {@link Navigable}.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Features.md}.
 */
public final class Features {

    private Features() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("features", new Feature(), List.of(new River(), new Stream(),
            new Canal(), new Lake(), new Pond(), new Marsh(), new Sea(), new Bay(), new Bank(), new Shore(),
            new Beach()),
            Meanings.INSTANCE.beside(Features.class));

    /** The root: a feature of the land, laid over its terrain. */
    public record Feature() implements L0 {
    }

    // Water
    public record Water() implements L1<Feature>, Wet { public Feature parent() { return new Feature(); } }

    public record Flowing() implements L2<Water> { public Water parent() { return new Water(); } }
    public record River() implements Leaf<Flowing>, Navigable { public Flowing parent() { return new Flowing(); } }
    public record Stream() implements Leaf<Flowing> { public Flowing parent() { return new Flowing(); } }
    public record Canal() implements Leaf<Flowing>, Navigable { public Flowing parent() { return new Flowing(); } }

    public record Still() implements L2<Water> { public Water parent() { return new Water(); } }
    public record Lake() implements Leaf<Still>, Navigable { public Still parent() { return new Still(); } }
    public record Pond() implements Leaf<Still> { public Still parent() { return new Still(); } }
    public record Marsh() implements Leaf<Still> { public Still parent() { return new Still(); } }

    public record Open() implements L2<Water> { public Water parent() { return new Water(); } }
    public record Sea() implements Leaf<Open>, Navigable { public Open parent() { return new Open(); } }
    public record Bay() implements Leaf<Open>, Navigable { public Open parent() { return new Open(); } }

    // Edge: where the land meets the water
    public record Edge() implements L1<Feature> { public Feature parent() { return new Feature(); } }
    public record Bank() implements Leaf<Edge> { public Edge parent() { return new Edge(); } }
    public record Shore() implements Leaf<Edge> { public Edge parent() { return new Edge(); } }
    public record Beach() implements Leaf<Edge> { public Edge parent() { return new Edge(); } }
}
