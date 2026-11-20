package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Extrusion;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.GateTowerSpec;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a city's gate, as the Qingming scroll draws the one its street ends at: a base through
 * the wall, a passage through the base, a tower on it. Its width along the wall across (x), its passage along z, its
 * outer face toward +z.
 *
 * <ul>
 * <li><b>Its base</b> (城台), faced in brick, battered as the wall is: narrower at its top than at its foot by half its
 * height.
 * <li><b>Its passage</b>, not an arch: the Song's gates were framed in timber. Posts (排叉柱) stand along both its
 * sides; over each pair, two struts slant in to a beam across the top, so its head is a trapezoid; its two door
 * leaves stand open against its sides, as the scroll has the gate standing open.
 * <li><b>Its tower</b> (城楼), a hall of the Song's frame ({@link SongFrame}) under a folded gable roof, open all
 * round, looked out from; a railing along the base's front and back edges.
 * <li><b>Its way up</b> (慢道), inside the wall beside its base, along the wall: a ramp of brick from the ground to the
 * base's end, a foot up for every five along, as the Yingzao Fashi has a city gate's (城门慢道); a stone curb along its
 * open edge. Where it passes the height of a wall as high as the Song's rule makes one beside it, the wall's top is
 * stepped onto from it.
 * </ul>
 * Estimates: its passage 6 m to its head, its posts 4.2 m, its tower's bays 4 m wide and the tower 8 m deep, its
 * railings 1 m high, its way up 2.75 m of the base's end wide where it lands. The scroll's tower has a hipped roof; a
 * gable is built here.
 */
