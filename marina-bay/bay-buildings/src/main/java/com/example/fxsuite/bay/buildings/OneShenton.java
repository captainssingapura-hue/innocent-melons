package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.Map;

/**
 * One Shenton, its two homes towers on Shenton Way: Carlos Ott's and Architects 61's, each a homes tower ({@link Homes})
 * on the map's outline, its developer's brochure giving its floors but no lengths. Published: Tower 1 50 storeys,
 * 214 m to its top, its top floor occupied 168.7 m up; Tower 2 43 storeys (the brochure; the Skyscraper Center says
 * 42), 177.7 m, 141.5 m; both with their car park on floors 2 to 7, their club on 8, their wellness floors on 24 and 25;
 * Tower 1's sky villa on 50, Tower 2's on 41 to 43. Their crowns, the fins over their roofs, tens of metres high.
 *
 * <p>Their lifts: the brochure draws one core of eight cars and one of four, not saying clearly which tower's is
 * which; Tower 1, its floor the smaller, given four, Tower 2 eight, in two zones each. Their lobbies 6 m high: an
 * estimate.
 */
public final class OneShenton {

    private OneShenton() {
    }

    /** Tower 1 as the map draws it, anticlockwise from the edge its way in is in, by Shenton Lane. */
    static final Mapped TOWER_1 = Mapped.of(new double[][] {
            {-930.53, -763.61}, {-927.37, -753.00}, {-928.96, -752.57}, {-927.96, -749.08}, {-936.51, -745.41},
            {-944.99, -743.15}, {-953.44, -742.32}, {-955.49, -749.50}, {-956.24, -752.10}, {-952.14, -753.15},
            {-953.05, -758.55}});

    /** Tower 2 as the map draws it, anticlockwise from the edge its way in is in, by Shenton Lane. */
    static final Mapped TOWER_2 = Mapped.of(new double[][] {
            {-943.66, -806.97}, {-941.18, -797.77}, {-940.05, -792.20}, {-939.53, -789.61}, {-942.60, -788.18},
            {-941.37, -782.60}, {-959.60, -778.17}, {-962.87, -786.07}, {-965.62, -795.20}, {-967.16, -802.60},
            {-964.83, -803.44}, {-966.09, -808.22}, {-961.62, -810.84}, {-955.52, -812.75}, {-951.22, -813.05},
            {-946.66, -812.16}});

    /** Tower 1: its floors 3.39 m apart, so its top floor is where it is published to be, an estimate. */
    public static OfficeTower tower1() {
        final Map<Integer, String> said = said();
        said.put(50, "sky villa");
        return Homes.tower("One Shenton Tower 1", TOWER_1, 50, 600, 339, 21_400, said, 28, 2, 400, 600, 4, 31);
    }

    /** Tower 2: its floors 3.3 m apart, so its top floor is where it is published to be, an estimate. */
    public static OfficeTower tower2() {
        final Map<Integer, String> said = said();
        for (int n = 41; n <= 43; n++) {
            said.put(n, "sky villas");
        }
        return Homes.tower("One Shenton Tower 2", TOWER_2, 43, 600, 330, 17_770, said, 24, 4, 400, 600, -204, -72);
    }

    /** What both towers' floors are but homes: their car park, their club, their wellness floors. */
    private static Map<Integer, String> said() {
        final Map<Integer, String> said = new HashMap<>();
        for (int n = 2; n <= 7; n++) {
            said.put(n, OfficeTower.PARKING);
        }
        said.put(8, "club");
        said.put(24, "wellness");
        said.put(25, "wellness");
        return said;
    }
}
