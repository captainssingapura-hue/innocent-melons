package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The shell's rear, behind the rear seat: each rear rail from under the seat's pan up over the rear subframe's front
 * mount, down over its back mount and on to the tail; each rear tower over the axle, its top holding the damper's,
 * the spring's cup under it; each rear wheelhouse, its inner wall just inside the tyre over the links and its arch
 * over it out to the body side; the floor over the axle, the riser down from it and the boot's floor behind, out to
 * the body sides behind the wheelhouses; a pad over each rear subframe mount.
 *
 * @param lines  where the shell runs
 * @param body   the chassis's lines, its rails over the subframes' mounts
 * @param sheets the sheet it is pressed from
 */
record ShellRear(ShellLines lines, BodyLines body, Sheets sheets) {

    private static final Section RAIL = new Section(8, 12, 8);
    /** How far round the wheelhouses go, in degrees from ahead; how finely their walls are drawn. */
    private static final double ARCH_FROM = -15;
    private static final double ARCH_TO = 195;
    private static final int STEPS = 24;
    /** How far inside the body sides' faces the arches and the boot's floor stop. */
    private static final double INSIDE = 2;

    /** Its panels and members. */
    List<Mesh> meshes() {
        final Shapes shapes = new Shapes(16);
        final Towers towers = new Towers(body.front(), body.rear(), sheets.skin());
        final Proportions p = lines.proportions();
        final double wall = lines.wall();
        final double out = lines.half(p.back(), p.tyre() + lines.reach()) - INSIDE;
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(1, -1)) {
            made.add(sheets.member(sheets.sided(body.rearRail(), side), RAIL));
            made.addAll(towers.rear(side));
            made.add(sheets.upright(side * wall, wheelhouse()));
            made.add(shapes.band(new Vec3(side * (wall + out) / 2, p.tyre(), p.back()), lines.reach(), sheets.skin(),
                    out - wall, ARCH_FROM, ARCH_TO));
            for (final Vec3 mount : List.of(body.rear().mountFront(), body.rear().mountBack())) {
                made.add(new Sleeve(new Vec3(side * mount.x(), mount.y() + 5, mount.z()), new Vec3(0, 1, 0), 2, 5, 1,
                        16).mesh());
            }
        }
        made.add(sheets.level(lines.overAxle(), -wall, lines.seatWall(), wall, lines.step()));
        made.add(sheets.across(lines.step(), -wall, lines.boot(), wall, lines.overAxle()));
        made.add(sheets.level(lines.boot(), boot()));
        return made;
    }

    /**
     * A rear wheelhouse's inner wall: round inside the top of the arch, closed along its foot just under the damper's
     * top, the links and the wheel's carrier passing under it; along and up.
     */
    private List<Spot> wheelhouse() {
        final Proportions p = lines.proportions();
        final double foot = body.rear().damperTop().y() - 2;
        final double from = Math.toDegrees(Math.asin((foot - p.tyre()) / lines.reach()));
        final List<Spot> out = new ArrayList<>();
        for (int k = 0; k <= STEPS; k++) {
            final double a = Math.toRadians(from + (180 - 2 * from) * k / STEPS);
            out.add(new Spot(p.back() + lines.reach() * Math.cos(a), p.tyre() + lines.reach() * Math.sin(a)));
        }
        return out;
    }

    /** The boot's floor, between the wheelhouses' walls, then out to the body sides behind them; across and along. */
    private List<Spot> boot() {
        final double wall = lines.wall();
        final double behind = lines.proportions().back() - lines.reach();
        final double out = lines.half(lines.tail(), lines.boot()) - INSIDE;
        final double step = lines.step();
        final double tail = lines.tail();
        return List.of(new Spot(-wall, step), new Spot(wall, step), new Spot(wall, behind), new Spot(out, behind),
                new Spot(out, tail), new Spot(-out, tail), new Spot(-out, behind), new Spot(-wall, behind));
    }
}
