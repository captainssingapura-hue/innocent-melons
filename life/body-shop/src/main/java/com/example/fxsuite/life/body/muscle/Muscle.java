package com.example.fxsuite.life.body.muscle;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A muscle, or a tendon or a band that goes with them: each its own stateless object, saying what it is, where in the
 * body, how deep it lies, and its shape: its sections from its origin to its insertion ({@link Section}), each held to a
 * point on a bone, so that it goes where the bones go. As on the right; its fellow on the left the mirror.
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

    /** Its sections, from its origin to its insertion. */
    List<Section> sections();

    /** Where it comes from: the bone its first section is on. */
    default String origin() {
        return sections().getFirst().at().bone().name();
    }

    /** Where it goes to: the bone its last section is on. */
    default String insertion() {
        return sections().getLast().at().bone().name();
    }
}
