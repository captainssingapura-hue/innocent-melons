package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A view flown over the land, as one flies with a jet on one's back: an eye held at a height, moved ahead, back and
 * aside along the way it is turned, raised and lowered, turned round and tipped up and down; and the land drawn so
 * many times as big as it is, scaled about the spot under the eye, so coming nearer is the land growing, not the eye
 * dropping. The arithmetic only: the keys and the mouse say what is done ({@link Pilot}).
 *
 * <p>Where the eye is over the map is in the map's own centimetres; how high it is and how far it goes are as drawn,
 * so it goes as fast over the land on the screen whatever the land's scale.
 *
 * @param x       how far across the map the eye is
 * @param z       how far along the map the eye is
 * @param height  how high the eye is over the sea's level, as drawn, in centimetres
 * @param yaw     how far round the eye is turned, to the right as it grows, in degrees: nothing looks along the map
 * @param pitch   how far down it looks, in degrees from level
 * @param scale   how many times as big the land is drawn as it is
 */
public record Flight(double x, double z, double height, double yaw, double pitch, double scale) {

    /** No nearer looking straight up or down than this, past which ahead and aside lose their meaning. */
    private static final double STEEPEST = 89;
    /** How many degrees the eye turns for each unit the mouse is dragged, as the engine's own benches turn. */
    private static final double TURNED = 0.4;
    /** How many times as big one notch of the wheel makes the land; how small and how big it may be drawn. */
    private static final double NOTCHED = 1.1;
    private static final double SMALLEST = 0.01;
    private static final double BIGGEST = 100;

    public Flight {
        if (!(scale > 0) || !Double.isFinite(scale)) {
            throw new IllegalArgumentException("the land drawn some size: " + scale);
        }
    }

    /**
     * Gone {@code ahead} along the way it is turned, {@code aside} to its right, both level, and {@code up}, all as
     * drawn, in centimetres; the eye's place over the map moving as much less as the land is drawn bigger.
     */
    public Flight flown(final double ahead, final double aside, final double up) {
        final double round = Math.toRadians(yaw);
        final double across = (Math.sin(round) * ahead + Math.cos(round) * aside) / scale;
        final double along = (Math.cos(round) * ahead - Math.sin(round) * aside) / scale;
        return new Flight(x + across, z + along, height + up, yaw, pitch, scale);
    }

    /** Turned {@code round} degrees more to the right and {@code down} degrees more down, no steeper than it may be. */
    public Flight turned(final double round, final double down) {
        return new Flight(x, z, height, yaw + round, Math.clamp(pitch + down, -STEEPEST, STEEPEST), scale);
    }

    /** Turned as the mouse dragged {@code across} and {@code down} says. */
    public Flight dragged(final double across, final double down) {
        return turned(across * TURNED, down * TURNED);
    }

    /** The land drawn as many times bigger as {@code notches} of the wheel say, or smaller, no smaller nor bigger. */
    public Flight scrolled(final double notches) {
        return new Flight(x, z, height, yaw, pitch, Math.clamp(scale * Math.pow(NOTCHED, notches), SMALLEST,
                BIGGEST));
    }

    /** The eye, as drawn, seeing {@code fieldOfView} degrees from top to bottom, from {@code near} on. */
    public Camera camera(final double fieldOfView, final double near) {
        final double round = Math.toRadians(yaw);
        final double down = Math.toRadians(pitch);
        final Vec3 ahead = new Vec3(Math.sin(round) * Math.cos(down), -Math.sin(down),
                Math.cos(round) * Math.cos(down));
        final Vec3 right = new Vec3(0, 1, 0).cross(ahead).unit();
        return new Camera(new Vec3(x, height, z), right, ahead.cross(right), ahead, fieldOfView, near);
    }

    /** Where the land is drawn: scaled about the spot on the sea's level under the eye. */
    public Mat4 land() {
        return new Mat4(new Vec3(x, 0, z)).times(new Sized(scale).matrix()).times(new Mat4(new Vec3(-x, 0, -z)));
    }

    /** How many times as far off the land looks, drawn smaller than it is: once, drawn as big or bigger. */
    public double coarsened() {
        return Math.max(1, 1 / scale);
    }
}
