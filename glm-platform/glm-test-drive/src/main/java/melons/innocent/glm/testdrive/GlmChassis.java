package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.model.Proportions;

import java.util.List;

/**
 * The GLM car as the avatar engine's land vehicle knows one, its test engine ({@link Course.Chassis}): its wheels as
 * the car's figures have them, its track, its tyres and its wheelbase, the front axle the one that steers; a bed 5 m
 * long under it, or as long as it is told, riding as high as a car's; its driver on the left; and driven as a large
 * SUV is, as estimates: up to 250 km/h, from nought to 100 in some six seconds, braking as hard as the car's own does,
 * 2.1 tonnes. And what carries the car as it is made, its front axle's middle at nought on the ground, to where the
 * vehicle is driven to.
 *
 * @param proportions the car's figures
 * @param length      how long its bed is
 */
public record GlmChassis(Proportions proportions, double length) {

    /** How long its bed is; how high its top rides over the ground. */
    private static final double LENGTH = 505;
    private static final double RIDE = 75;
    /** Where its driver sits, across and along from its middle. */
    private static final double DRIVER_ACROSS = -40;
    private static final double DRIVER_ALONG = 40;
    /** How it drives: its top speed and reverse, cm/s; getting up to speed, braking, rolling to a stop, cm/s². */
    private static final Course.Drive DRIVE = new Course.Drive(6_944, 700, 470, 2_600, 350, 34, 140, 2_100);

    /** A large SUV's, its bed 5 m long. */
    public GlmChassis(final Proportions proportions) {
        this(proportions, LENGTH);
    }

    /** It, as the avatar engine knows it. */
    public Course.Chassis chassis() {
        final double tyre = proportions.width() * 0.1;
        final double width = 2 * (proportions.halfTrack() - 2 - tyre / 2);
        return new Course.Chassis("GLM", width, length, RIDE, proportions.tyre(), tyre, List.of(front(), -front()),
                DRIVER_ACROSS, DRIVER_ALONG, DRIVE);
    }

    /** How far its front axle is ahead of its middle. */
    public double front() {
        return proportions.wheelbase() / 2;
    }

    /** How high its bed's top rides over the ground, where the vehicle is. */
    public double ride() {
        return RIDE;
    }

    /**
     * What carries the car as it is made to where the vehicle is: at {@code at}, headed as it is, its nose {@code tilt}
     * degrees up and rolled {@code roll} degrees, its left side up.
     */
    public Mat4 carried(final Place at, final double tilt, final double roll) {
        final double y = Math.toRadians(at.yaw());
        final double p = Math.toRadians(tilt);
        final double r = Math.toRadians(roll);
        final Mat4 headed = new Mat4(new double[] {Math.cos(y), 0, Math.sin(y), at.x(), 0, 1, 0, at.height(),
            -Math.sin(y), 0, Math.cos(y), at.z(), 0, 0, 0, 1});
        final Mat4 pitched = new Mat4(new double[] {1, 0, 0, 0, 0, Math.cos(p), Math.sin(p), 0,
            0, -Math.sin(p), Math.cos(p), 0, 0, 0, 0, 1});
        final Mat4 rolled = new Mat4(new double[] {Math.cos(r), Math.sin(r), 0, 0, -Math.sin(r), Math.cos(r), 0, 0,
            0, 0, 1, 0, 0, 0, 0, 1});
        final Mat4 made = new Mat4(new double[] {1, 0, 0, 0, 0, 1, 0, -RIDE, 0, 0, 1, front(), 0, 0, 0, 1});
        return headed.times(pitched).times(rolled).times(made);
    }
}
