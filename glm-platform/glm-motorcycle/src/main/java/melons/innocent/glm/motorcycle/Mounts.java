package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Part;

/**
 * Where a motorcycle's frames are bolted to its twin, which carries their loads between them: each cylinder head on
 * its outer face, away from the V, a little under its top, the front head's facing ahead and the back head's behind,
 * a lug out from it to a node clear of the head and its cam chain's tower; and the crankcase at its mount behind the
 * gearbox. Found from the engine's own parts, made where they go in its frame; the frame's sides as far out as clears
 * the timing cover on the left.
 *
 * @param figures   the bike's figures
 * @param front     the front cylinder's head
 * @param rear      the back cylinder's head
 * @param crankcase the crankcase
 * @param timing    the timing cover
 */
record Mounts(BikeFigures figures, Part front, Part rear, Part crankcase, Part timing) {

    /** How far under a head's top its mount is, down its cylinder; how far its node stands out past the head. */
    private static final double UNDER = 4;
    private static final double OUT = 5;
    /** How far past the timing cover the frame's sides stand. */
    private static final double CLEAR = 1.8;

    /** Where head {@code head}'s mount is on its outer face, across the crank, in the middle along it. */
    Vec3 face(final Part head) {
        return under(head).plus(outward(head).times(reach(head)));
    }

    /** Where head {@code head}'s node is, out past its mount, where the frame's tubes meet. */
    Vec3 node(final Part head) {
        return face(head).plus(outward(head).times(OUT));
    }

    /** How far out from the middle the trellis's sides are: past the timing cover on the left, the same on the right. */
    double sides() {
        final double left = timing.form().points().stream().mapToDouble(Vec3::z).min().orElseThrow();
        return figures.centre() - left + CLEAR;
    }

    /** Where the crankcase's mount behind the gearbox is. */
    Vec3 rearMount() {
        return crankcase.point("mount, right").at();
    }

    /** The way out of head {@code head}, square to its cylinder, away from the V. */
    Vec3 outward(final Part head) {
        final Vec3 up = up(head);
        final Vec3 n = new Vec3(up.y(), -up.x(), 0);
        return up.x() > 0 ? n : n.times(-1);
    }

    /** The place on head {@code head}'s cylinder's axis a little under its top, in the middle along the crank. */
    private Vec3 under(final Part head) {
        final Vec3 top = head.point("top").at();
        final Vec3 up = up(head);
        final double along = top.x() * up.x() + top.y() * up.y() - UNDER;
        return new Vec3(up.x() * along, up.y() * along, figures.centre());
    }

    /** How far out from its cylinder's axis head {@code head} reaches, away from the V. */
    private double reach(final Part head) {
        final Vec3 out = outward(head);
        return head.form().points().stream().mapToDouble(p -> p.x() * out.x() + p.y() * out.y()).max()
                .orElseThrow();
    }

    /** The way up head {@code head}'s cylinder. */
    private Vec3 up(final Part head) {
        final Vec3 top = head.point("top").at();
        return new Vec3(top.x(), top.y(), 0).unit();
    }
}
