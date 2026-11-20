package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * Altez, on Enggor Street: RSP's homes tower, its developer (Far East Organization) publishing no lengths, so on the
 * map's outline. Published: 62 storeys, 250 m, its top floor 240 m up, five lifts (the Skyscraper Center); 280 homes;
 * five sky gardens, on floors 18, 28, 40, 52 and 60 (as listings have them). Its floors 3.9 m apart, four of its cars
 * in two zones: estimates.
 */
public final class Altez {

    private Altez() {
    }

    /**
     * Altez as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Enggor Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1475.40, -1498.95}, {-1446.05, -1494.07}, {-1450.34, -1468.08}, {-1479.69, -1472.96}});

    /**
     * Altez, as above.
     */
    public static OfficeTower tower() {
        return Homes.tower("Altez", MAPPED, 62, 600, 390, 25_000, Offices.uses(18, 18, "sky garden", 28, 28,
                "sky garden", 40, 40, "sky garden", 52, 52, "sky garden", 60, 60, "sky garden"), 31, 2, 400, 600, -15,
                8);
    }
}
