package com.example.fxsuite.campus.emblem;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The Innocent Melons emblem, pure geometry: a melon, a sphere {@code radius} round, cut three ways (east from west,
 * north from south, upper from lower) into eight octants; any of them ({@code lifted}) moved out from the middle along
 * its own diagonal by {@code lift}, or cut away altogether ({@code cutAway}); a gold halo over it, if {@code halo}.
 *
 * <p>Each octant whole, as the melon is: its skin in lunes and bands of {@value #STEP}° each, in three layers out from
 * the middle, red flesh, a cream band and a green rind striped along its meridians; so wherever a neighbour is lifted
 * away, its flat faces show the flesh, the cream and the rind round them, and its seeds, dark studs on its cut faces.
 * Facing south, as it is looked at from: east to its right.
 *
 * <p>In centimetres, a solid's frame: across (east), along (north), up; the melon's middle {@link #middle} over the
 * ground.
 *
 * @param radius  how big the melon is, to its rind
 * @param lifted  which octants are lifted out ({@link #octant}), 0 to 7
 * @param lift    how far each lifted one is moved out from the middle
 * @param cutAway whether the lifted ones are cut away instead, not there at all
 * @param halo    whether its halo is over it
 */
public record Emblem(double radius, Set<Integer> lifted, double lift, boolean cutAway, boolean halo) {

    /** How finely its skin is drawn: so many degrees a lune, a band. */
    static final int STEP = 15;
    /** How thick its rind and its cream are, as shares of its radius. */
    static final double RIND = 0.07;
    static final double CREAM = 0.05;
    /** Every how many lunes round a dark stripe runs, a lune wide. */
    static final int STRIPE = 3;
    /** Its seeds: so many on a face, so square, standing so far out of it. */
    static final int SEEDS = 5;
    static final double SEED = 3;
    static final double SEED_OUT = 0.8;
    /** Its halo: so far over its top, so wide round, its tube so thick, as shares of its radius; in so many pieces. */
    static final double HALO_OVER = 0.4;
    static final double HALO_ROUND = 0.55;
    static final double HALO_TUBE = 0.06;
    static final int HALO_PIECES = 32;

    /** A quarter cut out, as drawn: the upper east, front and back. */
    public static final Set<Integer> QUARTER = Set.of(octant(true, false, true), octant(true, true, true));
    /** An eighth: the upper south-east, toward who looks at it. */
    public static final Set<Integer> EIGHTH = Set.of(octant(true, false, true));
    /** Half: the east. */
    public static final Set<Integer> HALF = Set.of(octant(true, false, false), octant(true, true, false),
            octant(true, false, true), octant(true, true, true));

    public Emblem {
        lifted = Set.copyOf(lifted);
        if (lifted.stream().anyMatch(o -> o < 0 || o > 7) || !(radius > 0) || lift < 0) {
            throw new IllegalArgumentException("octants 0 to 7, a melon of some size, lifted out, not in");
        }
    }

    /** The octant east or west, north or south, upper or lower: its number, 0 to 7. */
    public static int octant(final boolean east, final boolean north, final boolean upper) {
        return (east ? 1 : 0) | (north ? 2 : 0) | (upper ? 4 : 0);
    }

    /** What octant {@code o} is called: upper or lower, then north or south, then east or west. */
    public static String name(final int o) {
        return ((o & 4) != 0 ? "upper " : "lower ") + ((o & 2) != 0 ? "north" : "south") + "-"
                + ((o & 1) != 0 ? "east" : "west");
    }

    /** How high its middle is over the ground: its radius, and a little more. */
    public double middle() {
        return radius * 1.2;
    }

    /** All of it: each octant not cut away, where it is; its halo. */
    public List<Solid> solids() {
        final List<Solid> all = new ArrayList<>();
        for (int o = 0; o < 8; o++) {
            if (lifted.contains(o) && cutAway) {
                continue;
            }
            final double out = lifted.contains(o) ? lift / Math.sqrt(3) : 0;
            final double ox = sign(o, 1) * out;
            final double oz = sign(o, 2) * out;
            final double oy = sign(o, 4) * out;
            for (final Solid s : octantSolids(o)) {
                all.add(moved(s, ox, oz, oy));
            }
        }
        if (halo) {
            all.addAll(haloSolids());
        }
        return all;
    }

    /** Octant {@code o} whole, in its place: its layers lune by lune and band by band, and its seeds. */
    List<Solid> octantSolids(final int o) {
        final List<Solid> solids = new ArrayList<>();
        final int quadrant = quadrant(o);
        final double latFrom = (o & 4) != 0 ? 0 : -90;
        final double[] layers = {0, radius * (1 - RIND - CREAM), radius * (1 - RIND), radius};
        final Material[] stuff = {Material.LACQUER, Material.LIMESTONE, null};
        for (int a = 0; a < 90 / STEP; a++) {
            final double az0 = quadrant * 90 + a * STEP;
            final boolean dark = (quadrant * (90 / STEP) + a) % STRIPE == 0;
            for (int b = 0; b < 90 / STEP; b++) {
                final double lat0 = latFrom + b * STEP;
                for (int l = 0; l < 3; l++) {
                    final Material m = l < 2 ? stuff[l] : dark ? Material.LEAVES : Material.GREEN_GLAZED;
                    solids.add(patch(String.format(Locale.ROOT, "%s, %s %d %d", name(o), l == 0 ? "flesh"
                            : l == 1 ? "cream" : "rind", a, b), m, az0, az0 + STEP, lat0, lat0 + STEP, layers[l],
                            layers[l + 1]));
                }
            }
        }
        solids.addAll(seeds(o));
        return solids;
    }

    /** Which quarter round octant {@code o} is, counting from the east toward the north: 0 to 3. */
    static int quadrant(final int o) {
        final boolean east = (o & 1) != 0;
        final boolean north = (o & 2) != 0;
        return east ? (north ? 0 : 3) : (north ? 1 : 2);
    }

    /** +1 or -1: whether octant {@code o} has the side {@code bit} says (east 1, north 2, upper 4). */
    static int sign(final int o, final int bit) {
        return (o & bit) != 0 ? 1 : -1;
    }

    /**
     * A piece of a layer: from azimuth {@code az0} to {@code az1} (degrees from the east toward the north), latitude
     * {@code lat0} to {@code lat1}, out from {@code r0} to {@code r1}; its corners by azimuth, latitude, radius, as a
     * solid's are by its three ways.
     */
    private Solid patch(final String name, final Material m, final double az0, final double az1, final double lat0,
                        final double lat1, final double r0, final double r1) {
        final List<Point> corners = new ArrayList<>(8);
        for (int k = 0; k < 8; k++) {
            corners.add(at((k & 4) == 0 ? r0 : r1, (k & 1) == 0 ? az0 : az1, (k & 2) == 0 ? lat0 : lat1));
        }
        return new Solid(name, Role.DRESSING, m, corners);
    }

    /** {@code r} out from its middle, at azimuth {@code az} and latitude {@code lat}, degrees. */
    private Point at(final double r, final double az, final double lat) {
        final double a = Math.toRadians(az);
        final double t = Math.toRadians(lat);
        return new Point(r * Math.cos(t) * Math.cos(a), r * Math.cos(t) * Math.sin(a), middle() + r * Math.sin(t));
    }

    /**
     * Octant {@code o}'s seeds, on each of its three cut faces (the meridian faces east and north of it, its equator's):
     * so many, in its flesh, spread as a sunflower's seeds are, by the golden angle; each a dark stud standing out of
     * its face toward the neighbour across it, hidden in that while the neighbour is there.
     */
    List<Solid> seeds(final int o) {
        final List<Solid> seeds = new ArrayList<>();
        final int sx = sign(o, 1);
        final int sz = sign(o, 2);
        final int sy = sign(o, 4);
        final double flesh = radius * (1 - RIND - CREAM);
        final double h = SEED / 2;
        for (int n = 0; n < SEEDS; n++) {
            final double angle = Math.toRadians(10 + (n * 137.508) % 70);
            final double r = flesh * (0.35 + 0.45 * Math.sqrt((n + 0.5) / SEEDS));
            final double c = r * Math.cos(angle);
            final double s = r * Math.sin(angle);
            final double y = middle() + sy * s;
            // on the plane east of west (x = 0), standing out toward the west or east beyond
            seeds.add(box(name(o) + ", seed", -sx * SEED_OUT, 0, sz * c - h, sz * c + h, y - h, y + h));
            // on the plane north of south (z = 0)
            seeds.add(box(name(o) + ", seed", sx * c - h, sx * c + h, -sz * SEED_OUT, 0, y - h, y + h));
            // on its equator's plane, standing out down from an upper octant, up from a lower
            seeds.add(box(name(o) + ", seed", sx * c - h, sx * c + h, sz * s - h, sz * s + h, middle() - sy * SEED_OUT,
                    middle()));
        }
        return seeds;
    }

    /** Its halo: a ring of so many pieces of a square tube, level, over its top. */
    List<Solid> haloSolids() {
        final List<Solid> halo = new ArrayList<>();
        final double up = middle() + radius * (1 + HALO_OVER);
        final double round = radius * HALO_ROUND;
        final double t = radius * HALO_TUBE;
        for (int k = 0; k < HALO_PIECES; k++) {
            final double a0 = 2 * Math.PI * k / HALO_PIECES;
            final double a1 = 2 * Math.PI * (k + 1) / HALO_PIECES;
            final List<Point> corners = new ArrayList<>(8);
            for (int c = 0; c < 8; c++) {
                // out, round, up: as a solid's across, along, up are, where the ring starts, east of its middle
                final double r = (c & 1) == 0 ? round - t : round + t;
                final double a = (c & 2) == 0 ? a0 : a1;
                corners.add(new Point(r * Math.cos(a), r * Math.sin(a), (c & 4) == 0 ? up - t : up + t));
            }
            halo.add(new Solid("halo " + k, Role.DRESSING, Material.GLAZED, corners));
        }
        return halo;
    }

    /** A box, square to the melon, its corners in the order a solid's are: across, along, up. */
    private static Solid box(final String name, final double x0, final double x1, final double z0, final double z1,
                             final double y0, final double y1) {
        final List<Point> corners = new ArrayList<>(8);
        for (int k = 0; k < 8; k++) {
            corners.add(new Point(Math.min(x0, x1) + ((k & 1) == 0 ? 0 : Math.abs(x1 - x0)),
                    Math.min(z0, z1) + ((k & 2) == 0 ? 0 : Math.abs(z1 - z0)), (k & 4) == 0 ? y0 : y1));
        }
        return new Solid(name, Role.DRESSING, Material.METAL, corners);
    }

    /** {@code s} moved so far east, north and up. */
    private static Solid moved(final Solid s, final double x, final double z, final double y) {
        return new Solid(s.name(), s.role(), s.material(), s.corners().stream()
                .map(p -> new Point(p.across() + x, p.along() + z, p.up() + y)).toList());
    }
}
