package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A front corner's air strut and what it stands in, as the current large SUV's workshop manual has them: the shock
 * absorber's fork, forged aluminium, bare, its clevis bolted round the control arm's boss, its two legs rising either
 * side of the drive shaft to the collar the strut is clamped in; the damper, steel, coated, from the collar up into
 * the strut tower; and the air spring, rubber, round the damper's upper half, its top held in the tower. Every figure
 * an estimate.
 *
 * @param points the front axle's points
 * @param shapes what it is shaped with
 */
record FrontStrut(FrontPoints points, Shapes shapes) {

    private static final Making FORGED = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making DAMPER = new Making(new Made(Material.STEEL, false));
    private static final Making BELLOWS = new Making(new Made(Material.RUBBER, true));
    private static final int ROUND = 24;
    private static final Vec3 UP = new Vec3(0, 1, 0);
    /** How far apart the fork's legs straddle the drive shaft; how far up its clevis's foot is. */
    private static final double STRADDLE = 5.5;
    private static final double FOOT = 3.5;
    /** How far up the damper the air spring sits; how round the bellows, the damper and its rod. */
    private static final double SEAT_UP = 18;
    private static final double BELLOWS_ROUND = 7.5;
    private static final double BODY = 2.8;
    private static final double ROD = 1.1;

    /** The shock absorber's fork. */
    Part fork() {
        final Vec3 at = points.fork();
        final Vec3 top = points.forkTop();
        final Vec3 foot = at.plus(UP.times(FOOT));
        final Vec3 bolt = new LinkForms(ROUND).bolt(points.controlArmInner(), points.controlArmOuter());
        final Vec3 aside = new Vec3(0, 0, 1);
        final Section leg = new Section(2.6, 2.2, 2.4);
        final List<Mesh> made = new ArrayList<>();
        made.add(new Clevis(at, foot, bolt, 5.6, 1.2, 2.8, 0.8, 6, ROUND).mesh());
        for (final int side : List.of(1, -1)) {
            final Vec3 off = aside.times(side * STRADDLE);
            made.add(new Swept(new Course(List.of(foot.plus(UP.times(0.6)), foot.plus(UP.times(5)).plus(off),
                    top.minus(UP.times(5)).plus(off.times(0.9)), top.minus(UP.times(1.5)).plus(off.times(0.5))), 6),
                    List.of(leg, leg, leg, leg), ROUND).mesh());
        }
        made.add(new Sleeve(top, UP, 5, 4.6, 3.0, ROUND).mesh());
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("arm", at);
        named.put("strut", top);
        return FORGED.part("shock absorber fork", Group.DAMPERS, true, named, shapes.joined(made));
    }

    /** The damper, from the fork's collar up to the tower. */
    Part damper() {
        final Vec3 foot = points.forkTop();
        final Vec3 top = points.strutTop();
        final Vec3 seat = seat();
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("foot", foot);
        named.put("spring seat", seat);
        named.put("top", top);
        return DAMPER.part("front damper", Group.DAMPERS, true, named, shapes.joined(List.of(
                shapes.capsule(foot.minus(UP.times(2)), seat, BODY), shapes.drum(seat, top, ROD),
                shapes.drum(seat.minus(UP.times(1)), seat.plus(UP.times(0.6)), BELLOWS_ROUND - 1))));
    }

    /** The air spring, round the damper's upper half, its top held in the tower. */
    Part airSpring() {
        final Vec3 seat = seat();
        final Vec3 top = points.strutTop();
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("seat", seat);
        named.put("top", top);
        final Vec3 lift = top.minus(seat).unit().times(BELLOWS_ROUND);
        return BELLOWS.part("front air spring", Group.SPRINGS, true, named, shapes.joined(List.of(
                shapes.capsule(seat.plus(lift), top.minus(lift.times(1.2)), BELLOWS_ROUND),
                shapes.drum(top.minus(UP.times(3)), top, BELLOWS_ROUND - 1.5))));
    }

    private Vec3 seat() {
        final Vec3 foot = points.forkTop();
        return foot.plus(points.strutTop().minus(foot).unit().times(SEAT_UP));
    }
}
