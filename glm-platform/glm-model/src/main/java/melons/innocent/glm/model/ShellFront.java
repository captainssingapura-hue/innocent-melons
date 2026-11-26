package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;

import java.util.ArrayList;
import java.util.List;

/**
 * The shell's front, ahead of the bulkhead: each front rail from the nose over the front subframe's mounts, falling to
 * the floor; each strut tower over the front axle, its top holding the strut's and its upper links' bushes inside it;
 * each front wheel arch's inner skin round the tyre, clear of it however far it is steered; each apron's upper rail
 * from the radiator's support over the arch and the tower to the windscreen pillar's foot; and across the nose, the
 * radiator's support, a lower member between the rails' tips, an upper one between the aprons', and an upright each
 * side; a pad under each rail where the front subframe's back mount bolts up.
 *
 * @param lines  where the shell runs
 * @param body   the chassis's lines, its rails over the subframes' mounts
 * @param sheets the sheet it is pressed from
 */
record ShellFront(ShellLines lines, BodyLines body, Sheets sheets) {

    private static final Section RAIL = new Section(8, 12, 8);
    private static final Section APRON = new Section(6, 8, 8);
    private static final Section SUPPORT = new Section(6, 7, 8);
    /** How far in and out the arch's inner skin reaches. */
    private static final double ARCH_IN = 52;
    private static final double ARCH_OUT = 84;
    /** Where the radiator's support stands, along; how high its top member; how far out its uprights. */
    private static final double SUPPORT_Z = 78;
    private static final double SUPPORT_TOP = 70;
    private static final double SUPPORT_X = 30;

    /** Its panels and members. */
    List<Mesh> meshes() {
        final Shapes shapes = new Shapes(16);
        final Towers towers = new Towers(body.front(), body.rear(), sheets.skin());
        final Proportions p = lines.proportions();
        final Vec3 tip = body.frontRail().getFirst();
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(1, -1)) {
            made.add(sheets.member(sheets.sided(body.frontRail(), side), RAIL));
            made.addAll(towers.front(side));
            made.add(shapes.band(new Vec3(side * (ARCH_IN + ARCH_OUT) / 2, p.tyre(), 0), lines.reach(), sheets.skin(),
                    ARCH_OUT - ARCH_IN, -15, 195));
            made.add(sheets.member(sheets.sided(apron(), side), APRON));
            made.add(sheets.member(sheets.sided(List.of(new Vec3(SUPPORT_X, tip.y(), SUPPORT_Z + 1),
                    new Vec3(SUPPORT_X, SUPPORT_TOP, SUPPORT_Z)), side), SUPPORT));
            final Vec3 mount = body.front().mountBack();
            made.add(new Sleeve(new Vec3(side * mount.x(), mount.y() + 5, mount.z()), new Vec3(0, 1, 0), 2, 5, 1, 16)
                    .mesh());
        }
        made.add(sheets.member(List.of(new Vec3(-tip.x(), tip.y(), SUPPORT_Z + 1), new Vec3(tip.x(), tip.y(),
                SUPPORT_Z + 1)), SUPPORT));
        final Vec3 top = apron().getFirst();
        made.add(sheets.member(List.of(new Vec3(-top.x(), top.y(), top.z()), new Vec3(top.x(), top.y(), top.z())),
                SUPPORT));
        return made;
    }

    /** An apron's upper rail, from the radiator's support over the arch and the tower to the windscreen's pillar. */
    private List<Vec3> apron() {
        final Vec3 strut = body.front().strutTop();
        return List.of(new Vec3(58, SUPPORT_TOP, SUPPORT_Z - 2), new Vec3(66, strut.y() + 1, 30),
                new Vec3(68, strut.y() + 5, strut.z()), new Vec3(72, 90, -30),
                new Vec3(lines.half(lines.bulkhead(), 97) - 6, 97, lines.bulkhead() + 1));
    }
}
