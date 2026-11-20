package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * The State Courts Towers, on Havelock Square: Serie Architects' and Multiply's, the government's, publishing no plans,
 * so each on the map's outline. Published: 35 storeys, 177.8 m, the top floor 163.9 m up (the Skyscraper Center); 39
 * bridges joining the two; the Court Tower's courtrooms on trays of one storey's height or two. Their floors 4.72 m
 * apart, their lifts two zones of six: estimates; the bridges are not drawn.
 */
public final class StateCourts {

    private StateCourts() {
    }

    /**
     * State Courts Office Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its
     * way in is in, toward Upper Cross Street.
     */
    static final Mapped OFFICE_TOWER = Mapped.of(new double[][] {
            {-1888.92, -245.38}, {-1916.74, -308.79}, {-1904.82, -313.92}, {-1869.02, -232.34}, {-1880.95, -227.20}});

    /**
     * The Office Tower, the thin slab.
     */
    public static OfficeTower officeTower() {
        final List<Double> levels = Offices.levels(800, 35, 2, 472);
        return Offices.slim("State Courts Office Tower", OFFICE_TOWER, levels, 17_780, Offices.uses(),
                List.of(new Offices.Zone(2, 18, 6, 400), new Offices.Zone(19, 35, 6, 600)), -3008, 0, List.of());
    }

    /**
     * State Courts Court Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its
     * way in is in, toward Upper Cross Street.
     */
    static final Mapped COURT_TOWER = Mapped.of(new double[][] {
            {-1925.74, -216.77}, {-1963.18, -302.11}, {-1948.94, -308.24}, {-1947.57, -305.13}, {-1935.38, -310.38},
            {-1931.44, -301.38}, {-1926.60, -303.47}, {-1899.18, -240.96}, {-1901.60, -239.91}, {-1900.83, -238.15},
            {-1902.25, -237.53}, {-1898.32, -228.58}});

    /**
     * The Court Tower, its courtrooms on all its floors.
     */
    public static OfficeTower courtTower() {
        final List<Double> levels = Offices.levels(800, 35, 2, 472);
        return Offices.slim("State Courts Court Tower", COURT_TOWER, levels, 17_780, Offices.uses(2, 35, "courts"),
                List.of(new Offices.Zone(2, 18, 6, 400), new Offices.Zone(19, 35, 6, 600)), -1421, 62, List.of());
    }
}
