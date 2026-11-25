package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.List;

/**
 * The rear subframe's pieces as each is made before they are welded together, each where it stands on the platform:
 * its two hydroformed tubes, the tube cross member and a side member; its pressed cross member behind, a web with its
 * edges turned down and its holes punched; the sleeves its bushes are pressed into; and the clevises the links are
 * bolted into, pressed from sheet.
 *
 * @param lines the subframe's lines
 */
record SubframePieces(SubframeLines lines) {

    /** How thick the pressed cross member's sheet is, and the clevises'. */
    private static final double SHEET = 0.3;
    private static final double CLEVIS_SHEET = 0.4;
    /** The body mounts' sleeves: how long, how far round outside and in their bores. */
    private static final double MOUNT_LONG = 8;
    private static final double MOUNT_OUT = 4.6;
    private static final double MOUNT_IN = 3.4;
    /** How far round the bolts' holes in the clevises are, an M14's, and how wide the clevises are at their foot. */
    private static final double BOLT = 0.75;
    private static final double CLEVIS_WIDE = 6.5;

    /** The tube cross member, across ahead of the axle, its arms sweeping up and forward to the front mounts. */
    Mesh tubeCrossMember() {
        return new Swept(lines.tubeCourse(), lines.tubeSections(), SubframeLines.ROUND).mesh();
    }

    /** A side member, from the tube cross member down and back, round its gooseneck to the back mount. */
    Mesh sideMember() {
        return new Swept(lines.sideCourse(), lines.sideSections(), SubframeLines.ROUND).mesh();
    }

    /**
     * The pressed cross member behind: its web, the differential's rear mounts and lightening holes punched in it,
     * and its edges ahead and behind turned down.
     */
    Mesh pressedCrossMember() {
        final Outlines outlines = new Outlines(SubframeLines.ROUND);
        final Spot diff = lines.diffPlate();
        final Spot light = lines.lightening();
        final List<List<Spot>> holes = List.of(outlines.circle(diff, diffPlate().outer()),
                outlines.circle(new Spot(-diff.u(), diff.v()), diffPlate().outer()), outlines.circle(light, 2),
                outlines.circle(new Spot(-light.u(), light.v()), 2), outlines.slot(lines.slot(), 9, 3.2));
        final Vec3 middle = lines.webMiddle();
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(lines.webOutline(), holes, middle, new Vec3(1, 0, 0), new Vec3(0, 0, 1), SHEET).mesh());
        final List<Spot> outline = lines.webOutline();
        final int half = outline.size() / 2;
        for (int k = 0; k + 1 < outline.size(); k++) {
            if (k + 1 != half) {
                made.add(flange(middle, outline.get(k), outline.get(k + 1)));
            }
        }
        return new Shapes(SubframeLines.ROUND).joined(made);
    }

    /** A body mount's sleeve at {@code at}, upright. */
    Sleeve mountSleeve(final Vec3 at) {
        return new Sleeve(at, new Vec3(0, 1, 0), MOUNT_LONG, MOUNT_OUT, MOUNT_IN, SubframeLines.ROUND);
    }

    /** The differential's rear mount's sleeve, on the right, through the web. */
    Sleeve diffPlate() {
        return new Sleeve(lines.diffPlateAt(), new Vec3(0, 1, 0), 6, 3, 2.2, SubframeLines.ROUND);
    }

    /** The differential's front mount's sleeve, through the tube cross member's middle. */
    Sleeve diffTube() {
        return new Sleeve(lines.diffTube(), new Vec3(0, 1, 0), 9, 3.2, 2.4, SubframeLines.ROUND);
    }

    /**
     * A clevis for a link from {@code inner} out to {@code outer}, welded to the tube along {@code course}, its cheeks
     * {@code gap} apart and reaching {@code tip} round its bolt.
     */
    Clevis clevis(final Course course, final Vec3 inner, final Vec3 outer, final double gap, final double tip) {
        return new Clevis(inner, lines.foot(course, inner), lines.bolt(inner, outer), gap, CLEVIS_SHEET, tip, BOLT,
                CLEVIS_WIDE, SubframeLines.ROUND);
    }

    /** The web's edge from {@code a} to {@code b}, turned down. */
    private Mesh flange(final Vec3 middle, final Spot a, final Spot b) {
        final Vec3 from = middle.plus(new Vec3(a.u(), 0, a.v()));
        final Vec3 way = new Vec3(b.u() - a.u(), 0, b.v() - a.v());
        final double length = way.length();
        return new Plate(List.of(new Spot(0, 0), new Spot(length, 0), new Spot(length, lines.flange()),
                new Spot(0, lines.flange())), List.of(), from, way, new Vec3(0, -1, 0), SHEET).mesh();
    }
}
