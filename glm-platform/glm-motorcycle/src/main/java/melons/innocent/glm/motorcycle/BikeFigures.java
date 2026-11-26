package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.engine.BeltFigures;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.wheels.WheelSize;

/**
 * Where a motorcycle's parts lie round its V twin, in the engine's frame, in centimetres: the crank's axis along z
 * from the crankcase's left end, up as y, the bike going toward +x; its middle halfway along the crank. After the
 * reference bike's figures: its wheelbase, 1,545 mm; its steering's rake, 30 degrees, and trail, 137 mm; its tyres,
 * 100/90 on 19 inches in front and 150/80 on 16 behind. Its back axle where its belt puts it ({@link BeltFigures}),
 * the crank 28 cm off the ground; its front axle a wheelbase ahead, as high as its tyre is round; its steering axis
 * leaning back from up by its rake, its fork's legs parallel to it and ahead of it by the offset that makes its
 * trail, 4.7 cm; its yokes, its legs' spacing and its swingarm's pivot, estimates.
 *
 * @param engine the twin's figures
 */
public record BikeFigures(EngineFigures engine) {

    /** Its wheelbase, its rake in degrees and its trail. */
    public static final double WHEELBASE = 154.5;
    public static final double RAKE = 30;
    public static final double TRAIL = 13.7;
    /** Its tyres, in front and behind. */
    public static final WheelSize FRONT = new WheelSize(100, 90, 19);
    public static final WheelSize REAR = new WheelSize(150, 80, 16);
    /** How far up its fork's legs from the front axle its yokes are, the bottom one and the top. */
    static final double LOW_YOKE = 50;
    static final double HIGH_YOKE = 71;
    /** How far apart its fork's legs are, across. */
    static final double LEGS = 21;

    /** Where its middle is along the crank. */
    public double centre() {
        return engine.length() / 2;
    }

    /** How high the ground is: under the crank by as far as the crank stands off it. */
    public double ground() {
        return -BeltFigures.CRANK;
    }

    /** Where its back axle's middle is: where its belt puts it, in its middle. */
    public Vec3 rearAxle() {
        final Spot axle = new BeltFigures(engine).axle();
        return new Vec3(axle.u(), axle.v(), centre());
    }

    /** Where its front axle's middle is: a wheelbase ahead of the back one, as high as its tyre is round. */
    public Vec3 frontAxle() {
        return new Vec3(rearAxle().x() + WHEELBASE, ground() + FRONT.radius(), centre());
    }

    /** The way its steering axis runs, up it: leaning back from up by its rake. */
    public Vec3 steer() {
        final double r = Math.toRadians(RAKE);
        return new Vec3(-Math.sin(r), Math.cos(r), 0);
    }

    /** The way ahead, square to its steering axis, in its middle plane. */
    public Vec3 ahead() {
        final double r = Math.toRadians(RAKE);
        return new Vec3(Math.cos(r), Math.sin(r), 0);
    }

    /** How far its fork's legs stand ahead of its steering axis: so far that its trail is as said. */
    public double offset() {
        final double r = Math.toRadians(RAKE);
        return FRONT.radius() * Math.sin(r) - TRAIL * Math.cos(r);
    }

    /** The place {@code along} up its fork's legs from the front axle, in its middle. */
    public Vec3 leg(final double along) {
        return frontAxle().plus(steer().times(along));
    }

    /** The place on its steering axis level with {@code along} up its legs, in its middle. */
    public Vec3 stem(final double along) {
        return leg(along).minus(ahead().times(offset()));
    }

    /** How far ahead of its front tyre's foot its steering axis meets the ground: its trail, worked out. */
    public double trail() {
        final Vec3 at = stem(0);
        final double down = (at.y() - ground()) / steer().y();
        return at.x() - steer().x() * down - frontAxle().x();
    }

    /** Where its swingarm pivots: behind the countershaft and a little over it, inside the belt's loop. */
    public Vec3 pivot() {
        final Spot shaft = new BeltFigures(engine).countershaft();
        return new Vec3(shaft.u() - 13, shaft.v() + 3.9, centre());
    }
}
