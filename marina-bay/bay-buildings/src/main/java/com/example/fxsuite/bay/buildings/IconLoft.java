package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * Icon, its two homes towers on Gopeng Street: Ong & Ong's, their developer (Far East Organization) publishing no
 * lengths, so each on the map's outline. Published: their pool and facilities on floor 7. Their car parks on floors 2
 * to 6, their floors some 3.5 m apart, their lifts two zones of two: estimates.
 */
public final class IconLoft {

    private IconLoft() {
    }

    /**
     * Icon Loft Tower 1 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Gopeng Street.
     */
    static final Mapped TOWER1 = Mapped.of(new double[][] {
            {-1462.52, -1380.25}, {-1519.05, -1388.34}, {-1516.10, -1409.18}, {-1459.58, -1401.09}});

    /**
     * Icon Loft Tower 1: 41 storeys, 147.2 m (the Skyscraper Center).
     */
    public static OfficeTower tower1() {
        return Homes.tower("Icon Loft Tower 1", TOWER1, 41, 600, 353, 14_720, Offices.uses(2, 6, OfficeTower.PARKING, 7,
                7, "pool and gym"), 21, 2, 400, 500, -1632, -2);
    }

    /**
     * Icon Loft Tower 2 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Gopeng Street.
     */
    static final Mapped TOWER2 = Mapped.of(new double[][] {
            {-1458.84, -1400.19}, {-1416.54, -1395.09}, {-1418.76, -1376.36}, {-1461.07, -1381.46}});

    /**
     * Icon Loft Tower 2: 46 storeys, 163 m (the Skyscraper Center).
     */
    public static OfficeTower tower2() {
        return Homes.tower("Icon Loft Tower 2", TOWER2, 46, 600, 348, 16_300, Offices.uses(2, 6, OfficeTower.PARKING, 7,
                7, "pool and gym"), 24, 2, 400, 500, 1018, 7);
    }
}
