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
     * drawn. Its two lines each of two points or more.
     *
     * @param origin    where it arises, along a line, each point on a bone
     * @param insertion where it ends, along a line
     * @param deep      how deep, cm
     * @param facing    which way its depth is, as its origin's bone is drawn
     */
    record Spread(List<Anchor> origin, List<Anchor> insertion, double deep, V3 facing) implements MuscleForm {

        /**
         * Its sections, as a strap's are counted: across its origin and across its insertion, each as wide as its
         * line is long end to end, lying at the point along it nearest its middle.
         */
        public List<Section> sections() {
            return List.of(across(origin), across(insertion));
        }

        private Section across(final List<Anchor> line) {
            final V3 first = line.getFirst().at();
            final V3 last = line.getLast().at();
            final V3 middle = first.plus(last).times(0.5);
            Anchor nearest = line.getFirst();
            for (final Anchor a : line) {
                if (a.at().minus(middle).length() < nearest.at().minus(middle).length()) {
                    nearest = a;
                }
            }
            return new Section(nearest, last.minus(first).length(), deep, facing);
        }
    }
}
