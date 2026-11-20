package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Robinson 77, once the SIA Building: SAA Partnership's, its owners publishing no plans of it, so on the map's outline.
 * Published: 40 storeys, 175.6 m (the Skyscraper Center; Wikipedia says 35 and 176 m). Its floors 4.09 m apart, its
 * tail fin the rest; its lifts three zones of six: estimates.
 */
public final class RobinsonSeventySeven {

    private RobinsonSeventySeven() {
    }

    /**
     * Robinson 77 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Maxwell Link.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1121.51, -1050.32}, {-1113.54, -1019.37}, {-1141.46, -1012.35}, {-1149.43, -1043.31}});

    /**
     * Robinson 77, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 40, 2, 409);
        return Offices.tower("Robinson 77", MAPPED, levels, 17_560, Offices.uses(), List.of(new Offices.Zone(2, 14, 6,
                400), new Offices.Zone(15, 27, 6, 600), new Offices.Zone(28, 40, 6, 700)), -7, 311, List.of());
    }
}
