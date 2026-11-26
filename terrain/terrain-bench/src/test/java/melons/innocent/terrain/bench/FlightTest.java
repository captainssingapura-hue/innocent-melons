package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A view flown over the land: ahead and aside along the way it is turned, level, its place over the map moving the
 * less the bigger the land is drawn; up and down; turned, never past looking straight down; the wheel drawing the land
 * bigger or smaller about the spot under the eye, which stays where it is, within bounds; the eye where it is, looking
 * the way it is turned.
 */
class FlightTest {

    private static final double CLOSE = 1e-9;

    private final Flight flight = new Flight(1_000, 2_000, 50_000, 0, 30, 2);

    /** Where {@code matrix} puts {@code point}. */
    private Vec3 put(final Mat4 matrix, final Vec3 point) {
        final double[] p = {point.x(), point.y(), point.z(), 1};
        final double[] out = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 4; column++) {
                out[row] += matrix.at(row, column) * p[column];
            }
        }
        return new Vec3(out[0], out[1], out[2]);
    }

    @Test
    void aheadAndAsideAlongTheWayItIsTurnedTheLessTheBiggerTheLand() {
        final Flight ahead = flight.flown(400, 0, 0);
        assertEquals(1_000, ahead.x(), CLOSE);
        assertEquals(2_200, ahead.z(), CLOSE, "400 as drawn, the land twice as big: 200 over the map");
        assertEquals(1_200, flight.flown(0, 400, 0).x(), CLOSE, "aside, to the right, looking along the map");
        final Flight east = flight.turned(90, 0).flown(400, 0, 0);
        assertEquals(1_200, east.x(), CLOSE);
        assertEquals(2_000, east.z(), 1e-6);
        assertEquals(50_300, flight.flown(0, 0, 300).height(), CLOSE, "up, as drawn, whatever the land's scale");
    }

    @Test
    void turnedNeverPastStraightDownTheWheelScalingTheLandAboutTheSpotUnderTheEye() {
        assertEquals(89, flight.turned(0, 200).pitch(), CLOSE);
        assertEquals(-89, flight.turned(0, -200).pitch(), CLOSE);
        assertEquals(2 * 1.1, flight.scrolled(1).scale(), CLOSE);
        assertEquals(100, flight.scrolled(1_000).scale(), CLOSE);
        assertEquals(0.01, flight.scrolled(-1_000).scale(), CLOSE);
        // the spot under the eye stays where it is; a spot 100 off it, and 30 up, drawn twice as far and as high
        assertEquals(0, put(flight.land(), new Vec3(1_000, 0, 2_000)).minus(new Vec3(1_000, 0, 2_000)).length(),
                CLOSE);
        assertEquals(0, put(flight.land(), new Vec3(1_100, 30, 2_000)).minus(new Vec3(1_200, 60, 2_000)).length(),
                CLOSE);
        assertThrows(IllegalArgumentException.class, () -> new Flight(0, 0, 0, 0, 0, 0));
    }

    @Test
    void theEyeWhereItIsLookingTheWayItIsTurned() {
        final Camera camera = flight.camera(50, 50);
        assertEquals(new Vec3(1_000, 50_000, 2_000), camera.eye());
        assertEquals(0, camera.ahead().x(), CLOSE);
        assertEquals(-0.5, camera.ahead().y(), CLOSE, "30 degrees down");
        assertEquals(Math.sqrt(3) / 2, camera.ahead().z(), CLOSE, "along the map");
        assertEquals(1, camera.right().x(), CLOSE, "its right, across the map");
    }
}
