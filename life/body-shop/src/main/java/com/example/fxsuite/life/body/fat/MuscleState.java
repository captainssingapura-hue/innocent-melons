package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.wear.Beneath;

import java.util.List;

/**
 * The state a body's muscles are in, all together, the way each presses on the others and on the bones: as drawn, each
 * its own shape, passing through what it meets; or at rest, soft, each pressed against what it meets and swelling
 * where nothing presses on it, keeping how much of it there is ({@link Packing}). Others to come: drawn tight, pulling.
 */
public sealed interface MuscleState permits MuscleState.Drawn, MuscleState.Natural {

    /** The muscles {@code surfaces}, as the body is bound, its bones {@code bones}, in this state. */
    List<MuscleSurface.Surface> shaped(List<MuscleSurface.Surface> surfaces, Beneath bones);

    /** Each as it is drawn. */
    record Drawn() implements MuscleState {

        @Override
        public List<MuscleSurface.Surface> shaped(final List<MuscleSurface.Surface> surfaces, final Beneath bones) {
            return surfaces;
        }
    }

    /** At rest: each pressed against what it meets, keeping how much of it there is ({@link Packing}). */
    record Natural() implements MuscleState {

        @Override
        public List<MuscleSurface.Surface> shaped(final List<MuscleSurface.Surface> surfaces, final Beneath bones) {
            return Packing.INSTANCE.packed(surfaces, bones);
        }
    }
}
