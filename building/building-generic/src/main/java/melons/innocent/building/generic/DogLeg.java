package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * A dog-leg stair in a floor plan, up from each storey to the next, as the housing board's are: come to on each floor
 * at its front edge; from there a flight up on its left going in ({@link Flights}), to a half landing across the whole
 * of it at the back, and a flight on up on its right, back to the front edge of the floor over; a wall between the two
 * flights, from the front edge to the landing, floor to floor. Each tread a block of concrete of its own, two risers
 * deep, so the flight is something to stand on step by step, and stairs over one another are clear of each other by
 * all but two risers of a storey. What it takes up in plan its well ({@link #well}), cut out of each slab it rises
 * through. On the floor at the top, where no flight goes on up, a rail across the head of the flight that is not
 * there and along the wall between the flights. Its sides and its back are walls the plan has round it.
 *
 * @param x    where the middle of its front edge is, across
 * @param z    where, along
 * @param runX which way it runs in from its front edge, across: one, nought or minus one
 * @param runZ which way, along: square to the plan, so one of the two nought
 * @param wide how wide it is, its two flights and the wall between them
 * @param deep how far in it goes from its front edge, its flights and its half landing
 */
record DogLeg(double x, double z, double runX, double runZ, double wide, double deep) {

    /** How thick the wall between its flights is, and its landing. */
    static final double CENTRE = 10;
    static final double LANDING = 15;
    /** The shallowest its half landing may be. */
    static final double LEAST = 100;
    /** How high its rail is at the top. */
    static final double RAIL = 110;

    DogLeg {
        if (Math.abs(runX) + Math.abs(runZ) != 1 || runX * runZ != 0) {
            throw new IllegalArgumentException("a stair runs in square to the plan: " + runX + ", " + runZ);
        }
        if (!(wide > 2 * CENTRE) || !(deep > 0)) {
            throw new IllegalArgumentException("a stair of some size: " + wide + " wide, " + deep + " deep");
        }
    }

    /** What it takes up in plan, from its front edge in: the well it rises through each slab by. */
    Box well() {
        return box(0, deep, -wide / 2, wide / 2);
    }

    /** Its parts up a storey from {@code floor}, {@code rise} high: its flights, its half landing, the wall between. */
    List<Part> storey(final double floor, final double rise) {
        final Flights flights = new Flights(rise);
        final int n = flights.treads();
        final double r = flights.riser();
        final double g = Flights.GOING;
        if (deep - flights.run() < LEAST) {
            throw new IllegalArgumentException("a half landing at least " + LEAST + " deep: " + (deep
                    - flights.run()) + " left of " + deep + " for a storey " + rise + " high");
        }
        final List<Part> parts = new ArrayList<>();
        for (int j = 1; j <= n; j++) {
            parts.add(tread("stair's tread", box((j - 1) * g, j * g, CENTRE / 2, wide / 2), floor, floor + j * r,
                    r));
        }
        for (int i = 1; i <= n; i++) {
            parts.add(tread("stair's tread", box((n - i) * g, (n - i + 1) * g, -wide / 2, -CENTRE / 2), floor,
                    floor + (n + 1 + i) * r, r));
        }
        final double landing = floor + (n + 1) * r;
        parts.add(part(new Elements.Steps(), "stair's half landing", box(flights.run(), deep, -wide / 2,
                wide / 2), landing - LANDING, landing));
        parts.add(part(new Elements.Steps(), "stair's middle wall", box(0, flights.run(), -CENTRE / 2,
                CENTRE / 2), floor, floor + rise));
        return parts;
    }

    /** Its parts on the floor at the top, at {@code floor}: its rails. */
    List<Part> top(final double floor) {
        return List.of(
                part(new Elements.Railing(), "stair's rail", box(0, CENTRE, CENTRE / 2, wide / 2), floor,
                        floor + RAIL),
                part(new Elements.Railing(), "stair's rail", box(0, deep, -CENTRE / 2, CENTRE / 2), floor,
                        floor + RAIL));
    }

    /**
     * It up a storey from {@code floor}, {@code rise} high, as a stair of the building: its foot at the front edge,
     * at the flight up; its head at the front edge of the floor over, at the flight on.
     */
    Structure.Stair stair(final double floor, final double rise) {
        final double side = (wide + CENTRE) / 4;
        return new Structure.Stair(new Structure.WayIn(across(0, side), along(0, side), floor),
                new Structure.WayIn(across(0, -side), along(0, -side), floor + rise));
    }

    /** A tread, its top at {@code top}, two risers {@code r} deep but none of it under {@code floor}. */
    private Part tread(final String name, final Box box, final double floor, final double top, final double r) {
        return part(new Elements.Steps(), name, box, Math.max(floor, top - 2 * r), top);
    }

    private Part part(final Leaf<?> element, final String name, final Box b, final double y0, final double y1) {
        return new Part(element, Timbers.INSTANCE.block(name, Role.STRUCTURE, Material.CONCRETE, b.x0(), b.x1(),
                b.z0(), b.z1(), y0, y1));
    }

    /** What lies from {@code u0} to {@code u1} in from its front edge and {@code v0} to {@code v1} to its left. */
    private Box box(final double u0, final double u1, final double v0, final double v1) {
        final double ax = across(u0, v0);
        final double az = along(u0, v0);
        final double bx = across(u1, v1);
        final double bz = along(u1, v1);
        return new Box(Math.min(ax, bx), Math.min(az, bz), Math.max(ax, bx), Math.max(az, bz));
    }

    /** Where {@code u} in from its front edge and {@code v} to its left is, across; its left a quarter on from in. */
    private double across(final double u, final double v) {
        return x + u * runX - v * runZ;
    }

    /** The same, along. */
    private double along(final double u, final double v) {
        return z + u * runZ + v * runX;
    }
}
