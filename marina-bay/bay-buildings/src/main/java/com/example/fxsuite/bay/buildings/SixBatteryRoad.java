package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Six Battery Road: its owner (CapitaLand Integrated Commercial Trust) publishing its floors by zone, the low 3 to 18,
 * the mid 19 to 29, the high 30 to 42, its walls 10 m from its core, but no lengths; so on the map's outline.
 * Published: 42 storeys (its owner; the Skyscraper Center says 44), 174 m, 21 lifts, here seven to each zone. Its lobby
 * 8 m high, its floor 2 a podium, its floors 4.04 m apart: estimates.
 */
public final class SixBatteryRoad {

    private SixBatteryRoad() {
    }

    /**
     * Six Battery Road as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Bonham Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1016.25, -59.40}, {-1007.60, -104.13}, {-996.81, -108.10}, {-979.43, -93.01}, {-987.72, -48.48},
            {-999.32, -44.84}});

    /**
     * Six Battery Road, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 42, 2, 404);
        return Offices.tower("Six Battery Road", MAPPED, levels, 17_400, Offices.uses(2, 2, "podium"),
                List.of(new Offices.Zone(3, 18, 7, 400), new Offices.Zone(19, 29, 7, 600), new Offices.Zone(30, 42, 7,
                700)), -412, 7, List.of());
    }
}
