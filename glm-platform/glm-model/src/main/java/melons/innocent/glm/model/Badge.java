package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;

import java.util.List;
import java.util.Map;

/**
 * A badge, the maker's roundel ({@link Roundel}), fixed where it is worn: its disc, painted, on what holds it; the
 * ring and the letters, a bright metal, bare, standing proud of the disc, their metal whatever they are made of.
 * Facing ahead or behind as {@code facing} says, read the right way round from there.
 *
 * @param name   what it is called
 * @param middle where its middle is, on the face of what holds it
 * @param radius how far round it is
 * @param facing which way it faces along, ahead if more than nought
 */
record Badge(String name, Vec3 middle, double radius, double facing) {

    private static final Making PAINTED = new Making(new Made(Material.PLASTIC, false));
    private static final Making BRIGHT = new Making(new Made(Material.STEEL, true));
    /** How far the disc stands off what holds it. */
    private static final double OFF = 0.05;

    /** Its disc fixed to {@code holder} at its point {@code at}, and its ring and letters on the disc. */
    List<Connection> joined(final Part holder, final String at) {
        final Roundel roundel = new Roundel(radius);
        final double off = OFF + roundel.disc().points().stream().mapToDouble(Vec3::z).map(Math::abs).max()
                .orElseThrow();
        final Part disc = PAINTED.part(name + " disc", Group.BADGES, false, Map.of("holder", middle),
                placed(roundel.disc(), off));
        final Part relief = BRIGHT.part(name, Group.BADGES, false, Map.of("disc", middle),
                placed(roundel.relief(), off));
        return List.of(new Connection(name + " disc", holder, at, disc, "holder", Joining.BOLTED),
                new Connection(name, disc, "holder", relief, "disc", Joining.BOLTED));
    }

    /** {@code made} in the roundel's own frame, its face {@code off} out from the holder, set at its middle. */
    private Mesh placed(final Mesh made, final double off) {
        final double ahead = Math.signum(facing);
        final Mat4 turned = new Mat4(new Vec3(ahead, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, ahead));
        final Mat4 at = new Mat4(middle.plus(new Vec3(0, 0, ahead * off)));
        return new Merged(List.of(new Merged.Placed(made, at.times(turned)))).mesh();
    }
}
