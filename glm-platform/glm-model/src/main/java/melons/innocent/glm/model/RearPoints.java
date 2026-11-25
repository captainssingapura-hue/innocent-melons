package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The points the rear axle is made to, on its right, in the platform's frame: across as x from its middle, up as y
 * from the ground, along as z from under the front axle, ahead. The back wheel's middle, over the ground by its tyre,
 * out by half the track, back by the wheelbase. Its five links each from the subframe, inboard, out to the wheel's
 * carrier, placed as the current large SUV's workshop manual draws them: high, the upper front link, the upper rear
 * link and the toe link, all three from the subframe's front, high, where its tube cross member's arms rise; low, the
 * lower front link, ahead of the axle, and the lower rear link behind it, which carries the spring and the damper's
 * foot and meets the carrier on a ball joint. The damper from the lower rear link up to its mount; the subframe's
 * mounts to the body, ahead of the axle high and behind it low. Every figure an estimate. Where a car's figures
 * change, the points on the wheel's carrier move with the wheel; those on the subframe and the body are the
 * platform's own.
 *
 * @param proportions the car's figures
 */
record RearPoints(Proportions proportions) implements WheelEnd {

    /** How high the springs' tops are over the wheel's middle. */
    private static final double MOUNTS = 21;
    /** Where the damper's foot is on the spring's link: how far out along it, how far over it and behind it. */
    private static final double DAMPER_ON = 0.72;
    private static final double DAMPER_OVER = 3;
    private static final double DAMPER_BEHIND = 4.5;

    /** The point {@code x} across, {@code up} over the wheel's middle, and {@code along} ahead of the back axle. */
    Vec3 at(final double x, final double up, final double along) {
        return new Vec3(x, proportions.tyre() + up, proportions.back() + along);
    }

    /** The same, {@code in} inboard of the wheel's middle. */
    private Vec3 out(final double in, final double up, final double along) {
        return at(proportions.halfTrack() - in, up, along);
    }

    @Override
    public String called() {
        return "";
    }

    /** Where each link meets the carrier: the upper links, the spring's link, the lower front link, the toe link. */
    @Override
    public Map<String, Vec3> carried() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("upper front", upperFrontOuter());
        named.put("upper rear", upperRearOuter());
        named.put("spring link", springOuter());
        named.put("lower front", lowerFrontOuter());
        named.put("toe link", toeOuter());
        return named;
    }

    /** The back wheel's middle. */
    @Override
    public Vec3 wheel() {
        return out(0, 0, 0);
    }

    /** Where the upper front link meets the carrier, and the subframe. */
    Vec3 upperFrontOuter() {
        return out(12, 20, 7);
    }

    Vec3 upperFrontInner() {
        return at(32, 14, 21);
    }

    /** Where the upper rear link meets the carrier, and the subframe. */
    Vec3 upperRearOuter() {
        return out(12, 21, -7);
    }

    Vec3 upperRearInner() {
        return at(39, 16, 24);
    }

    /** Where the spring's link meets the carrier, and the subframe. */
    Vec3 springOuter() {
        return out(8, -13, -5);
    }

    Vec3 springInner() {
        return at(31, -15, -10);
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
        return out(9, -12, 7);
    }

    Vec3 lowerFrontInner() {
        return at(30, -12, 5);
    }

    /** Where the toe link meets the carrier, and the subframe. */
    Vec3 toeOuter() {
        return out(10, 5, -10);
    }

    Vec3 toeInner() {
        return at(34, 9, 14);
    }

    /** Where the damper's foot is, on the spring's link behind its seat, and its top on the body. */
    Vec3 damperFoot() {
        final Vec3 on = springInner().plus(springOuter().minus(springInner()).times(DAMPER_ON));
        return on.plus(new Vec3(0, DAMPER_OVER, -DAMPER_BEHIND));
    }

    Vec3 damperTop() {
        final Vec3 foot = damperFoot();
        return new Vec3(foot.x() + 1, proportions.tyre() + 28, foot.z() - 2);
    }

    /** Where the hub turns on the carrier; where the brake's disc sits on the hub; where the wheel is bolted to it. */
    @Override
    public Vec3 hub() {
        return out(6, 0, 0);
    }

    @Override
    public Vec3 disc() {
        return out(3.5, 0, 0);
    }

    @Override
    public Vec3 hubFace() {
        return out(1, 0, 0);
    }

    /** Where the caliper is bolted to the carrier, behind the axle. */
    @Override
    public Vec3 caliper() {
        return out(7, 4, -13);
    }

    /**
     * The subframe's mounts to the body: ahead of the axle, high, at the ends of its tube cross member's arms, the
     * body's rails high over it; behind it, low, at the ends of its side members' goosenecks.
     */
    Vec3 mountFront() {
        return at(46, 18, 32);
    }

    Vec3 mountBack() {
        return at(45, -4, -31);
    }
}
