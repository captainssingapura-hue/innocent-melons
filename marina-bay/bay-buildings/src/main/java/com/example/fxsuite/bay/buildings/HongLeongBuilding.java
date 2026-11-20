package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Hong Leong Building, on Raffles Quay: its owner (Hong Leong Holdings) publishing no plans of it, so on the map's
 * outline. Published: 45 storeys, 158 m to its roof (the Skyscraper Center); parking on floors 4 to 7. Its floors 3.45
 * m apart, its lifts three zones of six: estimates.
 */
public final class HongLeongBuilding {

    private HongLeongBuilding() {
    }

    /**
     * Hong Leong Building as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Raffles Quay.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-957.95, -583.84}, {-947.21, -544.23}, {-978.26, -536.00}, {-989.02, -575.61}});

    /**
     * Hong Leong Building, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 45, 2, 345);
        return Offices.tower("Hong Leong Building", MAPPED, levels, 15_800, Offices.uses(4, 7, OfficeTower.PARKING),
                List.of(new Offices.Zone(2, 20, 6, 400), new Offices.Zone(21, 33, 6, 600), new Offices.Zone(34, 45, 6,
                700)), -11, 44, List.of());
    }
}
