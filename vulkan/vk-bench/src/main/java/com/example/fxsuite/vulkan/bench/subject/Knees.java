package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.sim.Body;

/**
 * How far each knee of a figure is bent, in degrees, for a body as the engine's simulation has it. The engine's pose
 * swings the legs and says nothing of knees, so they are worked out here, from the same stride the engine swings the
 * legs by ({@code Body.phase}): through its swing, as the leg passes forward under the body with its foot off the
 * ground, a knee bends most, the more the faster the body goes; while its foot bears the body it bends a little; and
 * standing it is nearly straight. In a jump one knee is raised and the other trails, and flying both trail a little,
 * as the engine's pose has the legs then. The figures are their author's, from general knowledge of a walk and a
 * run, not from a measured gait.
 *
 * @param left  how far the left knee is bent: nothing for straight
 * @param right how far the right knee is bent
 */
public record Knees(double left, double right) {

    /** Standing: the knees all but straight. */
    private static final double STANDING = 4;
    /** Bent most in the swing: so far walking, and so much more at a run. */
    private static final double SWING = 32;
    private static final double SWING_RUNNING = 72;
    /** Bent a little under the body's weight: so far walking, and so much more at a run. */
    private static final double LOADED = 7;
    private static final double LOADED_RUNNING = 14;
    private static final double TRAILING = 18;
    private static final double RAISED = 70;
    private static final double JUMP_TRAILING = 25;

    /** The knees of {@code body}. */
    public Knees(final Body body) {
        this(body.flying() ? TRAILING : body.grounded() ? new Stride(body).left() : RAISED,
                body.flying() ? TRAILING : body.grounded() ? new Stride(body).right() : JUMP_TRAILING);
    }

    /** The knees on foot: each by how far through its swing or its bearing its leg is. */
    private record Stride(Body body) {

        private static final double FULL = 1.5;
        /**
         * How far ahead of its passing under the body a leg's stride is taken to be when its knee bends: a knee
         * bends most just after its foot leaves the ground, with the thigh still behind, and bears most just after
         * its foot comes down, with the leg still ahead; in radians.
         */
        private static final double EARLY = 0.7;

        double left() {
            return bent(Math.cos(body.phase() + EARLY));
        }

        double right() {
            return bent(-Math.cos(body.phase() + EARLY));
        }

        /**
         * A knee whose leg is so far through its stride: {@code swinging} 1 as it passes forward under the body in
         * its swing, -1 as it passes under the body bearing it.
         */
        private double bent(final double swinging) {
            final double ground = body.groundSpeed();
            final double pace = Math.max(0, Math.min(1, ground / Body.RUN));
            final double ahead = ground > 1e-6 && body.speed() < 0 ? -1 : 1;
            final double going = Math.max(0, Math.min(1, pace / 0.1));
            final double forward = swinging * ahead;
            return STANDING + going * ((SWING + SWING_RUNNING * pace) * Math.pow(Math.max(0, forward), FULL)
                    + (LOADED + LOADED_RUNNING * pace) * Math.max(0, -forward));
        }
    }
}
