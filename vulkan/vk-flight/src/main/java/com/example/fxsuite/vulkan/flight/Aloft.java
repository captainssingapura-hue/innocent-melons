package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A figure on its jets, as one step leaves it: where its feet are, which way round it is turned (0 along the world,
 * growing to the right), how fast it is going each way, which of its speeds it flies at, and how hard its jets are
 * working, for their flames. Always flying: nothing lands it. Each step it is turned as the step says, and eased
 * toward the way the held keys ask it to go (W and S ahead and back, A and D sideways, Space up, Control down, Shift
 * at a quarter speed), at the speed it flies at, which R raises and V lowers, each a doubling, from about 80 cm to
 * 100 m a second; and then lifted to {@link #clear} above whatever is below it, if it was lower. In the renderer's
 * frame, height counted up, centimetres, degrees and seconds.
 *
 * @param feet   where its feet are
 * @param yaw    which way round it is turned
 * @param going  how fast it is going, each way, centimetres a second
 * @param gear   which of its speeds it flies at, 0 the slowest
 * @param thrust how hard its jets are working, from nothing to all they can
 */
public record Aloft(Vec3 feet, double yaw, Vec3 going, int gear, double thrust) {

    /** How far above whatever is below it the feet are kept, at least: half a metre. */
    private static final double CLEAR = 50;
    /** How many speeds it has, and the fastest, centimetres a second; each slower one is half the one above it. */
    private static final int GEARS = 8;
    private static final double FASTEST = 10_000;
    /** How quickly it comes to the way it is asked to go: most of the way in this many seconds. */
    private static final double EASING = 0.35;
    /** How fast it climbs and sinks, of the speed it flies at; and how much slower Shift makes it. */
    private static final double CLIMB = 0.6;
    private static final double SLOW = 0.25;
    /** How hard the jets work hovering, and how much more going as fast as asked. */
    private static final double HOVERING = 0.35;

    public Aloft {
        if (gear < 0 || gear >= GEARS) {
            throw new IllegalArgumentException("a figure flies at one of its " + GEARS + " speeds, not " + gear);
        }
    }

    /** Hovering with its feet at {@code feet}, turned {@code yaw} degrees, at speed {@code gear}. */
    public Aloft(final Vec3 feet, final double yaw, final int gear) {
        this(feet, yaw, new Vec3(0, 0, 0), gear, HOVERING);
    }

    /** How far above whatever is below it the feet are kept, at least, in centimetres. */
    public double clear() {
        return CLEAR;
    }

    /** How fast it flies, at the speed it is set to, in centimetres a second. */
    public double speed() {
        return FASTEST / Math.pow(2, GEARS - 1 - gear);
    }

    /** The gear nearest {@code metres} a second, for a run told how fast to begin. */
    public int gearFor(final double metres) {
        int best = 0;
        for (int g = 0; g < GEARS; g++) {
            final double at = FASTEST / Math.pow(2, GEARS - 1 - g);
            if (Math.abs(Math.log(at / (metres * 100))) < Math.abs(Math.log(speedOf(best) / (metres * 100)))) {
                best = g;
            }
        }
        return best;
    }

    private double speedOf(final int g) {
        return FASTEST / Math.pow(2, GEARS - 1 - g);
    }

    /** As {@code told} leaves it, {@code seconds} later, kept above what {@code below} says lies there. */
    public Aloft stepped(final Step told, final double seconds, final Below below) {
        int shifted = gear;
        for (final String key : told.pressed()) {
            shifted = key.equals("R") ? Math.min(GEARS - 1, shifted + 1)
                    : key.equals("V") ? Math.max(0, shifted - 1) : shifted;
        }
        final double turned = yaw + told.turn();
        final double fast = speedOf(shifted) * (told.held().contains("SHIFT") ? SLOW : 1);
        final double round = Math.toRadians(turned);
        final Vec3 ahead = new Vec3(Math.sin(round), 0, Math.cos(round));
        final Vec3 right = new Vec3(Math.cos(round), 0, -Math.sin(round));
        Vec3 level = ahead.times(held(told, "W") - held(told, "S"))
                .plus(right.times(held(told, "D") - held(told, "A")));
        level = level.length() > 0 ? level.unit().times(fast) : level;
        final Vec3 asked = level.plus(new Vec3(0, (held(told, "SPACE") - held(told, "CONTROL")) * fast * CLIMB, 0));
        final Vec3 now = going.plus(asked.minus(going).times(Math.min(1, seconds / EASING)));
        Vec3 at = feet.plus(now.times(seconds));
        Vec3 still = now;
        final double lowest = below.under(at.x(), at.z()) + CLEAR;
        if (at.y() < lowest) {
            at = new Vec3(at.x(), lowest, at.z());
            still = new Vec3(now.x(), Math.max(0, now.y()), now.z());
        }
        final double working = HOVERING + (1 - HOVERING) * Math.min(1, asked.length() / Math.max(1, fast));
        return new Aloft(at, turned, still, shifted, working);
    }

    /** One where {@code key} is held, else nothing. */
    private double held(final Step told, final String key) {
        return told.held().contains(key) ? 1 : 0;
    }

    /** Between {@code before} and this, {@code blend} of the way on, as it is drawn. */
    public Aloft between(final Aloft before, final double blend) {
        return new Aloft(before.feet.plus(feet.minus(before.feet).times(blend)),
                before.yaw + (yaw - before.yaw) * blend, going, gear,
                before.thrust + (thrust - before.thrust) * blend);
    }
}
