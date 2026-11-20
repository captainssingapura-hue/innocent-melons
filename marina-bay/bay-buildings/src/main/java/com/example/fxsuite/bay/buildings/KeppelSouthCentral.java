package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Keppel South Central, on Hoe Chiang Road: NBBJ's, finished in 2025, its owner (Keppel) publishing a factsheet of its
 * floors but no plans, so on the map's outline. Published: 33 storeys (its owner); 200 m and its top floor 170.8 m up
 * (the Skyscraper Center); 23 lifts; its offices in a low zone, floors 7 to 17, and a high, 20 to 31; its event space,
 * workspaces, gym and pool on 2 to 6, its sky terrace on 18, its restaurant and bar over 32. Its floors 5.25 m apart,
 * floor 19 plant: estimates.
 */
public final class KeppelSouthCentral {

    private KeppelSouthCentral() {
    }

    /**
     * Keppel South Central as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Hoe Chiang Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1731.75, -1595.72}, {-1695.20, -1584.96}, {-1689.93, -1545.14}, {-1695.44, -1535.08},
            {-1719.38, -1534.58}, {-1719.69, -1536.56}, {-1737.15, -1534.24}, {-1745.07, -1592.83}});

    /**
     * Keppel South Central, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 33, 2, 525);
        return Offices.tower("Keppel South Central", MAPPED, levels, 20_000, Offices.uses(2, 2, "event space", 3, 4,
                "flexible workspaces", 5, 6, "gym and pool", 18, 18, "sky terrace", 19, 19, OfficeTower.PLANT, 32, 33,
                "restaurant and bar"), List.of(new Offices.Zone(7, 17, 12, 500), new Offices.Zone(20, 31, 11, 700)), 32,
                -178, List.of());
    }
}
