package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Raffles City Tower: I. M. Pei's office tower, its owner (CapitaLand Integrated Commercial Trust) publishing a typical
 * floor's plan without its lengths, so on the map's outline. Published: 42 storeys, 158 m (the Skyscraper Center); its
 * floors some 11,000 sq ft, 8.5 m from wall to core low down, 8.5 to 14 m high up; a low zone and mid and high zones
 * reached through transfer floors 17 and 28. Its floors 3.7 m apart; its zones three of eight, all from the lobby:
 * estimates.
 */
public final class RafflesCityTower {

    private RafflesCityTower() {
    }

    /**
     * Raffles City Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward North Bridge Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1143.48, 898.03}, {-1172.91, 883.66}, {-1173.64, 879.93}, {-1167.66, 865.74}, {-1160.18, 850.88},
            {-1155.25, 849.50}, {-1126.80, 863.53}, {-1125.73, 868.56}, {-1139.21, 897.13}});

    /**
     * Raffles City Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 42, 2, 370);
        return Offices.tower("Raffles City Tower", MAPPED, levels, 15_800, Offices.uses(17, 17, "transfer floor", 28,
                28, "transfer floor"), List.of(new Offices.Zone(2, 16, 8, 400), new Offices.Zone(18, 27, 8, 600),
                new Offices.Zone(29, 42, 8, 700)), 10, 310, List.of());
    }
}
