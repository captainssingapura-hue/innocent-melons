package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * Whether something may be seen by {@code camera}: anything within {@code kept} of its eye is taken to be; anything
 * further, only if some of it lies within the widest view a picture could have, {@value #WIDEST} times as wide as it
 * is high. What is not may still cast a shadow into the view, which is then lost. Centimetres.
 *
 * @param camera what sees
 * @param kept   how near the eye everything is taken to be seen
 */
record InView(Camera camera, double kept) {

    /** The widest a picture is taken to be, for its height. */
    private static final double WIDEST = 2.4;

    /** Whether a thing whose middle is at {@code middle}, reaching {@code reach} round it, may be seen. */
    boolean holds(final Vec3 middle, final double reach) {
        final Vec3 towards = middle.minus(camera.eye());
        final double far = towards.length();
        if (far < kept || far <= reach) {
            return true;
        }
        final double half = Math.atan(Math.tan(Math.toRadians(camera.fieldOfView()) / 2)
                * Math.sqrt(1 + WIDEST * WIDEST));
        final double off = Math.acos(Math.clamp(towards.times(1 / far).dot(camera.ahead().unit()), -1, 1));
        return off < half + Math.asin(Math.min(1, reach / far));
    }
}
