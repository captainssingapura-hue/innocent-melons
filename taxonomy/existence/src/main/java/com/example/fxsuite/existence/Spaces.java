package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Spaces: the parts of a building one can be in, and move between.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Spaces.md}.
 */
public final class Spaces {

    private Spaces() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = Taxonomy.of("spaces", new Space(), List.of(new Room(), new Hall(),
            new ShopFloor(), new OfficeFloor(), new Unit(), new Courtyard(), new Terrace(), new Deck(), new Grounds(),
            new CoveredWay(), new Entrance(), new Stair(), new LiftLobby(), new Corridor(), new Gateway(), new Ramp(),
            new Core(), new Shaft(), new PlantRoom(), new Washroom()), Meanings.beside(Spaces.class));

    /** The root. A part of a building one can be in. */
    public record Space() implements L0 {
    }

    // Enclosed
    public record Enclosed() implements L1<Space> { public Space parent() { return new Space(); } }
    public record Room() implements Leaf<Enclosed> { public Enclosed parent() { return new Enclosed(); } }
    public record Hall() implements Leaf<Enclosed> { public Enclosed parent() { return new Enclosed(); } }
    public record ShopFloor() implements Leaf<Enclosed> { public Enclosed parent() { return new Enclosed(); } }
    public record OfficeFloor() implements Leaf<Enclosed> { public Enclosed parent() { return new Enclosed(); } }
    public record Unit() implements Leaf<Enclosed> { public Enclosed parent() { return new Enclosed(); } }

    // Open
    public record Open() implements L1<Space> { public Space parent() { return new Space(); } }
    public record Courtyard() implements Leaf<Open> { public Open parent() { return new Open(); } }
    public record Terrace() implements Leaf<Open> { public Open parent() { return new Open(); } }
    public record Deck() implements Leaf<Open> { public Open parent() { return new Open(); } }
    public record Grounds() implements Leaf<Open> { public Open parent() { return new Open(); } }
    public record CoveredWay() implements Leaf<Open> { public Open parent() { return new Open(); } }

    // Circulation
    public record Circulation() implements L1<Space> { public Space parent() { return new Space(); } }
    public record Entrance() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }
    public record Stair() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }
    public record LiftLobby() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }
    public record Corridor() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }
    public record Gateway() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }
    public record Ramp() implements Leaf<Circulation> { public Circulation parent() { return new Circulation(); } }

    // Servicing
    public record Servicing() implements L1<Space> { public Space parent() { return new Space(); } }
    public record Core() implements Leaf<Servicing> { public Servicing parent() { return new Servicing(); } }
    public record Shaft() implements Leaf<Servicing> { public Servicing parent() { return new Servicing(); } }
    public record PlantRoom() implements Leaf<Servicing> { public Servicing parent() { return new Servicing(); } }
    public record Washroom() implements Leaf<Servicing> { public Servicing parent() { return new Servicing(); } }
}
