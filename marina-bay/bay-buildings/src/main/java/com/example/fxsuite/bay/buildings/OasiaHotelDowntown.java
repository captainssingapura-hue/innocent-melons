package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Oasia Hotel Downtown, at Tanjong Pagar: WOHA's, its owner (Far East Organization) publishing no plans, so on the
 * map's outline. Published: 27 storeys, 190.9 m, its top floor 155.8 m up, 13 lifts (the Skyscraper Center); its
 * offices on floors 7 to 11, its hotel from 12, its rooms on 13 to 20 and 22 to 26; its sky terraces on 6, 12 and 21,
 * its roof terrace on 27; its crown 42.9 m high; four cores at its corners (here one). Its floors 5.91 m apart, its
 * lifts four to its offices and nine to its hotel: estimates.
 */
public final class OasiaHotelDowntown {

    private OasiaHotelDowntown() {
    }

    /**
     * Oasia Hotel Downtown as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Tras Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1533.35, -1327.42}, {-1531.04, -1349.93}, {-1529.41, -1354.75}, {-1526.39, -1358.57},
            {-1522.09, -1360.34}, {-1517.00, -1361.16}, {-1495.55, -1358.60}, {-1485.92, -1354.12},
            {-1483.83, -1345.16}, {-1485.78, -1324.37}, {-1487.55, -1319.62}, {-1491.63, -1315.04},
            {-1495.54, -1313.01}, {-1500.41, -1312.31}, {-1521.36, -1315.01}, {-1525.74, -1316.11},
            {-1529.52, -1318.70}, {-1531.78, -1322.45}});

    /**
     * Oasia Hotel Downtown, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 27, 2, 591);
        return Offices.tower("Oasia Hotel Downtown", MAPPED, levels, 19_090, Offices.uses(2, 5, "podium", 6, 6,
                "sky terrace", 12, 12, "sky terrace and hotel lobby", 13, 20, Offices.HOTEL, 21, 21, "sky terrace", 22,
                26, Offices.HOTEL, 27, 27, "roof terrace"), List.of(new Offices.Zone(6, 11, 4, 400),
                new Offices.Zone(12, 27, 9, 500)), -27, 5, List.of());
    }
}
