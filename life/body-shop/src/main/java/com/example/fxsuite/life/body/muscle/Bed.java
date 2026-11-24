package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;

import java.util.Optional;

/**
 * What a sheet of muscle lies on ({@link Muscle#sheet}): the body's bones and its other muscles, as one surface; at
 * any point, the nearest of it, and the way out of it there.
 */
public interface Bed {

    /** A point of the bed, the way out of it there, a unit, and whether it is a bone's, not a muscle's. */
    record Laid(V3 at, V3 out, boolean bone) {
    }

    /** The point of the bed nearest {@code p}, and the way out of it there; none, if none is near. */
    Optional<Laid> under(V3 p);
}
