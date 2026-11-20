package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * One George Street: SOM's, its owners publishing no plans of it, so on the map's outline. Published: 25 storeys (SOM;
 * the Skyscraper Center says 23), 153 m; its sky gardens on floors 5, 12, 15 and 22 (as listings have them). Its floors
 * 4.67 m apart, so its roof is the map's 121 m, the rest its crown; its lifts two zones of six: estimates.
 */
public final class OneGeorgeStreet {

    private OneGeorgeStreet() {
    }

    /**
     * One George Street as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward South Bridge Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1462.42, -137.35}, {-1482.24, -214.39}, {-1451.28, -222.17}, {-1431.45, -145.12}, {-1449.64, -140.56}});

    /**
     * One George Street, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(900, 25, 2, 467);
        return Offices.slim("One George Street", MAPPED, levels, 15_300, Offices.uses(5, 5, "sky garden", 12, 12,
                "sky garden", 15, 15, "sky garden", 22, 22, "sky garden"), List.of(new Offices.Zone(2, 13, 6, 400),
                new Offices.Zone(14, 25, 6, 600)), 1588, 4, List.of());
    }
}
