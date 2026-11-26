package melons.innocent.vehiclelab.wheels;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

/**
 * A tyre at any size, in the wheel's own frame, its axle across, its middle at nought: seated on the rim where the rim
 * reaches, out to where it meets the ground, as wide as its size says, round in section as its wall bulges.
 *
 * @param size   how big it is
 * @param shapes what it is shaped with
 */
public record Tyre(WheelSize size, Shapes shapes) {

    /** It. */
    public Mesh mesh() {
        final double out = size.wall() / 2;
        return shapes.ring(new Vec3(0, 0, 0), size.rimRadius() + out, size.wide() / 2, out);
    }
}
