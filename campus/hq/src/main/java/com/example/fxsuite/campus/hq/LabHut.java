package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;

import java.util.List;

/**
 * A library of a lab's, as the campus has it: a building in its quarter, standing for one library of the code (the
 * campus plan: each building a library, each quarter the libraries released at one version). A hut of luxury for
 * now ({@link LuxuryHut}), each its own finish, until the library is given a building fitting what it does.
 *
 * <p>It stands where it is put in its quarter's plot, on the lowest of the ground round it, its plinth taking up the
 * slope; its door to the nearest of the road.
 *
 * @param quarter what quarter it is in, by name ({@link Quarters})
 * @param library the module it stands for, by its artifact
 * @param hut     the hut it is
 * @param east    where its middle is, cm east of the meadow's origin
 * @param north   cm north of it
 */
public record LabHut(String quarter, String library, LuxuryHut hut, double east, double north) {

    /**
     * The body shop: the Life quarter's library of what a human or humanoid body is: its skeleton from a few
     * proportions, its shapes on its bones, its trims and outfits; and its bench. On the quarter's plot, along the
     * avenue up toward the chapel, its door to the road.
     */
    public static final LabHut BODY_SHOP = new LabHut("Life", "fxsuite-life-body-shop", new LuxuryHut("Body shop",
            new EarthHouse.Size(1_100, 380, 55, 460, 90, 150, 260, 140, true),
            new LuxuryHut.Finish(Material.SANDSTONE, Material.TILE, Material.TIMBER, Material.STONE, Material.STONE,
                    Material.COPPER, Material.GLAZED, Material.LACQUER, Material.PAINTED, Material.TIMBER), 220,
            new LuxuryHut.Team(2, 1)), -6_000, 22_000);

    /** Every lab's library standing on the campus, in the organisation's order. */
    public static final List<LabHut> ALL = List.of(BODY_SHOP);

    /** What it is called. */
    public String name() {
        return hut.name();
    }

    /** The quarter it is in. */
    public Quarter inQuarter() {
        return Quarters.ALL.stream().filter(q -> q.name().equals(quarter)).findFirst()
                .orElseThrow(() -> new IllegalStateException(name() + ": no quarter " + quarter));
    }

    /** Where it stands: on the lowest of the ground round it, its door to the nearest point of the road. */
    public Place place() {
        final Land land = Campus.land();
        double low = land.height(east, north);
        final double reach = hut.reach();
        for (int k = 0; k < ROUND; k++) {
            final double a = 2 * Math.PI * k / ROUND;
            low = Math.min(low, land.height(east + reach * Math.sin(a), north + reach * Math.cos(a)));
        }
        final Vec2 road = nearestRoad();
        return new Place(east, north, low, Math.toDegrees(Math.atan2(road.x() - east, road.z() - north)));
    }

    /** How many points round it the ground is looked at, to stand on the lowest. */
    static final int ROUND = 24;

    /** The nearest point of the road's line to it. */
    Vec2 nearestRoad() {
        Vec2 best = null;
        double nearest = Double.POSITIVE_INFINITY;
        for (final Vec2 v : Village.line("village road", Campus.HOUSED)) {
            final double d = Math.hypot(v.x() - east, v.z() - north);
            if (d < nearest) {
                nearest = d;
                best = v;
            }
        }
        return best;
    }
}
