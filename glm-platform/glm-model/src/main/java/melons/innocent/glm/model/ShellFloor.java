package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The shell's floor, pressed and welded as the original's is: the front floor each side of the tunnel, from the
 * bulkhead to the rear seat's heel, the tunnel down the middle, the seats' two cross members across it; an inner sill
 * along each side, boxed by the body side outside it; under it, each front rail carried on back as a floor
 * longitudinal; the heel panel rising to the rear seat's pan; and the wall behind the rear seat, up to the floor over
 * the rear axle.
 *
 * @param lines  where the shell runs
 * @param sheets the sheet it is pressed from
 * @param rails  the front rails' lines, from the nose to the bulkhead
 */
record ShellFloor(ShellLines lines, Sheets sheets, List<Vec3> rails) {

    private static final Section SILL = new Section(6, 14, 8);
    private static final Section CROSS = new Section(7, 5, 8);
    private static final Section RAIL = new Section(8, 12, 8);
    private static final Section LONGITUDINAL = new Section(8, 7, 8);
    /** Where the seats' cross members run across the floor. */
    private static final List<Double> SEATS = List.of(-100.0, -165.0);

    /** Its panels and members. */
    List<Mesh> meshes() {
        final double floor = lines.floor();
        final double t = lines.tunnelX();
        final double in = lines.sill() - 2;
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(1, -1)) {
            made.add(sheets.level(floor, side * t, lines.bulkhead(), side * in, lines.heel()));
            made.add(sheets.upright(side * t, lines.bulkhead(), floor, lines.heel(), lines.tunnelY()));
            made.add(sheets.member(sheets.sided(List.of(new Vec3(lines.sill(), 31, lines.bulkhead()),
                    new Vec3(lines.sill(), 31, lines.arch(lines.proportions().back()))), side), SILL));
            for (final double z : SEATS) {
                made.add(sheets.member(sheets.sided(List.of(new Vec3(t, floor + 2.5, z), new Vec3(in, floor + 2.5,
                        z)), side), CROSS));
            }
            made.add(longitudinal(side));
        }
        made.add(sheets.level(lines.tunnelY(), -t, lines.bulkhead(), t, lines.heel()));
        made.add(sheets.across(lines.heel(), heel(in), List.of()));
        made.add(sheets.level(lines.seat(), -in, lines.heel(), in, lines.seatWall()));
        made.add(sheets.across(lines.seatWall(), -lines.wall(), lines.seat(), lines.wall(), lines.overAxle()));
        return made;
    }

    /** A front rail carried on under the floor from the bulkhead, flattening as it goes, to the seats' members. */
    private Mesh longitudinal(final int side) {
        final Vec3 end = rails.getLast();
        final List<Vec3> through = List.of(end, new Vec3(end.x() + 2, lines.floor() - 4, end.z() - 33),
                new Vec3(end.x() + 3, lines.floor() - 4.5, SEATS.getLast()));
        return sheets.member(sheets.sided(through, side), List.of(RAIL, LONGITUDINAL, LONGITUDINAL));
    }

    /** The heel panel, up from the floor to the rear seat's pan, over the tunnel's end. */
    private List<Spot> heel(final double in) {
        final double floor = lines.floor();
        final double seat = lines.seat();
        final double t = lines.tunnelX();
        final double top = lines.tunnelY();
        return List.of(new Spot(-in, floor), new Spot(in, floor), new Spot(in, seat), new Spot(t, seat),
                new Spot(t, top), new Spot(-t, top), new Spot(-t, seat), new Spot(-in, seat));
    }
}
