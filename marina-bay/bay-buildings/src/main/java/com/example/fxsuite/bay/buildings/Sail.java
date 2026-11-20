package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.Map;

/**
 * The Sail @ Marina Bay, its two homes towers on Marina Boulevard: the Marina Bay Tower, curved as a sail, and the
 * Central Park Tower; each a homes tower ({@link Homes}) on the map's outline, its curves as the map draws them. Their
 * owner publishes no plans with their lengths written on them: their storeys and heights are as published, the rest
 * estimates, said so where they are made. NBBJ's, finished in 2008 and 2009; 1,111 homes between them, five of them
 * penthouses. Clad in glass, as the model draws them. How their tops slope is not drawn: each stands to its roof whole.
 */
public final class Sail {

    private Sail() {
    }

    /** The Marina Bay Tower as the map draws it, anticlockwise from the edge its way in is in, facing Marina Boulevard. */
    static final Mapped MARINA_BAY_TOWER = Mapped.of(new double[][] {
            {-748.68, -490.98}, {-761.30, -486.78}, {-771.98, -484.44}, {-782.71, -483.76}, {-797.37, -485.68},
            {-793.86, -494.17}, {-792.41, -502.14}, {-786.54, -503.79}, {-786.82, -504.92}, {-784.31, -505.84},
            {-775.80, -508.18}, {-772.56, -509.15}, {-757.21, -513.71}, {-754.56, -514.46}, {-748.65, -516.11},
            {-743.33, -516.90}, {-743.15, -515.98}, {-737.56, -517.29}, {-733.28, -511.65}, {-731.25, -512.96},
            {-724.59, -506.19}, {-738.31, -495.83}});

    /** The Central Park Tower as the map draws it, anticlockwise from the edge its way in is in, facing Marina View. */
    static final Mapped CENTRAL_PARK_TOWER = Mapped.of(new double[][] {
            {-737.09, -573.72}, {-731.35, -561.77}, {-729.52, -555.79}, {-727.74, -548.48}, {-725.90, -540.39},
            {-725.32, -535.21}, {-743.07, -530.21}, {-746.76, -535.32}, {-750.51, -543.13}, {-752.54, -549.70},
            {-754.18, -555.70}, {-754.89, -560.92}, {-751.75, -562.12}, {-753.04, -567.04}, {-744.58, -575.60},
            {-741.60, -570.84}});

    /**
     * The Marina Bay Tower: 70 storeys, 245 m to its roof, as published and as the map has it. Its lobby 6 m high and
     * its floors 3.45 m apart, estimates. Its club on its 44th storey, as a listing of its homes for sale has it; its
     * penthouses on its two top floors, which that listing gives a plan of their own, an estimate. Its core an estimate:
     * eight lifts in two zones (floors 2 to 36, 37 to 70), four cars each.
     */
    public static OfficeTower marinaBayTower() {
        return Homes.tower("The Sail, Marina Bay Tower", MARINA_BAY_TOWER, 70, 600, 345, 24_500,
                Map.of(44, "club", 69, Homes.PENTHOUSES, 70, Homes.PENTHOUSES), 36, 4, 400, 700, 140, -10);
    }

    /**
     * The Central Park Tower: 63 storeys, 215 m to its roof, as published and as the map has it. Its lobby 6 m high and
     * its floors 3.35 m apart, estimates. Its recreation room on its 34th storey, as a listing of its homes for sale has
     * it. Its core an estimate: six lifts in two zones (floors 2 to 32, 33 to 63), three cars each.
     */
    public static OfficeTower centralParkTower() {
        return Homes.tower("The Sail, Central Park Tower", CENTRAL_PARK_TOWER, 63, 600, 335, 21_500,
                Map.of(34, "recreation"), 32, 3, 400, 600, 20, 50);
    }
}
