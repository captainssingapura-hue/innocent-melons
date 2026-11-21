package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.QuaySpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * How the Northern Song built a quay along the Bian: its bank faced in stone, its face standing upright in the water,
 * a stone deck from it back to the towpath, timber posts along its edge to make boats fast to; flights of steps down
 * its face to landings a little over the water, each running along the face, for loading from boats moored there.
 * Its length across (x), its face toward +z, its top at its line's height.
 *
 * <p>Estimates: its face 60 cm thick, its deck reaching 70 cm back behind its line, its posts 60 cm high and 4 m apart,
 * its steps 1.5 m wide, each no higher than 18 cm and 30 cm deep, its landings 1.5 m long.
 */
public final class SongQuay implements NorthernSongImpl<Buildings.Quay, QuaySpec<Chinese, ChineseEras.NorthernSong>> {

    /** Its face so thick; its deck so far back behind its line, so thick. */
    static final double FACE = 60;
    static final double BACK = 70;
    static final double SLAB = 30;
    /** Its mooring posts so high, so thick, so far apart. */
    static final double POST = 60;
    static final double THICK = 24;
    static final double APART = 400;
    /** Its steps so wide, each no higher than so much and so deep; its landings so long. */
    static final double WIDE = 150;
    static final double RISER = 18;
    static final double TREAD = 30;
    static final double LANDING = 150;

    @Override
    public Buildings.Quay thing() {
        return new Buildings.Quay();
    }

    /** How many steps down to {@code s}'s landings. */
    static int steps(final QuaySpec<?, ?> s) {
        return (int) Math.ceil(s.landing() / RISER);
    }

    /** Where flight {@code i} of {@code s}'s starts, at its head, across: each in the middle of its share of it. */
    static double head(final QuaySpec<?, ?> s, final int i) {
        final double run = steps(s) * TREAD + LANDING;
        return -s.length() / 2 + (i + 0.5) * s.length() / s.flights() - run / 2;
    }

    @Override
    public Structure build(final QuaySpec<Chinese, ChineseEras.NorthernSong> s) {
        final double a = s.length() / 2;
        final int n = steps(s);
        final double run = n * TREAD + LANDING;
        if (s.flights() * (run + APART) > s.length()) {
            throw new IllegalArgumentException("a quay long enough for its flights of steps");
        }
        final List<Part> parts = new ArrayList<>();
        final double foot = -s.bed() - 40;
        parts.add(new Part(new Elements.Wall(), Timbers.block("quay face", Role.WALL, Material.STONE, -a, a,
                s.out() - FACE, s.out(), foot, 0)));
        parts.add(new Part(new Elements.Decking(), Timbers.block("quay deck", Role.BASE, Material.STONE, -a, a, -BACK,
                s.out() - FACE, -SLAB, 0)));
        final List<Structure.Room> rooms = new ArrayList<>(List.of(new Structure.Room(new Spaces.Deck(),
                new Uses.Storage(), List.of(new Vec2(-a, -BACK), new Vec2(a, -BACK), new Vec2(a, s.out()),
                        new Vec2(-a, s.out())), 0)));
        final List<Structure.Stair> stairs = new ArrayList<>();
        final double z0 = s.out();
        final double z1 = s.out() + WIDE;
        for (int i = 0; i < s.flights(); i++) {
            final double x = head(s, i);
            for (int j = 1; j <= n; j++) {
                parts.add(new Part(new Elements.Steps(), Timbers.block("step", Role.BASE, Material.STONE,
                        x + (j - 1) * TREAD, x + j * TREAD, z0, z1, foot, -j * s.landing() / n)));
            }
            parts.add(new Part(new Elements.Steps(), Timbers.block("landing", Role.BASE, Material.STONE, x + n * TREAD,
                    x + run, z0, z1, foot, -s.landing())));
            rooms.add(new Structure.Room(new Spaces.Stair(), new Uses.Storage(), List.of(new Vec2(x + n * TREAD, z0),
                    new Vec2(x + run, z0), new Vec2(x + run, z1), new Vec2(x + n * TREAD, z1)), -s.landing()));
            stairs.add(new Structure.Stair(new Structure.WayIn(x + n * TREAD + LANDING / 2, (z0 + z1) / 2,
                    -s.landing()), new Structure.WayIn(x + TREAD / 2, s.out(), 0)));
        }
        // mooring posts along its edge, none before a flight's head
        for (double x = -a + APART / 2; x < a; x += APART) {
            final double at = x;
            final boolean clear = IntStream.range(0, s.flights())
                    .allMatch(i -> at < head(s, i) - THICK || at > head(s, i) + run + THICK);
            if (clear) {
                parts.add(new Part(new Elements.Pole(), Timbers.block("mooring post", Role.FRAME, Material.TIMBER,
                        x - THICK / 2, x + THICK / 2, s.out() - FACE / 2 - THICK / 2, s.out() - FACE / 2 + THICK / 2,
                        -SLAB, POST)));
            }
        }
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), rooms,
                List.of(new Structure.WayIn(0, -BACK, 0)), stairs);
    }
}
