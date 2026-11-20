package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * DUO, its two towers on Ophir Road: Buro Ole Scheeren's, carved with concave cuts, their owners publishing no plans of
 * them, so each on the map's outlines.
 */
public final class Duo {

    private Duo() {
    }

    /**
     * DUO Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Beach Road.
     */
    static final Mapped TOWER = Mapped.of(new double[][] {
            {-734.87, 1545.77}, {-739.87, 1562.89}, {-762.30, 1582.38}, {-770.42, 1576.27}, {-779.17, 1570.50},
            {-790.42, 1566.92}, {-801.35, 1566.82}, {-807.21, 1568.83}, {-815.94, 1573.17}, {-823.37, 1561.82},
            {-811.08, 1552.65}, {-806.32, 1545.35}, {-805.39, 1542.14}, {-779.51, 1525.94}, {-773.39, 1527.32},
            {-764.16, 1526.91}, {-757.50, 1525.08}, {-751.24, 1521.86}});

    /** Where the map's parts of it step in, over 127 m. */
    private static final double[][] TOWER_OVER_127 = {
            {-779.51, 1525.94}, {-777.13, 1535.12}, {-773.86, 1542.08}, {-767.91, 1549.89}, {-760.96, 1555.95},
            {-752.71, 1560.43}, {-770.42, 1576.27}, {-779.17, 1570.50}, {-790.42, 1566.92}, {-801.35, 1566.82},
            {-807.21, 1568.83}, {-815.94, 1573.17}, {-823.37, 1561.82}, {-811.08, 1552.65}, {-806.32, 1545.35},
            {-805.39, 1542.14}};

    /**
     * DUO Tower: 39 storeys, 179.4 m, its top floor 168.6 m up (the Skyscraper Center; its roof 185.35 m over the sea);
     * its offices on its lower floors, the Andaz hotel on 25 to 39, its lobby on 25, its rooftop bar on 39; its lifts
     * at up to 7 m/s. Stepping in over 127 m, as the map's parts do. Its floors 4.34 m apart; its lifts two office
     * zones of eight and six cars to its hotel: estimates.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 39, 2, 434);
        return Offices.tower("DUO Tower", TOWER, levels, 17_940, Offices.uses(2, 4, "shops", 25, 25, "hotel lobby", 26,
                37, Offices.HOTEL, 38, 38, "restaurant", 39, 39, "rooftop bar"), List.of(new Offices.Zone(5, 14, 8,
                400), new Offices.Zone(15, 24, 8, 600), new Offices.Zone(25, 39, 6, 700)), 21, 389,
                List.of(new OfficeTower.Setback(Offices.over(levels, 12_700), TOWER.own(TOWER_OVER_127))));
    }

    /**
     * DUO Residences as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Beach Road.
     */
    static final Mapped RESIDENCES = Mapped.of(new double[][] {
            {-725.39, 1625.02}, {-707.05, 1616.28}, {-703.82, 1620.79}, {-699.57, 1622.94}, {-694.32, 1624.98},
            {-690.33, 1624.40}, {-683.87, 1640.09}, {-694.65, 1642.46}, {-702.46, 1646.61}, {-708.70, 1654.00},
            {-712.05, 1664.12}, {-730.29, 1657.28}, {-727.04, 1649.95}, {-725.04, 1642.12}, {-724.01, 1631.33}});

    /**
     * DUO Residences: 50 storeys, 186 m, its top floor 175 m up (the Skyscraper Center); 660 homes; its pool deck on
     * floor 2, gardens on 3, sky terraces on 20, its sky pool on 31 (as listings have them). On the map's outline of
     * its tower, its lower, wider parts left out. Its floors 3.52 m apart, its lifts two zones of three: estimates.
     */
    public static OfficeTower residences() {
        return Homes.tower("DUO Residences", RESIDENCES, 50, 600, 352, 18_600, Offices.uses(2, 2, "pool deck", 3, 3,
                "gardens", 20, 20, "sky terraces", 31, 31, "sky pool"), 25, 3, 400, 600, 218, -205);
    }
}
