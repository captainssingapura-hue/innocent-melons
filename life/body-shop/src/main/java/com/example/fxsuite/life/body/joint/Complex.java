package com.example.fxsuite.life.body.joint;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;
import java.util.Map;

/**
 * A joint as the body has it, what moves as one: its articulations ({@link Joint}), each between two bones, and the
 * ways it moves; and, moved so far each way, how far each of its articulations turns: one as it is moved, another
 * following it, as the kneecap rides with the knee. A simple joint is one articulation moving its own ways
 * ({@link Simple}); a complex one, as the knee, several.
 */
public interface Complex extends StatelessFunctionalObject {

    /** What it is called. */
    String name();

    /** Its articulations. */
    List<Joint<?, ?>> articulations();

    /** The ways it moves. */
    List<Movement> movements();

    /** How far it may move {@code m}. */
    Range range(Movement m);

    /** How far its articulation {@code joint} turns each way, the joint moved {@code moved}. */
    Map<Movement, Double> turns(Joint<?, ?> joint, Map<Movement, Double> moved);
}
