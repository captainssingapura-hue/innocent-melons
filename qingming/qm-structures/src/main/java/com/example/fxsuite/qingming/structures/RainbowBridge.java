package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Sites;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.build.Town;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Extrusion;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.qingming.land.Rivers;

import java.util.ArrayList;
import java.util.List;

/**
 * The Rainbow Bridge (虹桥): a woven timber arch over the Bian with no pier in the river, "其橋無柱，皆以巨木虛架" (东京梦华录),
 * as the scroll draws it and its historians read it. Across the river, square to it at the reach's origin; in
 * centimetres, x across the bridge, z along it (north), up from the street.
 *
 * <ul>
 * <li><b>Its arch</b>, woven of two systems of squared logs 40 cm thick (茅以升's reading), 21 groups side by side 45 cm
 * apart: eleven of four members, two long from the banks and two short to the crown, the outermost among them; between
 * them ten of three, two from the banks and one level across the top; tied by five logs across the whole width where
 * the systems meet. Its feet on the masonry end walls (金刚墙) a little over the water; its crown some 5 m over the water,
 * its span 20 m between the walls, as 唐寰澄 reckons them.
 * <li><b>Its deck</b>, boards across the arch, 8 m wide, rising from the street at each end to 3.6 m over it at the
 * crown, as steep as the scroll draws it; walked up and over, the river open under the arch.
 * <li><b>Its railings</b>, 23 posts a side about 1 m apart, a top rail, a middle rail and a sill, two taller posts at
 * each end; <b>four poles</b>, two at each end, a cross-bar, a disc and a crane at the top of each.
 * </ul>
 * Estimates, nothing published saying: the railings 1 m high, the deck's boards 15 cm thick, the end walls as high as
 * the bank, the poles 9 m.
 */
public final class RainbowBridge {

    private RainbowBridge() {
    }

    /** The key it is drawn by. */
    public static final int KEY = 700;
    /** Its deck: so wide either side of its middle; so high over the street at its crown; meeting it so far out. */
    static final double HALF = 400;
    static final double CROWN = 360;
    static final double REACH = 1_250;
    static final double BOARD = 15;
    /** Its arch: its logs so thick, so far apart across, so many groups; its feet so far out and so high. */
    static final double LOG = 40;
    static final double PITCH = 45;
    static final int GROUPS = 21;
    static final double FOOT = 1_000;
    static final double SPRING = -Rivers.DEPTH + Rivers.WATER + 44;
    /** Where the systems meet: the three-member system's two joints, the four-member's two and its crown. */
    static final double THREE = 560;
    static final double FOUR = 300;
    /** Its end walls, of stone: from their face at the arch's feet back to the bank, so wide across. */
    static final double WALL = 1_150;
    static final double WALL_HALF = 480;
    /** Its railings: so high, so many posts a side; its poles so high, so far out from its middle and its ends. */
    static final double RAIL = 100;
    static final int POSTS = 23;
    static final double POLE = 900;

    /** Its deck's top at {@code z} along: rising from the street at {@link #REACH} each way to {@link #CROWN}. */
    static double deck(final double z) {
        final double t = Math.abs(z) / REACH;
        return t >= 1 ? 0 : CROWN * (1 - t * t);
    }

    /** Where its arch's members are under the deck at {@code z}: their middles, so the deck's boards lie on them. */
    private static double under(final double z) {
        return deck(z) - BOARD - LOG / 2;
    }

    /** All of it, timber and stone. Made once. */
    public static List<Solid> solids() {
        return Made.SOLIDS;
    }

    private static final class Made {
        static final List<Solid> SOLIDS = make();
    }

    private static List<Solid> make() {
        final List<Solid> s = new ArrayList<>();
        // the arch: the four-member groups on the even places across, the outermost among them
        for (int g = 0; g < GROUPS; g++) {
            final double x = (g - (GROUPS - 1) / 2.0) * PITCH;
            if (g % 2 == 0) {
                for (final int side : new int[] {-1, 1}) {
                    s.add(Timbers.member("arch four long", Role.STRUCTURE, Material.TIMBER, x, side * FOOT, SPRING,
                            side * FOUR, under(FOUR), LOG));
                    s.add(Timbers.member("arch four short", Role.STRUCTURE, Material.TIMBER, x, side * FOUR,
                            under(FOUR), 0, under(0), LOG));
                }
            } else {
                for (final int side : new int[] {-1, 1}) {
                    s.add(Timbers.member("arch three side", Role.STRUCTURE, Material.TIMBER, x, side * FOOT, SPRING,
                            side * THREE, under(THREE), LOG));
                }
                s.add(Timbers.member("arch three top", Role.STRUCTURE, Material.TIMBER, x, -THREE, under(THREE), THREE,
                        under(THREE), LOG));
            }
        }
        // the five logs across where the systems meet
        final double span = GROUPS * PITCH / 2;
        for (final double z : new double[] {-THREE, -FOUR, 0, FOUR, THREE}) {
            final double y = under(z) - LOG;
            s.add(Timbers.block("arch across", Role.STRUCTURE, Material.TIMBER, -span, span, z - LOG / 2, z + LOG / 2,
                    y - LOG / 2, y + LOG / 2));
        }
        // its deck: boards across, each half a metre along, on the slope
        final int boards = (int) Math.round(2 * REACH / 50);
        for (int b = 0; b < boards; b++) {
            final double z0 = -REACH + b * 50;
            final double z1 = z0 + 50;
            s.add(Timbers.board("deck", Role.STRUCTURE, Material.TIMBER, -HALF, HALF, z0, z1, deck(z0), deck(z1),
                    BOARD));
        }
        // its end walls, stone, from the bed to the street
        for (final int side : new int[] {-1, 1}) {
            s.add(Timbers.block("end wall", Role.BASE, Material.STONE, -WALL_HALF, WALL_HALF,
                    Math.min(side * FOOT, side * WALL), Math.max(side * FOOT, side * WALL), -Rivers.DEPTH, 0));
        }
        railings(s);
        poles(s);
        return List.copyOf(s);
    }

