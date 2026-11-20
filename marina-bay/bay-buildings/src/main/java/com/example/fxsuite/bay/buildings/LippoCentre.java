package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Lippo Centre (78 Shenton Way), its Tower 1: publishing no plans, so on the map's outline. Published: 34 storeys,
 * 150.1 m, 12 lifts (the Skyscraper Center). Its floors 4.36 m apart, eight of its cars in two zones of four, in a slim
 * core: estimates. Its glass Tower 2, eleven storeys, is not here.
 */
public final class LippoCentre {

    private LippoCentre() {
    }

    /**
     * Lippo Centre as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Keppel Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1359.35, -1619.99}, {-1386.88, -1605.37}, {-1388.58, -1608.53}, {-1395.24, -1604.98},
            {-1398.31, -1610.66}, {-1400.82, -1609.32}, {-1415.89, -1637.25}, {-1409.78, -1640.50},
            {-1406.56, -1634.52}, {-1400.14, -1637.92}, {-1396.84, -1631.82}, {-1390.53, -1635.17},
            {-1387.54, -1629.62}, {-1381.22, -1632.98}, {-1378.12, -1627.25}, {-1372.35, -1630.31},
            {-1369.52, -1625.05}, {-1363.74, -1628.11}});

    /**
     * Lippo Centre, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 34, 2, 436);
        return Offices.slim("Lippo Centre", MAPPED, levels, 15_010, Offices.uses(), List.of(new Offices.Zone(2, 17, 4,
                400), new Offices.Zone(18, 34, 4, 600)), 65, -202, List.of());
    }
}
