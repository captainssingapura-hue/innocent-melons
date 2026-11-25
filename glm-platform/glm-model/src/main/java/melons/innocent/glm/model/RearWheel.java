package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A rear wheel and its tyre, as big as the car's figures say: the rim's barrel, as wide as the tyre, round the hub; its
 * centre bolted to the hub's flange, and {@link #SPOKES} spokes, cast with it, out from its cap on its face to the
 * rim's edge; and the tyre seated on the rim, wider than it is deep, its outside on the ground under the axle.
 *
 * @param proportions the car's figures
 * @param points      the rear axle's points
 * @param shapes      what it is shaped with
 */
record RearWheel(Proportions proportions, RearPoints points, Shapes shapes) {

    /** How many spokes; how thick the rim is; how far out its face is from the wheel's middle; its cap, how round. */
    private static final int SPOKES = 10;
    private static final double RIM = 2;
    private static final double FACE = 5;
    private static final double CAP = 8;
    private static final double SPOKE = 1.4;

    /** The wheel, bolted to the hub. */
    Part wheel() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("hub", points.hubFace());
        named.put("tyre", points.wheel());
        final Vec3 middle = points.wheel();
        final double wide = proportions.width() * 0.1 * 0.8;
        final double reach = proportions.rimRadius() - RIM;
        final Vec3 face = middle.plus(new Vec3(FACE, 0, 0));
        final List<Mesh> forms = new ArrayList<>();
        forms.add(shapes.band(middle, reach, RIM, wide, 0, 360));
        forms.add(shapes.drum(points.hubFace(), face.plus(new Vec3(1, 0, 0)), CAP));
        for (int k = 0; k < SPOKES; k++) {
            final double turn = 2 * Math.PI * k / SPOKES;
            final Vec3 out = new Vec3(0, Math.sin(turn), Math.cos(turn));
            forms.add(shapes.capsule(face.plus(out.times(CAP - 1)), face.plus(out.times(reach)), SPOKE));
        }
        return new Making().part("wheel", Group.WHEELS, true, named, shapes.joined(forms));
    }

    /** The tyre, seated on the rim. */
    Part tyre() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("rim", points.wheel());
        final double out = proportions.wall() / 2;
        return new Making().part("tyre", Group.TYRES, true, named, shapes.ring(points.wheel(),
                proportions.rimRadius() + out, proportions.width() * 0.1 / 2, out));
    }
}
