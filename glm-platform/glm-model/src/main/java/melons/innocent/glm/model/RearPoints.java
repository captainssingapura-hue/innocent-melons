package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The points the rear axle is made to, on its right, in the platform's frame: across as x from its middle, up as y
 * from the ground, along as z from under the front axle, ahead. The back wheel's middle, over the ground by its tyre,
 * out by half the track, back by the wheelbase. Its five links each from the subframe, inboard, out to the wheel's
 * carrier: two upper links, ahead of the axle and behind it; a lower link carrying the spring, about under it; a lower
 * link ahead; and the toe link, behind, which keeps the wheel straight. The damper from the carrier, behind the axle,
 * up to its mount; the subframe's mounts to the body, ahead of the axle and behind, on the rails. Where a car's figures
 * change, the points on the wheel's carrier move with the wheel; those on the subframe and the body are the
 * platform's own.
 *
 * @param proportions the car's figures
 */
record RearPoints(Proportions proportions) {

    /** How high the subframe's mounts are over the wheel's middle. */
    private static final double MOUNTS = 21;

    /** The point {@code x} across, {@code up} over the wheel's middle, and {@code along} ahead of the back axle. */
    private Vec3 at(final double x, final double up, final double along) {
        return new Vec3(x, proportions.tyre() + up, proportions.back() + along);
    }

    /** The same, {@code in} inboard of the wheel's middle. */
    private Vec3 out(final double in, final double up, final double along) {
        return at(proportions.halfTrack() - in, up, along);
    }

    /** The back wheel's middle. */
    Vec3 wheel() {
        return out(0, 0, 0);
    }

    /** Where the upper front link meets the carrier, and the subframe. */
    Vec3 upperFrontOuter() {
        return out(12, 20, 5);
    }

    Vec3 upperFrontInner() {
        return at(36, 18, 16);
    }

    /** Where the upper rear link meets the carrier, and the subframe. */
    Vec3 upperRearOuter() {
        return out(12, 20, -6);
    }

    Vec3 upperRearInner() {
        return at(36, 18, -18);
    }

    /** Where the spring's link meets the carrier, and the subframe. */
    Vec3 springOuter() {
        return out(9, -14, -2);
    }

    Vec3 springInner() {
        return at(28, -16, -4);
    }

    /** Where the spring sits on its link: three fifths of the way out. */
    Vec3 springSeat() {
        return springInner().plus(springOuter().minus(springInner()).times(0.6));
    }

    /** Where the spring's top meets the body, over its seat. */
    Vec3 springTop() {
        final Vec3 seat = springSeat();
        return new Vec3(seat.x(), proportions.tyre() + MOUNTS, seat.z());
    }

    /** Where the lower front link meets the carrier, and the subframe. */
    Vec3 lowerFrontOuter() {
        return out(9, -12, 8);
    }

    Vec3 lowerFrontInner() {
        return at(26, -14, 24);
    }

    /** Where the toe link meets the carrier, and the subframe. */
    Vec3 toeOuter() {
        return out(10, -4, -14);
    }

    Vec3 toeInner() {
        return at(30, -6, -20);
    }

    /** Where the damper meets the carrier, and the body. */
    Vec3 damperFoot() {
        return out(12, -8, -11);
    }

    Vec3 damperTop() {
        return out(18, 28, -9);
    }

    /** Where the hub turns on the carrier; where the brake's disc sits on the hub; where the wheel is bolted to it. */
    Vec3 hub() {
        return out(6, 0, 0);
    }

    Vec3 disc() {
        return out(3.5, 0, 0);
    }

    Vec3 hubFace() {
        return out(1, 0, 0);
    }

    /** Where the caliper is bolted to the carrier, behind the axle. */
    Vec3 caliper() {
        return out(7, 4, -13);
    }

    /** The subframe's mounts to the body, ahead of the axle and behind. */
    Vec3 mountFront() {
        return at(46, MOUNTS, 32);
    }

    Vec3 mountBack() {
        return at(46, MOUNTS, -34);
    }

    /** The subframe's lower rail, ahead and behind. */
    Vec3 railFront() {
        return at(32, -14, 26);
    }

    Vec3 railBack() {
        return at(32, -14, -24);
    }
}
