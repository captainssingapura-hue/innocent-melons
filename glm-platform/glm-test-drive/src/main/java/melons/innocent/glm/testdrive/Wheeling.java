package melons.innocent.glm.testdrive;

import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.model.Proportions;

import java.util.List;

/**
 * How each part at the car's wheels moves as it is driven, in the car's own frame: a front corner, its wheel carrier,
 * hub, brake disc, shield and caliper and its wheel and tyre, steered about a line upright through its wheel's middle,
 * as far as the front wheels are turned; and at each wheel its hub, brake disc, wheel and tyre turning about its axle
 * as far as the car has rolled, the tyre's round its own radius. Every other part stays where it is.
 *
 * @param proportions the car's figures
 */
public record Wheeling(Proportions proportions) {

    /** The parts a front corner's steering turns; and those of them that turn with the wheel. */
    private static final List<String> STEERED = List.of("wheel carrier", "hub", "brake disc", "brake shield",
            "brake caliper", "wheel", "tyre");
    private static final List<String> SPINNING = List.of("hub", "brake disc", "wheel", "tyre");

    /**
     * How far {@code part} has moved, the front wheels turned {@code steer} degrees to the right and the car rolled
     * {@code rolled} centimetres ahead.
     */
    public Mat4 moved(final PartAt part, final double steer, final double rolled) {
        // a front corner's parts are called after it, a rear one's by what they are alone
        final String name = part.part().name();
        final boolean front = name.startsWith("front ");
        final String what = front ? name.substring("front ".length()) : name;
        if (!STEERED.contains(what)) {
            return new Mat4();
        }
        final double x = (part.side() == Side.LEFT ? -1 : 1) * proportions.halfTrack();
        final double y = proportions.tyre();
        final double z = front ? 0 : proportions.back();
        Mat4 moved = new Mat4();
        if (front && STEERED.contains(what)) {
            moved = about(x, y, z, steered(steer));
        }
        if (SPINNING.contains(what)) {
            moved = moved.times(about(x, y, z, spun(rolled / proportions.tyre())));
        }
        return moved;
    }

    /** {@code turn} done about the point ({@code x}, {@code y}, {@code z}). */
    private Mat4 about(final double x, final double y, final double z, final Mat4 turn) {
        final Mat4 there = new Mat4(new double[] {1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1});
        final Mat4 back = new Mat4(new double[] {1, 0, 0, -x, 0, 1, 0, -y, 0, 0, 1, -z, 0, 0, 0, 1});
        return there.times(turn).times(back);
    }

    /** Turned {@code degrees} to the right about the upright, its front toward +x. */
    private Mat4 steered(final double degrees) {
        final double s = Math.toRadians(degrees);
        return new Mat4(new double[] {Math.cos(s), 0, Math.sin(s), 0, 0, 1, 0, 0, -Math.sin(s), 0, Math.cos(s), 0,
            0, 0, 0, 1});
    }

    /** Turned {@code radians} about the axle, its top going ahead as it rolls ahead. */
    private Mat4 spun(final double radians) {
        return new Mat4(new double[] {1, 0, 0, 0, 0, Math.cos(radians), -Math.sin(radians), 0,
            0, Math.sin(radians), Math.cos(radians), 0, 0, 0, 0, 1});
    }
}
