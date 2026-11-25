package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The rear subframe's lines, as its drawing gives them, after the current large SUV's welded steel subframe: where its
 * two hydroformed tubes run and how they are pressed out along the way, where its pressed cross member lies and what
 * is punched in it, where its sleeves are welded in and where the links' clevises stand. On the right; the left the
 * right's mirror. Every figure an estimate scaled from photographs of the part; the mounts and the links' pivots are
 * the rear axle's own points.
 *
 * @param points the rear axle's points
 */
record SubframeLines(RearPoints points) {

    /** How finely the tubes' lines are walked between their points; how many points round they are drawn in. */
    static final int STEPS = 8;
    static final int ROUND = 24;
    /** How far a tube's end stops short of the mount's axis, inside its sleeve's wall, clear of its bore. */
    private static final double SHORT = 3.7;
    /** How far a clevis's foot sinks into the tube it is welded to, from the tube's skin. */
    private static final double SUNK = 1.0;
    /** How high the pressed cross member's web lies over the wheel's middle. */
    private static final double WEB = -9;

    /** The back cross member's line, from its left mount round across to its right. */
    Course backCourse() {
        final List<Vec3> right = List.of(at(18, 8, -22), at(29, 8.8, -23), at(36, 12.5, -26.5),
                ended(at(36, 12.5, -26.5), points.mountBack()));
        final List<Vec3> through = new ArrayList<>(right.reversed().stream().map(this::mirrored).toList());
        through.add(at(0, 8, -22));
        through.addAll(right);
        return new Course(through, STEPS);
    }

    /** The back cross member's sections, at each point of its line: flatter at its ends, where it meets the eyes. */
    List<Section> backSections() {
        final List<Section> right = List.of(new Section(7, 7, 2.2), new Section(7, 7, 2.2), new Section(6.5, 6, 2.4),
                new Section(6, 5.5, 2.6));
        final List<Section> all = new ArrayList<>(right.reversed());
        all.add(new Section(7.5, 6.5, 2.4));
        all.addAll(right);
        return all;
    }

    /** A side member's line: from inside the back cross member, down and forward, and out round its gooseneck. */
    Course sideCourse() {
        return new Course(List.of(sideStart(), at(31.5, 3, -14), at(32, -4, -4), at(32, -8, 6), at(32.5, -9.5, 16),
                at(34.5, -9.4, 23.5), at(38.5, -7, 27.8), ended(at(38.5, -7, 27.8), points.mountFront())), STEPS);
    }

    /** Its sections: round down its length, pressed flatter round the gooseneck to its eye. */
    List<Section> sideSections() {
        return List.of(new Section(6.5), new Section(6.5), new Section(6.5), new Section(6.5), new Section(6.5),
                new Section(6.2, 6, 2.3), new Section(6, 5.8, 2.4), new Section(6, 5.5, 2.6));
    }

    /** Where a side member starts, inside the back cross member. */
    Vec3 sideStart() {
        return at(31, 8.8, -22.8);
    }

    /** How far round a tube is, about. */
    double tubeRadius() {
        return 3.25;
    }

    /** Where the pressed cross member's web lies, its middle. */
    Vec3 webMiddle() {
        return at(0, WEB, 0);
    }

    /** Where a side member meets the pressed cross member, at its end. */
    Vec3 webEnd() {
        return at(32.5, WEB, 20);
    }

    /** The pressed cross member's web, drawn from above, across and along, from the web's middle. */
    List<Spot> webOutline() {
        return List.of(new Spot(-33, 11.5), new Spot(-26, 12.5), new Spot(-18, 15), new Spot(18, 15),
                new Spot(26, 12.5), new Spot(33, 11.5), new Spot(33, 27.5), new Spot(26, 27), new Spot(18, 25.5),
                new Spot(-18, 25.5), new Spot(-26, 27), new Spot(-33, 27.5));
    }

    /** Where the differential's front mounts are punched through the web, drawn as its outline is. */
    Spot diffFront() {
        return new Spot(12, 20.2);
    }

    /** The lightening holes punched in the web, right and left of its slot. */
    Spot lightening() {
        return new Spot(22.5, 20);
    }

    Spot slot() {
        return new Spot(0, 20.2);
    }

    /** How deep the web's flanges are turned down at its edges. */
    double flange() {
        return 3;
    }

    /** Where the differential's back mount stands, through the back cross member's middle. */
    Vec3 diffBack() {
        return at(0, 8, -22);
    }

    /** Where the differential's front mount stands on the right, through the web. */
    Vec3 diffFrontAt() {
        return at(diffFront().u(), WEB, diffFront().v());
    }

    /** Where a clevis's foot is, on {@code course}, for its link's pivot at {@code pivot}. */
    Vec3 foot(final Course course, final Vec3 pivot) {
        final Vec3 middle = course.nearest(pivot);
        return middle.plus(pivot.minus(middle).unit().times(tubeRadius() - SUNK));
    }

    /** The way a link's bolt runs at its pivot: level, and square to the link. */
    Vec3 bolt(final Vec3 inner, final Vec3 outer) {
        final Vec3 way = outer.minus(inner);
        return new Vec3(0, 1, 0).cross(way).unit();
    }

    private Vec3 at(final double x, final double up, final double along) {
        return points.at(x, up, along);
    }

    /** The end of a tube coming from {@code from} to a mount's eye at {@code eye}: short of its axis, level with it. */
    private Vec3 ended(final Vec3 from, final Vec3 eye) {
        final Vec3 level = new Vec3(eye.x() - from.x(), 0, eye.z() - from.z()).unit();
        return eye.minus(level.times(SHORT));
    }

    private Vec3 mirrored(final Vec3 p) {
        return new Vec3(-p.x(), p.y(), p.z());
    }
}
