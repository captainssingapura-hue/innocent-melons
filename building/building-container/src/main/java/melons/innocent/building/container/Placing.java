package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Where a container stands: the middle of its floor's underside ({@code x}, {@code z}), {@code y} high; its length
 * along, or turned to lie across ({@code acrossX}). In its own frame, {@code u} is across its width and {@code v}
 * along its length, each from its middle; up is up from its underside. Every piece of it a box square to it.
 *
 * @param x       where its middle is, across
 * @param z       where, along
 * @param y       how high its underside is
 * @param acrossX whether its length lies across, rather than along
 */
record Placing(double x, double z, double y, boolean acrossX) {

    /** Where {@code u} across and {@code v} along it is, seen from above. */
    Vec2 at(final double u, final double v) {
        return acrossX ? new Vec2(x + v, z + u) : new Vec2(x + u, z + v);
    }

    /** A box of it from {@code u0} to {@code u1}, {@code v0} to {@code v1}, {@code y0} to {@code y1} up. */
    Part box(final Leaf<?> element, final String name, final Role role, final Finish finish, final double u0,
             final double u1, final double v0, final double v1, final double y0, final double y1) {
        return new Part(element, finish.on(solid(name, role, finish.material(), u0, u1, v0, v1, y0, y1)));
    }

    /** The same of glass. */
    Part glass(final Leaf<?> element, final String name, final double u0, final double u1, final double v0,
               final double v1, final double y0, final double y1) {
        return new Part(element, solid(name, Role.GLAZING, Material.GLASS, u0, u1, v0, v1, y0, y1));
    }

    private Solid solid(final String name, final Role role, final Material material, final double u0,
                        final double u1, final double v0, final double v1, final double y0, final double y1) {
        final Vec2 a = at(u0, v0);
        final Vec2 b = at(u1, v1);
        return Timbers.INSTANCE.block(name, role, material, Math.min(a.x(), b.x()), Math.max(a.x(), b.x()),
                Math.min(a.z(), b.z()), Math.max(a.z(), b.z()), y + y0, y + y1);
    }
}
