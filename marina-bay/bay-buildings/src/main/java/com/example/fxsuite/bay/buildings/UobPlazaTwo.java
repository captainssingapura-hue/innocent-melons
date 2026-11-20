package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * UOB Plaza Two, at Raffles Place: Architects Team 3's and Tange Associates' octagonal tower, its owner (United
 * Overseas Bank) publishing no plans of it, so on the map's outline. Published: 38 storeys, 162 m to the top of its
 * crown, over the podium it shares with UOB Plaza One. Its podium of six storeys (as a search had it), its floors 3.3 m
 * apart so its roof is the map's 128 m, its lifts two zones of six: estimates.
 */
public final class UobPlazaTwo {

    private UobPlazaTwo() {
    }

    /**
     * UOB Plaza Two as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Bonham Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1068.04, -92.57}, {-1082.37, -95.19}, {-1090.90, -106.79}, {-1088.47, -121.48}, {-1076.17, -130.07},
            {-1061.74, -127.46}, {-1053.11, -114.85}, {-1055.99, -100.93}});

    /**
     * UOB Plaza Two, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 38, 2, 330);
        return Offices.tower("UOB Plaza Two", MAPPED, levels, 16_200, Offices.uses(2, 6, "podium"),
                List.of(new Offices.Zone(7, 22, 6, 400), new Offices.Zone(23, 38, 6, 600)), -5, 5, List.of());
    }
}
