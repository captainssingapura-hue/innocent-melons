package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;
import melons.innocent.physics.existences.Surfaces;

import java.util.Optional;

/**
 * Level ground as a provider: one level surface at a height, going on every way without an edge. Its frame is the
 * world's, since the ground goes nowhere.
 *
 * @param name  what the ground is called
 * @param high  how high it lies, in centimetres
 * @param facts what its surface is made of and what state it is in
 */
public record Levelled(String name, double high, Facts facts) implements Provider {

    /** How far over the ground a spot may be and still count as on it, in centimetres. */
    private static final double OVER = 2;

    @Override
    public Optional<Patch> under(final Spot spot, final Spot down, final double reach) {
        final Spot faces = new Spot(0, 1, 0);
        final double against = -down.dot(faces);
        if (against <= 0) {
            return Optional.empty();
        }
        final double below = (spot.up() - high) / against;
        if (below < -OVER || below > reach) {
            return Optional.empty();
        }
        final Spot at = spot.plus(down.times(below));
        return Optional.of(new Patch(name, at, faces, down, new Surfaces.Level(), facts.material(name, at),
                facts.condition(name, at), Double.POSITIVE_INFINITY, below));
    }
}
