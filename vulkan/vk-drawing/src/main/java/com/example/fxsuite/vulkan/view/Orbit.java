package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A bench's way of looking: round a point, from so far off. {@code yaw} is how far round the point the view has
 * turned, in degrees, to the right as it grows; {@code pitch} how far down it looks, in degrees, from level; and
 * {@code distance} how far the eye is from the point, in centimetres. The arithmetic only: a window says what the
 * mouse did, and shows what comes of it. Dragged, carried and scrolled, it moves as the engine's own benches do.
 */
public record Orbit(Vec3 round, double yaw, double pitch, double distance) {

    /** No nearer level with the ground, or looking straight down, than this: past it, right and left lose meaning. */
    private static final double STEEPEST = 89;
    /** How many degrees the view turns for each unit the mouse is dragged: as the engine's own benches turn. */
    private static final double TURNED = 0.4;
    /** How far off the eye is when a drag carries the point looked at exactly as far as the mouse moves. */
    private static final double CARRIED = 900;
    /** How many times as far off one count of the wheel puts the eye, and how many counts there are to a notch. */
    private static final double SCROLLED = 1.0015;
    private static final double NOTCH = 40;

    public Orbit {
        if (!(distance > 0)) {
            throw new IllegalArgumentException("the eye is some way off what it looks at: " + distance);
        }
        if (Math.abs(pitch) > STEEPEST) {
            throw new IllegalArgumentException("looked at from no steeper than " + STEEPEST + " degrees: " + pitch);
        }
    }

    /** The camera there, seeing {@code fieldOfView} degrees from top to bottom, from {@code near} on. */
    public Camera camera(final double fieldOfView, final double near) {
        final double round = Math.toRadians(yaw);
        final double down = Math.toRadians(pitch);
        final Vec3 ahead = new Vec3(Math.sin(round) * Math.cos(down), -Math.sin(down),
                Math.cos(round) * Math.cos(down));
        final Vec3 right = new Vec3(0, 1, 0).cross(ahead).unit();
        final Vec3 up = ahead.cross(right);
        return new Camera(this.round.minus(ahead.times(distance)), right, up, ahead, fieldOfView, near);
    }

    /** Turned {@code round} degrees more to the right and {@code down} degrees more down, no steeper than it may be. */
    public Orbit turned(final double round, final double down) {
        return new Orbit(this.round, yaw + round, Math.clamp(pitch + down, -STEEPEST, STEEPEST), distance);
    }

    /**
     * Dragged by the mouse {@code across} and {@code down}, in the screen's own units: turned to the right as the
     * mouse goes right, and looking further down as it goes down.
     */
    public Orbit dragged(final double across, final double down) {
        return turned(across * TURNED, down * TURNED);
    }

    /**
     * Carried by the mouse {@code across} and {@code down}, in the screen's own units: the point looked at moved so
     * that what is seen follows the mouse, across the way the view faces and straight up and down, the further the
     * further off the eye is.
     */
    public Orbit carried(final double across, final double down) {
        final double each = distance / CARRIED;
        final double turn = Math.toRadians(yaw);
        final Vec3 right = new Vec3(Math.cos(turn), 0, -Math.sin(turn));
        return new Orbit(round.minus(right.times(across * each)).plus(new Vec3(0, down * each, 0)), yaw, pitch,
                distance);
    }

    /**
     * Scrolled {@code notches} of the wheel, nearer as the wheel is turned away: each notch as much nearer or
     * further in proportion, so it is fine close to and quick far off; and no nearer than {@code nearest} nor
     * further than {@code furthest}.
     */
    public Orbit scrolled(final double notches, final double nearest, final double furthest) {
        return new Orbit(round, yaw, pitch,
                Math.clamp(distance * Math.pow(SCROLLED, -NOTCH * notches), nearest, furthest));
    }

    /** {@code times} as far off. */
    public Orbit backed(final double times) {
        return new Orbit(round, yaw, pitch, distance * times);
    }
}
