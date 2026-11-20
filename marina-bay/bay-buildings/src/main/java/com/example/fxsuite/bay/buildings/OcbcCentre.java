package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * OCBC Centre, on Chulia Street: I. M. Pei's slab, its owner (OCBC Bank) publishing no plans of it, so on the map's
 * outline. Published: 52 storeys, 197.7 m (the Skyscraper Center); its banking hall 12 m high, here floors 1 to 3; its
 * floors carried in three tiers by girders on floors 4, 20 and 35; 27 lifts in two half-round cores at its ends
 * (Docomomo). Here its lifts are in one slim core along it, three zones of eight cars, one to each tier: an estimate,
 * as are its floors 3.64 m apart.
 */
public final class OcbcCentre {

    private OcbcCentre() {
    }

    /**
     * OCBC Centre as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward South Canal Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1291.19, -192.75}, {-1243.96, -200.91}, {-1238.29, -198.84}, {-1234.74, -193.95}, {-1233.26, -188.87},
            {-1234.72, -184.36}, {-1237.93, -180.81}, {-1245.25, -178.62}, {-1287.58, -171.08}, {-1292.29, -172.38},
            {-1296.49, -175.61}, {-1298.10, -180.25}, {-1297.44, -186.23}, {-1294.31, -190.57}});

    /**
     * OCBC Centre, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(1200, 52, 2, 364);
        return Offices.slim("OCBC Centre", MAPPED, levels, 19_770, Offices.uses(2, 3, "banking hall", 4, 4,
                "transfer floor", 20, 20, "transfer floor", 35, 35, "transfer floor"), List.of(new Offices.Zone(5, 19,
                8, 400), new Offices.Zone(21, 34, 8, 600), new Offices.Zone(36, 52, 8, 700)), -111, -20, List.of());
    }
}
