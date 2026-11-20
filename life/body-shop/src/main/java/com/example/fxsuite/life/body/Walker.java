package com.example.fxsuite.life.body;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A humanoid walking or running at a speed asked for, as its gait has it ({@link Gait}), carried on by its feet: while
 * a foot is down it stays where it is, the body going on as far as the foot goes back under it, rolling from its heel
 * to its toes; in the air, at the speed asked. Its strides as often as carry it that fast ({@link #cadence}): as many a
 * second as its feet, down, go back under it a stride's worth at that speed. Off the ground between strides running,
 * as high as falling for so long would bring it down from.
 */
public final class Walker {

    /** How fast things fall, cm/s². */
    static final double GRAVITY = 981;
    /** Strides a second, at fewest and most. */
    static final double SLOWEST = 0.5;
    static final double FASTEST = 2.3;

    private final Proportions proportions;
    private final Map<Long, Double> strides = new HashMap<>();
    private double c;
    private double ahead;
    private double time;
    private Rig rig;
    private int down;

    public Walker(final Proportions proportions) {
        this.proportions = proportions;
        rig = new Rig(proportions, Gait.at(0, 0, 0, false));
    }

    /** How far round its stride, 0 to 1, the right heel striking at 0. */
    public double cycle() {
        return c;
    }

    /** How far it has gone ahead, cm. */
    public double ahead() {
        return ahead;
    }

    /** As it stands now, on the ground. */
    public Rig rig() {
        return rig;
    }

    /** Which foot is down, carrying it on: 1 the right, -1 the left, 0 neither. */
    public int down() {
        return down;
    }

    /** How high off the ground in its flight, cm. */
    public double flying(final double speed) {
        final double flight = Gait.flight(speed) / 2;
        if (flight <= 0) {
            return 0;
        }
        final double stance = Gait.stance(speed);
        final double half = c * 2 - Math.floor(c * 2);
        final double u = (half * 0.5 - stance) / flight;
        if (u <= 0 || u >= 1) {
            return 0;
        }
        final double seconds = flight / cadence(speed);
        final double high = GRAVITY * seconds * seconds / 8;
        return 4 * high * u * (1 - u);
    }

    /**
     * {@code seconds} on at {@code speed} cm/s: round its stride, and on as far as its foot down carries it; holding
     * something in both hands, if {@code holding}.
     */
    public void step(final double seconds, final double speed, final boolean holding) {
        time += Math.abs(seconds);
        final double f = speed < 1 ? 0 : cadence(speed);
        // backward, if the seconds are: walking back, its stride run the other way
        c += f * seconds;
        c -= Math.floor(c);
        final Rig before = rig;
        final int was = down;
        rig = new Rig(proportions, Gait.at(c, speed, time, holding));
        down = down(c, speed);
        if (down != 0 && down == was && seconds > 0) {
            ahead += carried(before, rig, down);
        } else {
            ahead += speed * seconds;
        }
    }

    /** Set at {@code cycle} of its stride, still, as it is at {@code speed}: to look at. */
    public void at(final double cycle, final double speed, final boolean holding) {
        c = cycle - Math.floor(cycle);
        rig = new Rig(proportions, Gait.at(c, speed, time, holding));
        down = down(c, speed);
    }

    /** Which foot carries it at {@code c}: the last to strike, while it is down. */
    static int down(final double c, final double speed) {
        final double stance = Gait.stance(speed);
        final double right = c - Math.floor(c);
        final double left = (right + 0.5) % 1;
        // the last to strike is the one whose strike is the nearer behind
        if (right < left) {
            return right < stance ? 1 : 0;
        }
        return left < stance ? -1 : 0;
    }

    /** How far the body goes on, from {@code was} to {@code is}, its {@code side} foot down: as far back as it goes. */
    static double carried(final Rig was, final Rig is, final int side) {
        final List<V3> then = was.contacts();
        final List<V3> now = is.contacts();
        // that foot's point lowest now, heel, ball or toe: it rolls over them
        final int from = side > 0 ? 0 : 5;
        int low = from;
        for (int k = from; k < from + 5; k++) {
            if (now.get(k).y() < now.get(low).y()) {
                low = k;
            }
        }
        return then.get(low).z() - now.get(low).z();
    }

    /** Strides a second at {@code speed}: as many as its feet, down, carry it that fast. */
    public double cadence(final double speed) {
        final double stride = stride(speed);
        final double carried = speed * (1 - Gait.flight(speed));
        return stride <= 1e-6 ? SLOWEST : Math.clamp(carried / stride, SLOWEST, FASTEST);
    }

    /** How far its feet, down, carry it in a stride at {@code speed}, cm: worked out once for every 10 cm/s. */
    public double stride(final double speed) {
        final long key = Math.round(speed / 10);
        return strides.computeIfAbsent(key, k -> measured(k * 10.0));
    }

    private double measured(final double speed) {
        final int n = 400;
        double sum = 0;
        Rig was = new Rig(proportions, Gait.at(0, speed, 0, false));
        int side = down(0, speed);
        for (int k = 1; k <= n; k++) {
            final double at = (double) k / n;
            final Rig is = new Rig(proportions, Gait.at(at, speed, 0, false));
            final int now = down(at, speed);
            if (now != 0 && now == side) {
                sum += carried(was, is, now);
            }
            was = is;
            side = now;
        }
        return sum;
    }
}
