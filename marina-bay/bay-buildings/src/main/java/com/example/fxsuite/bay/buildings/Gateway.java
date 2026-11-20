package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * The Gateway, its two prism towers on Beach Road: I. M. Pei's, publishing no plans, so each on the map's outline.
 * Published: 37 storeys each, 150 m (the Skyscraper Center); their floors 11,200 to 12,000 sq ft (Singapore Land).
 * Their floors 4 m apart, their lifts three zones of six: estimates.
 */
public final class Gateway {

    private Gateway() {
    }

    /**
     * Gateway East as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Ophir Road.
     */
    static final Mapped EAST = Mapped.of(new double[][] {
            {-631.32, 1561.11}, {-582.10, 1534.38}, {-586.28, 1548.74}, {-592.37, 1551.93}, {-588.88, 1557.70},
            {-593.03, 1571.96}, {-641.88, 1598.57}});

    /**
     * Gateway East, at 152 Beach Road.
     */
    public static OfficeTower east() {
        final List<Double> levels = Offices.levels(600, 37, 2, 400);
        return Offices.tower("Gateway East", EAST, levels, 15_000, Offices.uses(), List.of(new Offices.Zone(2, 13, 6,
                400), new Offices.Zone(14, 25, 6, 600), new Offices.Zone(26, 37, 6, 700)), 90, -67, List.of());
    }

    /**
     * Gateway West as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Beach Road.
     */
    static final Mapped WEST = Mapped.of(new double[][] {
            {-693.24, 1507.58}, {-644.55, 1480.56}, {-630.50, 1484.53}, {-627.19, 1490.50}, {-621.12, 1487.19},
            {-606.09, 1491.44}, {-654.91, 1518.31}});

    /**
     * Gateway West, at 150 Beach Road.
     */
    public static OfficeTower west() {
        final List<Double> levels = Offices.levels(600, 37, 2, 400);
        return Offices.tower("Gateway West", WEST, levels, 15_000, Offices.uses(), List.of(new Offices.Zone(2, 13, 6,
                400), new Offices.Zone(14, 25, 6, 600), new Offices.Zone(26, 37, 6, 700)), 37, -2, List.of());
    }
}
