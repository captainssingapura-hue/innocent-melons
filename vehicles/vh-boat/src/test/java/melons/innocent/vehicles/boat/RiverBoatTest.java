package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The river boat's lines and what is built on them. Its lines are a graph of named points and typed edges, three
 * lines running from the transom to the stem and the stem running down from the sheer to the keel; each line,
 * followed through the graph, runs aft to fore, its points evenly spaced, the sheer swelling amidships where its
 * edges bow. The hull is painted in every one of its paints, and the boat is as long, wide and deep as its figures
 * say, its keel below the water's line by its draught, and its wheel the one part that turns.
 */
class RiverBoatTest {

    private final HullLines lines = new HullLines();

    @Test
    void itsLinesAreAGraphOfThreeLinesFromTransomToStemAndTheStemDownFromSheerToKeel() {
        final var graph = lines.graph();
        assertEquals(24, graph.vertices().listed().size(), "seven stations of three lines, and three on the stem");
        assertEquals(23, graph.edges().listed().size());
        for (final HullLine.Kind kind : List.of(HullLine.Kind.SHEER, HullLine.Kind.CHINE, HullLine.Kind.KEEL)) {
            final List<Vec3> line = lines.line(kind, 30);
            assertEquals(30, line.size());
            assertEquals(-650, line.getFirst().z(), 1e-9, kind + " from the transom");
            assertTrue(line.getLast().z() > 640, kind + " to the stem: " + line.getLast());
            for (int k = 1; k < line.size(); k++) {
                assertTrue(line.get(k).z() >= line.get(k - 1).z() - 1e-9, kind + " runs aft to fore");
            }
        }
        final List<Vec3> stem = lines.line(HullLine.Kind.STEM, 5);
        assertTrue(stem.getFirst().y() > stem.getLast().y(), "the stem runs down");
    }

    @Test
    void theSheersPointsAreEvenlySpacedAndItSwellsAmidshipsWhereItsEdgesBow() {
        final List<Vec3> sheer = lines.line(HullLine.Kind.SHEER, 60);
        final double step = sheer.get(1).minus(sheer.get(0)).length();
        for (int k = 1; k < sheer.size(); k++) {
            assertEquals(step, sheer.get(k).minus(sheer.get(k - 1)).length(), 0.05 * step, "evenly at " + k);
        }
        final double widest = sheer.stream().mapToDouble(Vec3::x).max().orElseThrow();
        assertTrue(widest > 160.5, "swelling over its stations' 160 cm: " + widest);
    }

    @Test
    void itsHullIsPaintedInEveryPaintAndItIsAsBigAsItsFiguresSay() {
        final Map<Hull.Paint, Mesh> painted = new Hull(lines).painted();
        assertEquals(Hull.Paint.values().length, painted.size());
        painted.forEach((paint, mesh) -> assertTrue(mesh.triangles() > 0, paint + " is painted somewhere"));
        final RiverBoat boat = new RiverBoat();
        double low = 0;
        double fore = 0;
        double aft = 0;
        double wide = 0;
        for (final BoatPart part : boat.parts()) {
            for (final Vec3 point : part.form().points()) {
                if (part.wheel()) {
                    continue;
                }
                low = Math.min(low, point.y());
                fore = Math.max(fore, point.z());
                aft = Math.min(aft, point.z());
                wide = part.name().startsWith("hull") ? Math.max(wide, Math.abs(point.x()) * 2) : wide;
            }
        }
        assertEquals(-boat.draught(), low, 1, "its keel its draught below the water's line");
        assertEquals(boat.length(), fore - aft, 30, "its length overall");
        assertEquals(boat.beam(), wide, 5, "its hull's beam");
        assertEquals(1, boat.parts().stream().filter(BoatPart::wheel).count(), "the wheel alone turns");
    }
}
