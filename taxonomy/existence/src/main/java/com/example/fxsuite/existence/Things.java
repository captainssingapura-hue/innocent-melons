package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Things: what lies about the world and is handled, a piece at a time: fruit, a ball, a crate, a loose piece of
 * something built, a door, a wagon. A thing says what it is and nothing of what can be done with it: that is bound
 * to its kind apart, by whoever lets it be handled, a kind with nothing bound to it taking what is bound to the kind
 * over it. Only the kinds something has so far been made as are here.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Things.md}.
 */
public final class Things {

    private Things() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("things", new Thing(), List.of(new Watermelon(),
            new MelonSlice(), new Apple(), new Pineapple(), new Ball(), new Crate(), new Pillar(), new Beam(),
            new Chock(), new Ramp(), new Door(), new Wagon()), Meanings.INSTANCE.beside(Things.class));

    /** The root. A thing that lies about the world and is handled. */
    public record Thing() implements L0 {
    }

    // Fruit: what grew to be eaten
    public record Fruit() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Melon() implements L2<Fruit> { public Fruit parent() { return new Fruit(); } }
    public record Watermelon() implements Leaf<Melon> { public Melon parent() { return new Melon(); } }
    public record MelonSlice() implements Leaf<Melon> { public Melon parent() { return new Melon(); } }
    public record Apple() implements Leaf<Fruit> { public Fruit parent() { return new Fruit(); } }
    public record Pineapple() implements Leaf<Fruit> { public Fruit parent() { return new Fruit(); } }

    // Plaything: made to be played with
    public record Plaything() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Ball() implements Leaf<Plaything> { public Plaything parent() { return new Plaything(); } }

    // Container: made to hold other things
    public record Container() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Crate() implements Leaf<Container> { public Container parent() { return new Container(); } }

    // Piece: a piece of something built, standing or lying by itself
    public record Piece() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Pillar() implements Leaf<Piece> { public Piece parent() { return new Piece(); } }
    public record Beam() implements Leaf<Piece> { public Piece parent() { return new Piece(); } }
    public record Chock() implements Leaf<Piece> { public Piece parent() { return new Piece(); } }
    public record Ramp() implements Leaf<Piece> { public Piece parent() { return new Piece(); } }

    // Fixture: fixed to something built, and worked where it is
    public record Fixture() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Door() implements Leaf<Fixture> { public Fixture parent() { return new Fixture(); } }

    // Carrier: made to carry things from place to place
    public record Carrier() implements L1<Thing> { public Thing parent() { return new Thing(); } }
    public record Wagon() implements Leaf<Carrier> { public Carrier parent() { return new Carrier(); } }
}
