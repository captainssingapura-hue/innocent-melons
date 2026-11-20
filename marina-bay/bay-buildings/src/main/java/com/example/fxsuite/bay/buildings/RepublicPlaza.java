package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Republic Plaza, at Raffles Place: Kisho Kurokawa's octagonal office tower, its owner (City Developments) publishing
 * no plans of it, so on the map's outline ({@link Offices}). Published: 66 storeys, 280 m (its owner; the Skyscraper
 * Center says 276.3), its top floor occupied 262.9 m up; a podium of ten storeys, three of them shops, with its car
 * park; 15 passenger lifts, double-deck, as its owner counts them; its ceilings 2.6 m high. How its octagon turns to
 * a square at its top is not drawn.
 */
public final class RepublicPlaza {

    private RepublicPlaza() {
    }

    /** Republic Plaza as the map draws it, anticlockwise from the edge its way in is in, by D'Almeida Street. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1015.28, -397.87}, {-1001.68, -374.20}, {-1003.97, -367.16}, {-1027.39, -354.33}, {-1035.01, -356.81},
            {-1048.43, -379.78}, {-1046.08, -387.01}, {-1022.56, -400.23}});

    /**
     * Its lobby 8 m high and its floors 3.98 m apart, so its top floor is where it is published to be: estimates. Its
     * shops on floors 2 and 3, its car park on 4 to 10, its offices on 11 to 66: estimates, the podium's floors not
     * published one by one. Its fifteen lifts in three zones of five, single-deck: an estimate.
     */
    public static OfficeTower tower() {
        final Map<Integer, String> said = new HashMap<>();
        said.put(2, "shops");
        said.put(3, "shops");
        for (int n = 4; n <= 10; n++) {
            said.put(n, OfficeTower.PARKING);
        }
        return Offices.tower("Republic Plaza", MAPPED, Offices.levels(800, 66, 2, 398), 28_000, said,
                List.of(new Offices.Zone(11, 28, 5, 400), new Offices.Zone(29, 46, 5, 600),
                        new Offices.Zone(47, 66, 5, 800)), 1, -177, List.of());
    }
}
