package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The steering gear, bolted to the front subframe's nodes, ahead of the axle: its housing, cast aluminium, bare, a
 * tube across the car with the rack in it, a boot at each end; the electric motor along it, off the rack's middle;
 * the pinion's neck rising to the column; and its lugs out to the nodes' bosses. The tie rods hang from the rack's
 * ends. Every figure an estimate.
 *
 * @param points the front axle's points
 * @param shapes what it is shaped with
 */
record SteeringGear(FrontPoints points, Shapes shapes) {

    private static final Making CAST = new Making(new Made(Material.ALUMINIUM, true));
    private static final double TUBE = 3.2;
    private static final double BOOT = 2.6;
    private static final double MOTOR = 4.8;

    /** It. */
    Part part() {
        final Vec3 end = points.rackEnd();
        final Vec3 left = new Vec3(-end.x(), end.y(), end.z());
        final Vec3 across = new Vec3(1, 0, 0);
        final List<Mesh> made = new ArrayList<>();
        made.add(shapes.drum(left.plus(across.times(5)), end.minus(across.times(5)), TUBE));
        for (final Vec3 tip : List.of(end, left)) {
            final Vec3 in = new Vec3(tip.x() * 0.8, tip.y(), tip.z());
            made.add(shapes.capsule(in, tip, BOOT));
        }
        final Vec3 motor = new Vec3(end.x() * 0.3, end.y() - 3.5, end.z() - 5);
        made.add(shapes.capsule(motor.minus(across.times(9)), motor.plus(across.times(9)), MOTOR));
        made.add(shapes.drum(new Vec3(-end.x() * 0.35, end.y(), end.z()), new Vec3(-end.x() * 0.35, end.y() + 14,
                end.z() - 6), 2.2));
        for (final int side : List.of(1, -1)) {
            final Vec3 boss = mirrored(points.steeringMount(), side);
            made.add(shapes.capsule(new Vec3(side * (end.x() - 9), end.y(), end.z()), boss, 1.8));
        }
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("node mount, right", points.steeringMount());
        named.put("node mount, left", mirrored(points.steeringMount(), -1));
        named.put("rack end, right", end);
        named.put("rack end, left", left);
        return CAST.part("steering gear", Group.STEERING, false, named, shapes.joined(made));
    }

    private Vec3 mirrored(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
