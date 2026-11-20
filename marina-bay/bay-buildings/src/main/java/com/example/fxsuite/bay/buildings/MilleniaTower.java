package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Millenia Tower, on Temasek Avenue: Kevin Roche's, its owner (Pontiac Land) publishing no plans of it, so on the map's
 * outline. Published: 41 storeys, 223 m to its top, 218 m to its roof (Wikipedia); its lobby five storeys high, here
 * one of 20 m; its floors column-free, some 18,000 sq ft. Its floors 4.95 m apart, its lifts three zones of eight:
 * estimates. Its pyramid crown is not drawn.
 */
public final class MilleniaTower {

    private MilleniaTower() {
    }

    /**
     * Millenia Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Temasek Avenue.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-295.34, 953.48}, {-336.35, 961.78}, {-344.72, 921.67}, {-303.70, 913.36}});

    /**
     * Millenia Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(2000, 41, 2, 495);
        return Offices.tower("Millenia Tower", MAPPED, levels, 22_300, Offices.uses(), List.of(new Offices.Zone(2, 14,
                8, 400), new Offices.Zone(15, 28, 8, 600), new Offices.Zone(29, 41, 8, 700)), -4, -274, List.of());
    }
}
