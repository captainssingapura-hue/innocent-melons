package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Earthworks: the second part of the made layer ({@link Made}), laid over the networks: the ground itself reshaped, for
 * what will stand on it, to hold water back or out, or to be farmed. An earthwork changes the ground's height and
 * stands on nothing; a building stands on the ground and changes nothing of it. Ground levelled is {@link Level}.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Earthworks.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Earthworks {

    private Earthworks() {
    }

    /** The branch: the ground reshaped. */
    public record Earthwork() implements L1<Made> { public Made parent() { return new Made(); } }

    // Levelled: made level, to be built on or farmed
    public record Levelled() implements L2<Earthwork>, Level { public Earthwork parent() { return new Earthwork(); } }
    public record Pad() implements Leaf<Levelled> { public Levelled parent() { return new Levelled(); } }
    public record Terrace() implements Leaf<Levelled> { public Levelled parent() { return new Levelled(); } }
    public record Reclaimed() implements Leaf<Levelled> { public Levelled parent() { return new Levelled(); } }

    // Raised: earth heaped up
    public record Raised() implements L2<Earthwork> { public Earthwork parent() { return new Earthwork(); } }
    public record Embankment() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }
    public record Dike() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }
    public record Mound() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }
    public record CityWall() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }

    // Cut: earth dug away
    public record Cut() implements L2<Earthwork> { public Earthwork parent() { return new Earthwork(); } }
    public record Cutting() implements Leaf<Cut> { public Cut parent() { return new Cut(); } }
    public record Ditch() implements Leaf<Cut> { public Cut parent() { return new Cut(); } }
    public record Moat() implements Leaf<Cut> { public Cut parent() { return new Cut(); } }

    // Faced: a bank given a face of stone
    public record Faced() implements L2<Earthwork> { public Earthwork parent() { return new Earthwork(); } }
    public record Revetment() implements Leaf<Faced> { public Faced parent() { return new Faced(); } }
    public record Seawall() implements Leaf<Faced> { public Faced parent() { return new Faced(); } }

    // Cultivated: the ground worked to grow crops
    public record Cultivated() implements L2<Earthwork> { public Earthwork parent() { return new Earthwork(); } }
    public record Field() implements Leaf<Cultivated> { public Cultivated parent() { return new Cultivated(); } }
    public record Paddy() implements Leaf<Cultivated> { public Cultivated parent() { return new Cultivated(); } }
}
