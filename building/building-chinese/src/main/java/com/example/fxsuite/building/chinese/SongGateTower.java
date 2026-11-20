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
 * </ul>
 * Estimates: its passage 6 m to its head, its posts 4.2 m, its tower's bays 4 m wide and the tower 8 m deep, its
 * railings 1 m high. The scroll's tower has a hipped roof; a gable is built here.
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

    @Override
    public Buildings.GateTower thing() {
        return new Buildings.GateTower();
    }

    /** How far {@code s}'s base reaches along from its middle at its top: in from its foot by a quarter its height. */
    static double top(final GateTowerSpec<?, ?> s) {
        return s.deep() / 2 - s.high() / 4;
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
        // a railing along the base's front and back edges, posts and three rails
        final Leaf<?> railing = new Elements.Railing();
        for (final int side : new int[] {-1, 1}) {
            final double z = side * (top(s) - 10);
            for (double x = -a + 10; x <= a - 10 + 1e-6; x += (s.wide() - 20) / Math.ceil((s.wide() - 20) / 250)) {
                parts.add(new Part(railing, Timbers.block("post", Role.FRAME, Material.TIMBER, x - 6, x + 6, z - 6,
                        z + 6, s.high(), s.high() + RAIL + 10)));
            }
            for (final double at : new double[] {12, RAIL / 2, RAIL}) {
                parts.add(new Part(railing, Timbers.block("rail", Role.FRAME, Material.TIMBER, -a + 10, a - 10, z - 4,
                        z + 4, s.high() + at - 8, s.high() + at)));
            }
        }
        SongFrame.storey(parts, tower, s.high(), null);
        SongFrame.pediments(parts, tower, s.high());
        SongFrame.roofing(parts, tower, s.high());
        return new Structure(parts, shape(s, parts),
                List.of(new Structure.Room(new Spaces.Gateway(), new Uses.Passage(), List.of(new Vec2(-p, -d),
                                new Vec2(p, -d), new Vec2(p, d), new Vec2(-p, d)), 0),
                        new Structure.Room(new Spaces.Hall(), new Uses.Watch(), SongFrame.floor(tower), s.high())),
                List.of(new Structure.WayIn(0, d, 0), new Structure.WayIn(0, -d, 0)));
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
     * What it is in the way: its base as its front carried through it (an {@link Extrusion}), its passage open through
     * it; everything else, its passage's posts and leaves, its railings, its tower, each what it covers seen from
     * above.
     */
    private static List<Volume> shape(final GateTowerSpec<?, ?> s, final List<Part> parts) {
        final double a = s.wide() / 2;
        final double p = s.passage() / 2;
        final List<Vec2> front = List.of(new Vec2(-a, -FOOTING), new Vec2(a, -FOOTING), new Vec2(a, s.high()),
                new Vec2(-a, s.high()));
        final List<Vec2> way = List.of(new Vec2(-p, -FOOTING + 5), new Vec2(p, -FOOTING + 5), new Vec2(p, HEAD),
                new Vec2(-p, HEAD));
        final List<Volume> shape = new ArrayList<>(List.of(new Extrusion(0, 0, 0, s.deep() / 2, front, List.of(way))));
        shape.addAll(StandIns.prisms(parts.stream().filter(part -> !(part.element() instanceof Elements.Platform)
                && !part.solid().name().startsWith("passage strut") && !part.solid().name().startsWith("passage beam"))
                .map(Part::solid).toList()));
        return shape;
    }
}
