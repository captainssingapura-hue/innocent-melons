package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * Singapore Land Tower, on Raffles Place: its owner (Singapore Land) publishing its zones' typical plans without their
 * lengths, and their columns 13 m apart, by which they scale to a square some 39 m a side, as the map draws it.
 * Published: 190 m to its roof (Wikipedia); 49 storeys since Maki & Associates' upgrade, its offices on floors 6 to 47
 * in three zones (6 to 18, 19 to 32, 33 to 47), six cars to each as the plans draw them; its lounge and auditorium on
 * floor 4, co-working on 5, its City Gallery on 49. Its lobby 8 m high and its floors 3.79 m apart, floor 48 plant:
 * estimates.
 */
public final class SingaporeLandTower {

    private SingaporeLandTower() {
    }

    /**
     * Singapore Land Tower as the map draws it, simplified to within half a metre, anticlockwise from the edge its way
     * in is in, toward Battery Road.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-956.28, -188.08}, {-932.84, -155.53}, {-965.18, -132.56}, {-988.61, -165.13}});

    /**
     * Singapore Land Tower, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(800, 49, 2, 379);
        return Offices.tower("Singapore Land Tower", MAPPED, levels, 19_000, Offices.uses(2, 3, "podium", 4, 4,
                "lounge and auditorium", 5, 5, "co-working", 48, 48, OfficeTower.PLANT, 49, 49, "city gallery"),
                List.of(new Offices.Zone(6, 18, 6, 400), new Offices.Zone(19, 32, 6, 600), new Offices.Zone(33, 47, 6,
                700)), 6, -458, List.of());
    }
}
