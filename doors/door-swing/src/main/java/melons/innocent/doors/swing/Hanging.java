package melons.innocent.doors.swing;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * How a leaf hangs in its doorway: where a place on the leaf is in the door's own frame, where its hinges' line
 * is, and how the leaf is turned about that line when it has swung so far open. A place on the leaf is said from
 * its hinge edge outward, from its foot upward, and from the middle of its thickness toward its back, whichever
 * side it is hung on; so a design is laid out once, and hangs either way.
 *
 * <p>The door's own frame: the middle of the doorway's floor where nothing is; rightward across the opening as seen
 * from the front; up; and through the wall, away from the front. Shut, the leaf lies across the opening with the
 * middle of its thickness where the front and back are told apart.
 *
 * @param size  how big the leaf is
 * @param swing how it swings
 */
public record Hanging(Size size, Swing swing) {

    /** How far the leaf's foot is over the floor, in centimetres. */
    private static final double CLEARS = 0.8;

    /** How far the leaf's foot is over the floor. */
    public double clears() {
        return CLEARS;
    }

    /** Where on the door the place so far {@code out} from the hinge edge, {@code up} and {@code back} is, shut. */
    public Vec3 at(final double out, final double up, final double back) {
        return new Vec3(swing.runs() * (out - size.wide() / 2), CLEARS + up, back);
    }

    /** A spot on the hinges' line, at the floor: at the leaf's hinge edge, on the face it opens toward. */
    public Vec3 hinge() {
        return new Vec3(-swing.runs() * size.wide() / 2, 0, swing.side() * size.thick() / 2);
    }

    /** How far the leaf is turned about the upright, as the drawing counts a turn, when so many degrees open. */
    public double turned(final double degrees) {
        return -swing.runs() * swing.side() * degrees;
    }

    /** What takes the leaf from where it is shut to where it is when so many {@code degrees} open. */
    public Mat4 swung(final double degrees) {
        return new Turned(new Vec3(0, 1, 0), turned(degrees)).matrix(hinge());
    }
}
