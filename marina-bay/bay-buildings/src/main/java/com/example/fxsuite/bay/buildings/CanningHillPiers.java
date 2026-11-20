package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

/**
 * CanningHill Piers, its homes tower by the river: BIG's and DP Architects', finished in 2025, its developers (City
 * Developments and CapitaLand) publishing no lengths, so on the map's outline. Published: 48 storeys, 180 m; its
 * residents' level on floor 3, its club and pool on 24, its sky club on 45, its penthouse on 48. Its floors 3.45 m
 * apart, so its roof is the map's 168 m, the rest its crown; its lifts two zones of four: estimates.
 */
public final class CanningHillPiers {

    private CanningHillPiers() {
    }

    /**
     * CanningHill Piers as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in
     * is in, toward Clarke Quay.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-2020.30, 297.24}, {-1987.48, 285.63}, {-1985.94, 285.68}, {-1984.09, 286.30}, {-1982.68, 287.87},
            {-1970.92, 319.75}, {-1970.95, 322.24}, {-1972.79, 324.29}, {-1987.27, 328.65}, {-1990.21, 320.58},
            {-1988.48, 319.96}, {-1991.18, 312.81}, {-2000.96, 316.49}, {-1998.58, 322.89}, {-1996.94, 322.21},
            {-1993.68, 330.57}, {-1994.11, 331.43}, {-2005.36, 335.39}, {-2009.66, 333.88}, {-2021.54, 299.08}});

    /**
     * CanningHill Piers, as above.
     */
    public static OfficeTower tower() {
        return Homes.tower("CanningHill Piers", MAPPED, 48, 600, 345, 18_000, Offices.uses(3, 3, "residents' level", 24,
                24, "club and pool", 45, 45, "sky club", 48, 48, Homes.PENTHOUSES), 24, 4, 400, 600, 154, -727);
    }
}
