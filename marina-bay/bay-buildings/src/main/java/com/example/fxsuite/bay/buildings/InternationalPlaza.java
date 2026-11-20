package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * International Plaza, on Anson Road: Ang Kheng Leng's tower of 1976, offices with homes over them, publishing no
 * plans, so on the map's outline (its podium's, the tower on it octagonal). Published (Wikipedia): 50 storeys, 190 m;
 * shops on floors 1 to 3, its car park on 4 to 8, offices on 9 to 35, its pool and health club on 36, 209 homes on 37
 * to 50; 28 lifts (the Skyscraper Center). Its floors 3.75 m apart, its lifts two office zones of eight and six cars to
 * its homes: estimates.
 */
public final class InternationalPlaza {

    private InternationalPlaza() {
    }

    /**
     * International Plaza as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Anson Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1361.25, -1320.07}, {-1306.64, -1296.91}, {-1303.59, -1290.79}, {-1316.35, -1260.34},
            {-1322.86, -1258.18}, {-1381.45, -1282.00}, {-1384.33, -1288.94}, {-1373.94, -1313.34},
            {-1365.67, -1309.84}});

    /**
     * International Plaza, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 50, 2, 375);
        return Offices.slim("International Plaza", MAPPED, levels, 19_000, Offices.uses(2, 3, "shops", 4, 8,
                OfficeTower.PARKING, 36, 36, "pool and health club", 37, 50, Homes.HOMES), List.of(new Offices.Zone(9,
                22, 8, 400), new Offices.Zone(23, 35, 8, 600), new Offices.Zone(36, 50, 6, 600)), 233, 207, List.of());
    }
}
