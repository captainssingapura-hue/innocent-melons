package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Terrains: the base layer, the land as it lies before anything is on it or cut into it. Its shapes (landforms) and
 * what naturally lies on it (covers). Under every other layer, and changed by none of them: each is laid over it.
 * Ground that is {@link Level} may be built on.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Terrains.md}.
 */
public final class Terrains {

    private Terrains() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("terrains", new Terrain(), List.of(new Plain(), new Floodplain(),
            new Plateau(), new Hill(), new Ridge(), new Dune(), new Valley(), new Hollow(), new Gorge(), new Slope(),
            new Cliff(), new Earth(), new Sand(), new Clay(), new Rock(), new Grass(), new Meadow(), new Moss(),
            new Scrub()), Meanings.INSTANCE.beside(Terrains.class));

    /** The root: the land as it lies. */
    public record Terrain() implements L0 {
    }

    // Landform: the land's shape
    public record Landform() implements L1<Terrain> { public Terrain parent() { return new Terrain(); } }

    public record Flat() implements L2<Landform>, Level { public Landform parent() { return new Landform(); } }
    public record Plain() implements Leaf<Flat> { public Flat parent() { return new Flat(); } }
    public record Floodplain() implements Leaf<Flat> { public Flat parent() { return new Flat(); } }
    public record Plateau() implements Leaf<Flat> { public Flat parent() { return new Flat(); } }

    public record Raised() implements L2<Landform> { public Landform parent() { return new Landform(); } }
    public record Hill() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }
    public record Ridge() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }
    public record Dune() implements Leaf<Raised> { public Raised parent() { return new Raised(); } }

    public record Sunken() implements L2<Landform> { public Landform parent() { return new Landform(); } }
    public record Valley() implements Leaf<Sunken> { public Sunken parent() { return new Sunken(); } }
    public record Hollow() implements Leaf<Sunken> { public Sunken parent() { return new Sunken(); } }
    public record Gorge() implements Leaf<Sunken> { public Sunken parent() { return new Sunken(); } }

    public record Sloped() implements L2<Landform> { public Landform parent() { return new Landform(); } }
    public record Slope() implements Leaf<Sloped> { public Sloped parent() { return new Sloped(); } }
    public record Cliff() implements Leaf<Sloped> { public Sloped parent() { return new Sloped(); } }

    // Cover: what naturally lies on it
    public record Cover() implements L1<Terrain> { public Terrain parent() { return new Terrain(); } }

    public record Bare() implements L2<Cover> { public Cover parent() { return new Cover(); } }
    public record Earth() implements Leaf<Bare> { public Bare parent() { return new Bare(); } }
    public record Sand() implements Leaf<Bare> { public Bare parent() { return new Bare(); } }
    public record Clay() implements Leaf<Bare> { public Bare parent() { return new Bare(); } }
    public record Rock() implements Leaf<Bare> { public Bare parent() { return new Bare(); } }

    public record Grown() implements L2<Cover> { public Cover parent() { return new Cover(); } }
    public record Grass() implements Leaf<Grown> { public Grown parent() { return new Grown(); } }
    public record Meadow() implements Leaf<Grown> { public Grown parent() { return new Grown(); } }
    public record Moss() implements Leaf<Grown> { public Grown parent() { return new Grown(); } }
    public record Scrub() implements Leaf<Grown> { public Grown parent() { return new Grown(); } }
}
