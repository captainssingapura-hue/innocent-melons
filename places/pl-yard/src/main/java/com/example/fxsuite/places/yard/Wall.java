package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A straight run of wall: from {@code from} to {@code to} along the foot of its outer face, which faces {@code out};
 * from {@code low} above the ground up to {@code high}; {@code thick} from its outer face to its inner. Everything
 * is in centimetres, up the way the second number grows.
 *
 * <p>{@code along} is how far round the building its outer face begins, for the surface laid on it: a place on the
 * wall's surface is so far round the building and so far up, so courses run level right round and a joint that
 * meets a corner goes on round it. Over the wall's top, down its ends and under it, the surface goes on the same
 * way, as if folded over the edge.
 *
 * <p>One run gives both what is drawn of it and what is in the way of whoever walks there, so the two cannot differ.
 */
public record Wall(Vec3 from, Vec3 to, Vec3 out, double low, double high, double thick, double along) {

    /** How long it is. */
    public double length() {
        return to.minus(from).length();
    }

    /** Its six faces added to {@code forming}: outer, inner, top, underside and two ends. */
    public void formed(final Forming forming) {
        final Vec3 run = to.minus(from).unit();
        final Vec3 in = out.times(-thick);
        final Vec3 up = new Vec3(0, 1, 0);
        final double far = along + length();
        final Vec3 foot = from.plus(up.times(low));
        final Vec3 head = from.plus(up.times(high));
        final Vec3 across = run.times(length());
        final Vec3 rise = up.times(high - low);
        // each face: a corner, its two sides, the way it faces, and where on the surface its corner and far corner are
        face(forming, foot, across, rise, out, along, low, far, high);
        face(forming, foot.plus(in), across, rise, out.times(-1), along, low, far, high);
        face(forming, head, across, in, up, along, high, far, high + thick);
        face(forming, foot, across, in, up.times(-1), along, low, far, low - thick);
        face(forming, foot, in, rise, run.times(-1), along, low, along - thick, high);
        face(forming, foot.plus(across), in, rise, run, far, low, far + thick, high);
    }

    /**
     * One flat face: from {@code corner} along {@code one} and along {@code other}, facing {@code faces}; on the
     * surface it runs from ({@code fromAlong}, {@code fromUp}) at its corner to ({@code toAlong}, {@code toUp}) at
     * the corner opposite, the first of each along {@code one} or {@code other}, whichever of the two runs level.
     */
    private void face(final Forming forming, final Vec3 corner, final Vec3 one, final Vec3 other, final Vec3 faces,
                      final double fromAlong, final double fromUp, final double toAlong, final double toUp) {
        // of a face's two sides, the one that does not climb carries how far along; on a level face, the first does
        final boolean firstLevel = Math.abs(one.y()) < Math.abs(other.y()) || other.y() == 0;
        final Vec3 alongOne = new Vec3(firstLevel ? toAlong - fromAlong : 0, firstLevel ? 0 : toUp - fromUp, 0);
        final Vec3 alongOther = new Vec3(firstLevel ? 0 : toAlong - fromAlong, firstLevel ? toUp - fromUp : 0, 0);
        final Vec3 on = new Vec3(fromAlong, fromUp, 0);
        final int first = forming.point(corner, faces, on);
        forming.point(corner.plus(one), faces, on.plus(alongOne));
        forming.point(corner.plus(one).plus(other), faces, on.plus(alongOne).plus(alongOther));
        forming.point(corner.plus(other), faces, on.plus(alongOther));
        forming.faced(first, first + 1, first + 2);
        forming.faced(first, first + 2, first + 3);
    }

    /** The run as what is in the way, for the engine's simulation: a box as long, as thick and as high as it is. */
    public Block inTheWay() {
        final Vec3 run = to.minus(from).unit();
        final Vec3 middle = from.plus(to).times(0.5).plus(out.times(-thick / 2));
        // the engine turns a block as it turns anything about the upright: its own length then runs this way
        final double turned = Math.toDegrees(Math.atan2(-run.z(), run.x()));
        return new Block(middle.x(), middle.z(), length() / 2, thick / 2, turned, low, high);
    }
}
