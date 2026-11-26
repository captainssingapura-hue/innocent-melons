package melons.innocent.testkit.curved;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.surface.Passage;
import melons.innocent.core.surface.Patch;
import melons.innocent.core.surface.Span;
import melons.innocent.core.surface.Underfoot;
import melons.innocent.physics.existences.Movers;

import java.util.Locale;
import java.util.Optional;

/**
 * What whoever walks the trial is treading on, asked as any mover asks it: the surface under the feet's middle,
 * how it differs between the left foot and the right, and how it differs a metre ahead. The last two are what a
 * thing on two feet, or on an axle and a wheelbase, is told of a surface that curves: asked of the sheet itself, so
 * they change evenly with every step and never by a facet. It is asked and said in words; the avatar's legs are
 * the walk's own to draw.
 */
final class Treading {

    /** How far under the feet a surface may be and still be what is stood on, in centimetres. */
    private static final double REACH = 60;
    /** How far either foot is from the feet's middle, and how far ahead is asked about, in centimetres. */
    private static final double ASIDE = 9.5;
    private static final double AHEAD = 100;
    /** How far over the surface the asking is made from, so that a rise ahead is still under it. */
    private static final double OVER = 40;

    private final Curves curves;
    private Optional<Spot> feet = Optional.empty();
    private double facing;

    /** What is trodden on in {@code curves}. */
    Treading(final Curves curves) {
        this.curves = curves;
    }

    /** Whoever walks is now standing with the feet's middle at {@code at}, facing {@code yaw} degrees round. */
    void stepped(final Spot at, final double yaw) {
        this.feet = Optional.of(at);
        this.facing = yaw;
    }

    private Underfoot underfoot() {
        return new Underfoot(curves.world(), new Ground(), curves);
    }

    /** The surface under the feet's middle, with its provider's frame; none, in the air or before any step. */
    Optional<Underfoot.Found> under() {
        return feet.flatMap(at -> underfoot().under(at, REACH));
    }

    /** How the surface differs from under the left foot to under the right; none, where either has none. */
    Optional<Span> across() {
        final Spot right = new Spot(Math.cos(Math.toRadians(facing)), 0, -Math.sin(Math.toRadians(facing)));
        return feet.flatMap(at -> underfoot().between(at.plus(right.times(-ASIDE)).plus(new Spot(0, OVER, 0)),
                at.plus(right.times(ASIDE)).plus(new Spot(0, OVER, 0)), REACH + OVER));
    }

    /** How the surface differs from under the feet to a metre ahead of them; none, where either has none. */
    Optional<Span> ahead() {
        final Spot onward = new Spot(Math.sin(Math.toRadians(facing)), 0, Math.cos(Math.toRadians(facing)));
        return feet.flatMap(at -> underfoot().between(at.plus(new Spot(0, OVER, 0)),
                at.plus(onward.times(AHEAD)).plus(new Spot(0, OVER, 0)), REACH + OVER));
    }

    /** All of it in words, a line each: what is underfoot, between the feet, and a metre ahead. */
    String said() {
        final String under = under().map(found -> {
            final Patch patch = found.patch();
            final String form = patch.steep() < 0.5 ? patch.form().name()
                    : String.format(Locale.ENGLISH, "a %s of %.1f degrees", patch.form().name(), patch.steep());
            final String edge = Double.isInfinite(patch.within()) ? "no edge"
                    : String.format(Locale.ENGLISH, "%.0f cm to its edge", patch.within());
            return String.format(Locale.ENGLISH, "Underfoot: %s; %s, %s, %s; %s; a walker goes on it: %s",
                    patch.provider(), form, patch.material().name().replace('-', ' '), patch.condition().name(),
                    edge, new Passage(new Movers.Walker(), patch.form()).how());
        }).orElse("Underfoot: nothing within reach");
        final String between = across().map(span -> String.format(Locale.ENGLISH,
                "Between the feet, %.0f cm apart: the right %.2f cm %s than the left; it faces %.2f degrees"
                        + " otherwise", span.run(), Math.abs(span.rise()), span.rise() < 0 ? "lower" : "higher",
                span.turned())).orElse("Between the feet: nothing under one of them");
        final String onward = ahead().map(span -> String.format(Locale.ENGLISH,
                "A metre ahead: %.2f cm %s, a grade of %.1f in a hundred; it faces %.2f degrees otherwise%s",
                Math.abs(span.rise()), span.rise() < 0 ? "lower" : "higher", 100 * span.grade(), span.turned(),
                span.same() ? "" : ", on another thing")).orElse("A metre ahead: nothing within reach");
        return under + System.lineSeparator() + between + System.lineSeparator() + onward;
    }
}
