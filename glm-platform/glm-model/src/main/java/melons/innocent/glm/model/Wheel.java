package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.wheels.Rim;
import melons.innocent.vehiclelab.wheels.Tyre;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A wheel and its tyre, front or rear, as big as the car's figures say, held to the platform's range by them: the rim,
 * of the car's design ({@link Rim}), its barrel round the hub, its centre bolted to the hub's flange; and the tyre
 * seated on it, its outside on the ground under the axle. Each made in the wheel's own frame and set where the
 * corner's wheel is.
 *
 * @param proportions the car's figures
 * @param points      the corner's points
 * @param shapes      what it is shaped with
 * @param rim         its rim's design
 */
record Wheel(Proportions proportions, WheelEnd points, Shapes shapes, Rim rim) {

    /** What its parts are made of, and whether they are bare. */
    private static final Making WHEEL_MADE = new Making(new Made(Material.ALUMINIUM, false));
    private static final Making TYRE_MADE = new Making(new Made(Material.RUBBER, true));

    /** The wheel, bolted to the hub. */
    Part wheel() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("hub", points.hubFace());
        named.put("tyre", points.wheel());
        final double mount = points.hubFace().x() - points.wheel().x();
        return WHEEL_MADE.part(points.called() + "wheel", Group.WHEELS, true, named,
                placed(rim.mesh(proportions.wheel(), mount)));
    }

    /** The tyre, seated on the rim. */
    Part tyre() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("rim", points.wheel());
        return TYRE_MADE.part(points.called() + "tyre", Group.TYRES, true, named,
                placed(new Tyre(proportions.wheel(), shapes).mesh()));
    }

    /** {@code made} in the wheel's own frame, set where the corner's wheel is. */
    private Mesh placed(final Mesh made) {
        return new Merged(List.of(new Merged.Placed(made, new Mat4(points.wheel())))).mesh();
    }
}
