package melons.innocent.vehicles.osprey;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Osprey holds to its published figures: 17.48 m long, 25.78 m across with its rotors turning, its rotors 11.61 m
 * across, its wing 13.97 m, and 6.73 m high to the top of its spinners with its nacelles up. Every part has some
 * form; the body stands on its wheels; flying ahead its rotors' tips reach below its wheels, and standing up they
 * do not.
 */
class OspreyTest {

    private final Osprey osprey = new Osprey();
    private final List<Part> parts = osprey.parts();

    @Test
    void itIsAsLongAsPublishedAndAsWideWithItsRotorsTurning() {
        final Mesh body = part("body");
        assertEquals(1748, extent(body, 2), 2, "nose to tail");
        assertEquals(1397, 2 * osprey.figures().wingReach(), 1e-9, "the wing");
        assertEquals(2578, 2 * (osprey.pivot(1).x() + osprey.figures().rotorReach()), 1e-9, "rotors turning");
        assertEquals(-osprey.pivot(-1).x(), osprey.pivot(1).x(), 1e-9, "the same either side");
        assertEquals(1161, 2 * osprey.figures().rotorReach(), 1e-9, "each rotor");
    }

    @Test
    void itStandsAsHighAsPublishedWithItsNacellesUp() {
        // the spinner's tip, standing straight up over the pivot
        final double tip = osprey.pivot(1).y() + osprey.hub() + highest(part("right hub"), 2);
        assertEquals(673, tip, 5, "to the top of its spinners");
        assertTrue(lowest(part("wheels"), 1) >= -1e-9 && lowest(part("wheels"), 1) < 1, "on its wheels");
        assertTrue(lowest(part("body"), 1) > 40, "its belly clear of the ground");
    }

    @Test
    void flyingAheadItsRotorsReachBelowItsWheelsAndStandingUpTheyDoNot() {
        assertTrue(osprey.belowWheels(0) > 100, "flying ahead: " + osprey.belowWheels(0));
        assertEquals(0, osprey.belowWheels(90), 1e-9, "standing up");
        assertEquals(9, parts.size());
        parts.forEach(part -> assertTrue(part.form().triangles() > 0, part.name()));
    }

    private Mesh part(final String name) {
        return parts.stream().filter(part -> part.name().equals(name)).findFirst().orElseThrow().form();
    }

    private double extent(final Mesh mesh, final int way) {
        return highest(mesh, way) - lowest(mesh, way);
    }

    private double highest(final Mesh mesh, final int way) {
        return mesh.points().stream().mapToDouble(point -> of(point, way)).max().orElseThrow();
    }

    private double lowest(final Mesh mesh, final int way) {
        return mesh.points().stream().mapToDouble(point -> of(point, way)).min().orElseThrow();
    }

    private double of(final Vec3 point, final int way) {
        return way == 0 ? point.x() : way == 1 ? point.y() : point.z();
    }
}
