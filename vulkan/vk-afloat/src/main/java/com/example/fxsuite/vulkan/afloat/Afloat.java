package com.example.fxsuite.vulkan.afloat;

import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.flight.Attitude;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A boat on the water, as one step leaves it: where it floats (its water's line), which way it heads, how fast it
 * goes ahead (less than nothing astern), its throttle and its wheel, how long it has been afloat (for its bobbing),
 * how many times faster than time it is driven, and whether it is seen from its helm. Driven for the eye, not the
 * naval architect: it comes to the speed its throttle asks slowly, as a boat does; it turns as its wheel is over and
 * the faster the faster it goes; it rides at the water's top, bobbing a little and heeling into its turns. It goes
 * only where there is water deep enough for its draught under both its middle and its bow: anywhere else it stops,
 * as a boat run aground does, and may back off.
 *
 * <p>The keys: W and S open and close the throttle (to half astern); A and D put the wheel over to the left and
 * right; Space puts it amidships; the panel's AHEAD, STOP and ASTERN set the throttle; C (or VIEW) the helm or
 * behind; T (or TIME) one, four or sixteen times as fast. Centimetres, seconds and degrees.
 *
 * @param at       where its water's line is, in its middle
 * @param heading  which way it heads, round from along the world, to the right as it grows
 * @param speed    how fast it goes ahead
 * @param throttle how far its throttle is open, from half astern to full ahead
 * @param wheel    how far its wheel is over, from all the way left to all the way right
 * @param afloat   how long it has been afloat, of its own time
 * @param faster   how many times faster than time it is driven: 1, 4 or 16
 * @param helm     whether it is seen from its helm
 */
public record Afloat(Vec3 at, double heading, double speed, double throttle, double wheel, double afloat,
                     int faster, boolean helm) {

    private static final double OPENING = 0.4;
    private static final double TURNING_WHEEL = 0.8;
    /** How quickly it comes to its speed: most of the way in so many seconds. */
    private static final double COMING = 5;
    /** How fast it turns with its wheel hard over at full speed, in degrees a second; and at least, under way. */
    private static final double TURNS = 9;
    private static final double STEERAGE = 0.2;
    private static final double BOB = 5;
    private static final double HEEL = 6;

    /** Floating at {@code at}, heading {@code heading}, still. */
    public Afloat(final Vec3 at, final double heading) {
        this(at, heading, 0, 0, 0, 0, 1, false);
    }

    /** As {@code told} leaves it, {@code seconds} of time later, driven as {@code vessel} is, on {@code waters}. */
    public Afloat stepped(final Step told, final double seconds, final Vessel vessel, final Waters waters) {
        final double dt = seconds * faster;
        double open = throttle + (held(told, "W") - held(told, "S")) * OPENING * dt;
        int quicker = faster;
        boolean seen = helm != told.behind();
        for (final String key : told.pressed()) {
            open = switch (key) {
                case "AHEAD" -> 0.6;
                case "STOP" -> 0;
                case "ASTERN" -> -0.3;
                default -> open;
            };
            quicker = key.equals("T") || key.equals("TIME") ? (quicker == 16 ? 1 : quicker * 4) : quicker;
            seen = key.equals("VIEW") != seen;
        }
        open = Math.clamp(open, -0.5, 1);
        double over = Math.clamp(wheel + (held(told, "D") - held(told, "A")) * TURNING_WHEEL * dt, -1, 1);
        over = held(told, "SPACE") > 0 ? 0 : over;
        final double wanted = open * vessel.top();
        double going = speed + (wanted - speed) * Math.min(1, dt / COMING);
        final double share = Math.abs(going) / vessel.top();
        final double turned = heading + over * TURNS * Math.max(share, going == 0 ? 0 : STEERAGE)
                * Math.signum(going) * dt;
        final Vec3 ahead = new Vec3(Math.sin(Math.toRadians(turned)), 0, Math.cos(Math.toRadians(turned)));
        Vec3 there = at.plus(ahead.times(going * dt));
        if (!floats(there, ahead, going, vessel, waters)) {
            there = at;
            going = 0;
        }
        final double top = waters.top(there.x(), there.z());
        final Vec3 floating = new Vec3(there.x(), Double.isNaN(top) ? at.y() : top, there.z());
        return new Afloat(floating, turned, going, open, over, afloat + dt, quicker, seen);
    }

    /** Whether at {@code there} it would have water deep enough under its middle and the end it goes toward. */
    private boolean floats(final Vec3 there, final Vec3 ahead, final double going, final Vessel vessel,
                           final Waters waters) {
        final Vec3 end = there.plus(ahead.times(Math.signum(going) * vessel.length() / 2));
        return waters.deep(there.x(), there.z()) >= vessel.draught()
                && waters.deep(end.x(), end.z()) >= vessel.draught();
    }

    private double held(final Step told, final String key) {
        return told.held().contains(key) ? 1 : 0;
    }

    /** Which way it is turned, bobbing and heeling as it floats. */
    public Attitude attitude() {
        // a boat heels out of its turn, to the left turning right
        final double heel = -wheel * Math.abs(speed) / Math.max(1, Math.abs(speed) + 300) * HEEL
                + Math.sin(afloat * 0.7) * 1.2;
        return new Attitude(heading, Math.sin(afloat * 0.9 + 1) * 0.8, heel);
    }

    /** Where it stands, bobbing on the water. */
    public Vec3 floating() {
        return at.plus(new Vec3(0, Math.sin(afloat * 1.3) * BOB, 0));
    }

    /** Between {@code before} and this, {@code blend} of the way on, as it is drawn. */
    public Afloat between(final Afloat before, final double blend) {
        return new Afloat(before.at.plus(at.minus(before.at).times(blend)),
                before.heading + (heading - before.heading) * blend, speed, throttle,
                before.wheel + (wheel - before.wheel) * blend, before.afloat + (afloat - before.afloat) * blend,
                faster, helm);
    }
}
