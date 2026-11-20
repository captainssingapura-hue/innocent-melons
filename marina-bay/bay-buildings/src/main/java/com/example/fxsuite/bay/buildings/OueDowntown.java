package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * OUE Downtown, on Shenton Way: Architects Team 3's two towers over the Downtown Gallery, their owners (OUE) publishing
 * no plans of them, so each on the map's outline.
 */
public final class OueDowntown {

    private OueDowntown() {
    }

    /**
     * OUE Downtown Tower 1 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Shenton Way.
     */
    static final Mapped TOWER1 = Mapped.of(new double[][] {
            {-1096.22, -1102.09}, {-1089.06, -1075.09}, {-1102.66, -1069.52}, {-1114.34, -1068.54},
            {-1121.50, -1095.54}, {-1109.62, -1100.39}});

    /**
     * Tower 1, once DBS Tower One: 50 storeys, 201.2 m (the Skyscraper Center); its offices on floors 35 to 46 (its
     * owner); serviced residences on 8 to 18 and 21 to 32, their pool and gym on 7, reached by lifts of their own. Its
     * floors 3.98 m apart; plant on 19, 20, 33, 34 and 47 to 50; six cars to its residences and six to its offices:
     * estimates.
     */
    public static OfficeTower tower1() {
        final List<Double> levels = Offices.levels(600, 50, 2, 398);
        return Offices.tower("OUE Downtown Tower 1", TOWER1, levels, 20_120, Offices.uses(2, 6, "shops", 7, 7,
                "residents' pool and gym", 8, 18, Homes.HOMES, 19, 20, OfficeTower.PLANT, 21, 32, Homes.HOMES, 33, 34,
                OfficeTower.PLANT, 47, 50, OfficeTower.PLANT), List.of(new Offices.Zone(7, 32, 6, 500),
                new Offices.Zone(35, 46, 6, 600)), -100, -8, List.of());
    }

    /**
     * OUE Downtown Tower 2 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Shenton Way.
     */
    static final Mapped TOWER2 = Mapped.of(new double[][] {
            {-1078.33, -1041.93}, {-1060.34, -975.29}, {-1087.51, -968.11}, {-1105.52, -1034.76}});

    /**
     * Tower 2, once DBS Tower Two: 37 storeys (its owner; the Skyscraper Center says 36), 150 m; its offices on floors
     * 7 to 34, the podium and car park under them. Its floors 4 m apart, plant over 34, two zones of eight: estimates.
     */
    public static OfficeTower tower2() {
        final List<Double> levels = Offices.levels(600, 37, 2, 400);
        return Offices.slim("OUE Downtown Tower 2", TOWER2, levels, 15_000, Offices.uses(2, 6, "podium and car park",
                35, 37, OfficeTower.PLANT), List.of(new Offices.Zone(7, 20, 8, 400), new Offices.Zone(21, 34, 8, 600)),
                -210, -6, List.of());
    }
}
