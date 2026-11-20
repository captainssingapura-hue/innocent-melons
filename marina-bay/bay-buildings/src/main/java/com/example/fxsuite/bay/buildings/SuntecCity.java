package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Suntec City, its four towers of 45 storeys: DP Architects' and Tsao & McKown's, strata-titled, publishing no plans,
 * so each on the map's outlines, stepping in over 90 m and 150 m as those do. Published: 45 storeys each, 180.8 m (the
 * Skyscraper Center); a podium of four storeys of shops (DP Architects); low, mid and high zones of lifts. Their floors
 * 3.97 m apart, their zones three of six: estimates. Tower 5, of 18 storeys, is not here.
 */
public final class SuntecCity {

    private SuntecCity() {
    }

    /**
     * Suntec Tower 1 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Fountain of Wealth.
     */
    static final Mapped TOWER1 = Mapped.of(new double[][] {
            {-578.19, 1190.46}, {-621.51, 1200.04}, {-630.35, 1161.15}, {-587.02, 1151.56}});

    /** Where the map's parts of it step in, over 90 m. */
    private static final double[][] TOWER1_OVER_90 = {
            {-594.12, 1193.98}, {-621.51, 1200.04}, {-625.68, 1181.69}, {-608.01, 1156.21}, {-587.02, 1151.56},
            {-578.19, 1190.46}};

    /** Where the map's parts of it step in, over 150 m. */
    private static final double[][] TOWER1_OVER_150 = {
            {-625.68, 1181.69}, {-608.01, 1156.21}, {-587.02, 1151.56}, {-582.85, 1169.94}, {-599.53, 1195.19},
            {-621.51, 1200.04}};

    /**
     * Tower 1.
     */
    public static OfficeTower tower1() {
        final List<Double> levels = Offices.levels(600, 45, 2, 397);
        return Offices.tower("Suntec Tower 1", TOWER1, levels, 18_080, Offices.uses(2, 4, "shops"),
                List.of(new Offices.Zone(5, 18, 6, 400), new Offices.Zone(19, 32, 6, 600), new Offices.Zone(33, 45, 6,
                700)), -255, -594, List.of(new OfficeTower.Setback(Offices.over(levels, 9000),
                TOWER1.own(TOWER1_OVER_90)), new OfficeTower.Setback(Offices.over(levels, 15_000),
                TOWER1.own(TOWER1_OVER_150))));
    }

    /**
     * Suntec Tower 2 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Temasek Boulevard.
     */
    static final Mapped TOWER2 = Mapped.of(new double[][] {
            {-534.89, 1190.13}, {-526.03, 1229.48}, {-569.64, 1239.05}, {-578.51, 1199.71}});

    /** Where the map's parts of it step in, over 90 m. */
    private static final double[][] TOWER2_OVER_90 = {
            {-534.89, 1190.13}, {-530.61, 1209.15}, {-549.06, 1234.54}, {-569.64, 1239.05}, {-578.51, 1199.71}};

    /** Where the map's parts of it step in, over 150 m. */
    private static final double[][] TOWER2_OVER_150 = {
            {-574.08, 1219.37}, {-555.52, 1194.66}, {-534.89, 1190.13}, {-530.61, 1209.15}, {-549.06, 1234.54},
            {-569.64, 1239.05}};

    /**
     * Tower 2.
     */
    public static OfficeTower tower2() {
        final List<Double> levels = Offices.levels(600, 45, 2, 397);
        return Offices.tower("Suntec Tower 2", TOWER2, levels, 18_080, Offices.uses(2, 4, "shops"),
                List.of(new Offices.Zone(5, 18, 6, 400), new Offices.Zone(19, 32, 6, 600), new Offices.Zone(33, 45, 6,
                700)), -129, 842, List.of(new OfficeTower.Setback(Offices.over(levels, 9000),
                TOWER2.own(TOWER2_OVER_90)), new OfficeTower.Setback(Offices.over(levels, 15_000),
                TOWER2.own(TOWER2_OVER_150))));
    }

    /**
     * Suntec Tower 3 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Temasek Boulevard.
     */
    static final Mapped TOWER3 = Mapped.of(new double[][] {
            {-447.83, 1208.22}, {-486.00, 1217.37}, {-491.88, 1218.78}, {-502.60, 1175.20}, {-458.55, 1164.64}});

    /** Where the map's parts of it step in, over 90 m. */
    private static final double[][] TOWER3_OVER_90 = {
            {-497.80, 1194.72}, {-502.60, 1175.20}, {-458.55, 1164.64}, {-447.83, 1208.22}, {-465.80, 1212.53}};

    /** Where the map's parts of it step in, over 150 m. */
    private static final double[][] TOWER3_OVER_150 = {
            {-483.04, 1170.51}, {-452.68, 1188.51}, {-447.83, 1208.22}, {-465.80, 1212.53}, {-497.80, 1194.72},
            {-502.60, 1175.20}};

    /**
     * Tower 3.
     */
    public static OfficeTower tower3() {
        final List<Double> levels = Offices.levels(600, 45, 2, 397);
        return Offices.slim("Suntec Tower 3", TOWER3, levels, 18_080, Offices.uses(2, 4, "shops"),
                List.of(new Offices.Zone(5, 18, 6, 400), new Offices.Zone(19, 32, 6, 600), new Offices.Zone(33, 45, 6,
                700)), 22, 906, List.of(new OfficeTower.Setback(Offices.over(levels, 9000), TOWER3.own(TOWER3_OVER_90)),
                new OfficeTower.Setback(Offices.over(levels, 15_000), TOWER3.own(TOWER3_OVER_150))));
    }

    /**
     * Suntec Tower 4 as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Temasek Avenue.
     */
    static final Mapped TOWER4 = Mapped.of(new double[][] {
            {-454.41, 1160.75}, {-465.55, 1116.99}, {-421.68, 1106.11}, {-410.55, 1149.86}});

    /** Where the map's parts of it step in, over 90 m. */
    private static final double[][] TOWER4_OVER_90 = {
            {-454.41, 1160.75}, {-465.55, 1116.99}, {-441.69, 1111.07}, {-416.45, 1126.64}, {-410.55, 1149.86}};

    /** Where the map's parts of it step in, over 150 m. */
    private static final double[][] TOWER4_OVER_150 = {
            {-441.69, 1111.07}, {-416.45, 1126.64}, {-410.55, 1149.86}, {-429.76, 1154.63}, {-460.58, 1136.48},
            {-465.55, 1116.99}};

    /**
     * Tower 4.
     */
    public static OfficeTower tower4() {
        final List<Double> levels = Offices.levels(600, 45, 2, 397);
        return Offices.tower("Suntec Tower 4", TOWER4, levels, 18_080, Offices.uses(2, 4, "shops"),
                List.of(new Offices.Zone(5, 18, 6, 400), new Offices.Zone(19, 32, 6, 600), new Offices.Zone(33, 45, 6,
                700)), 104, -735, List.of(new OfficeTower.Setback(Offices.over(levels, 9000),
                TOWER4.own(TOWER4_OVER_90)), new OfficeTower.Setback(Offices.over(levels, 15_000),
                TOWER4.own(TOWER4_OVER_150))));
    }
}
