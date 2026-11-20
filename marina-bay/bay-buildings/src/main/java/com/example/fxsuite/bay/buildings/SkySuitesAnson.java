package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * Sky Suites@Anson: P & T Group's slender homes tower, its developer (Allgreen) publishing its units' plans but no
 * lengths, so on the map's outline. Published: 72 storeys, 250 m, its top floor 246 m up, four lifts (the Skyscraper
 * Center); 360 homes; its club on floor 7, its gallery on 31, its sky garden on 60, its viewing deck on 72 (as listings
 * have them). Its floors 3.43 m apart, its lifts two zones of two: estimates.
 */
public final class SkySuitesAnson {

    private SkySuitesAnson() {
    }

    /**
     * Sky Suites@Anson as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Enggor Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1426.39, -1444.93}, {-1447.88, -1465.01}, {-1430.71, -1483.19}, {-1409.21, -1463.11}});

    /**
     * Sky Suites@Anson, as above.
     */
    public static OfficeTower tower() {
        return Homes.tower("Sky Suites@Anson", MAPPED, 72, 600, 343, 25_000, Offices.uses(7, 7, "club", 31, 31,
                "gallery", 60, 60, "sky garden", 72, 72, "viewing deck"), 36, 2, 400, 600, -52, -1);
    }
}
