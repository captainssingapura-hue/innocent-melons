package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Maybank Tower, on Battery Road: its owner (Maybank) publishing no plans of it, so on the map's narrow outline.
 * Published (Wikipedia): 32 storeys, 175.26 m to its roof, on a podium of six. Its podium's floors 6 m, its tower's
 * 5.31 m, so it reaches its roof; its lifts two zones of four in a slim core: estimates. How it tapers at its top is
 * not drawn.
 */
public final class MaybankTower {

    private MaybankTower() {
    }

    /**
     * Maybank Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is
     * in, toward Flint Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-965.37, -10.03}, {-940.33, -37.76}, {-931.84, -30.16}, {-933.54, -12.67}, {-939.67, -5.75},
            {-956.89, -2.45}});

    /**
     * Maybank Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(700, 32, 2, 600, 7, 531);
        return Offices.slim("Maybank Tower", MAPPED, levels, 17_526, Offices.uses(2, 6, "podium"),
                List.of(new Offices.Zone(7, 19, 4, 400), new Offices.Zone(20, 32, 4, 600)), 5, -56, List.of());
    }
}
