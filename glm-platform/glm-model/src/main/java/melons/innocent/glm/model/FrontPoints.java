package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The points the front axle is made to, on its right, in the platform's frame: across as x from its middle, up as y
 * over the front wheel's middle, along as z from the front axle, ahead. The front subframe's: each main node about on
 * the axle's line, low, its back mount bushed to the body behind the axle; the two lower links' pivots on cast ears
 * under it, ahead of the axle and behind; the strut tube from the node's top forward and up to the front mount, a
 * casting bolted to the side of the body's front rail. Every figure an estimate scaled from photographs of the part;
 * the platform's own, whatever the car's figures.
 *
 * @param proportions the car's figures
 */
record FrontPoints(Proportions proportions) {

    /** The point {@code x} across, {@code up} over the front wheel's middle, and {@code along} ahead of the axle. */
    Vec3 at(final double x, final double up, final double along) {
        return new Vec3(x, proportions.tyre() + up, along);
    }

    /** The front wheel's middle. */
    Vec3 wheel() {
        return at(proportions.halfTrack(), 0, 0);
    }

    /** Where a main node stands: the middle of its cast web. */
    Vec3 node() {
        return at(36, -6, -6);
    }

    /** The subframe's back mount, a sleeve at the node's back, bushed to the body. */
    Vec3 mountBack() {
        return at(36, -6, -24);
    }

    /** The subframe's front mount, the casting at the strut tube's end, bolted to the side of the front rail. */
    Vec3 mountFront() {
        return at(42, 17, 52);
    }

    /** Where the lower links are bolted into the node's ears: the front lower link's, and the rear's. */
    Vec3 lowerFrontInner() {
        return at(31, -17, 5);
    }

    Vec3 lowerRearInner() {
        return at(32, -17, -15);
    }

    /** Where the lower links meet the wheel's carrier, for the ears' bolts to lie square to them. */
    Vec3 lowerFrontOuter() {
        return at(proportions.halfTrack() - 10, -13, 3);
    }

    Vec3 lowerRearOuter() {
        return at(proportions.halfTrack() - 10, -13, -6);
    }

    /** Where the extruded cross member meets the node, at its inner face. */
    Vec3 crossMember() {
        return at(35.4, -10, -12);
    }

    /** Where the strut tube is let into the node's top, and where into the front mount's spigot. */
    Vec3 strutFoot() {
        return at(36, 3, 3);
    }

    Vec3 strutHead() {
        return at(42, 10.4, 48.5);
    }
}
