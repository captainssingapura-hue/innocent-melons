package com.example.fxsuite.life.body.joint;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A joint as the body has it, what moves as one: its articulations ({@link Joint}), each between two bones, and the
 * ways it moves, each driving a way one of them turns. A simple joint one articulation and its own ways ({@link
 * Simple}); a complex one, as the elbow, several.
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

    /** Which of its articulations moving {@code m} turns, and which way: none, if it follows another. */
    Drive drive(Movement m);

    /** An articulation, and the way it turns. */
    record Drive(Joint<?, ?> joint, Movement movement) {
    }
}
