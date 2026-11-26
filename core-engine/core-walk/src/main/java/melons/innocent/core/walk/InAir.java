package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;

import java.util.Optional;

/**
 * A walker in the air, for a tick: the engine under the walking engine for one that stands on nothing. It is pulled
 * down as its world pulls, keeps the way it left the ground with, and where it is asked to go is steered toward
 * that only a little. It is moved by the mean of how it went and how it goes, so a tick lands exactly on the curve.
 * Coming down, it lands on whatever surface lies between where its feet were and where they have come to.
 *
 * @param saying its explanations, as the run's switch has them
 */
record InAir(Saying saying) {

    /** How quickly it can be steered in the air: a share of what is left, each second. */
    private static final double STEERS = 3;
    /** How near a surface its feet count as on it, in centimetres. */
    private static final double NEAR = 0.01;

    /** {@code before}, in the air and asked as {@code wish} says, so many {@code seconds} on. */
    Out stepped(final Walking before, final Wish wish, final double seconds) {
        final Standing now = before.standing();
        final Level level = new Level();
        final double yaw = now.yaw() + wish.turn();
        final Spot kept = level.of(now.going());
        final Spot steered = wish.going() ? kept.plus(wish.wanted(before.walker(), yaw).minus(kept)
                .times(1 - Math.exp(-STEERS * seconds))) : kept;
        final Spot going = level.at(steered, now.going().up() - before.gravity().pull() * seconds);
        final Spot to = now.feet().plus(now.going().plus(going).times(seconds / 2));
        final double from = Math.max(now.feet().up(), to.up());
        final Optional<Stood> met = going.up() > 0 ? Optional.empty()
                : before.under().under(level.at(to, from + NEAR), from - to.up() + 2 * NEAR)
                        .filter(stood -> stood.high() >= to.up() - NEAR);
        if (met.isEmpty()) {
            return new Out(new Standing(to, yaw, going, Optional.empty(), now.stride()),
                    saying.of(() -> new Stride.Aloft(going.minus(now.going()))), Optional.empty());
        }
        final Stood on = met.get();
        // on its feet where it came down, keeping its way along the ground and losing its fall
        return new Out(new Standing(level.at(to, on.high()), yaw, level.at(steered, on.going().up()),
                Optional.of(on.provider()), now.stride()),
                saying.of(() -> new Stride.CameDown(on.provider(), on.going().up() - going.up())),
                Optional.of(on.provider()));
    }
}
