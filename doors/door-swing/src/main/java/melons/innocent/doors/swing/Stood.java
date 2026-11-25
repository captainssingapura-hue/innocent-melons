package melons.innocent.doors.swing;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.mechanism.Swung;

import java.util.ArrayList;
import java.util.List;

/**
 * A swing door standing somewhere: the middle of its doorway's floor at a spot in the world, the whole turned about
 * the upright. From the one description come where its fixed parts and its leaf are drawn, however far open the
 * leaf is; what of it is in the way of whoever walks there, the leaf as a thin wall turned as it has swung, the
 * jambs as posts and the head as a beam to walk under; where its leaf's middle is, for whoever reaches for it; and
 * whether someone stands where the leaf is about to swing, so that it waits for them.
 *
 * @param door   the door
 * @param at     where the middle of its doorway's floor is, in centimetres
 * @param turned how far it is turned about the upright, in degrees, as the drawing counts a turn
 */
public record Stood(SwingDoor<?> door, Vec3 at, double turned) {

    /** The least a leaf is counted as thick where it is in the way, to either side of its middle. */
    private static final double THIN = 3;
    /** How far past the leaf's edge, and how near ahead of it in degrees, someone is still in its way. */
    private static final double NEAR = 2;

    /** What takes the door's own frame to the world: where its fixed parts are drawn. */
    public Mat4 frame() {
        return new Mat4(at).times(new Turned(new Vec3(0, 1, 0), turned).matrix());
    }

    /** What takes the shut leaf to where it is in the world when it has swung as {@code swung} says. */
    public Mat4 leaf(final Swung swung) {
        return frame().times(door.hanging().swung(swung.degrees()));
    }

    /** Where the middle of the leaf is, a metre up, swung so: what someone reaches for. */
    public Vec3 reached(final Swung swung) {
        return taken(leaf(swung), door.hanging().at(door.size().wide() / 2, 100, 0));
    }

    /** What of it is in the way, the leaf swung as {@code swung} says. */
    public List<Block> inTheWay(final Swung swung) {
        final Hanging hanging = door.hanging();
        final Size size = door.size();
        final List<Block> blocks = new ArrayList<>();
        final Vec3 middle = taken(leaf(swung), hanging.at(size.wide() / 2, size.high() / 2, 0));
        blocks.add(new Block(middle.x(), middle.z(), size.wide() / 2, Math.max(size.thick() / 2, THIN),
                turned + hanging.turned(swung.degrees()), at.y() + hanging.clears(),
                at.y() + hanging.clears() + size.high()));
        if (door.design().framed()) {
            final Framing framing = new Framing(hanging);
            for (final int side : new int[] {-1, 1}) {
                final Vec3 jamb = taken(frame(), new Vec3(side * framing.aside(), 0, framing.back()));
                blocks.add(new Block(jamb.x(), jamb.z(), framing.jamb() / 2, framing.deep() / 2, turned, at.y(),
                        at.y() + framing.head()));
            }
            final Vec3 head = taken(frame(), new Vec3(0, 0, framing.back()));
            blocks.add(new Block(head.x(), head.z(), framing.aside() + framing.jamb() / 2, framing.deep() / 2,
                    turned, at.y() + framing.head(), at.y() + framing.head() + framing.jamb()));
        }
        return blocks;
    }

    /**
     * Whether someone at {@code spot}, taking up {@code room} to every side, stands where the leaf is about to
     * swing: within its reach, and just ahead of it the way it is going, wanted {@code open} or shut.
     */
    public boolean sweeps(final Swung now, final boolean open, final Vec3 spot, final double room) {
        final Hanging hanging = door.hanging();
        final double wanted = open ? door.swing().widest() : 0;
        final Vec3 own = taken(new Turned(new Vec3(0, 1, 0), -turned).matrix(), spot.minus(at));
        // from the hinges: along the shut leaf, and out the side it opens toward
        final double along = hanging.swing().runs() * (own.x() - hanging.hinge().x());
        final double out = hanging.swing().side() * (own.z() - hanging.hinge().z());
        final double far = Math.hypot(along, out);
        if (wanted == now.degrees() || far < 1 || far > door.size().wide() + room + NEAR
                || own.y() > hanging.clears() + door.size().high()) {
            return false;
        }
        final double round = Math.toDegrees(Math.atan2(out, along));
        final double ahead = wanted > now.degrees() ? round - now.degrees() : now.degrees() - round;
        return ahead > 0 && ahead < Math.toDegrees(Math.asin(Math.min(1, room / far))) + NEAR;
    }

    /** {@code point} taken by {@code by}. */
    private Vec3 taken(final Mat4 by, final Vec3 point) {
        return new Vec3(by.at(0, 0) * point.x() + by.at(0, 1) * point.y() + by.at(0, 2) * point.z() + by.at(0, 3),
                by.at(1, 0) * point.x() + by.at(1, 1) * point.y() + by.at(1, 2) * point.z() + by.at(1, 3),
                by.at(2, 0) * point.x() + by.at(2, 1) * point.y() + by.at(2, 2) * point.z() + by.at(2, 3));
    }
}
