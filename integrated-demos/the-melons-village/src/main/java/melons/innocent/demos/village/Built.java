package melons.innocent.demos.village;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.building.walk.Doorless;

import java.util.List;

/**
 * A building of the buildings' own, built as its realizer builds it, standing in the village: its middle at
 * {@code stands}, turned {@code turned} degrees as a melon house is, so its front, toward along in its own frame,
 * faces where that turn has it. Drawn as finely as it is built, and in the way as its own shape is, each turned and
 * set down with it. Its doors left out, neither drawn nor in the way ({@link Doorless}). Set down {@link #LIFT} over
 * the ground: a floor laid where height is nothing, as a block's void deck is, would else be drawn where the
 * village's ground is, and the two would flicker, each drawn over the other by turns (the test kit draws its ground
 * as far under for the same reason).
 *
 * @param structure what was built
 * @param stands    where its middle stands
 * @param turned    how far it is turned, in degrees
 */
public record Built(Structure structure, Vec3 stands, double turned) {

    /** How far over the ground it is set down, in centimetres. */
    static final double LIFT = 2;

    /** What takes it, in its own frame, to where it stands: turned, and set down {@link #LIFT} over the ground. */
    public Mat4 placed() {
        return new Mat4(stands.plus(new Vec3(0, LIFT, 0))).times(new Turned(new Vec3(0, 1, 0), turned).matrix());
    }

    /** Its solids, in its own frame, to be drawn where {@link #placed} has them. */
    public List<Solid> solids() {
        return new Doorless(structure).solids();
    }

    /** What of it is in the way, where it stands. */
    public List<Volume> inTheWay() {
        final Mat4 placed = placed();
        return new Doorless(structure).shape().stream().map(v -> (Volume) moved((Prism) v, placed)).toList();
    }

    /** {@code prism} taken by {@code placed}, lifted with it. */
    private Prism moved(final Prism prism, final Mat4 placed) {
        return new Prism(moved(prism.outline(), placed), prism.holes().stream().map(h -> moved(h, placed)).toList(),
                prism.bottom() + LIFT, prism.top() + LIFT);
    }

    private List<Vec2> moved(final List<Vec2> ring, final Mat4 placed) {
        return ring.stream().map(p -> {
            final Vec3 at = placed.clip(new Vec3(p.x(), 0, p.z())).shown();
            return new Vec2(at.x(), at.z());
        }).toList();
    }
}
