package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Networks: the first part of the made layer ({@link Made}), laid over the terrain and its features before anything
 * else is made: the ways along which people and goods go, the waterways dug for boats, what carries a way where the
 * ground cannot, and what a way is paved with. A canal is {@link Wet} and {@link Navigable}.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Networks.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Networks {

    private Networks() {
    }

    /** The branch: a network's part. */
    public record Network() implements L1<Made> { public Made parent() { return new Made(); } }

    // Way: along the ground
    public record Way() implements L2<Network> { public Network parent() { return new Network(); } }
    public record Road() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Street() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Lane() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Path() implements Leaf<Way> { public Way parent() { return new Way(); } }
    public record Towpath() implements Leaf<Way> { public Way parent() { return new Way(); } }

    // Waterway: dug for boats
    public record Waterway() implements L2<Network> { public Network parent() { return new Network(); } }
    public record Canal() implements Leaf<Waterway>, Wet, Navigable {
        public Waterway parent() { return new Waterway(); }
    }

    // Carrier: what holds a way where the ground cannot
    public record Carrier() implements L2<Network> { public Network parent() { return new Network(); } }
    public record Bridge() implements Leaf<Carrier> { public Carrier parent() { return new Carrier(); } }
    public record Viaduct() implements Leaf<Carrier> { public Carrier parent() { return new Carrier(); } }
    public record Tunnel() implements Leaf<Carrier> { public Carrier parent() { return new Carrier(); } }
    public record Ford() implements Leaf<Carrier> { public Carrier parent() { return new Carrier(); } }

    // Pavement: what a way is surfaced with
    public record Pavement() implements L2<Network> { public Network parent() { return new Network(); } }
    public record Paving() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Setts() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Asphalt() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
    public record Gravel() implements Leaf<Pavement> { public Pavement parent() { return new Pavement(); } }
}
