package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The points the front axle is made to, on its right, in the platform's frame: across as x from its middle, up as y
 * over the front wheel's middle, along as z from the front axle, ahead. Placed as the current large SUV's workshop
 * manual draws them. The subframe: each main node about on the axle's line, low, bolted to the body behind it; the
 * strut tube from the node's top forward to the front mount, a casting bolted to the body's front rail, which holds
 * the guide link's bush. The links: the control arm, from its ear under the node out to a ball joint in the wheel
 * bearing housing, carrying the shock absorber's fork; the guide link, from its bush in the front mount back and out
 * to its ball pin low in the housing, ahead of the axle; the two upper links, from the body's strut tower out to the
 * housing's top; the tie rod, from the steering gear's rack out to the housing's arm, ahead. The air strut stands in
 * the fork up into the tower. Every figure an estimate; the platform's own, whatever the car's figures.
 *
 * @param proportions the car's figures
 */
record FrontPoints(Proportions proportions) implements WheelEnd {

    /** How far out along the control arm its fork is; how far over the arm the strut stands in the fork. */
    private static final double FORK_ON = 0.68;
    private static final double FORK_UP = 21;

    /** The point {@code x} across, {@code up} over the front wheel's middle, and {@code along} ahead of the axle. */
    Vec3 at(final double x, final double up, final double along) {
        return new Vec3(x, proportions.tyre() + up, along);
    }

    /** The same, {@code in} inboard of the wheel's middle. */
    private Vec3 out(final double in, final double up, final double along) {
        return at(proportions.halfTrack() - in, up, along);
    }

    @Override
    public String called() {
        return "front ";
    }

    /** Where each link meets the housing. */
    @Override
    public Map<String, Vec3> carried() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("control arm", controlArmOuter());
        named.put("guide link", guideOuter());
        named.put("upper front", upperFrontOuter());
        named.put("upper rear", upperRearOuter());
        named.put("tie rod", tieRodOuter());
        return named;
    }

    /** Every link meets the front carrier on a ball pin: the upper links' studs down into it, the others' up. */
    @Override
    public Map<String, Vec3> axes() {
        final Map<String, Vec3> axes = new LinkedHashMap<>();
        for (final String name : carried().keySet()) {
            axes.put(name, new Vec3(0, name.startsWith("upper") ? -1 : 1, 0));
        }
        return axes;
    }

    /** The front carrier is forged, as its maker says. */
    @Override
    public boolean forged() {
        return true;
    }

    @Override
    public Vec3 wheel() {
        return out(0, 0, 0);
    }

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

    @Override
    public Vec3 caliper() {
        return out(7, 4, -13);
    }

    /** Where a main node stands: the middle of its cast web. */
    Vec3 node() {
        return at(36, -6, -6);
    }

    /** The subframe's back mount, a sleeve at the node's back, bolted to the body. */
    Vec3 mountBack() {
        return at(36, -6, -24);
    }

    /** The subframe's front mount, the casting at the strut tube's end, bolted to the body's front rail. */
    Vec3 mountFront() {
        return at(42, 8, 42);
    }

    /** Where the extruded cross member meets the node, at its inner face. */
    Vec3 crossMember() {
        return at(35.4, -10, -12);
    }

    /** Where the strut tube is let into the node's top, where it bends, and where into the front mount's spigot. */
    Vec3 strutFoot() {
        return at(36, 3, 3);
    }

    List<Vec3> strutBends() {
        return List.of(at(36.5, 3.5, 12), at(38.5, 4, 22), at(41, 2.5, 32));
    }

    Vec3 strutHead() {
        return at(42, 1.4, 38.5);
    }

    /** Where the steering gear is bolted to the node's boss. */
    Vec3 steeringMount() {
        return at(31.5, -1, 8.5);
    }

    /** The control arm: its ear under the node, its ball joint in the housing, its fork on it, the strut in the fork. */
    Vec3 controlArmInner() {
        return at(31, -17, 0);
    }

    Vec3 controlArmOuter() {
        return out(10, -14, 0);
    }

    Vec3 fork() {
        return controlArmInner().plus(controlArmOuter().minus(controlArmInner()).times(FORK_ON));
    }

    Vec3 forkTop() {
        return fork().plus(new Vec3(0, FORK_UP, 0));
    }

    /** Where the air strut's top is held in the tower. */
    Vec3 strutTop() {
        return new Vec3(fork().x() + 2, proportions.tyre() + 46, fork().z() - 1);
    }

    /** The guide link: its bush in the front mount, inboard of it, and its ball pin in the housing. */
    Vec3 guideInner() {
        return at(37.1, 6.5, 39.1);
    }

    Vec3 guideOuter() {
        return out(10, -13, 5);
    }

    /** The upper links: from the strut tower, ahead and behind, to the housing's top. */
    Vec3 upperFrontInner() {
        return at(54, 26, 12);
    }

    Vec3 upperFrontOuter() {
        return out(12, 22, 4);
    }

    Vec3 upperRearInner() {
        return at(54, 26, -12);
    }

    Vec3 upperRearOuter() {
        return out(12, 22, -4);
    }

    /** The tie rod: from the steering gear's rack end out to the housing's arm, ahead. */
    Vec3 rackEnd() {
        return at(29, -6, 12);
    }

    Vec3 tieRodOuter() {
        return out(10, -5, 13);
    }
}
