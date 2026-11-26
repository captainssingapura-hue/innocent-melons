package melons.innocent.core.walk;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.surface.Passage;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Movers;

import java.util.Optional;

/**
 * A walker on its feet, for a tick: the engine under the walking engine for one that stands on a surface. It is
 * carried with whatever provides the surface, and goes along the surface against that: its going is eased toward
 * what it is asked, as quickly as its footing lets it, and its feet are set on the surface where it comes to, so it
 * rises and falls with a slope or a curve. A ledge no higher than its step it steps up; one higher, a slope too
 * steep, or a form its kind cannot use refuses it, and it stays, carried only. Where nothing is under it within a
 * step it has walked off an edge. Asked to jump, it leaves what it stands on.
 *
 * @param saying its explanations, as the run's switch has them
 */
record OnFoot(Saying saying) {

    /** How quickly it comes to the speed asked on dry footing: a share of what is left, each second. */
    private static final double GRIP = 10;

    /** {@code before}, standing on {@code on} and asked as {@code wish} says, so many {@code seconds} on. */
    Out stepped(final Walking before, final Wish wish, final Stood on, final double seconds) {
        final Walker walker = before.walker();
        final Standing now = before.standing();
        final Level level = new Level();
        final double yaw = now.yaw() + wish.turn();
        if (wish.jump()) {
            final double up = walker.jumps() + on.going().up();
            return new Out(new Standing(now.feet(), yaw, level.at(now.going(), up), Optional.empty(), now.stride()),
                    saying.of(() -> new Stride.Jumped(on.provider(), up)), Optional.empty());
        }
        final Spot own = level.of(now.going().minus(on.going()));
        final Spot asked = wish.wanted(walker, yaw);
        final Spot came = own.plus(asked.minus(own).times(1 - Math.exp(-GRIP * grip(on.condition()) * seconds)));
        final Spot carried = now.feet().plus(level.of(on.going()).times(seconds));
        final Spot to = carried.plus(came.times(seconds));
        final Optional<Stood> there = before.under().under(level.at(to, now.feet().up() + walker.steps()),
                2 * walker.steps());
        if (there.isEmpty()) {
            return gone(before, yaw, came, carried, to, on);
        }
        final Stood next = there.get();
        final Optional<String> barred = barred(walker, next, came);
        if (barred.isPresent()) {
            return refused(now, yaw, carried, on, next.provider(), barred.get());
        }
        return new Out(new Standing(level.at(to, next.high()), yaw, came.plus(level.of(next.going())),
                Optional.of(next.provider()), now.stride() + came.length() * seconds),
                saying.of(() -> new Stride.Afoot(next.provider(), next.grade(), asked, came,
                        next.high() - now.feet().up())), Optional.empty());
    }

    /** Nothing within a step under where it would come to: a ledge too high to step up, or an edge walked off. */
    private Out gone(final Walking before, final double yaw, final Spot came, final Spot carried, final Spot to,
                     final Stood on) {
        final Walker walker = before.walker();
        final Standing now = before.standing();
        final Level level = new Level();
        final Optional<Stood> higher = before.under().under(level.at(to, now.feet().up() + walker.tall()),
                walker.tall() - walker.steps());
        if (higher.isPresent()) {
            return refused(now, yaw, carried, on, higher.get().provider(), "too high to step up");
        }
        return new Out(new Standing(level.at(to, now.feet().up()), yaw, came.plus(level.of(on.going())),
                Optional.empty(), now.stride()), saying.of(() -> new Stride.SteppedOff(on.provider())),
                Optional.empty());
    }

    /** It stays where it was on what it stands on, carried only, and why. */
    private Out refused(final Standing now, final double yaw, final Spot carried, final Stood on, final String by,
                        final String why) {
        final Level level = new Level();
        return new Out(new Standing(level.at(carried, now.feet().up()), yaw, level.of(on.going()), now.on(),
                now.stride()), saying.of(() -> new Stride.Refused(by, why)), Optional.empty());
    }

    /** Why a walker going as {@code came} says cannot go onto {@code next}: nothing, where it can. */
    private Optional<String> barred(final Walker walker, final Stood next, final Spot came) {
        final Passage passage = new Passage(new Movers.Walker(), next.form());
        if (passage.use() == Passage.Use.NOT) {
            return Optional.of("a walker does not go on it: " + passage.how());
        }
        final boolean climbing = came.dot(new Level().of(next.faces())) < 0;
        return climbing && next.grade() > walker.climbs() ? Optional.of("too steep to climb") : Optional.empty();
    }

    /** How much of its grip a walker has on a surface in {@code condition}: all of it, where it is dry. */
    private double grip(final Node condition) {
        return switch (condition) {
            case Conditions.Icy icy -> 0.12;
            case Conditions.Wet wet -> 0.6;
            case Conditions.Loose loose -> 0.5;
            case Conditions.Flooded flooded -> 0.4;
            default -> 1;
        };
    }
}
