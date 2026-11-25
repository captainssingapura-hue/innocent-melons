package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A ball joint, steel: the ball in its housing, closed below; the stud's tapered neck rising from the ball along
 * {@code axis} to its threaded shank, and the nut on its end. The ball's middle is where it turns.
 *
 * @param ball   the ball's middle
 * @param axis   the way the stud rises from the ball
 * @param radius how far round the ball is
 * @param length how far the stud reaches from the ball's middle to its end
 * @param round  how many points round it is drawn in
 */
public record BallJoint(Vec3 ball, Vec3 axis, double radius, double length, int round) {

    public BallJoint {
        axis = axis.unit();
        if (radius <= 0 || length <= 2 * radius) {
            throw new IllegalArgumentException("a ball joint's stud reaches past its ball");
        }
    }

    /** Where the stud's end is. */
    public Vec3 end() {
        return ball.plus(axis.times(length));
    }

    /** It. */
    public Mesh mesh() {
        final Shapes shapes = new Shapes(round);
        final Vec3 below = ball.minus(axis.times(radius * 1.3));
        final Vec3 rim = ball.plus(axis.times(radius * 0.4));
        final Vec3 neck = ball.plus(axis.times(radius * 1.6));
        final Vec3 nut = end().minus(axis.times(radius * 0.9));
        return shapes.joined(List.of(
                shapes.drum(below, rim, radius * 1.45),
                shapes.capsule(ball, ball.plus(axis.times(radius * 0.5)), radius),
                shapes.drum(rim, neck, radius * 0.75),
                shapes.drum(neck, end(), radius * 0.55),
                shapes.drum(nut, end(), radius * 0.95)));
    }
}
