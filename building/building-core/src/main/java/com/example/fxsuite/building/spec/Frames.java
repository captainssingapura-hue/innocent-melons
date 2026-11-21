package com.example.fxsuite.building.spec;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * What every frame of bays under a roof is held to ({@link Framed}), whatever it is for: checked as each specification
 * of one is made.
 */
record Frames() implements StatelessFunctionalObject {

    static final Frames INSTANCE = new Frames();

    /** Refuses a frame of no bays, bays under 2 m, less than 3 m deep, or with no roofing. */
    void check(final int bays, final double bay, final double depth, final Roofing roofing) {
        if (bays < 1 || !(bay >= 200) || !(depth >= 300) || roofing == null) {
            throw new IllegalArgumentException("a frame of a bay or more, each 2 m or more, 3 m deep or more, roofed");
        }
    }
}
