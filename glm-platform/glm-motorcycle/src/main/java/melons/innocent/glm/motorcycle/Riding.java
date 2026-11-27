package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

/**
 * Where a motorcycle's rider meets it, as it stands, its bars straight, in the engine's frame, in centimetres: where
 * they sit on the seat, the top of its cushion in its middle; where each hand holds its grip, on the grip's axis
 * halfway along it; where the ball of each foot rests on its peg, on the peg's top halfway out; where the left toe
 * lifts the gear lever, under its toe peg; and where the right toe presses the brake pedal, on its pad. Each a point
 * on the part it is on, called as {@link #SITS}, {@link #HOLDS}, {@link #RESTS} and {@link #PRESSES} say, so a figure
 * riding it can find each where its part is put, the grips turning with the bars, the pegs folding and the pedals
 * pressed.
 *
 * @param platform the motorcycle
 */
public record Riding(Platform platform) {

    /** What the seat's point is called where the rider sits on it. */
    public static final String SITS = "rider";
    /** What each grip's point is called where a hand holds it. */
    public static final String HOLDS = "hand";
    /** What each peg's point is called where a foot rests on it. */
    public static final String RESTS = "foot";
    /** What the gear lever's and the brake pedal's points are called where a toe works them. */
    public static final String PRESSES = "toe";

    /** Where the rider sits. */
    public Vec3 seat() {
        return at("seat", SITS);
    }

    /** Where the rider's hand on side {@code side}, -1 the left, holds its grip. */
    public Vec3 grip(final int side) {
        return at("grip, " + called(side), HOLDS);
    }

    /** Where the ball of the rider's foot on side {@code side}, -1 the left, rests on its peg. */
    public Vec3 peg(final int side) {
        return at("footpeg, " + called(side), RESTS);
    }

    /** Where the rider's left toe lifts the gear lever. */
    public Vec3 gearLever() {
        return at("gear lever", PRESSES);
    }

    /** Where the rider's right toe presses the brake pedal. */
    public Vec3 brakePedal() {
        return at("brake pedal", PRESSES);
    }

    /** Where the part called {@code part} has its point called {@code called}. */
    private Vec3 at(final String part, final String called) {
        final Part found = platform.connections().stream().map(Connection::carried)
                .filter(p -> p.name().equals(part)).findFirst().orElseThrow();
        return found.point(called).at();
    }

    private String called(final int side) {
        return side < 0 ? "left" : "right";
    }
}
