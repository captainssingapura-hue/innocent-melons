package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UOB Plaza One, at Raffles Place: Kenzo Tange's office tower, its owner (United Overseas Bank) publishing no plans of
 * it, so on the map's outline, stepping in where the map's parts of it do ({@link Offices}). Published: 66 storeys, 280
 * m (the Skyscraper Center), 29 lifts at up to 6 m/s; its sky lobby on floors 37 and 38, where its low zones' riders
 * change for its high zones'; its restaurant on floor 60; its banking hall in a podium 45 m high. A floor's plan, as a
 * leasing agent has it, draws its core as a band across the whole of it, its offices either side.
 */
public final class UobPlaza {

    private UobPlaza() {
    }

    /** UOB Plaza One as the map draws it, anticlockwise from the edge its way in is in, toward Chulia Street. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1196.78, -122.36}, {-1195.16, -130.66}, {-1194.79, -132.59}, {-1194.24, -135.39}, {-1193.04, -141.52},
            {-1192.35, -145.03}, {-1191.90, -147.41}, {-1190.94, -152.28}, {-1190.42, -155.00}, {-1182.61, -153.46},
            {-1180.51, -153.05}, {-1177.05, -152.37}, {-1172.14, -151.40}, {-1168.28, -150.65}, {-1165.59, -150.12},
            {-1158.12, -148.65}, {-1159.62, -141.02}, {-1160.09, -138.75}, {-1160.77, -135.11}, {-1161.76, -130.00},
            {-1162.56, -126.21}, {-1162.88, -124.17}, {-1164.49, -116.02}, {-1172.45, -117.58}, {-1174.71, -118.03},
            {-1178.30, -118.73}, {-1183.48, -119.75}, {-1187.47, -120.53}, {-1189.46, -120.93}});

    /** Where the map's parts of it step in, over 226 m. */
    private static final double[][] OVER_226 = {
            {-1166.48, -118.69}, {-1164.89, -126.82}, {-1164.67, -128.00}, {-1162.76, -137.78}, {-1162.40, -139.59},
            {-1161.08, -146.37}, {-1168.65, -147.86}, {-1169.86, -148.09}, {-1180.33, -150.15}, {-1181.50, -150.38},
            {-1188.44, -151.75}, {-1189.81, -144.74}, {-1190.03, -143.56}, {-1192.20, -132.43}, {-1192.28, -132.05},
            {-1193.84, -124.06}, {-1187.05, -122.73}, {-1185.30, -122.39}, {-1175.70, -120.50}, {-1173.78, -120.12}};

    /**
     * Its lobby 8 m high and its floors 3.9 m apart, estimates; its podium, floors 2 to 11, so 45 m high, as published.
     * Stepping in from the first floor over 226 m. Its lifts in three zones of six cars, all from its lobby (its sky
     * lobby's change not modelled): floors 12 to 24, 25 to 36, 39 to 66, estimates, none published.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 66, 2, 390);
        final Map<Integer, String> said = new HashMap<>();
        for (int n = 2; n <= 11; n++) {
            said.put(n, "podium");
        }
        said.put(37, "sky lobby");
        said.put(38, "sky lobby");
        said.put(60, "restaurant");
        int over = 1;
        while (levels.get(over) < 22_600) {
            over++;
        }
        return Offices.tower("UOB Plaza One", MAPPED, levels, 28_000, said,
                List.of(new Offices.Zone(12, 24, 6, 400), new Offices.Zone(25, 36, 6, 600),
                        new Offices.Zone(39, 66, 6, 600)), -141, 279,
                List.of(new OfficeTower.Setback(over, MAPPED.own(OVER_226))));
    }
}
