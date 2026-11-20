package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * The Bank of China Building's tower of 1999, on Battery Road, its owner publishing no plans of it, so on the map's
 * outline. Published: 37 storeys (its owner and the Skyscraper Center; Wikipedia says 36), 168 m. Its floors 4.47 m
 * apart, its lifts two zones of four in a slim core: estimates.
 */
public final class BankOfChinaBuilding {

    private BankOfChinaBuilding() {
    }

    /**
     * Bank of China Building as the map draws it, simplified to within half a metre, anticlockwise from the edge its
     * way in is in, toward Flint Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-967.24, -54.51}, {-957.63, -46.08}, {-953.66, -48.67}, {-950.81, -46.17}, {-950.89, -40.15},
            {-954.70, -36.01}, {-953.66, -35.07}, {-957.64, -30.49}, {-958.83, -31.56}, {-960.63, -29.51},
            {-964.08, -32.50}, {-976.42, -33.19}, {-978.86, -30.57}, {-983.25, -34.51}, {-980.66, -37.27},
            {-981.61, -47.72}, {-983.89, -49.69}, {-981.78, -52.10}, {-982.83, -53.04}, {-978.76, -57.53},
            {-977.82, -56.69}, {-974.27, -60.69}, {-968.42, -61.43}, {-965.57, -58.94}});

    /**
     * Bank of China Building, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(700, 37, 2, 447);
        return Offices.slim("Bank of China Building", MAPPED, levels, 16_800, Offices.uses(),
                List.of(new Offices.Zone(2, 19, 4, 400), new Offices.Zone(20, 37, 4, 600)), -86, -103, List.of());
    }
}
