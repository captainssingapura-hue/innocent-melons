package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Frasers Tower, on Cecil Street: DP Architects', its owner (Frasers Property) publishing no plans of it, so on the
 * map's outline. Published: 38 storeys, 235 m (its owner); its top floor 193.6 m up (the Skyscraper Center); 22 lifts;
 * its lobby 11 m high; a podium of three storeys of shops, its garden terrace on 4; its offices some 16 m deep from
 * window to core. Its floors 5.07 m apart, so its top floor is where it is published to be; its lifts two zones of 11:
 * estimates.
 */
public final class FrasersTower {

    private FrasersTower() {
    }

    /**
     * Frasers Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Cecil Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1297.53, -953.02}, {-1249.50, -965.31}, {-1234.87, -909.48}, {-1282.90, -897.19}});

    /**
     * Frasers Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(1100, 38, 2, 507);
        return Offices.tower("Frasers Tower", MAPPED, levels, 23_500, Offices.uses(2, 3, "shops", 4, 4, "sky garden"),
                List.of(new Offices.Zone(5, 21, 11, 500), new Offices.Zone(22, 38, 11, 700)), 4, 14, List.of());
    }
}
