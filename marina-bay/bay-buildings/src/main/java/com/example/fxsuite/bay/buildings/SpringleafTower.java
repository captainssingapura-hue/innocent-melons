package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Springleaf Tower, on Anson Road: AC Martin's, strata-titled, publishing no plans, so on the map's outlines, its round
 * drum over 130 m. Published: 37 storeys, 165 m, 13 lifts (Wikipedia); its tower starting 15 m up over a tall lobby,
 * its car park on floors 3 to 5; two floors of penthouses at its top (AC Martin). Its floors 4.16 m apart, its lifts
 * two zones: estimates.
 */
public final class SpringleafTower {

    private SpringleafTower() {
    }

    /**
     * Springleaf Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Anson Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1273.55, -1375.94}, {-1254.47, -1336.81}, {-1253.55, -1331.50}, {-1255.03, -1326.59},
            {-1257.92, -1322.31}, {-1262.43, -1319.75}, {-1267.64, -1318.84}, {-1272.57, -1320.35},
            {-1277.01, -1323.22}, {-1279.64, -1327.37}, {-1280.54, -1332.78}, {-1294.59, -1364.36},
            {-1292.06, -1365.52}, {-1287.70, -1369.97}, {-1284.12, -1371.98}, {-1283.20, -1369.61},
            {-1281.36, -1370.45}, {-1282.31, -1372.18}});

    /** Where the map's parts of it step in, over 130 m. */
    private static final double[][] MAPPED_OVER_130 = {
            {-1262.43, -1319.75}, {-1267.64, -1318.84}, {-1272.57, -1320.35}, {-1277.01, -1323.22},
            {-1279.64, -1327.37}, {-1280.54, -1332.78}, {-1278.99, -1337.62}, {-1276.17, -1342.08},
            {-1271.61, -1344.40}, {-1266.45, -1345.56}, {-1261.39, -1344.40}, {-1257.08, -1341.29},
            {-1254.47, -1336.81}, {-1253.55, -1331.50}, {-1255.03, -1326.59}, {-1257.92, -1322.31}};

    /**
     * Springleaf Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(1500, 37, 2, 416);
        return Offices.slim("Springleaf Tower", MAPPED, levels, 16_500, Offices.uses(2, 2, "podium", 3, 5,
                OfficeTower.PARKING, 36, 37, Homes.HOMES), List.of(new Offices.Zone(6, 20, 7, 400), new Offices.Zone(21,
                37, 6, 600)), 931, 70, List.of(new OfficeTower.Setback(Offices.over(levels, 13_000),
                MAPPED.own(MAPPED_OVER_130))));
    }
}
