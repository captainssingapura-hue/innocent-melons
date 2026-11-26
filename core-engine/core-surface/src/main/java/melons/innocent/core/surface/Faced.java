package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.physics.existences.Surfaces;

import java.util.List;
import java.util.Optional;

/**
 * A box as a provider: each of its faces that looks upward is a surface, flat, as far as the face goes. Which faces
 * look upward is by which way down is in the box's own frame, so a box lying square offers its top as a level
 * surface, a slab that leans offers it as a slope, and one fallen on its side offers the face that is then
 * uppermost. It answers with the first of them under the spot.
 *
 * @param name  what the box is called
 * @param box   its shape
 * @param facts what its surface is made of and what state it is in
 */
public record Faced(String name, Box box, Facts facts) implements Provider {

    /** How far over a surface a spot may be and still count as on it, in centimetres. */
    private static final double OVER = 2;
    /** How far from square to down a face may be and count as level, in degrees; and the least it looks upward. */
    private static final double LEVEL = 0.5;
    private static final double UPWARD = 0.02;

    @Override
    public Optional<Patch> under(final Spot spot, final Spot down, final double reach) {
        Optional<Patch> nearest = Optional.empty();
        for (final Spot faces : List.of(new Spot(1, 0, 0), new Spot(-1, 0, 0), new Spot(0, 1, 0),
                new Spot(0, -1, 0), new Spot(0, 0, 1), new Spot(0, 0, -1))) {
            final Optional<Patch> met = met(spot, down, reach, faces);
            if (met.isPresent() && (nearest.isEmpty() || met.get().below() < nearest.get().below())) {
                nearest = met;
            }
        }
        return nearest;
    }

    /** The face that looks the way {@code faces} goes, where it is under {@code spot}, going {@code down}. */
    private Optional<Patch> met(final Spot spot, final Spot down, final double reach, final Spot faces) {
        final double against = -down.dot(faces);
        if (against < UPWARD) {
            return Optional.empty();
        }
        final Spot half = box.half();
        final double out = Math.abs(faces.dot(half));
        final double below = (spot.dot(faces) - out) / against;
        final Spot at = spot.plus(down.times(below));
        // how far in from the face's edges it is met, each of the two ways the face runs
        double within = Double.POSITIVE_INFINITY;
        for (final Spot way : List.of(new Spot(1, 0, 0), new Spot(0, 1, 0), new Spot(0, 0, 1))) {
            if (Math.abs(way.dot(faces)) < 0.5) {
                within = Math.min(within, Math.abs(way.dot(half)) - Math.abs(at.dot(way)));
            }
        }
        if (below < -OVER || below > reach || within < 0) {
            return Optional.empty();
        }
        final boolean level = Math.toDegrees(Math.acos(Math.min(1, against))) < LEVEL;
        return Optional.of(new Patch(name, at, faces, down, level ? new Surfaces.Level() : new Surfaces.Slope(),
                facts.material(name, at), facts.condition(name, at), within, below));
    }
}
