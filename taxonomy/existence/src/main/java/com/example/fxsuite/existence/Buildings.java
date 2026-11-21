package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Buildings: the third part of the made layer ({@link Made}), laid over the networks and the earthworks: the kinds
 * of building, by what each is for. A building stands on the ground and changes nothing of it.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Buildings.md}.
 * Their tree is the made layer's, {@link Made#TREE}.
 */
public final class Buildings {

    private Buildings() {
    }

    /** The branch: a thing built to stand on the ground, for people to be in, or to land at, bar or mark. */
    public record Building() implements L1<Made> { public Made parent() { return new Made(); } }

    // Residence
    public record Residence() implements L2<Building> { public Building parent() { return new Building(); } }
    public record House() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record Farmhouse() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record CourtyardHouse() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }
    public record HomesTower() implements Leaf<Residence> { public Residence parent() { return new Residence(); } }

    // Commerce
    public record Commerce() implements L2<Building> { public Building parent() { return new Building(); } }
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
    public record Sacred() implements L2<Building> { public Building parent() { return new Building(); } }
    public record Temple() implements Leaf<Sacred> { public Sacred parent() { return new Sacred(); } }
    public record Church() implements Leaf<Sacred> { public Sacred parent() { return new Sacred(); } }

    // Civic
    public record Civic() implements L2<Building> { public Building parent() { return new Building(); } }
    public record Pavilion() implements Leaf<Civic> { public Civic parent() { return new Civic(); } }
    public record Courthouse() implements Leaf<Civic> { public Civic parent() { return new Civic(); } }

    // Infrastructure
    public record Infrastructure() implements L2<Building> { public Building parent() { return new Building(); } }
    public record Quay() implements Leaf<Infrastructure> {
        public Infrastructure parent() { return new Infrastructure(); }
    }

    // Fortification
    public record Fortification() implements L2<Building> { public Building parent() { return new Building(); } }
    public record GateTower() implements Leaf<Fortification> {
        public Fortification parent() { return new Fortification(); }
    }
}
