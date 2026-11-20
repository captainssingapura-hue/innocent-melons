package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.List;

/**
 * 18 Robinson: KPF's tower lifted over its podium, publishing no plans, so on the map's outline of its tower, standing
 * from the ground. Published: 28 storeys, 180 m to its top, its top floor 150 m up, ten lifts at up to 5 m/s (the
 * Skyscraper Center); its twenty office floors over the podium, its garden on floors 6 to 8. Its podium's floors 7 m
 * and its tower's 4.95 m, so its tower starts 56 m up as the map has it; its lifts two zones of five: estimates.
 */
public final class EighteenRobinson {

    private EighteenRobinson() {
    }

    /**
     * 18 Robinson as the map draws it, simplified to within half a metre, anticlockwise from the edge its way in is in,
     * toward Market Street.
     */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1045.45, -494.46}, {-1037.33, -529.93}, {-1018.05, -529.75}, {-1010.97, -506.12}, {-1015.70, -489.54},
            {-1044.83, -489.73}});

    /**
     * 18 Robinson, as above.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(700, 28, 2, 700, 9, 495);
        return Offices.slim("18 Robinson", MAPPED, levels, 18_000, Offices.uses(2, 5, "podium", 6, 8, "garden"),
                List.of(new Offices.Zone(9, 18, 5, 400), new Offices.Zone(19, 28, 5, 500)), 90, -219, List.of());
    }
}
