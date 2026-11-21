package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Elements: the parts a building is made of, by what each does.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Elements.md}.
 */
public final class Elements {

    private Elements() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("elements", new Element(), List.of(new Column(), new BearingWall(),
            new Arch(), new Abutment(), new Platform(), new Bracket(), new Beam(), new Floor(), new Decking(),
            new Rib(), new Roof(), new Eave(), new Canopy(), new Crown(), new Wall(), new CurtainWall(), new Spandrel(),
            new Louvre(), new Screen(), new Door(), new Window(), new Shopfront(), new Archway(), new Railing(),
            new Parapet(), new Steps(), new LiftBank(), new Incline(), new Pole(), new Sign(),
            new Finial(), new Counter(),
            new Bench()), Meanings.INSTANCE.beside(Elements.class));

    /** The root. A part a building is made of. */
    public record Element() implements L0 {
    }

    // Support
    public record Support() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Column() implements Leaf<Support> { public Support parent() { return new Support(); } }
    public record BearingWall() implements Leaf<Support> { public Support parent() { return new Support(); } }
    public record Arch() implements Leaf<Support> { public Support parent() { return new Support(); } }
    public record Abutment() implements Leaf<Support> { public Support parent() { return new Support(); } }
    public record Platform() implements Leaf<Support> { public Support parent() { return new Support(); } }
    public record Bracket() implements Leaf<Support> { public Support parent() { return new Support(); } }

    // Span
    public record Span() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Beam() implements Leaf<Span> { public Span parent() { return new Span(); } }
    public record Floor() implements Leaf<Span> { public Span parent() { return new Span(); } }
    public record Decking() implements Leaf<Span> { public Span parent() { return new Span(); } }
    public record Rib() implements Leaf<Span> { public Span parent() { return new Span(); } }

    // Cover
    public record Cover() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Roof() implements Leaf<Cover> { public Cover parent() { return new Cover(); } }
    public record Eave() implements Leaf<Cover> { public Cover parent() { return new Cover(); } }
    public record Canopy() implements Leaf<Cover> { public Cover parent() { return new Cover(); } }
    public record Crown() implements Leaf<Cover> { public Cover parent() { return new Cover(); } }

    // Enclosure
    public record Enclosure() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Wall() implements Leaf<Enclosure> { public Enclosure parent() { return new Enclosure(); } }
    public record CurtainWall() implements Leaf<Enclosure> { public Enclosure parent() { return new Enclosure(); } }
    public record Spandrel() implements Leaf<Enclosure> { public Enclosure parent() { return new Enclosure(); } }
    public record Louvre() implements Leaf<Enclosure> { public Enclosure parent() { return new Enclosure(); } }
    public record Screen() implements Leaf<Enclosure> { public Enclosure parent() { return new Enclosure(); } }

    // Opening
    public record Opening() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Door() implements Leaf<Opening> { public Opening parent() { return new Opening(); } }
    public record Window() implements Leaf<Opening> { public Opening parent() { return new Opening(); } }
    public record Shopfront() implements Leaf<Opening> { public Opening parent() { return new Opening(); } }
    public record Archway() implements Leaf<Opening> { public Opening parent() { return new Opening(); } }

    // Guard
    public record Guard() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Railing() implements Leaf<Guard> { public Guard parent() { return new Guard(); } }
    public record Parapet() implements Leaf<Guard> { public Guard parent() { return new Guard(); } }

    // Conveyance
    public record Conveyance() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Steps() implements Leaf<Conveyance> { public Conveyance parent() { return new Conveyance(); } }
    public record LiftBank() implements Leaf<Conveyance> { public Conveyance parent() { return new Conveyance(); } }
    public record Incline() implements Leaf<Conveyance> { public Conveyance parent() { return new Conveyance(); } }

    // Ornament
    public record Ornament() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Pole() implements Leaf<Ornament> { public Ornament parent() { return new Ornament(); } }
    public record Sign() implements Leaf<Ornament> { public Ornament parent() { return new Ornament(); } }
    public record Finial() implements Leaf<Ornament> { public Ornament parent() { return new Ornament(); } }

    // Fitting
    public record Fitting() implements L1<Element> { public Element parent() { return new Element(); } }
    public record Counter() implements Leaf<Fitting> { public Fitting parent() { return new Fitting(); } }
    public record Bench() implements Leaf<Fitting> { public Fitting parent() { return new Fitting(); } }
}
