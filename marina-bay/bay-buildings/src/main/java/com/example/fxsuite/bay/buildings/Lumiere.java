package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * Lumiere, on Mistri Road: a homes tower publishing no lengths, so on the map's small outline. Published: 45 storeys,
 * five homes a floor; its car park on floors 1 to 7, its pool and gym on 8, its penthouse on 45 (as listings have
 * them). Its height the map's 153 m; its floors 3.34 m apart, its lifts two zones of two: estimates.
 */
public final class Lumiere {

    private Lumiere() {
    }

    /**
     * Lumiere as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Parsi Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1277.62, -1442.52}, {-1285.88, -1458.75}, {-1272.35, -1465.53}, {-1264.08, -1449.29}});

    /**
     * Lumiere, as above.
     */
    public static OfficeTower tower() {
        return Homes.tower("Lumiere", MAPPED, 45, 600, 334, 15_300, Offices.uses(2, 7, OfficeTower.PARKING, 8, 8,
                "pool and gym", 45, 45, Homes.PENTHOUSES), 26, 2, 400, 500, 9, -7);
    }
}
