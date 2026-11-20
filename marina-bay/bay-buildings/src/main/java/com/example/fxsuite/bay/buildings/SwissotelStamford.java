package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Swissôtel The Stamford, at Raffles City: I. M. Pei's hotel tower, publishing no plans, so on the map's outline.
 * Published: 73 storeys, 226 m, 1,261 rooms (the Skyscraper Center); its restaurants and bars at its top, SKAI on floor
 * 70; six cars to its floors over 38 at 5 m/s (a lift fans' wiki). Standing in the Raffles City podium, here floors 2
 * to 8; its floors 3.05 m apart; its lifts two zones of eight: estimates.
 */
public final class SwissotelStamford {

    private SwissotelStamford() {
    }

    /**
     * Swissôtel The Stamford as the map draws it, simplified to within half a metre, anticlockwise from the edge its
     * way in is in, toward Beach Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1050.57, 791.02}, {-1065.19, 821.51}, {-1069.83, 823.30}, {-1099.93, 808.01}, {-1102.37, 800.35},
            {-1102.59, 793.07}, {-1100.43, 785.66}, {-1095.25, 776.81}, {-1089.42, 772.78}, {-1081.33, 769.95},
            {-1071.37, 770.13}, {-1063.21, 773.19}, {-1056.26, 779.02}, {-1052.86, 784.77}});

    /**
     * Swissôtel The Stamford, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 73, 2, 305);
        return Offices.tower("Swissôtel The Stamford", MAPPED, levels, 22_600, Offices.uses(2, 8, "podium", 9, 68,
                Offices.HOTEL, 69, 72, "restaurants and bars", 73, 73, OfficeTower.PLANT), List.of(new Offices.Zone(9,
                37, 8, 400), new Offices.Zone(38, 73, 8, 500)), 103, -5, List.of());
    }
}
