package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * SGX Centre, its two towers on Shenton Way: Kohn Pedersen Fox's, publishing no plans, so each on the map's outline
 * (which calls them Unity Tower One and Two). Published: 30 storeys each, 187 m to their antennas, their roofs 172 m
 * (Wikipedia; the Skyscraper Center says 29 floors). Their floors 4.5 m apart, their crowns and antennas the rest;
 * their lifts two zones of six: estimates.
 */
public final class SgxCentre {

    private SgxCentre() {
    }

    /**
     * SGX Centre 1 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Boon Tat Link.
     */
    static final Mapped CENTRE1 = Mapped.of(new double[][] {
            {-1023.17, -733.21}, {-1034.16, -773.65}, {-1024.22, -776.35}, {-1022.94, -772.22}, {-1004.40, -776.92},
            {-1000.62, -763.13}, {-998.34, -748.96}, {-998.82, -733.75}, {-1017.97, -728.78}, {-1019.62, -734.64}});

    /**
     * SGX Centre 1, at 2 Shenton Way; its TV studios on floor 2.
     */
    public static OfficeTower centre1() {
        final List<Double> levels = Offices.levels(800, 30, 2, 450);
        return Offices.slim("SGX Centre 1", CENTRE1, levels, 18_730, Offices.uses(2, 2, "TV studios"),
                List.of(new Offices.Zone(3, 16, 6, 400), new Offices.Zone(17, 30, 6, 600)), -39, 50, List.of());
    }

    /**
     * SGX Centre 2 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Boon Tat Link.
     */
    static final Mapped CENTRE2 = Mapped.of(new double[][] {
            {-1052.39, -839.18}, {-1063.16, -877.88}, {-1058.92, -879.17}, {-1060.45, -884.92}, {-1040.89, -890.21},
            {-1033.38, -876.88}, {-1027.76, -862.52}, {-1024.27, -849.83}, {-1045.33, -844.34}, {-1044.65, -841.04}});

    /**
     * SGX Centre 2, at 4 Shenton Way.
     */
    public static OfficeTower centre2() {
        final List<Double> levels = Offices.levels(800, 30, 2, 450);
        return Offices.slim("SGX Centre 2", CENTRE2, levels, 18_730, Offices.uses(), List.of(new Offices.Zone(2, 16, 6,
                400), new Offices.Zone(17, 30, 6, 600)), 20, 63, List.of());
    }
}
