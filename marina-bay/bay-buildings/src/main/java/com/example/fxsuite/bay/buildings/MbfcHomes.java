package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.Map;

/**
 * The financial centre's two homes towers, beside its three office towers ({@link Mbfc}): Marina Bay Residences and
 * Marina Bay Suites, each a homes tower ({@link Homes}) on the map's outline. Their owners publish no plans with their
 * lengths written on them, nor sheets of their floors or lifts: their storeys and heights are as published, the rest
 * estimates, said so where they are made.
 */
public final class MbfcHomes {

    private MbfcHomes() {
    }

    /** Marina Bay Residences as the map draws it, anticlockwise from the edge its way in is in, facing Marina Way. */
    static final Mapped RESIDENCES = Mapped.of(new double[][] {
            {-452.35, -603.81}, {-442.49, -579.95}, {-464.17, -573.29}, {-474.77, -591.34}, {-493.07, -608.09},
            {-474.56, -625.84}, {-466.53, -617.89}});

    /** Marina Bay Suites as the map draws it, anticlockwise from the edge its way in is in, its long side to the west. */
    static final Mapped SUITES = Mapped.of(new double[][] {
            {-776.74, -634.05}, {-797.66, -601.18}, {-803.01, -599.49}, {-818.07, -607.36}, {-797.18, -647.23},
            {-782.08, -639.25}});

    /**
     * Marina Bay Residences: 55 floors, 221.9 m to its top, 428 homes, as the Skyscraper Center has them (the map says
     * 54 floors and 245 m, the financial centre's Tower 1's height: the published taken). Kohn Pedersen Fox's, finished
     * in 2010. Its lobby 7.5 m high and its floors 3.75 m apart, its top floor its penthouses, estimates; so is its
     * core: six lifts in two zones (floors 2 to 28, 29 to 55), three cars each. Its skin, flat panels turning to
     * louvres and to windows set in, balconies standing out, is drawn as glass: the model has no other yet.
     */
    public static OfficeTower residences() {
        return Homes.tower("Marina Bay Residences", RESIDENCES, 55, 750, 375, 22_190, Map.of(55, Homes.PENTHOUSES),
                28, 3, 400, 600, 840, -310);
    }

    /**
     * Marina Bay Suites: 66 floors, 227 m to its top, 221 homes (108 of three bedrooms, 110 of four, three
     * penthouses), as published; the map's floors and height the same. Kohn Pedersen Fox's. Its lobby 7.5 m high and
     * its floors 3.3 m apart, its penthouses on its top floor, estimates; so is its core: six lifts in two zones (floors
     * 2 to 34, 35 to 66), three cars each.
     */
    public static OfficeTower suites() {
        return Homes.tower("Marina Bay Suites", SUITES, 66, 750, 330, 22_690, Map.of(66, Homes.PENTHOUSES),
                34, 3, 400, 600, -730, 10);
    }
}
