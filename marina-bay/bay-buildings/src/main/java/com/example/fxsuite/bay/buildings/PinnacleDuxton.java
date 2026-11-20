package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * The Pinnacle@Duxton, its seven homes blocks: ARC Studio's, built by the Housing and Development Board, publishing no
 * lengths, so each on the map's outline. Published: 50 storeys (Block 1C 51), their roofs 156 m up (Wikipedia; the
 * Skyscraper Center says 160); their car park on a deck at floors 1 and 2; their sky gardens, joining them all, on
 * floors 26 and 50; five lifts to a block (Block 1C, the Skyscraper Center). Their ground floors 3.6 m high, as the
 * Board's are, and their floors 2.99 m apart, so their roofs are the map's 150 m; four of their cars in two zones:
 * estimates.
 */
public final class PinnacleDuxton {

    private PinnacleDuxton() {
    }

    /**
     * The Pinnacle@Duxton Block 1A as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Cantonment Road.
     */
    static final Mapped BLOCK_A = Mapped.of(new double[][] {
            {-1934.86, -1257.62}, {-1908.59, -1228.93}, {-1926.77, -1212.49}, {-1953.03, -1241.19}});

    /**
     * Block 1A: as its six neighbours.
     */
    public static OfficeTower blockA() {
        return Homes.tower("The Pinnacle@Duxton Block 1A", BLOCK_A, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, -553, 0);
    }

    /**
     * The Pinnacle@Duxton Block 1B as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Neil Road.
     */
    static final Mapped BLOCK_B = Mapped.of(new double[][] {
            {-1858.29, -1222.51}, {-1894.14, -1205.78}, {-1904.60, -1227.81}, {-1868.75, -1244.53}});

    /**
     * Block 1B: as its six neighbours.
     */
    public static OfficeTower blockB() {
        return Homes.tower("The Pinnacle@Duxton Block 1B", BLOCK_B, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, 589, 6);
    }

    /**
     * The Pinnacle@Duxton Block 1C as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Craig Road.
     */
    static final Mapped BLOCK_C = Mapped.of(new double[][] {
            {-1865.32, -1247.08}, {-1837.09, -1277.74}, {-1819.47, -1261.69}, {-1847.70, -1231.03}});

    /**
     * Block 1C: as its six neighbours.
     */
    public static OfficeTower blockC() {
        return Homes.tower("The Pinnacle@Duxton Block 1C", BLOCK_C, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, 734, 8);
    }

    /**
     * The Pinnacle@Duxton Block 1D as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Cantonment Road.
     */
    static final Mapped BLOCK_D = Mapped.of(new double[][] {
            {-1836.98, -1293.51}, {-1866.85, -1320.47}, {-1852.28, -1336.44}, {-1822.40, -1309.48}});

    /**
     * Block 1D: as its six neighbours.
     */
    public static OfficeTower blockD() {
        return Homes.tower("The Pinnacle@Duxton Block 1D", BLOCK_D, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, -443, -6);
    }

    /**
     * The Pinnacle@Duxton Block 1E as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Cantonment Road.
     */
    static final Mapped BLOCK_E = Mapped.of(new double[][] {
            {-1864.34, -1379.82}, {-1856.35, -1340.86}, {-1877.77, -1336.60}, {-1885.75, -1375.55}});

    /**
     * Block 1E: as its six neighbours.
     */
    public static OfficeTower blockE() {
        return Homes.tower("The Pinnacle@Duxton Block 1E", BLOCK_E, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, 730, 8);
    }

    /**
     * The Pinnacle@Duxton Block 1F as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Cantonment Road.
     */
    static final Mapped BLOCK_F = Mapped.of(new double[][] {
            {-1865.98, -1413.04}, {-1844.66, -1446.57}, {-1826.30, -1434.98}, {-1847.62, -1401.45},
            {-1851.10, -1403.65}});

    /**
     * Block 1F: as its six neighbours.
     */
    public static OfficeTower blockF() {
        return Homes.tower("The Pinnacle@Duxton Block 1F", BLOCK_F, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, -741, -11);
    }

    /**
     * The Pinnacle@Duxton Block 1G as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Cantonment Road.
     */
    static final Mapped BLOCK_G = Mapped.of(new double[][] {
            {-1841.05, -1453.09}, {-1824.83, -1485.91}, {-1804.95, -1475.91}, {-1821.06, -1443.29}});

    /**
     * Block 1G: as its six neighbours.
     */
    public static OfficeTower blockG() {
        return Homes.tower("The Pinnacle@Duxton Block 1G", BLOCK_G, 50, 360, 299, 15_600, Offices.uses(2, 2,
                OfficeTower.PARKING, 26, 26, "sky garden", 50, 50, "sky garden"), 26, 2, 250, 400, -553, 13);
    }
}