    /** Its railings along both edges: posts; a sill, a middle rail and a top rail between each two; tall end posts. */
    private static void railings(final List<Solid> s) {
        for (final int side : new int[] {-1, 1}) {
            final double x = side * (HALF - 8);
            for (int p = 0; p < POSTS; p++) {
                final double z = -REACH + p * 2 * REACH / (POSTS - 1);
                final boolean end = p == 0 || p == POSTS - 1;
                final double tall = end ? RAIL + 50 : RAIL;
                s.add(Timbers.block(end ? "end post" : "post", Role.FRAME, Material.TIMBER, x - 8, x + 8, z - 8, z + 8,
                        deck(z), deck(z) + tall));
                if (p + 1 < POSTS) {
                    final double z1 = -REACH + (p + 1) * 2 * REACH / (POSTS - 1);
                    for (final double at : new double[] {8, RAIL / 2, RAIL - 6}) {
                        s.add(Timbers.board("rail", Role.FRAME, Material.TIMBER, x - 5, x + 5, z + 8, z1 - 8,
                                deck(z + 8) + at, deck(z1 - 8) + at, 8));
                    }
                }
            }
        }
    }

    /** Its four poles, two at each end beside it: a pole, a cross-bar, a disc on it, a crane on the disc. */
    private static void poles(final List<Solid> s) {
        for (final int end : new int[] {-1, 1}) {
            for (final int side : new int[] {-1, 1}) {
                final double x = side * (HALF + 200);
                final double z = end * (REACH + 150);
                s.add(Timbers.block("pole", Role.DRESSING, Material.TIMBER, x - 12, x + 12, z - 12, z + 12, 0, POLE));
                s.add(Timbers.block("pole bar", Role.DRESSING, Material.TIMBER, x - 80, x + 80, z - 5, z + 5,
                        POLE - 60, POLE - 50));
                s.add(Timbers.block("pole bar", Role.DRESSING, Material.TIMBER, x - 5, x + 5, z - 80, z + 80,
                        POLE - 60, POLE - 50));
                s.add(Timbers.block("pole disc", Role.DRESSING, Material.TIMBER, x - 30, x + 30, z - 30, z + 30,
                        POLE, POLE + 6));
                s.add(Timbers.block("pole crane", Role.DRESSING, Material.PAINTED, x - 8, x + 8, z - 25, z + 25,
                        POLE + 6, POLE + 40));
            }
        }
    }

    /**
     * What it is in the way, in its own frame: its deck and arch one thing, its side a profile along it carried across
     * its width (an {@link Extrusion}), walked up and over as a ramp is, the river's passage under the arch open
     * through it; its railings and poles each what it covers seen from above ({@link StandIns#prisms}).
     */
    public static List<Volume> shape() {
        final List<Vec2> side = new ArrayList<>();
        for (double z = -REACH; z <= REACH + 1; z += 50) {
            side.add(new Vec2(-z, deck(z)));
        }
        final double bed = -Rivers.DEPTH;
        side.add(new Vec2(-REACH, bed - 10));
        side.add(new Vec2(REACH, bed - 10));
        // under the arch: from the bed up the walls' faces to the long members' undersides, level under the crown
        final double low = under(FOUR) - LOG;
        final List<Vec2> passage = List.of(new Vec2(-(FOOT - 20), bed - 5), new Vec2(FOOT - 20, bed - 5),
                new Vec2(FOOT - 20, SPRING - LOG), new Vec2(FOUR, low), new Vec2(-FOUR, low),
                new Vec2(-(FOOT - 20), SPRING - LOG));
        final List<Volume> shape = new ArrayList<>(List.of(new Extrusion(0, 0, 90, HALF, side, List.of(passage))));
        shape.addAll(StandIns.prisms(solids().stream().filter(t -> t.role() == Role.FRAME || t.role() == Role.DRESSING)
                .toList()));
        return shape;
    }

    /** It as a piece of the world, standing at the reach's origin, square to it; told to the drawing by its key. */
    public static List<Course.Piece> pieces() {
        Sites.register(KEY, KEY + 1, k -> solids());
        return List.of(Town.building(solids(), new Place(0, 0, 0, 0), KEY, shape(), 1));
    }
}
