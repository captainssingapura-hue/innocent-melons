package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.sheet.Lift;
import melons.innocent.physics.existences.Surfaces;

import java.util.Optional;

/**
 * A shell as a provider: its top is one surface that curves, and it answers what is under a spot from the sheet
 * itself, exactly, never from pieces. So its answers at two spots however near each other differ by just as much as
 * the surface does between them, in height and in the way it faces. Where it passes over itself, as a spiral ramp
 * does, the answer is the pass nearest under the spot. A mover meets each spot of it as a slope, or as level where
 * it is level there; that it curves is told by asking again a little further on.
 *
 * <p>It answers along its own upright: what is straight under the spot in its own frame. A shell that leans is not
 * answered rightly yet.
 *
 * @param name  what the shell is called
 * @param shell its shape
 * @param facts what its surface is made of and what state it is in
 */
public record Curved(String name, Shell shell, Facts facts) implements Provider {

    /** How far over the surface a spot may be and still count as on it, in centimetres. */
    private static final double OVER = 2;
    /** How far from square to down it may face and count as level, in degrees. */
    private static final double LEVEL = 0.5;

    @Override
    public Optional<Patch> under(final Spot spot, final Spot down, final double reach) {
        if (down.up() >= 0) {
            return Optional.empty();
        }
        Optional<Lift> nearest = Optional.empty();
        for (final Lift lift : shell.sheet().over(spot.across(), spot.along())) {
            final double below = spot.up() - lift.high();
            if (below >= -OVER && below <= reach && (nearest.isEmpty() || lift.high() > nearest.get().high())) {
                nearest = Optional.of(lift);
            }
        }
        return nearest.map(lift -> {
            final Spot at = new Spot(spot.across(), lift.high(), spot.along());
            final Spot faces = lift.faces();
            final double steep = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, -faces.dot(down)))));
            return new Patch(name, at, faces, down, steep < LEVEL ? new Surfaces.Level() : new Surfaces.Slope(),
                    facts.material(name, at), facts.condition(name, at), lift.within(), spot.up() - lift.high());
        });
    }
}
