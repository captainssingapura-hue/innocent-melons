package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * SBF Center, on Robinson Road: DP Architects', its developer (Far East Organization) publishing its floors' uses: its
 * lobby on floor 1, medical suites on 3 to 5, offices on 6 to 8 and 10 to 31, its sky terrace on 9. Published: 31
 * storeys, 187 m (Wikipedia). On the map's outlines: its podium's to floor 9, 60 m up, its tower's from 10. Its
 * podium's floors 6.75 m, its tower's 5 m (as listings say 4.5 to 5), its lifts two zones of six: estimates.
 */
public final class SbfCenter {

    private SbfCenter() {
    }

    /**
     * SBF Center as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Cecil Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1219.60, -948.22}, {-1234.11, -1000.04}, {-1173.10, -1016.62}, {-1168.08, -998.53},
            {-1207.42, -985.20}, {-1212.68, -986.42}, {-1203.24, -952.70}});

    /** Where the map's parts of it step in, from floor 10. */
    private static final double[][] MAPPED_FROM_10 = {
            {-1219.60, -948.22}, {-1234.11, -1000.04}, {-1217.74, -1004.52}, {-1203.24, -952.70}};

    /**
     * SBF Center, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 31, 2, 675, 10, 500);
        return Offices.slim("SBF Center", MAPPED, levels, 18_700, Offices.uses(2, 2, "podium", 3, 5, "medical suites",
                9, 9, "sky terrace"), List.of(new Offices.Zone(3, 19, 6, 400), new Offices.Zone(20, 31, 6, 600)), -1883,
                -1571, List.of(new OfficeTower.Setback(Homes.storey(10), MAPPED.own(MAPPED_FROM_10))));
    }
}
