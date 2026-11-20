package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Hitachi Tower (now Collyer Quay Centre): Murphy/Jahn's, publishing no plans, so on the map's outline of its tower and
 * its middle block. Published: 37 storeys, 166 m to its roof and 179 m to its beacon's top (Wikipedia), 12 lifts, a
 * podium of two storeys of shops. Its floors 4.39 m apart, its lifts two zones of six in a slim core: estimates.
 */
public final class HitachiTower {

    private HitachiTower() {
    }

    /**
     * Hitachi Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Fullerton Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-896.20, -191.10}, {-896.20, -213.50}, {-894.10, -217.60}, {-890.70, -220.30}, {-886.40, -221.90},
            {-882.20, -221.60}, {-878.00, -219.40}, {-875.40, -216.10}, {-873.70, -211.80}, {-873.90, -191.50}});

    /**
     * Hitachi Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 37, 2, 439);
        return Offices.slim("Hitachi Tower", MAPPED, levels, 17_930, Offices.uses(2, 2, "shops"),
                List.of(new Offices.Zone(3, 20, 6, 400), new Offices.Zone(21, 37, 6, 600)), 64, -13, List.of());
    }
}
