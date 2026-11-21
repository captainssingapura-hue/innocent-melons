package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.FarmhouseSpec;
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
 * How the Northern Song built a farmhouse, as the Qingming scroll draws them where its countryside begins: the frame
 * the street's buildings share ({@link SongFrame}), walled all round in clay plaster but for a doorway in the middle of
 * its front, usually thatched; a table and a bench inside; and the house and its yard before it closed round by a
 * wattle fence, a gateway in its front before the door.
 *
 * <p>Estimates: its fence 1.2 m high, 60 cm out from the house's sides and back, its gateway 2 m wide.
 */
public final class SongFarmhouse
        implements NorthernSongImpl<Buildings.Farmhouse, FarmhouseSpec<Chinese, ChineseEras.NorthernSong>> {

    private static final SongFrame FRAME = SongFrame.INSTANCE;

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    /** Its fence so high, its wattle so thick, a post so far from the next, so far out from the house. */
    static final double FENCE = 120;
    static final double WATTLE = 8;
    static final double POSTS = 150;
    static final double OUT = 60;
    /** Its fence's gateway so wide. */
    static final double GATE = 200;

    @Override
    public Buildings.Farmhouse thing() {
        return new Buildings.Farmhouse();
    }

    /** How far {@code s}'s fence reaches across from its middle; how far back, and how far forward. */
    double[] fence(final FarmhouseSpec<?, ?> s) {
        final double back = s.depth() / 2 + SongFrame.APRON + OUT;
        return new double[] {s.width() / 2 + SongFrame.APRON + OUT, -back, s.depth() / 2 + s.yard()};
    }

    @Override
    public Structure build(final FarmhouseSpec<Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        final double floor = SongFrame.PLATFORM;
        FRAME.platform(parts, s);
        FRAME.storey(parts, s, floor, null);
        FRAME.back(parts, s, floor);
        FRAME.gables(parts, s, floor);
        FRAME.front(parts, s, floor);
        FRAME.pediments(parts, s, floor);
        FRAME.roofing(parts, s, floor);
        // a table in the back of the end bay, a bench before it
        final double x = -s.width() / 2 + s.bay() / 2;
        final double z = -s.depth() / 4;
        parts.add(new Part(new Elements.Counter(), TIMBERS.block("table", Role.DRESSING, Material.TIMBER,
                x - SongTeaHouse.TABLE_LONG / 2, x + SongTeaHouse.TABLE_LONG / 2, z - SongTeaHouse.TABLE_WIDE / 2,
                z + SongTeaHouse.TABLE_WIDE / 2, floor, floor + SongTeaHouse.TABLE_HIGH)));
        final double bench = z + SongTeaHouse.TABLE_WIDE / 2 + SongTeaHouse.BENCH_OFF + SongTeaHouse.BENCH_WIDE / 2;
        parts.add(new Part(new Elements.Bench(), TIMBERS.block("bench", Role.DRESSING, Material.TIMBER,
                x - SongTeaHouse.TABLE_LONG / 2, x + SongTeaHouse.TABLE_LONG / 2, bench - SongTeaHouse.BENCH_WIDE / 2,
                bench + SongTeaHouse.BENCH_WIDE / 2, floor, floor + SongTeaHouse.BENCH_HIGH)));
        // the fence round the house and its yard, its gateway before the door
        final double[] f = fence(s);
        final double gate = FRAME.door(s);
        fence(parts, -f[0], f[1], f[0], f[1]);
        fence(parts, -f[0], f[1], -f[0], f[2]);
        fence(parts, f[0], f[1], f[0], f[2]);
        fence(parts, -f[0], f[2], gate - GATE / 2, f[2]);
        fence(parts, gate + GATE / 2, f[2], f[0], f[2]);
        final double d = s.depth() / 2;
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.Room(), new Uses.Homes(), FRAME.floor(s), floor),
                        new Structure.Room(new Spaces.Courtyard(), new Uses.Homes(), List.of(new Vec2(-f[0], d),
                                new Vec2(f[0], d), new Vec2(f[0], f[2]), new Vec2(-f[0], f[2])), 0)),
                List.of(new Structure.WayIn(gate, f[2], 0), FRAME.wayIn(s)));
    }

    /** A stretch of wattle fence, straight across or along, from one place to another: its posts, its wattle. */
    private void fence(final List<Part> parts, final double x0, final double z0, final double x1,
                              final double z1) {
        final Leaf<?> screen = new Elements.Screen();
        final int posts = Math.max(1, (int) Math.ceil(Math.hypot(x1 - x0, z1 - z0) / POSTS));
        for (int k = 0; k <= posts; k++) {
            final double x = x0 + (x1 - x0) * k / posts;
            final double z = z0 + (z1 - z0) * k / posts;
            parts.add(new Part(screen, TIMBERS.block("fence post", Role.FRAME, Material.TIMBER, x - 6, x + 6, z - 6,
                    z + 6, -20, FENCE + 10)));
        }
        parts.add(new Part(screen, TIMBERS.block("wattle", Role.WALL, Material.REED,
                Math.min(x0, x1) - WATTLE / 2, Math.max(x0, x1) + WATTLE / 2, Math.min(z0, z1) - WATTLE / 2,
                Math.max(z0, z1) + WATTLE / 2, 10, FENCE)));
    }
}
