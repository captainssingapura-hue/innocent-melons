package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Marina One, its two office towers on Straits View either side of its garden, the Green Heart: ingenhoven
 * architects', its owner (M+S) publishing no plans of their floors, so each on the map's outline ({@link Offices}).
 * Published, for both, the sources treating them as a pair: 34 storeys, 225.5 m, the top floor occupied 189.3 m up (the
 * Skyscraper Center; others say 30 storeys and 200 m); floors of 34,000 to 40,000 sq ft, their ceilings 3 m clear;
 * their office lobbies on floor 3, reached by escalators from the garden on floor 1; their lifts, as the owner's plan
 * of floor 3 draws them, eight cars to each of a low, a mid and a high zone. How their garden sides curve and lean out
 * as they rise is not drawn.
 */
public final class MarinaOne {

    private MarinaOne() {
    }

    /** The East Tower as the map draws it, anticlockwise from the edge its way in is in, on Straits View. */
    static final Mapped EAST_TOWER = Mapped.of(new double[][] {
            {-690.77, -835.83}, {-703.33, -880.47}, {-699.71, -881.55}, {-661.93, -891.76}, {-656.52, -893.22},
            {-656.01, -891.27}, {-652.83, -886.85}, {-646.61, -883.47}, {-638.21, -880.48}, {-634.02, -876.99},
            {-628.13, -872.41}, {-617.04, -866.51}, {-615.29, -865.57}, {-610.97, -849.78}, {-610.56, -848.35},
            {-606.82, -835.89}, {-662.15, -820.65}, {-664.25, -829.13}, {-667.30, -840.01}, {-674.39, -838.07},
            {-682.37, -835.88}, {-682.97, -838.14}});

    /** The West Tower as the map draws it, anticlockwise from the edge its way in is in, by Park Street. */
    static final Mapped WEST_TOWER = Mapped.of(new double[][] {
            {-708.89, -986.34}, {-654.06, -1001.22}, {-650.48, -989.46}, {-650.12, -987.81}, {-645.17, -969.76},
            {-647.01, -964.98}, {-647.34, -964.55}, {-650.45, -960.64}, {-651.15, -959.90}, {-659.18, -951.42},
            {-663.55, -942.24}, {-666.33, -932.18}, {-665.55, -927.68}, {-676.55, -924.63}, {-709.13, -915.62},
            {-711.43, -914.86}, {-712.97, -914.34}, {-725.86, -958.59}, {-717.54, -960.89}, {-718.12, -962.92},
            {-710.79, -964.93}, {-703.25, -966.98}, {-706.63, -978.37}});

    /** The East Tower, at 7 Straits View. */
    public static OfficeTower eastTower() {
        return tower("Marina One East Tower", EAST_TOWER, -789, 710);
    }

    /** The West Tower, at 9 Straits View. */
    public static OfficeTower westTower() {
        return tower("Marina One West Tower", WEST_TOWER, -1_225, 1_115);
    }

    /**
     * Either: its floor 1 10 m high and its floors 5.6 m apart, so its top floor is where it is published to be;
     * shops on floor 2; its zones floors 4 to 14, 15 to 24 and 25 to 34: estimates.
     */
    private static OfficeTower tower(final String name, final Mapped mapped, final double across, final double along) {
        final Map<Integer, String> said = new HashMap<>();
        said.put(2, "shops");
        said.put(3, "office lobby");
        return Offices.tower(name, mapped, Offices.levels(1_000, 34, 2, 560), 22_550, said,
                List.of(new Offices.Zone(4, 14, 8, 400), new Offices.Zone(15, 24, 8, 600),
                        new Offices.Zone(25, 34, 8, 800)), across, along, List.of());
    }
}
