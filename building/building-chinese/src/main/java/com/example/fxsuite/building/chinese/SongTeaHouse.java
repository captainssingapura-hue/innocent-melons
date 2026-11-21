package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a tea house, as the Qingming scroll draws them by the bridge and the river: a shed of
 * one storey ({@link SongFrame}), walled along its back only, open along its front and at both ends under its gables,
 * for the street to see in and its guests to see out; a table and two benches in each bay.
 */
public final class SongTeaHouse implements NorthernSongImpl<Buildings.TeaHouse,
        HallSpec<Buildings.TeaHouse, Chinese, ChineseEras.NorthernSong>> {

    /** A table so long across and so wide, so high; a bench so wide, so high, so far from the table. */
    static final double TABLE_LONG = 160;
    static final double TABLE_WIDE = 70;
    static final double TABLE_HIGH = 75;
    static final double BENCH_WIDE = 28;
    static final double BENCH_HIGH = 45;
    static final double BENCH_OFF = 20;

    @Override
    public Buildings.TeaHouse thing() {
        return new Buildings.TeaHouse();
    }

    @Override
    public Structure build(final HallSpec<Buildings.TeaHouse, Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        final double floor = SongFrame.PLATFORM;
        SongFrame.platform(parts, s);
        SongFrame.storey(parts, s, floor, null);
        SongFrame.back(parts, s, floor);
        SongFrame.pediments(parts, s, floor);
        SongFrame.roofing(parts, s, floor);
        // a table in the back half of each bay, a bench before it and one behind
        final double z = -s.depth() / 4;
        for (int i = 0; i < s.bays(); i++) {
            final double x = -s.width() / 2 + (i + 0.5) * s.bay();
            parts.add(new Part(new Elements.Counter(), Timbers.block("table", Role.DRESSING, Material.TIMBER,
                    x - TABLE_LONG / 2, x + TABLE_LONG / 2, z - TABLE_WIDE / 2, z + TABLE_WIDE / 2, floor,
                    floor + TABLE_HIGH)));
            for (final int side : new int[] {-1, 1}) {
                final double at = z + side * (TABLE_WIDE / 2 + BENCH_OFF + BENCH_WIDE / 2);
                parts.add(new Part(new Elements.Bench(), Timbers.block("bench", Role.DRESSING, Material.TIMBER,
                        x - TABLE_LONG / 2, x + TABLE_LONG / 2, at - BENCH_WIDE / 2, at + BENCH_WIDE / 2, floor,
                        floor + BENCH_HIGH)));
            }
        }
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.Hall(), new Uses.Restaurant(), SongFrame.floor(s), floor)),
                List.of(SongFrame.wayIn(s), new Structure.WayIn(-s.width() / 2, s.depth() / 4, floor),
                        new Structure.WayIn(s.width() / 2, s.depth() / 4, floor)));
    }
}
