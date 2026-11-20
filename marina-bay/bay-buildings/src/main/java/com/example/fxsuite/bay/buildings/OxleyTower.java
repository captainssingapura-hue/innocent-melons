package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Oxley Tower, on Robinson Road: RSP's, strata-titled, its developer publishing no plans of it, so on the map's
 * outline. Published: 32 storeys, 174.3 m, five lifts (the Skyscraper Center); a podium of three storeys of shops; its
 * offices on floors 5 to 32, 4.9 m to their ceilings. Its floors 5.3 m apart, its four cars one zone in a slim core:
 * estimates.
 */
public final class OxleyTower {

    private OxleyTower() {
    }

    /**
     * Oxley Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Stanley Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1165.27, -909.18}, {-1166.47, -916.22}, {-1166.40, -918.70}, {-1165.11, -918.94}, {-1166.36, -925.02},
            {-1165.56, -928.38}, {-1163.84, -930.65}, {-1161.23, -932.39}, {-1155.99, -932.40}, {-1151.68, -928.87},
            {-1150.04, -922.46}, {-1148.74, -922.68}, {-1146.67, -918.52}, {-1145.55, -911.75}, {-1146.53, -904.97},
            {-1149.85, -901.07}, {-1152.06, -900.02}, {-1156.89, -899.65}, {-1161.67, -902.86}});

    /**
     * Oxley Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 32, 2, 530);
        return Offices.slim("Oxley Tower", MAPPED, levels, 17_430, Offices.uses(2, 3, "shops", 4, 4, "podium"),
                List.of(new Offices.Zone(5, 32, 4, 500)), -351, 44, List.of());
    }
}
