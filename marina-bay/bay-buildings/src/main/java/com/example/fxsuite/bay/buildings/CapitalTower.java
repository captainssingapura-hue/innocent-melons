package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Capital Tower, on Robinson Road: an office tower its owner publishes no lengths of, so on the map's outline,
 * stepping in where the map's parts of it do ({@link Offices}). From its owner (CapitaLand Integrated Commercial
 * Trust): 52 storeys, 254 m; its offices in three zones, the low from floor 10 to 20, the mid from 22 to 35, the high
 * from 38 to 50, the mid and the high reached through transfer floors 36 and 37; its auditorium and fitness club on
 * floor 9; floors 10 to 20 some 20,400 sq ft each, 22 to 35 some 19,900, 38 to 50 some 14,000; its core 8 to 15 m
 * from its walls. Its top floor occupied 231.7 m up, as the Skyscraper Center has it; its restaurant there.
 */
public final class CapitalTower {

    private CapitalTower() {
    }

    /** Capital Tower as the map draws it, anticlockwise from the edge its way in is in, facing Robinson Road. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1189.65, -1080.80}, {-1178.04, -1040.03}, {-1186.62, -1037.64}, {-1185.63, -1034.16},
            {-1226.59, -1022.80}, {-1227.57, -1026.27}, {-1235.92, -1023.95}, {-1247.52, -1064.72},
            {-1239.16, -1067.05}, {-1239.96, -1069.85}, {-1199.28, -1081.17}, {-1198.47, -1078.35}});

    /** Where the map's parts of it step in: over 190 m, its fins left out; over 229 m. */
    private static final double[][] OVER_190 = {
            {-1232.63, -1029.76}, {-1228.86, -1030.81}, {-1187.91, -1042.17}, {-1183.84, -1043.31},
            {-1192.86, -1075.03}, {-1197.18, -1073.82}, {-1237.87, -1062.53}, {-1241.64, -1061.48}};
    private static final double[][] OVER_229 = {
            {-1229.00, -1036.18}, {-1190.32, -1046.93}, {-1196.48, -1068.59}, {-1235.16, -1057.86}};

    /**
     * Its lobby 8 m high and its floors 4.47 m apart, so its top floor is where it is published to be: estimates. Its
     * floors 2 to 8 a podium, 21 and 51 plant, estimates. Stepping in over its transfer floors, from floor 38, where
     * its floors are published to grow smaller, and again from floor 51, on the map's outlines over 190 m and 229 m.
     * Thirty lifts, ten to each zone, all from the lobby (its transfer floors and their shuttles not modelled): an
     * estimate, the Skyscraper Center counting 35 lifts in all.
     */
    public static OfficeTower tower() {
        final Map<Integer, String> said = new HashMap<>();
        for (int n = 2; n <= 8; n++) {
            said.put(n, "podium");
        }
        said.put(9, "auditorium and fitness club");
        said.put(21, OfficeTower.PLANT);
        said.put(36, "transfer floor");
        said.put(37, "transfer floor");
        said.put(51, OfficeTower.PLANT);
        said.put(52, "restaurant");
        return Offices.tower("Capital Tower", MAPPED, Offices.levels(800, 52, 2, 447), 25_400, said,
                List.of(new Offices.Zone(10, 20, 10, 400), new Offices.Zone(22, 35, 10, 600),
                        new Offices.Zone(38, 50, 10, 800)), -676, 420,
                List.of(new OfficeTower.Setback(Homes.storey(38), MAPPED.own(OVER_190)),
                        new OfficeTower.Setback(Homes.storey(51), MAPPED.own(OVER_229))));
    }
}
