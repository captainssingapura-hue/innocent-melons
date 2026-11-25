package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Profiled;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a suspension link is forged, as the current large SUV's are, from aluminium: a beam of I section, its web
 * upright and its flanges top and bottom, bowed as the link must bow to clear what is round it, running between its
 * eyes; each eye a bored boss its bush is pressed into, its bore along the link's bolt, level and square to the link.
 * And the spring's link, the stoutest: a tray forged flat with its rims turned up, the spring's cup and holes in its
 * floor, an eye inboard and a fork outboard.
 *
 * @param round how many points a whole round is drawn in
 */
record LinkForms(int round) {

    /** An eye: how long its boss is along its bore, how far round outside and in its bore. */
    record Eye(double length, double outer, double bore) {
    }

    /** A beam's I section: how wide its flanges, how high over them, how thick its web and its flanges. */
    record Beam(double wide, double high, double web, double flange) {
    }

    /** The way a link's bolt runs from {@code inner} out to {@code outer}: level, square to the link. */
    Vec3 bolt(final Vec3 inner, final Vec3 outer) {
        return new Vec3(0, 1, 0).cross(outer.minus(inner)).unit();
    }

    /**
     * A link from its eye at {@code inner} to its eye at {@code outer}, bowed {@code bow} off the straight at its
     * middle, its beam of {@code beam}.
     */
    Mesh link(final Vec3 inner, final Vec3 outer, final Vec3 bow, final Eye in, final Eye out, final Beam beam) {
        final Vec3 bolt = bolt(inner, outer);
        return new Shapes(round).joined(List.of(
                beam(inner, in.outer(), outer, out.outer(), bow, beam),
                new Sleeve(inner, bolt, in.length(), in.outer(), in.bore(), round).mesh(),
                new Sleeve(outer, bolt, out.length(), out.outer(), out.bore(), round).mesh()));
    }

    /**
     * A beam from the eye at {@code from}, {@code fromReach} round, to the one at {@code to}, {@code toReach} round,
     * bowed {@code bow} at its middle, each end let into its eye.
     */
    Mesh beam(final Vec3 from, final double fromReach, final Vec3 to, final double toReach, final Vec3 bow,
              final Beam beam) {
        final Vec3 middle = from.plus(to).times(0.5).plus(bow);
        final Vec3 start = from.plus(middle.minus(from).unit().times(fromReach - 0.4));
        final Vec3 end = to.plus(middle.minus(to).unit().times(toReach - 0.4));
        final List<Spot> section = new Outlines(round).ibeam(beam.wide(), beam.high(), beam.web(), beam.flange());
        return new Profiled(new Course(List.of(start, middle, end), 8), List.of(section, section, section)).mesh();
    }

    /**
     * The spring's link, from its eye at {@code inner} to its fork at {@code outer}, the spring's cup at {@code seat}:
     * a tray {@code wide} at its widest, its eye as {@code eye} says.
     */
    Mesh springLink(final Vec3 inner, final Vec3 outer, final Vec3 seat, final Eye eye, final double wide) {
        final Vec3 along = outer.minus(inner).unit();
        final Vec3 bolt = bolt(inner, outer);
        final Vec3 up = along.cross(bolt).unit();
        final double length = outer.minus(inner).length();
        final double s = seat.minus(inner).dot(along);
        final double half = wide / 2;
        final List<Spot> outline = List.of(new Spot(eye.outer() - 0.6, -eye.length() / 2),
                new Spot(0.3 * length, -half * 0.85), new Spot(s, -half), new Spot(length - 6, -3.2),
                new Spot(length - 4, -3.2), new Spot(length - 4, 3.2), new Spot(length - 6, 3.2),
                new Spot(s, half), new Spot(0.3 * length, half * 0.85), new Spot(eye.outer() - 0.6, eye.length() / 2));
        final Outlines outlines = new Outlines(round);
        final List<List<Spot>> holes = List.of(outlines.circle(new Spot(s, 0), 2.2),
                outlines.circle(new Spot(s - 4.8, 0), 0.8), outlines.circle(new Spot(s + 4.8, 0), 0.8),
                outlines.slot(new Spot(0.32 * length, 0), 6, 2.6));
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(outline, holes, inner, along, bolt, 0.8).mesh());
        for (final int side : List.of(0, 5)) {
            for (int k = side; k < side + 4; k++) {
                made.add(rim(inner, along, bolt, up, outline.get(k), outline.get(k + 1)));
            }
        }
        made.add(new Sleeve(inner, bolt, eye.length(), eye.outer(), eye.bore(), round).mesh());
        made.add(new Sleeve(seat.plus(up.times(0.8)), up, 1.6, 7, 6.2, round).mesh());
        made.add(new Clevis(outer, outer.minus(along.times(5)), bolt, 4.6, 1.0, 2.6, 0.8, 5.5, round).mesh());
        return new Shapes(round).joined(made);
    }

    /** A rim turned up along the tray's edge from {@code a} to {@code b}, drawn as its floor is. */
    private Mesh rim(final Vec3 origin, final Vec3 along, final Vec3 across, final Vec3 up, final Spot a,
                     final Spot b) {
        final Vec3 from = origin.plus(along.times(a.u())).plus(across.times(a.v()));
        final Vec3 to = origin.plus(along.times(b.u())).plus(across.times(b.v()));
        final double length = to.minus(from).length();
        return new Plate(List.of(new Spot(0, -0.4), new Spot(length, -0.4), new Spot(length, 2.2),
                new Spot(0, 2.2)), List.of(), from, to.minus(from), up, 0.7).mesh();
    }
}
