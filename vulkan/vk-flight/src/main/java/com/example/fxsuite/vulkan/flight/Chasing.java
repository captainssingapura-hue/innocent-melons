package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;

/**
 * The camera that follows a flight, as one step leaves it: behind the figure and a little above, looking at its
 * head, swung round it so far (Q and E, at a quarter turn a second; C puts it behind again), looking so far up or
 * down (the mouse), and so far off (the wheel, nearer or further by a tenth a notch). It is stepped with the flight,
 * not by the clock, so a run kept and played again is seen the same. Drawn, its eye is kept above whatever is below
 * it as the figure's feet are, so it never looks out from inside a hill. In centimetres and degrees.
 *
 * @param round    how far it is swung round from behind the figure
 * @param down     how far down it looks, from level
 * @param distance how far off the figure's head it is
 */
public record Chasing(double round, double down, double distance) {

    /** As it begins: behind, looking down a little, a few metres off. */
    private static final double FIRST_DOWN = 12;
    private static final double FIRST_DISTANCE = 700;
    /** How fast Q and E swing it round, in degrees a second; how far up and down it may look. */
    private static final double SWING = 90;
    private static final double STEEPEST = 85;
    /** The nearest and the furthest it may be, and how much each notch of the wheel changes that. */
    private static final double NEAREST = 150;
    private static final double FURTHEST = 50_000;
    private static final double NOTCH = 0.9;
    private static final double FIELD = 48;
    private static final double NEAR = 10;
    private static final double CLEAR = 50;

    /** As it begins. */
    public Chasing() {
        this(0, FIRST_DOWN, FIRST_DISTANCE);
    }

    /** As {@code told} leaves it, {@code seconds} later. */
    public Chasing stepped(final Step told, final double seconds) {
        final double swung = told.behind() ? 0 : round + told.circling() * SWING * seconds;
        final double looked = Math.clamp(down - told.tilt(), -STEEPEST, STEEPEST);
        final double off = Math.clamp(distance * Math.pow(NOTCH, told.scrolled()), NEAREST, FURTHEST);
        return new Chasing(swung, looked, off);
    }

    /** Between {@code before} and this, {@code blend} of the way on. */
    public Chasing between(final Chasing before, final double blend) {
        return new Chasing(before.round + (round - before.round) * blend,
                before.down + (down - before.down) * blend,
                before.distance + (distance - before.distance) * blend);
    }

    /** The camera looking at {@code head} of a figure turned {@code yaw} degrees, its eye kept above {@code below}. */
    public Camera camera(final Vec3 head, final double yaw, final Below below) {
        final Camera orbit = new Orbit(head, yaw + round, down, distance).camera(FIELD, NEAR);
        final Vec3 eye = orbit.eye();
        final double lowest = below.under(eye.x(), eye.z()) + CLEAR;
        if (eye.y() >= lowest) {
            return orbit;
        }
        // raised over the hill it would be inside, still looking at the head
        final Vec3 raised = new Vec3(eye.x(), lowest, eye.z());
        final Vec3 ahead = head.minus(raised).unit();
        final Vec3 right = new Vec3(0, 1, 0).cross(ahead).unit();
        return new Camera(raised, right, ahead.cross(right), ahead, FIELD, NEAR);
    }
}
