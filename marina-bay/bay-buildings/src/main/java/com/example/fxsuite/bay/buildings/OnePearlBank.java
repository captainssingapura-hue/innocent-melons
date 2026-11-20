package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * One Pearl Bank, its two curved homes towers on Pearl's Hill: Serie Architects' and Multiply's, their developer
 * (CapitaLand) publishing no lengths, so each on the map's outline. Published: 39 storeys each, 155.1 m, their top
 * floors 143.7 m up, four lifts each (the Skyscraper Center; their developer says 178 m, from the road below the hill);
 * 774 homes; their sky bridge and the Sky Oculus at their roofs. Their floors 3.72 m apart, their lifts two zones of
 * two: estimates.
 */
public final class OnePearlBank {

    private OnePearlBank() {
    }

    /**
     * One Pearl Bank, west tower as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Pearl Bank.
     */
    static final Mapped WEST_TOWER = Mapped.of(new double[][] {
            {-2221.71, -743.26}, {-2217.57, -752.33}, {-2219.16, -752.82}, {-2220.16, -749.52}, {-2226.40, -751.28},
            {-2225.34, -754.53}, {-2227.09, -754.86}, {-2227.70, -750.98}, {-2226.71, -750.78}, {-2227.26, -746.57},
            {-2228.26, -746.77}, {-2228.48, -745.38}, {-2234.46, -745.71}, {-2239.91, -744.91}, {-2248.15, -741.86},
            {-2249.03, -743.00}, {-2248.23, -743.70}, {-2250.41, -747.30}, {-2247.27, -748.89}, {-2248.59, -751.60},
            {-2242.63, -753.49}, {-2242.45, -752.81}, {-2237.53, -753.71}, {-2237.98, -757.11}, {-2246.13, -755.67},
            {-2245.75, -754.76}, {-2248.33, -753.75}, {-2249.64, -757.47}, {-2251.08, -758.69}, {-2251.96, -761.22},
            {-2252.90, -760.84}, {-2253.55, -762.23}, {-2243.36, -765.18}, {-2238.40, -765.71}, {-2224.20, -764.87},
            {-2216.44, -762.83}, {-2210.29, -760.24}, {-2209.81, -761.17}, {-2200.90, -755.83}, {-2193.34, -748.75},
            {-2191.01, -745.57}, {-2195.56, -742.67}, {-2196.44, -743.67}, {-2198.60, -742.22}, {-2204.98, -748.34},
            {-2206.33, -747.09}, {-2205.05, -745.91}, {-2205.97, -744.93}, {-2206.65, -745.39}, {-2209.25, -742.62},
            {-2207.48, -740.94}, {-2206.55, -741.72}, {-2203.44, -737.97}, {-2210.58, -733.32}, {-2217.08, -739.39},
            {-2216.42, -740.45}});

    /**
     * The tower to the west.
     */
    public static OfficeTower westTower() {
        return Homes.tower("One Pearl Bank, west tower", WEST_TOWER, 39, 600, 372, 15_510, Offices.uses(39, 39,
                "sky oculus"), 20, 2, 400, 500, 464, 1136);
    }

    /**
     * One Pearl Bank, east tower as the map draws it, simplified to within half a metre, anticlockwise from the edge
     * its way in is in, toward Pearl Bank.
     */
    static final Mapped EAST_TOWER = Mapped.of(new double[][] {
            {-2191.05, -719.70}, {-2190.34, -710.27}, {-2188.67, -710.28}, {-2189.24, -713.65}, {-2183.16, -714.42},
            {-2182.45, -711.06}, {-2181.00, -711.43}, {-2184.48, -719.08}, {-2183.57, -719.32}, {-2184.01, -720.76},
            {-2178.66, -722.72}, {-2171.25, -728.36}, {-2170.15, -727.39}, {-2170.70, -726.69}, {-2165.59, -722.02},
            {-2169.36, -718.75}, {-2170.11, -719.59}, {-2173.30, -717.43}, {-2171.74, -715.13}, {-2171.02, -715.57},
            {-2169.86, -714.03}, {-2165.14, -717.27}, {-2162.42, -720.23}, {-2161.62, -719.48}, {-2160.88, -720.13},
            {-2154.38, -715.68}, {-2158.08, -711.82}, {-2168.22, -704.86}, {-2174.33, -702.48}, {-2183.20, -700.38},
            {-2192.77, -699.89}, {-2192.86, -698.69}, {-2208.84, -701.86}, {-2216.22, -705.30}, {-2222.35, -709.48},
            {-2216.74, -714.99}, {-2211.49, -711.36}, {-2203.21, -708.45}, {-2202.82, -710.32}, {-2204.15, -710.85},
            {-2203.61, -711.95}, {-2202.92, -711.78}, {-2201.86, -715.53}, {-2204.15, -716.23}, {-2204.77, -715.20},
            {-2209.18, -717.35}, {-2205.03, -724.33}, {-2196.59, -721.31}, {-2196.73, -720.21}});

    /**
     * The tower to the east.
     */
    public static OfficeTower eastTower() {
        return Homes.tower("One Pearl Bank, east tower", EAST_TOWER, 39, 600, 372, 15_510, Offices.uses(39, 39,
                "sky oculus"), 20, 2, 400, 500, 276, 897);
    }
}
