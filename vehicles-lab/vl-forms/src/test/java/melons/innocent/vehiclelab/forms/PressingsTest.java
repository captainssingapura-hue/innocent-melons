package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What pressed and hydroformed parts are made with: an outline with holes cut into triangles that cover it and not
 * its holes; a plate as thick as its sheet; a tube swept through its points, as wide and high as its sections; a
 * course through its points; a sleeve with its bore clear; a clevis's holes on its bolt; and an I-beam swept as wide
 * and high as its section, and a ball joint's stud reaching its end.
 */
class PressingsTest {

    private final Outlines outlines = new Outlines(24);

    @Test
    void anOutlineWithHolesCutIntoTrianglesCoveringItAndNotItsHoles() {
        final List<Spot> outline = List.of(new Spot(0, 0), new Spot(40, 0), new Spot(40, 10), new Spot(0, 10));
        final List<List<Spot>> holes = List.of(outlines.circle(new Spot(10, 5), 3),
                outlines.slot(new Spot(25, 5), 8, 3), outlines.circle(new Spot(34, 5), 2).reversed());
        final EarClipping cut = new EarClipping(outline, holes);
        final double expected = 400 - area(holes.get(0)) - area(holes.get(1)) - area(holes.get(2));
        assertEquals(expected, covered(cut.spots(), cut.triangles()), 1e-6);
    }

    @Test
    void aPlateAsThickAsItsSheet() {
        final Mesh plate = new Plate(outlines.tab(6, 5, 2.5), List.of(outlines.circle(new Spot(0, 5), 0.7)),
                new Vec3(0, 0, 0), new Vec3(1, 0, 0), new Vec3(0, 1, 0), 0.4).mesh();
        final double front = plate.points().stream().mapToDouble(Vec3::z).max().orElseThrow();
        final double back = plate.points().stream().mapToDouble(Vec3::z).min().orElseThrow();
        assertEquals(0.4, front - back, 1e-9);
        assertEquals(7.5, plate.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9, "its tip");
    }

    @Test
    void aTubeSweptThroughItsPointsAsWideAndHighAsItsSections() {
        final Course course = new Course(List.of(new Vec3(0, 0, 0), new Vec3(20, 0, 0), new Vec3(40, 0, 0)), 6);
        assertEquals(new Vec3(20, 0, 0), course.points().get(6));
        final Mesh tube = new Swept(course, List.of(new Section(6), new Section(6), new Section(8, 4, 4)), 16)
                .mesh();
        final double highest = tube.points().stream().filter(p -> p.x() > 39.9).mapToDouble(Vec3::y).max()
                .orElseThrow();
        final double widest = tube.points().stream().filter(p -> p.x() > 39.9).mapToDouble(Vec3::z).max()
                .orElseThrow();
        assertEquals(2, highest, 1e-9);
        assertEquals(4, widest, 1e-9);
        assertEquals(new Vec3(20, 0, 0), course.nearest(new Vec3(19, 5, 0)));
    }

    @Test
    void aSleevesBoreClear() {
        final Mesh sleeve = new Sleeve(new Vec3(0, 0, 0), new Vec3(0, 1, 0), 8, 4.5, 3.3, 24).mesh();
        assertTrue(sleeve.points().stream().allMatch(p -> Math.hypot(p.x(), p.z()) > 3.3 - 1e-9));
        assertEquals(4, sleeve.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9);
    }

    @Test
    void aClevisHolesOnItsBolt() {
        final Vec3 pivot = new Vec3(0, 6, 0);
        final Mesh clevis = new Clevis(pivot, new Vec3(0, 0, 0), new Vec3(0, 0, 1), 5, 0.4, 2.5, 0.75, 6, 24)
                .mesh();
        final double nearest = clevis.points().stream().filter(p -> Math.abs(p.z()) > 2.5)
                .mapToDouble(p -> Math.hypot(p.x() - pivot.x(), p.y() - pivot.y())).min().orElseThrow();
        assertEquals(0.75, nearest, 1e-9, "the bolt's hole clear in each cheek");
        assertEquals(2.5 + 0.4, clevis.points().stream().mapToDouble(Vec3::z).max().orElseThrow(), 1e-9);
    }

    @Test
    void anIBeamSweptAsWideAndHighAsItsSectionAndABallJointsStudToItsEnd() {
        final Course course = new Course(List.of(new Vec3(0, 0, 0), new Vec3(10, 0, 0), new Vec3(20, 0, 0)), 4);
        final List<Spot> section = outlines.ibeam(2.6, 3.2, 0.8, 0.7);
        final Mesh beam = new Profiled(course, List.of(section, section, section)).mesh();
        assertEquals(1.6, beam.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9);
        assertEquals(1.3, beam.points().stream().mapToDouble(Vec3::z).max().orElseThrow(), 1e-9);
        assertTrue(beam.points().stream().anyMatch(p -> Math.abs(p.z() - 0.4) < 1e-9), "its web");
        final BallJoint joint = new BallJoint(new Vec3(0, 0, 0), new Vec3(0, 1, 0), 1.4, 7, 16);
        assertEquals(7, joint.mesh().points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9);
    }

    private double covered(final List<Spot> spots, final List<Integer> triangles) {
        double sum = 0;
        for (int k = 0; k < triangles.size(); k += 3) {
            final double turned = spots.get(triangles.get(k)).turn(spots.get(triangles.get(k + 1)),
                    spots.get(triangles.get(k + 2)));
            assertTrue(turned >= -1e-9, "every triangle round to the left");
            sum += turned / 2;
        }
        return sum;
    }

    private double area(final List<Spot> loop) {
        double sum = 0;
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            sum += a.u() * b.v() - b.u() * a.v();
        }
        return Math.abs(sum / 2);
    }
}
