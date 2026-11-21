package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Rot;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;
import java.util.Map;

/**
 * How a joint may move, by its kind, each way it turns within its range: not at all (a suture, a tooth in its socket);
 * one way, as a hinge or a pivot; two ways, as a knuckle; or every way, a little, as two vertebrae on the disc between
 * them, or far, as a hip in its socket.
 */
public sealed interface Motion extends Scalar
        permits Motion.Fixed, Motion.Hinge, Motion.Pivot, Motion.Condyloid, Motion.Disc, Motion.Ball {

    /** The ways it turns. */
    List<Movement> movements();

    /** How far it may turn {@code m}; not at all, if it does not. */
    Range range(Movement m);

    /**
     * The turn of the bone it carries, in the joint's frame, turned so many degrees each way ({@code degrees}; a way
     * not given, none): twisted, then bent forward, then to the side, as the papers' rig turns its spine.
     */
    default Rot turn(final Map<Movement, Double> degrees) {
        final double twist = degrees.getOrDefault(Movement.TWIST, 0.0);
        final double bend = degrees.getOrDefault(Movement.FLEXION, 0.0);
        final double side = degrees.getOrDefault(Movement.SIDE_BEND, 0.0);
        return Rot.aboutY(-twist).then(Rot.aboutX(bend)).then(Rot.aboutZ(side));
    }

    /** Not moving at all. */
    record Fixed() implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of();
        }

        @Override
        public Range range(final Movement m) {
            return Range.NONE;
        }
    }

    /** Bending one way only, forward and back, as an elbow does. */
    record Hinge(Range flexion) implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of(Movement.FLEXION);
        }

        @Override
        public Range range(final Movement m) {
            return m == Movement.FLEXION ? flexion : Range.NONE;
        }
    }

    /** Twisting only, about the bone it carries, as the atlas does about the axis's dens. */
    record Pivot(Range twist) implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of(Movement.TWIST);
        }

        @Override
        public Range range(final Movement m) {
            return m == Movement.TWIST ? twist : Range.NONE;
        }
    }

    /** Flexing and bending to the side, not twisting: as a toe on its metatarsal. */
    record Condyloid(Range flexion, Range side) implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of(Movement.FLEXION, Movement.SIDE_BEND);
        }

        @Override
        public Range range(final Movement m) {
            return m == Movement.FLEXION ? flexion : m == Movement.SIDE_BEND ? side : Range.NONE;
        }
    }

    /** Far every way, a ball in its socket: flexing, out to the side (abducting), turning about itself. */
    record Ball(Range flexion, Range abduction, Range rotation) implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of(Movement.FLEXION, Movement.SIDE_BEND, Movement.TWIST);
        }

        @Override
        public Range range(final Movement m) {
            return switch (m) {
                case FLEXION -> flexion;
                case SIDE_BEND -> abduction;
                case TWIST -> rotation;
            };
        }
    }

    /** A little every way, on a disc: bending forward and back, to either side, twisting. */
    record Disc(Range flexion, Range side, Range twist) implements Motion {

        @Override
        public List<Movement> movements() {
            return List.of(Movement.FLEXION, Movement.SIDE_BEND, Movement.TWIST);
        }

        @Override
        public Range range(final Movement m) {
            return switch (m) {
                case FLEXION -> flexion;
                case SIDE_BEND -> side;
                case TWIST -> twist;
            };
        }
    }
}
