package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Pickering Operations Complex: Tange Associates' telecom tower, its owner (Singtel) publishing no plans of it, so on
 * the map's outline. Published: 43 storeys, 177 m (the Skyscraper Center); technical floors on 5 and 29 (Wikipedia).
 * Its floors 3.98 m apart, its lifts two zones of six: estimates.
 */
public final class PickeringOperationsComplex {

    private PickeringOperationsComplex() {
    }

    /**
     * Pickering Operations Complex as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Pickering Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1402.93, -239.89}, {-1372.44, -245.85}, {-1371.21, -245.47}, {-1368.46, -246.71}, {-1366.16, -246.04},
            {-1365.36, -245.04}, {-1364.94, -243.83}, {-1365.96, -239.80}, {-1363.29, -240.08}, {-1360.75, -227.79},
            {-1364.16, -227.24}, {-1364.01, -225.64}, {-1362.60, -225.37}, {-1361.92, -224.26}, {-1362.40, -221.01},
            {-1364.25, -220.00}, {-1401.70, -213.60}, {-1404.67, -215.70}, {-1404.61, -217.53}, {-1402.68, -219.58},
            {-1402.95, -221.06}, {-1406.27, -220.33}, {-1408.66, -232.45}, {-1405.44, -233.04}, {-1406.24, -234.37},
            {-1408.46, -236.87}, {-1408.31, -238.99}, {-1405.81, -240.87}});

    /**
     * Pickering Operations Complex, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 43, 2, 398);
        return Offices.slim("Pickering Operations Complex", MAPPED, levels, 17_700, Offices.uses(5, 5,
                "technical floor", 29, 29, "technical floor"), List.of(new Offices.Zone(2, 22, 6, 400),
                new Offices.Zone(23, 43, 6, 600)), 22, -65, List.of());
    }
}
