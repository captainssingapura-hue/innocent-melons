package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Sites;
import com.example.fxsuite.avatar.build.Town;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The campus, the virtual headquarters, as it starts: laid on the meadow ({@link com.example.fxsuite.avatar.worlds.Meadow}),
 * its land, its river and bridge, its roads, its trees, its church on its hill all as they are; its village's huts its
 * offices ({@link Office}), each where the hut stands, numbered in the village's order, furnished; and east of the
 * green, its door to the well, its main office ({@link MainOffice}), a hut of the greatest luxury.
 */
public final class Campus {

    /** The keys its offices are drawn by, one each, in order: clear of every other world's. */
    public static final int KEYS = 2_000_000;

    /** The key its main office is drawn by: after room for a thousand offices. */
    public static final int MAIN = KEYS + 1_000;

    /** Its offices: every hut of the village, numbered from 1. */
    public static final List<Office> OFFICES = IntStream.range(0, Village.VILLAGE.size())
            .mapToObj(k -> new Office(k + 1, k)).toList();

    private Campus() {
    }

    /** Office {@code number}'s key. */
    static int key(final Office office) {
        return KEYS + office.number() - 1;
    }

    /** Its offices, told to the drawing: each key drawn as its office, the hut and its furniture; its main office. */
    public static void register() {
        Sites.register(KEYS, KEYS + OFFICES.size(), k -> OFFICES.get(k).solids());
        Sites.register(MAIN, MAIN + 1, k -> MainOffice.solids());
    }

    /**
     * What stands on it: the meadow's pieces but the village's huts, and in their places its offices, each one piece,
     * in the way as its hut and its furniture are.
     */
    public static List<Course.Piece> pieces() {
        final List<Course.Piece> pieces = new ArrayList<>();
        for (final Course.Piece p : com.example.fxsuite.avatar.worlds.Meadow.pieces()) {
            if (!hut(p)) {
                pieces.add(p);
            }
        }
        for (final Office o : OFFICES) {
            pieces.add(Town.building(o.solids(), o.place(), key(o), o.shape(), 1));
        }
        pieces.add(Town.building(MainOffice.solids(), MainOffice.place(), MAIN, MainOffice.shape(), 1));
        return pieces;
    }

    /** Whether {@code p} is one of the village's huts: a building drawn by one of their keys. */
    static boolean hut(final Course.Piece p) {
        return p.kind() == Course.Kind.BUILDING && p.tint() >= Village.KEYS
                && p.tint() < Village.KEYS + Village.VILLAGE.size();
    }
}