public final class SongGateTower
        implements NorthernSongImpl<Buildings.GateTower, GateTowerSpec<Chinese, ChineseEras.NorthernSong>> {

    /** Its passage so high to its head, its posts so high and so thick, a frame of them so far from the next. */
    static final double HEAD = 600;
    static final double POST = 420;
    static final double THICK = 30;
    static final double FRAMES = 150;
    /** Its tower's bays so wide, the tower so deep; its railings so high; its base's footing. */
    static final double BAY = 400;
    static final double DEPTH = 800;
    static final double RAIL = 100;
    static final double FOOTING = 40;
    /** Its way up so far along for each of its rise; so much of the base's end wide where it lands; its curb. */
    static final double SLOW = 5;
    static final double LANDING = 275;
    static final double CURB = 30;

    @Override
    public Buildings.GateTower thing() {
        return new Buildings.GateTower();
    }

    /** How far {@code s}'s base reaches along from its middle at its top: in from its foot by a quarter its height. */
    static double top(final GateTowerSpec<?, ?> s) {
        return s.deep() / 2 - s.high() / 4;
    }

    /**
     * Where {@code s}'s way up is: across, its head at the base's end and its foot; along, its open edge at the base's
     * foot and its other, in from the base's top's edge by its landing.
     */
    static double[] wayUp(final GateTowerSpec<?, ?> s) {
        final int side = s.ascent() == GateTowerSpec.Ascent.RIGHT ? 1 : -1;
        return new double[] {side * s.wide() / 2, side * (s.wide() / 2 + SLOW * s.high()), -s.deep() / 2,
            -top(s) + LANDING};
    }

    private static List<Vec2> floor(final double x0, final double x1, final double z0, final double z1) {
        return List.of(new Vec2(Math.min(x0, x1), z0), new Vec2(Math.max(x0, x1), z0), new Vec2(Math.max(x0, x1), z1),
                new Vec2(Math.min(x0, x1), z1));
    }

    @Override
    public Structure build(final GateTowerSpec<Chinese, ChineseEras.NorthernSong> s) {
        final HallSpec<Buildings.GateTower, Chinese, ChineseEras.NorthernSong> tower = new HallSpec<>(s.bays(), BAY,
                DEPTH, Roofing.TILE);
        if (s.high() < HEAD + 150 || top(s) < DEPTH / 2 + SongFrame.EAVE || s.wide() < tower.width() + 200) {
            throw new IllegalArgumentException("a gate's base high enough over its passage and large enough for its"
                    + " tower");
        }
        final List<Part> parts = new ArrayList<>();
        final double a = s.wide() / 2;
        final double p = s.passage() / 2;
        final double d = s.deep() / 2;
        final Leaf<?> base = new Elements.Platform();
        parts.add(new Part(base, Timbers.battered("gate base", Role.BASE, Material.BRICK, -a, -p, d, top(s),
                -FOOTING, s.high())));
        parts.add(new Part(base, Timbers.battered("gate base", Role.BASE, Material.BRICK, p, a, d, top(s), -FOOTING,
                s.high())));
        parts.add(new Part(base, Timbers.battered("over the passage", Role.BASE, Material.BRICK, -p, p,
                d - HEAD / 4, top(s), HEAD, s.high())));
        passage(s, parts);
        // a railing along the base's front and back edges
        for (final int side : new int[] {-1, 1}) {
            railing(parts, -a + 10, side * (top(s) - 10), a - 10, side * (top(s) - 10), s.high());
        }
        // its way up: the ramp, its top at the base's at its head; the curb along its open edge
        final double[] up = wayUp(s);
        final boolean right = up[1] > up[0];
        parts.add(new Part(new Elements.Incline(), Timbers.wedge("way up", Role.BASE, Material.BRICK,
                Math.min(up[0], up[1]), Math.max(up[0], up[1]), up[2], up[3], -FOOTING, right ? s.high() : 0,
                right ? 0 : s.high())));
        parts.add(new Part(new Elements.Parapet(), Timbers.strut("curb", Role.BASE, Material.STONE,
                up[2] + CURB / 2, up[1], CURB / 2, up[0], s.high() + CURB / 2, CURB)));
        // a railing across what of its head is past the base's top's edge, where there is nothing to go on to
        railing(parts, up[0], -d + 10, up[0], -top(s) - 6, s.high());
        final double middle = (up[2] + up[3]) / 2;
        final Structure.WayIn foot = new Structure.WayIn(up[1], middle, 0);
        SongFrame.storey(parts, tower, s.high(), null);
        SongFrame.pediments(parts, tower, s.high());
        SongFrame.roofing(parts, tower, s.high());
        return new Structure(parts, shape(s, parts),
                List.of(new Structure.Room(new Spaces.Gateway(), new Uses.Passage(), List.of(new Vec2(-p, -d),
                                new Vec2(p, -d), new Vec2(p, d), new Vec2(-p, d)), 0),
                        new Structure.Room(new Spaces.Hall(), new Uses.Watch(), SongFrame.floor(tower), s.high()),
                        new Structure.Room(new Spaces.Terrace(), new Uses.Watch(), floor(-a, a, -top(s), top(s)),
                                s.high()),
                        new Structure.Room(new Spaces.Ramp(), new Uses.Watch(), floor(up[0], up[1], up[2], up[3]), 0)),
                List.of(new Structure.WayIn(0, d, 0), new Structure.WayIn(0, -d, 0), foot),
                List.of(new Structure.Stair(foot, new Structure.WayIn(up[0], middle, s.high()))));
    }

    /** A railing from one place to another, level, square to the base, its foot at {@code y}: posts and three rails. */
    private static void railing(final List<Part> parts, final double x0, final double z0, final double x1,
                                final double z1, final double y) {
        final Leaf<?> railing = new Elements.Railing();
        final int posts = (int) Math.ceil(Math.hypot(x1 - x0, z1 - z0) / 250);
        for (int k = 0; k <= posts; k++) {
            final double x = x0 + (x1 - x0) * k / posts;
            final double z = z0 + (z1 - z0) * k / posts;
            parts.add(new Part(railing, Timbers.block("post", Role.FRAME, Material.TIMBER, x - 6, x + 6, z - 6, z + 6,
                    y, y + RAIL + 10)));
        }
        for (final double at : new double[] {12, RAIL / 2, RAIL}) {
            parts.add(new Part(railing, Timbers.block("rail", Role.FRAME, Material.TIMBER, Math.min(x0, x1) - 4,
                    Math.max(x0, x1) + 4, Math.min(z0, z1) - 4, Math.max(z0, z1) + 4, y + at - 8, y + at)));
        }
    }

    /** Its passage's timber: a frame every so far along it, two posts, two struts and a beam; its two leaves, open. */
    private static void passage(final GateTowerSpec<?, ?> s, final List<Part> parts) {
        final double p = s.passage() / 2;
        final double d = s.deep() / 2;
        final double in = p - THICK / 2;
        final Leaf<?> beam = new Elements.Beam();
        final int frames = (int) Math.floor((s.deep() - 120) / FRAMES);
        for (int k = 0; k <= frames; k++) {
            final double z = -d + 60 + k * (s.deep() - 120) / frames;
            for (final int side : new int[] {-1, 1}) {
                parts.add(new Part(new Elements.Column(), Timbers.block("passage post", Role.STRUCTURE,
                        Material.TIMBER, side * in - THICK / 2, side * in + THICK / 2, z - THICK / 2, z + THICK / 2, 0,
                        POST)));
                parts.add(new Part(beam, Timbers.strut("passage strut", Role.STRUCTURE, Material.TIMBER, z, side * in,
                        POST, side * p / 2, HEAD - THICK / 2, THICK - 6)));
            }
            parts.add(new Part(beam, Timbers.strut("passage beam", Role.STRUCTURE, Material.TIMBER, z, -p / 2,
                    HEAD - THICK / 2, p / 2, HEAD - THICK / 2, THICK - 6)));
        }
        for (final int side : new int[] {-1, 1}) {
            parts.add(new Part(new Elements.Door(), Timbers.block("gate leaf", Role.DRESSING, Material.LACQUER,
                    side * (p - THICK - 4) - 4, side * (p - THICK - 4) + 4, d - 50 - p, d - 50, 0, POST - 20)));
        }
    }

    /**
     * What it is in the way: its base as its side carried along it ({@link Extrusion}), battered as it is drawn, too
     * steep to walk up, in three: either side of its passage, and over it; its way up as its side carried across it,
     * walked up; everything else, its passage's posts and leaves, its railings, its tower, each what it covers seen
     * from above.
     */
    private static List<Volume> shape(final GateTowerSpec<?, ?> s, final List<Part> parts) {
        final double a = s.wide() / 2;
        final double p = s.passage() / 2;
        final double d = s.deep() / 2;
        final List<Vec2> pier = List.of(new Vec2(-d, -FOOTING), new Vec2(d, -FOOTING), new Vec2(top(s), s.high()),
                new Vec2(-top(s), s.high()));
        final List<Vec2> over = List.of(new Vec2(-d + HEAD / 4, HEAD), new Vec2(d - HEAD / 4, HEAD),
                new Vec2(top(s), s.high()), new Vec2(-top(s), s.high()));
        final double[] up = wayUp(s);
        final List<Vec2> side = List.of(new Vec2(up[0], -FOOTING), new Vec2(up[1], -FOOTING), new Vec2(up[1], 0),
                new Vec2(up[0], s.high()));
        final List<Volume> shape = new ArrayList<>(List.of(new Extrusion(-(a + p) / 2, 0, 90, (a - p) / 2, pier),
                new Extrusion((a + p) / 2, 0, 90, (a - p) / 2, pier), new Extrusion(0, 0, 90, p, over),
                new Extrusion(0, (up[2] + up[3]) / 2, 0, (up[3] - up[2]) / 2, side)));
        shape.addAll(StandIns.prisms(parts.stream().filter(part -> !(part.element() instanceof Elements.Platform)
                && !(part.element() instanceof Elements.Incline) && !(part.element() instanceof Elements.Parapet)
                && !part.solid().name().startsWith("passage strut") && !part.solid().name().startsWith("passage beam"))
                .map(Part::solid).toList()));
        return shape;
    }
}
