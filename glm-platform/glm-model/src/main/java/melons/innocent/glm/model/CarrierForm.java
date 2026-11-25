package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * How a wheel bearing housing is shaped, cast or forged from aluminium as the current large SUV's are: a ring round
 * the wheel bearing, its four bolts' bosses at its corners; a web, inboard of the ring, spread as far toward each arm
 * as the arms let it; an arm out from the ring to each link, ending in a fork round the link's bush where the link is
 * bushed, or in a boss its ball pin is drawn into where it meets on a ball joint; and two lugs for the caliper. A
 * wheel's axle runs across.
 *
 * @param points the corner's points
 * @param arm    an arm's section
 * @param round  how many points a whole round is drawn in
 */
record CarrierForm(WheelEnd points, Section arm, int round) {

    private static final Vec3 ACROSS = new Vec3(1, 0, 0);
    private static final Vec3 UP = new Vec3(0, 1, 0);
    /** The ring: how far round outside, how long across; the bolts' bosses, how far out and how round. */
    private static final double RING = 7.5;
    private static final double RING_LONG = 6;
    private static final double BOLTS = 6.2;
    private static final double BOLT_BOSS = 1.5;
    /** How thick the web is cast, and how far toward each arm's end it spreads. */
    private static final double WEB = 2.6;
    private static final double SPREAD = 0.7;

    /** It. */
    Mesh mesh() {
        final Vec3 hub = points.hub();
        final Vec3 ring = hub.minus(ACROSS.times(1.5));
        final List<Mesh> made = new ArrayList<>();
        made.add(new Sleeve(ring, ACROSS, RING_LONG, RING, RING - 2.6, round).mesh());
        for (final int y : List.of(1, -1)) {
            for (final int z : List.of(1, -1)) {
                made.add(new Sleeve(ring.plus(new Vec3(RING_LONG / 2, y * BOLTS, z * BOLTS)), ACROSS, 2,
                        BOLT_BOSS, 0.6, round).mesh());
            }
        }
        final List<Vec3> ends = new ArrayList<>();
        for (final Map.Entry<String, Vec3> c : points.carried().entrySet()) {
            final Vec3 axis = points.axes().get(c.getKey());
            final Vec3 end = ended(c.getValue(), axis);
            ends.add(end);
            made.add(arm(ring, end));
            made.add(Math.abs(axis.y()) > 0.9
                    ? new Sleeve(end, axis, 3.2, 2.6, 1.0, round).mesh()
                    : new Clevis(c.getValue(), end, axis, 5.2, 1.2, 2.8, 0.8, 6, round).mesh());
        }
        for (final int y : List.of(1, -1)) {
            final Vec3 lug = points.caliper().plus(UP.times(y * 4.5));
            ends.add(lug);
            made.add(arm(ring, lug));
            made.add(new Sleeve(lug, ACROSS, 3, 1.8, 0.7, round).mesh());
        }
        made.add(web(ring, ends));
        return new Shapes(round).joined(made);
    }

    /** Where an arm ends for the link meeting it at {@code at}: at the fork's foot, or the ball pin's boss. */
    private Vec3 ended(final Vec3 at, final Vec3 axis) {
        if (Math.abs(axis.y()) > 0.9) {
            return at.plus(axis.times(3.4));
        }
        final Vec3 toward = points.hub().minus(at);
        final Vec3 square = toward.minus(axis.times(toward.dot(axis))).unit();
        return at.plus(square.times(4.5));
    }

    /** An arm from the ring out to {@code end}. */
    private Mesh arm(final Vec3 ring, final Vec3 end) {
        final Vec3 way = end.minus(ring);
        final Vec3 level = new Vec3(0, way.y(), way.z());
        final Vec3 start = ring.plus(level.unit().times(RING * 0.6));
        return new Swept(new Course(List.of(start, end), 4), List.of(arm, arm), round).mesh();
    }

    /** The web: across the ring's inboard face, as far toward each arm's end as it spreads, round them all. */
    private Mesh web(final Vec3 ring, final List<Vec3> ends) {
        final List<Spot> spots = new ArrayList<>();
        for (final Vec3 end : ends) {
            final Vec3 off = end.minus(ring);
            spots.add(new Spot(off.z() * SPREAD, off.y() * SPREAD));
        }
        for (int k = 0; k < 8; k++) {
            final double a = 2 * Math.PI * k / 8;
            spots.add(new Spot(RING * Math.cos(a), RING * Math.sin(a)));
        }
        return new Plate(hull(spots), List.of(), ring.minus(ACROSS.times(RING_LONG / 2 - WEB / 2)),
                new Vec3(0, 0, 1), UP, WEB).mesh();
    }

    /** The smallest outline round {@code spots}, drawn round to the left. */
    private List<Spot> hull(final List<Spot> spots) {
        final List<Spot> sorted = new ArrayList<>(spots);
        sorted.sort((a, b) -> a.u() != b.u() ? Double.compare(a.u(), b.u()) : Double.compare(a.v(), b.v()));
        final List<Spot> lower = new ArrayList<>();
        for (final Spot p : sorted) {
            while (lower.size() >= 2 && lower.get(lower.size() - 2).turn(lower.getLast(), p) <= 0) {
                lower.removeLast();
            }
            lower.add(p);
        }
        final List<Spot> upper = new ArrayList<>();
        for (final Spot p : sorted.reversed()) {
            while (upper.size() >= 2 && upper.get(upper.size() - 2).turn(upper.getLast(), p) <= 0) {
                upper.removeLast();
            }
            upper.add(p);
        }
        lower.removeLast();
        upper.removeLast();
        lower.addAll(upper);
        return lower;
    }
}
