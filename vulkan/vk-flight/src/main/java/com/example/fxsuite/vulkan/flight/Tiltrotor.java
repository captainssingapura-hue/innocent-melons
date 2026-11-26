package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A tiltrotor flown from the keyboard, as one step leaves it: where it stands (the ground under its wheels), how fast
 * it goes, which way it is turned, how far its nacelles are up and how far they are to go, its power, how far round
 * its rotors are, how many times faster than time it is flown, and whether it is seen from its cockpit. Its flying
 * is for the eye, not the engineer: its rotors push along its nacelles as hard as its power says; its wing holds it
 * up as the square of its speed ahead, all of its weight at its cruise; the air holds it back, ahead as its top speed
 * at full power says and much more sideways and up and down, so it hovers steadily; and it banks into its turns.
 * Kept half a metre over whatever is below its wheels, and its rotors' tips over it too.
 *
 * <p>The keys: W and S more and less power; the arrows down and up its nose, left and right its roll, which comes
 * back level when let go; A and D its heading; Q its nacelles up, E down, and 1, 2 and 3 (or the panel's HOVER,
 * CONVERT and PLANE) send them up, half way and down; C (or VIEW) the cockpit or behind; T (or TIME) one, four or
 * sixteen times as fast as time. Centimetres, seconds and degrees.
 *
 * @param at       where the ground under its wheels is
 * @param going    how fast it goes each way
 * @param attitude which way it is turned
 * @param tilt     how far its nacelles are up
 * @param aim      how far they are to go
 * @param power    how much of its power it uses, from nothing to all
 * @param turned   how far round its rotors are
 * @param faster   how many times faster than time it is flown: 1, 4 or 16
 * @param cockpit  whether it is seen from its cockpit
 */
