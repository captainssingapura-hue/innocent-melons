package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Centennial Tower, on Temasek Avenue: Kevin Roche's elliptical tower, its owner (Pontiac Land) publishing no plans of
 * it, so on the map's outline. Published: 37 storeys, 157.6 m (the Skyscraper Center), its trellis crown 12 m high. Its
 * floors 3.88 m apart, its lifts three zones of six: estimates.
 */
public final class CentennialTower {

    private CentennialTower() {
    }

    /**
     * Centennial Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Temasek Avenue.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-326.63, 1037.32}, {-336.81, 1040.89}, {-337.89, 1038.49}, {-342.43, 1037.86}, {-345.00, 1039.20},
            {-345.47, 1042.69}, {-350.62, 1044.12}, {-355.18, 1044.29}, {-366.87, 1042.46}, {-377.03, 1038.56},
            {-376.43, 1037.47}, {-375.55, 1037.88}, {-374.99, 1035.89}, {-378.00, 1034.32}, {-377.57, 1031.50},
            {-378.21, 1027.84}, {-380.60, 1023.14}, {-378.89, 1021.01}, {-380.38, 1019.43}, {-382.07, 1019.09},
            {-372.76, 1009.01}, {-367.17, 1004.99}, {-363.57, 1003.38}, {-355.67, 1000.81}, {-355.08, 1002.53},
            {-353.61, 1003.35}, {-350.38, 1003.75}, {-347.41, 1001.98}, {-346.53, 1000.32}, {-346.79, 998.84},
            {-341.39, 997.94}, {-330.58, 997.92}, {-325.35, 999.01}, {-315.75, 1002.77}, {-316.04, 1004.24},
            {-317.69, 1003.83}, {-318.16, 1005.63}, {-314.71, 1007.24}, {-315.03, 1011.78}, {-312.34, 1018.20},
            {-314.22, 1020.98}, {-313.16, 1022.97}, {-312.45, 1022.34}, {-311.00, 1023.39}, {-311.36, 1023.94},
            {-318.00, 1031.08}});

    /**
     * Centennial Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 37, 2, 388);
        return Offices.slim("Centennial Tower", MAPPED, levels, 15_760, Offices.uses(), List.of(new Offices.Zone(2, 13,
                6, 400), new Offices.Zone(14, 25, 6, 600), new Offices.Zone(26, 37, 6, 700)), 18, 18, List.of());
    }
}
