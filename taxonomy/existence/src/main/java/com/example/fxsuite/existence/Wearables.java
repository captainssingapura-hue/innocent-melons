package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Wearables: what is put on the body and goes with it as it moves, until it is taken off: garments, of cloth, and
 * armour, hard. A wearable says what it is, never how it is made nor what it is made of. Only the kinds something has
 * so far been made as are here.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Wearables.md}.
 */
public final class Wearables {

    private Wearables() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("wearables", new Wearable(), List.of(new Briefs(), new Tights(),
            new Helmet(), new Gorget(), new Cuirass(), new Pauldron(), new Vambrace(), new Gauntlet(), new Fauld(),
            new Greave()), Meanings.INSTANCE.beside(Wearables.class));

    /** The root. What is put on the body and goes with it. */
    public record Wearable() implements L0 {
    }

    // Garment: of cloth, bending as the body does
    public record Garment() implements L1<Wearable> { public Wearable parent() { return new Wearable(); } }
    public record Briefs() implements Leaf<Garment> { public Garment parent() { return new Garment(); } }
    public record Tights() implements Leaf<Garment> { public Garment parent() { return new Garment(); } }

    // Armour: hard, each piece keeping its shape over the part of the body it guards
    public record Armour() implements L1<Wearable> { public Wearable parent() { return new Wearable(); } }
    public record Helmet() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Cuirass() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Pauldron() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Vambrace() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Greave() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Fauld() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Gorget() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
    public record Gauntlet() implements Leaf<Armour> { public Armour parent() { return new Armour(); } }
}