public record Tiltrotor(Vec3 at, Vec3 going, Attitude attitude, double tilt, double aim, double power, double turned,
                        int faster, boolean cockpit) {

    private static final double G = 981;
    /** How hard its rotors push at full power, of its weight; how fast power, tilt, pitch, roll and heading change. */
    private static final double PUSH = 1.35;
    private static final double POWERING = 0.4;
    private static final double TILTING = 15;
    private static final double PITCHING = 25;
    private static final double ROLLING = 45;
    private static final double LEVELLING = 35;
    private static final double TURNING = 30;
    private static final double STEEPEST = 45;
    private static final double BANKED = 60;
    /** How much more the air holds it back sideways and up and down than ahead. */
    private static final double SIDEWAYS = 25;
    /** Its rotors' turns a minute, hovering and flying ahead. */
    private static final double HOVERING = 412;
    private static final double CRUISING = 333;
    private static final double CLEAR = 50;

    /** Standing at {@code at}, turned {@code heading}, nacelles up, its power idle. */
    public Tiltrotor(final Vec3 at, final double heading) {
        this(at, new Vec3(0, 0, 0), new Attitude(heading, 0, 0), 90, 90, 0, 0, 1, false);
    }

    /** As {@code told} leaves it, {@code seconds} of time later, flown as {@code frame} flies, over {@code below}. */
    public Tiltrotor stepped(final Step told, final double seconds, final Airframe frame, final Below below) {
        final double dt = seconds * faster;
        double aiming = aim;
        int quicker = faster;
        boolean inside = cockpit != told.behind();
        for (final String key : told.pressed()) {
            aiming = switch (key) {
                case "ONE", "HOVER" -> 90;
                case "TWO", "CONVERT" -> 60;
                case "THREE", "PLANE" -> 0;
                default -> aiming;
            };
            quicker = key.equals("T") || key.equals("TIME") ? (quicker == 16 ? 1 : quicker * 4) : quicker;
            inside = key.equals("VIEW") != inside;
        }
        aiming = Math.clamp(aiming + (held(told, "Q") - held(told, "E")) * TILTING * dt, 0, frame.tilted());
        final double tilted = toward(tilt, aiming, TILTING * dt);
        final double powered = Math.clamp(power + (held(told, "W") - held(told, "S")) * POWERING * dt, 0, 1);
        final Attitude turnedTo = steered(told, dt, frame);
        final Vec3 moving = moved(turnedTo, tilted, powered, dt, frame);
        Vec3 there = at.plus(moving.times(dt));
        Vec3 still = moving;
        final double lowest = below.under(there.x(), there.z()) + CLEAR + frame.belowWheels(tilted);
        if (there.y() < lowest) {
            there = new Vec3(there.x(), lowest, there.z());
            still = new Vec3(moving.x(), Math.max(0, moving.y()), moving.z());
        }
        final double rpm = CRUISING + (HOVERING - CRUISING) * tilted / 90;
        return new Tiltrotor(there, still, turnedTo, tilted, aiming, powered, (turned + rpm * 6 * dt) % 360, quicker,
                inside);
    }

    /** Its attitude, steered as {@code told} holds the keys, its roll and, hovering, its pitch coming back level. */
    private Attitude steered(final Step told, final double dt, final Airframe frame) {
        final double pitchKeys = held(told, "DOWN") - held(told, "UP");
        final double rollKeys = held(told, "RIGHT") - held(told, "LEFT");
        final double hovering = tilt / 90;
        double pitch = attitude.pitch() + pitchKeys * PITCHING * dt;
        if (pitchKeys == 0) {
            pitch = toward(pitch, 0, LEVELLING * hovering * dt);
        }
        double roll = attitude.roll() + rollKeys * ROLLING * dt;
        if (rollKeys == 0) {
            roll = toward(roll, 0, LEVELLING * dt);
        }
        // flying ahead it banks into its turn, as a wing holding it up round the turn does
        final double ahead = Math.max(0, going.dot(attitude.ahead()));
        final double banking = ahead > frame.cruise() / 4
                ? Math.toDegrees(G * Math.tan(Math.toRadians(attitude.roll())) / ahead) * (1 - hovering) : 0;
        final double heading = attitude.heading() + ((held(told, "D") - held(told, "A")) * TURNING + banking) * dt;
        return new Attitude(heading, Math.clamp(pitch, -STEEPEST, STEEPEST), Math.clamp(roll, -BANKED, BANKED));
    }

    /** How fast it goes after {@code dt} more seconds, pushed, held up and held back. */
    private Vec3 moved(final Attitude turnedTo, final double tilted, final double powered, final double dt,
                       final Airframe frame) {
        final double up = Math.toRadians(tilted);
        final Vec3 push = turnedTo.turned(new Vec3(0, Math.sin(up), Math.cos(up))).times(powered * PUSH * G);
        final double ahead = going.dot(turnedTo.ahead());
        final Vec3 lift = turnedTo.up().times(G * Math.max(0, ahead) * ahead / (frame.cruise() * frame.cruise()));
        // the air: ahead as its top speed at full power flying ahead says, and much more every other way
        final double drag = PUSH * G / (frame.top() * frame.top());
        final Vec3 along = turnedTo.ahead().times(ahead);
        final Vec3 across = going.minus(along);
        final Vec3 held = along.times(-drag * Math.abs(ahead)).plus(across.times(-drag * SIDEWAYS * across.length()))
                .plus(across.times(-1.5));
        final Vec3 pulled = push.plus(lift).plus(held).plus(new Vec3(0, -G, 0));
        return going.plus(pulled.times(dt));
    }

    /** {@code from} moved toward {@code to} by no more than {@code most}. */
    private double toward(final double from, final double to, final double most) {
        return from + Math.clamp(to - from, -most, most);
    }

    private double held(final Step told, final String key) {
        return told.held().contains(key) ? 1 : 0;
    }

    /** Between {@code before} and this, {@code blend} of the way on, as it is drawn. */
    public Tiltrotor between(final Tiltrotor before, final double blend) {
        final double round = before.turned + ((turned - before.turned + 540) % 360 - 180) * blend;
        return new Tiltrotor(before.at.plus(at.minus(before.at).times(blend)), going,
                attitude.between(before.attitude, blend), before.tilt + (tilt - before.tilt) * blend, aim,
                before.power + (power - before.power) * blend, round, faster, cockpit);
    }

    /** How fast it goes, all told. */
    public double speed() {
        return going.length();
    }
}
