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
 * The retro saloon's underbody, the root of its steel shell and of the platform under it: pressed steel panels and box
 * members spot-welded into one, as the original's is, sealed and coated underneath; its floor ({@link ShellFloor}),
 * its front ({@link ShellFront}) and its rear ({@link ShellRear}). Its points, both sides: every mount the chassis is
 * fixed to it by, where the platform puts them, so the axles and the engine bolt up to it as they would to the cast
 * body; where the body sides, the bulkhead and the tail panel are welded to it; and where the front wings bolt on.
 *
 * @param lines where the shell runs
 */
record SantanaShell(ShellLines lines) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    private static final double SKIN = 0.15;

    /** It, its own frame the platform's. */
    Part part() {
        final Proportions p = lines.proportions();
        final FrontPoints front = new FrontPoints(p);
        final RearPoints rear = new RearPoints(p);
        final BodyLines body = new BodyLines(p, lines.lines().figures(), front, rear);
        final Map<String, Vec3> named = new LinkedHashMap<>();
        for (final int side : List.of(1, -1)) {
            final String s = side > 0 ? ", right" : ", left";
            named.put("subframe mount, front" + s, sided(rear.mountFront(), side));
            named.put("subframe mount, back" + s, sided(rear.mountBack(), side));
            named.put("spring top" + s, sided(rear.springTop(), side));
            named.put("damper top" + s, sided(rear.damperTop(), side));
            named.put("front subframe mount, front" + s, sided(front.mountFront(), side));
            named.put("front subframe mount, back" + s, sided(front.mountBack(), side));
            named.put("upper front link" + s, sided(front.upperFrontInner(), side));
            named.put("upper rear link" + s, sided(front.upperRearInner(), side));
            named.put("strut top" + s, sided(front.strutTop(), side));
            named.put("body side" + s, new Vec3(side * lines.sill(), lines.floor(), lines.bulkhead()));
            final Vec3 wing = new FrontWing(lines.lines()).anchor();
            named.put("front wing" + s, new Vec3(side * wing.x(), wing.y(), wing.z()));
        }
        named.put("bulkhead", new Vec3(0, lines.floor(), lines.bulkhead()));
        named.put("tail panel", new Vec3(0, lines.boot(), lines.tail()));
        final Sheets sheets = new Sheets(SKIN);
        final List<Mesh> made = new ArrayList<>(new ShellFloor(lines, sheets, body.frontRail()).meshes());
        made.addAll(new ShellFront(lines, body, sheets).meshes());
        made.addAll(new ShellRear(lines, body, sheets).meshes());
        return PRESSED.root("underbody", Group.UNDERBODY, named, new Shapes(16).joined(made));
    }

    private Vec3 sided(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
