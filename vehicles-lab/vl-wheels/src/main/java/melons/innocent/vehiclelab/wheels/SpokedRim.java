package melons.innocent.vehiclelab.wheels;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The spoked rim, as the GLM platform's large cars wear: its barrel; its centre, a drum from the hub's face out to
 * its face; and ten round spokes, cast with it, out from its centre on its face to the barrel.
 *
 * @param shapes what it is shaped with
 */
public record SpokedRim(Shapes shapes) implements Rim {

    /** How many spokes; how far out its face is from the wheel's middle; its centre's reach; its spokes'. */
    private static final int SPOKES = 10;
    private static final double FACE = 5;
    private static final double CAP = 8;
    private static final double SPOKE = 1.4;

    @Override
    public Mesh mesh(final WheelSize size, final double mount) {
        final double reach = inside(size);
        final Vec3 face = new Vec3(Math.max(mount + 1, Math.min(FACE, barrel(size) / 2 - 2)), 0, 0);
        final List<Mesh> forms = new ArrayList<>();
        forms.add(shapes.band(new Vec3(0, 0, 0), reach, thick(), barrel(size), 0, 360));
        forms.add(shapes.drum(new Vec3(mount, 0, 0), face.plus(new Vec3(1, 0, 0)), CAP));
        for (int k = 0; k < SPOKES; k++) {
            final double turn = 2 * Math.PI * k / SPOKES;
            final Vec3 out = new Vec3(0, Math.sin(turn), Math.cos(turn));
            forms.add(shapes.capsule(face.plus(out.times(CAP - 1)), face.plus(out.times(reach)), SPOKE));
        }
        return shapes.joined(forms);
    }
}
