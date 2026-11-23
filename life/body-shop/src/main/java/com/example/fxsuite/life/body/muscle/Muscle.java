package com.example.fxsuite.life.body.muscle;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A muscle, or a tendon or a band that goes with them: each its own stateless object, saying what it is, where in the
 * body, how deep it lies, and its shape ({@link MuscleForm}): its sections from its origin to its insertion
 * ({@link Section}), each held to a point on a bone, so that it goes where the bones go; or, a ring round an eye, the
 * mouth, its sections round it, the last joined to the first; or a sheet spread from a line to a line. As on the right,
 * its fellow on the left the mirror; or one, down the middle.
 */
public interface Muscle extends StatelessFunctionalObject {

    /** What it is called, not saying which side. */
    String name();

    /** Where in the body it is. */
    MuscleRegion region();

    /** How deep it lies. */
    default Layer layer() {
        return Layer.SUPERFICIAL;
    }

    /** How deep it lies among the muscles about it, the outermost 1 ({@link MuscleDepth}). */
    default int depth() {
        return MuscleDepth.INSTANCE.of(this);
    }

    /**
     * Whether it is a sheet, wide and thin, lying over what is under it, bones and other muscles, and following their
     * shape: the platysma, the trapezius, the latissimus; drawn lying on them where the body's muscles are made
     * together ({@link MuscleBody}), each of its sections bent to lie along them; alone, as it is written.
     */
    default boolean sheet() {
        return false;
    }

    /** Its sections, from its origin to its insertion; or round it, if a ring. */
    List<Section> sections();

    /** The form it takes: a strap of its sections, as most are drawn; or a spread from a line to a line. */
    default MuscleForm form() {
        return new MuscleForm.Strap();
    }

    /** Whether it is a ring, its last section joined to its first, ends it has none. */
    default boolean ring() {
        return false;
    }

    /** Whether there is one on each side; else one, down the middle, as written. */
    default boolean paired() {
        return true;
    }

    /** Where it comes from: the bone its first section is on. */
    default String origin() {
        return sections().getFirst().at().bone().name();
    }

    /** Where it goes to: the bone its last section is on. */
    default String insertion() {
        return sections().getLast().at().bone().name();
    }
}
