package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;

import java.util.List;

/**
 * The form a muscle takes ({@link Muscle#form}), the way its shape is drawn: a strap, its sections from its origin to
 * its insertion ({@link Muscle#sections}), each an oval across it, as most are drawn; or a spread, a sheet from a line
 * where it arises to a line where it ends, each of its fibres from a point along the one to as far along the other, as
 * the trapezius spreads from the spines of the back to the shoulder blade.
 */
public sealed interface MuscleForm permits MuscleForm.Strap, MuscleForm.Spread {

    /** Its sections, from its origin to its insertion, each an oval across it ({@link Section}). */
    record Strap() implements MuscleForm {
    }

    /**
     * A sheet from a line to a line: where it arises, from the first point of {@code origin} to the last, each fibre
     * to as far along {@code insertion}, from its first to its last; so deep, the way it faces as its origin's bone is
     * drawn. Its two lines each of two points or more. Its fibres straight from line to line; or, if it has a line
     * {@code over}, each bent to pass over the point as far along that, halfway: so a sheet that goes down the back
     * of the neck before it turns forward over the shoulder does not cut across the neck's side.
     *
     * @param origin    where it arises, along a line, each point on a bone
     * @param insertion where it ends, along a line
     * @param deep      how deep, cm
     * @param facing    which way its depth is, as its origin's bone is drawn
     * @param over      where its fibres pass over halfway, along a line, from its first to its last; none, straight
     */
    record Spread(List<Anchor> origin, List<Anchor> insertion, double deep, V3 facing, List<Anchor> over)
            implements MuscleForm {

        public Spread {
            over = List.copyOf(over);
            if (over.size() == 1) {
                throw new IllegalArgumentException("a sheet's fibres pass over a line, not a point");
            }
        }

        /** A sheet whose fibres run straight from line to line. */
        public Spread(final List<Anchor> origin, final List<Anchor> insertion, final double deep, final V3 facing) {
            this(origin, insertion, deep, facing, List.of());
        }

        /**
         * Its sections, as a strap's are counted: across its origin and across its insertion, each as wide as its
         * line is long end to end, lying at its middle point, by their count: each point is given as its own bone is
         * drawn, the skull's in the head's frame, so none is to be measured against another's.
         */
        public List<Section> sections() {
            return List.of(across(origin), across(insertion));
        }

        private Section across(final List<Anchor> line) {
            return new Section(line.get(line.size() / 2), line.getLast().at().minus(line.getFirst().at()).length(),
                    deep, facing);
        }
    }
}
