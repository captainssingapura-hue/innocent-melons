package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Samsung Hub, on Church Street: publishing no plans, so on the map's outline. Published: 30 storeys, its roof 130 m
 * and its spire 172 m (Wikipedia), 11 lifts (the Skyscraper Center); its car park on floors 1 to 7, its offices from 8.
 * Its floors 4.28 m apart, its lifts two zones: estimates.
 */
public final class SamsungHub {

    private SamsungHub() {
    }

    /**
     * Samsung Hub as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Telok Ayer Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1201.95, -392.28}, {-1163.36, -417.63}, {-1155.27, -405.98}, {-1149.25, -410.11}, {-1135.80, -390.76},
            {-1151.86, -378.80}, {-1153.11, -374.91}, {-1182.60, -349.12}, {-1188.75, -356.22}, {-1188.82, -360.02},
            {-1204.71, -384.72}, {-1205.19, -387.57}});

    /**
     * Samsung Hub, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 30, 2, 428);
        return Offices.slim("Samsung Hub", MAPPED, levels, 17_200, Offices.uses(2, 7, OfficeTower.PARKING),
                List.of(new Offices.Zone(8, 19, 6, 400), new Offices.Zone(20, 30, 5, 600)), -122, -192, List.of());
    }
}
