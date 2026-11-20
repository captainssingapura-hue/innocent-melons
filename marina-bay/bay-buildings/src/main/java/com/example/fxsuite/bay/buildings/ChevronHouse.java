package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Chevron House, on Raffles Place: Murphy/Jahn's tower, publishing no plans, so on the map's outline. Published: 33
 * storeys, 151.5 m, 11 lifts (the Skyscraper Center); shops on its lower floors, offices on 3 to 5 and the tower over
 * them. Its floors 4.48 m apart, its lifts two zones (3 to 19, 20 to 33): estimates. Its rotunda crown is not drawn.
 */
public final class ChevronHouse {

    private ChevronHouse() {
    }

    /**
     * Chevron House as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Collyer Quay.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-962.16, -237.46}, {-929.20, -230.82}, {-930.12, -226.18}, {-918.93, -223.48}, {-923.02, -203.36},
            {-924.78, -201.58}, {-926.98, -201.00}, {-968.14, -209.09}, {-968.06, -210.92}, {-971.71, -211.71},
            {-975.47, -214.90}, {-978.20, -218.97}, {-979.00, -222.64}, {-978.64, -226.53}, {-977.01, -230.71},
            {-974.74, -233.67}, {-968.66, -237.27}, {-965.72, -237.88}});

    /**
     * Chevron House, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 33, 2, 448);
        return Offices.slim("Chevron House", MAPPED, levels, 15_150, Offices.uses(2, 2, "shops"),
                List.of(new Offices.Zone(3, 19, 6, 400), new Offices.Zone(20, 33, 5, 600)), -283, -99, List.of());
    }
}
