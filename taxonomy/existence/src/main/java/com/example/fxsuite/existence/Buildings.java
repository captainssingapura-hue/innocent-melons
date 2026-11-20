package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Buildings: the kinds of building, by what each is for.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Buildings.md}.
 */
public final class Buildings {

    private Buildings() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = Taxonomy.of("buildings", new Building(), List.of(new House(), new Farmhouse(),
            new CourtyardHouse(), new HomesTower(), new ShopHouse(), new WineHouse(), new TeaHouse(), new Stall(),
            new Warehouse(), new FestiveGateway(), new OfficeTower(), new HotelTower(), new MixedTower(), new Temple(),
            new Church(), new Pavilion(), new Courthouse(), new Bridge(), new Quay(), new GateTower(),
            new CityWall()), Meanings.beside(Buildings.class));

    /** The root. A thing built to stand: for people to be in, or to carry, bar or mark. */
    public record Building() implements L0 {
    }

    // Residence
    public record Residence() implements L1<Building> { public Building parent() { return new Building(); } }
    public record House() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record Farmhouse() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record CourtyardHouse() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record HomesTower() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }

    // Commerce
    public record Commerce() implements L1<Building> { public Building parent() { return new Building(); } }
    public record ShopHouse() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record WineHouse() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record TeaHouse() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record Stall() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record Warehouse() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record FestiveGateway() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record OfficeTower() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record HotelTower() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }
    public record MixedTower() implements Leaf<Commerce> { public Commerce parent() { return new Commerce(); } }

    // Sacred
    public record Sacred() implements L1<Building> { public Building parent() { return new Building(); } }
    public record Temple() implements Leaf<Sacred> { public Sacred parent() { return new Sacred(); } }
    public record Church() implements Leaf<Sacred> { public Sacred parent() { return new Sacred(); } }

    // Civic
    public record Civic() implements L1<Building> { public Building parent() { return new Building(); } }
    public record Pavilion() implements Leaf<Civic> { public Civic parent() { return new Civic(); } }
    public record Courthouse() implements Leaf<Civic> { public Civic parent() { return new Civic(); } }

    // Infrastructure
    public record Infrastructure() implements L1<Building> { public Building parent() { return new Building(); } }
    public record Bridge() implements Leaf<Infrastructure> {
        public Infrastructure parent() { return new Infrastructure(); }
    }
    public record Quay() implements Leaf<Infrastructure> {
        public Infrastructure parent() { return new Infrastructure(); }
    }

    // Fortification
    public record Fortification() implements L1<Building> { public Building parent() { return new Building(); } }
    public record GateTower() implements Leaf<Fortification> {
        public Fortification parent() { return new Fortification(); }
    }
    public record CityWall() implements Leaf<Fortification> {
        public Fortification parent() { return new Fortification(); }
    }
}
