package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;
import java.util.Map;

/**
 * Ocean Financial Centre, on Collyer Quay: Pelli Clarke Pelli's office tower, its plan a sail bowed toward the bay, its
 * owner (Keppel REIT) publishing no lengths of it, so on the map's outline ({@link Offices}). Published: 43 storeys,
 * 245 m, its crown roof gardens on four levels; its offices in three zones of eight cars each, the low serving floors 3
 * to 16, the mid 17 to 30, the high 31 to 43, floor 2 served by none; its plates 20,000 to 25,000 sq ft. Its lift zones
 * are from Keppel's specification sheet as a search gave it, the sheet itself not to be had.
 */
public final class OceanFinancialCentre {

    private OceanFinancialCentre() {
    }

    /** Ocean Financial Centre as the map draws it, anticlockwise from the edge its way in is in, facing Collyer Quay. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-887.17, -328.76}, {-883.40, -321.57}, {-880.82, -314.91}, {-879.18, -308.17}, {-886.11, -301.68},
            {-891.69, -303.56}, {-898.37, -306.04}, {-905.48, -309.18}, {-914.32, -314.44}, {-922.04, -319.44},
            {-927.61, -323.46}, {-933.17, -328.07}, {-938.06, -332.62}, {-942.16, -336.99}, {-945.64, -341.28},
            {-923.59, -364.20}, {-920.45, -367.45}, {-913.93, -361.97}, {-908.52, -356.73}, {-902.93, -350.81},
            {-897.41, -344.37}, {-891.51, -335.84}});

    /**
     * Its lobby 8 m high and its floors 4.5 m apart, estimates, so its crown of roof gardens stands some 48 m over its
     * roof. Its floor 2 a podium, no zone stopping there.
     */
    public static OfficeTower tower() {
        return Offices.tower("Ocean Financial Centre", MAPPED, Offices.levels(800, 43, 2, 450), 24_500,
                Map.of(2, "podium"), List.of(new Offices.Zone(3, 16, 8, 400), new Offices.Zone(17, 30, 8, 600),
                        new Offices.Zone(31, 43, 8, 800)), -932, 254, List.of());
    }
}
