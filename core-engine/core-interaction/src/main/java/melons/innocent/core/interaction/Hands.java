package melons.innocent.core.interaction;

import melons.innocent.core.motion.Spot;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * The hands of one acting: where they are, which way they face, how far they reach and how wide before them they
 * can take a thing. One rule for everything says what is at hand: of the things offered, the nearest within reach
 * before them.
 *
 * @param at     where the hands are, in centimetres
 * @param facing which way they face, in degrees from ahead round toward the right
 * @param reach  how far they reach
 * @param spread how far to either side of the way they face a thing may lie, in degrees
 */
public record Hands(Spot at, double facing, double reach, double spread) {

    /** Hands at {@code at} facing so, reaching a metre and a half and taking what lies within sixty degrees. */
    public Hands(final Spot at, final double facing) {
        this(at, facing, 150, 60);
    }

    /** The way they face over the ground, one long. */
    public Spot ahead() {
        return new Spot(Math.sin(Math.toRadians(facing)), 0, Math.cos(Math.toRadians(facing)));
    }

    /** The spot so far ahead of them and so far up from them. */
    public Spot before(final double ahead, final double up) {
        return at.plus(ahead().times(ahead)).plus(new Spot(0, up, 0));
    }

    /** Whether a thing at {@code spot} is within reach, and before them: any way, where it is right at them. */
    public boolean reaches(final Spot spot) {
        final Spot level = new Spot(spot.across() - at.across(), 0, spot.along() - at.along());
        return spot.minus(at).length() <= reach && (level.length() < 1
                || level.unit().dot(ahead()) >= Math.cos(Math.toRadians(spread)));
    }

    /** The thing at hand, of {@code things} by name and where each is: the nearest within reach, by name if two tie. */
    public Optional<String> atHand(final Map<String, Spot> things) {
        return things.entrySet().stream().filter(thing -> reaches(thing.getValue()))
                .min(Comparator.<Map.Entry<String, Spot>>comparingDouble(
                        thing -> thing.getValue().minus(at).length()).thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey);
    }
}
