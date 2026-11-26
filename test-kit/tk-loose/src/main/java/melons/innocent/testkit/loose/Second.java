package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.World;
import melons.innocent.core.surface.Facts;
import melons.innocent.core.walk.Bulk;
import melons.innocent.core.walk.Standing;
import melons.innocent.core.walk.Stride;
import melons.innocent.core.walk.Walker;
import melons.innocent.core.walk.Walks;
import melons.innocent.core.walk.Wish;
import melons.innocent.core.whole.Asking;
import melons.innocent.core.whole.Dispatcher;
import melons.innocent.core.whole.Given;
import melons.innocent.core.whole.Whole;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The second figure: a walker of the core's own, stepped by the walking engine from the very same keys as the walk's
 * avatar, which is the old engine's. It is set down beside the avatar at the walk's first step and from then on goes
 * by the core alone: where the two part is where the new engine differs from the old. It is there to be looked at
 * until the new one is trusted, and is asked for by the run.
 *
 * <p>Keys are turned into what a walker is asked here, outside the core: W and S ahead and back, A and D aside,
 * at a run unless Shift is held, Space to jump; and it turns as the avatar turns.
 */
final class Second {

    /** What it is called among the world's things and walkers. */
    static final String NAME = "the second figure";
    /** How far to the avatar's right it is set down, in centimetres. */
    private static final double ASIDE = 150;

    private final Walker walker = new Walker(NAME);
    private final Walks walks = new Walks();
    private Optional<Standing> standing = Optional.empty();
    private Optional<Stride> why = Optional.empty();
    private Wish wish = new Wish();
    private double faced;
    private boolean boxed;

    /**
     * What it is asked at this step: the keys {@code held} and {@code pressed}, and the avatar, whose feet are at
     * {@code feet} and which faces {@code yaw} degrees. At the first asking it is set down beside the avatar.
     */
    void asked(final Set<String> held, final List<String> pressed, final Spot feet, final double yaw) {
        if (standing.isEmpty()) {
            final double way = Math.toRadians(yaw);
            standing = Optional.of(new Standing(feet.plus(new Spot(Math.cos(way), 0, -Math.sin(way)).times(ASIDE)),
                    yaw));
            faced = yaw;
        }
        wish = new Keyed(held, pressed).wish((yaw - faced + 540) % 360 - 180);
        faced = yaw;
        boxed = true;
    }

    /**
     * Set down at {@code feet} facing {@code yaw}, to be the walk's own figure: asked directly from then on
     * ({@link #wished}), and drawn by the walk as its figure and not as a box.
     */
    void set(final Spot feet, final double yaw) {
        standing = Optional.of(new Standing(feet, yaw));
        boxed = false;
    }

    /** What it is asked at the next tick. */
    void wished(final Wish asked) {
        wish = asked;
    }

    /** Whether it is to be drawn as a box: where it is a second figure beside the walk's own. */
    boolean boxed() {
        return boxed;
    }

    /** Whether it has been set down yet. */
    boolean set() {
        return standing.isPresent();
    }

    /** A tick of {@code world} with it in it, what is {@code asked} of the things done first. */
    Given ticked(final World world, final Asked asked, final Facts facts) {
        final Given given = new Dispatcher(world.core(), walks, facts).ticked(
                new Whole(world.state()).walked(walker, standing.orElseThrow()),
                new Asking(asked, Map.of(NAME, wish)));
        standing = Optional.of(given.whole().standings().get(NAME));
        why = Optional.ofNullable(given.strides().get(NAME));
        // a jump is asked once, at the step its key was pressed
        wish = new Wish(wish.ahead(), wish.aside(), 0, wish.run(), false);
        return given;
    }

    /** Taken away, to be set down again beside the avatar at the next step. */
    void again() {
        standing = Optional.empty();
    }

    /** How it stands and goes; nothing, before it is set down. */
    Optional<Standing> standing() {
        return standing;
    }

    /** The whole of it as a box, for drawing. */
    Bulk bulk() {
        return new Bulk(walker);
    }

    /** Where the whole of it stands; nowhere, before it is set down. */
    Optional<Placed> stands() {
        return standing.map(stood -> bulk().stands(stood.feet(), stood.yaw()));
    }

    /** In words, for the walk's window. */
    String said() {
        final String who = boxed ? "The second figure" : "The walker";
        return standing.map(stood -> who + ", by the core: " + stood.on().map(on -> "on " + on)
                .orElse("in the air") + String.format(", %.1f m/s", stood.going().length() / 100))
                .orElse("");
    }
}
