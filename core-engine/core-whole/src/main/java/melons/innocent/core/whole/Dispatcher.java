package melons.innocent.core.whole;

import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Core;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Landing;
import melons.innocent.core.rest.Next;
import melons.innocent.core.rest.Outsider;
import melons.innocent.core.rest.Stamped;
import melons.innocent.core.rest.World;
import melons.innocent.core.step.Explained;
import melons.innocent.core.surface.Facts;
import melons.innocent.core.surface.Underfoot;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.walk.Bulk;
import melons.innocent.core.walk.Standing;
import melons.innocent.core.walk.Stride;
import melons.innocent.core.walk.Walker;
import melons.innocent.core.walk.Walking;
import melons.innocent.core.walk.Walks;
import melons.innocent.core.walk.Wish;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * What works out a tick of a whole world, each kind of object by the engine for its kind. It holds no state: a
 * whole and what is asked give the whole a tick on. The order is one and fixed:
 *
 * <ol>
 * <li>the things are ticked by the core, each walker standing in their paths where it stood when the tick began,
 *     going as it went, so what rolls or is thrown at a walker meets it;</li>
 * <li>each walker, in the order of their names, is stepped by the walking engine on the world as the things'
 *     tick left it: what is under it is asked of that world's surfaces, with how each provider is going;</li>
 * <li>what stood in each walker's path is found by the core, and the walking engine puts its stepping right.</li>
 * </ol>
 *
 * <p>A walker moves nothing, and walkers do not yet meet each other.
 *
 * @param core  what ticks the things
 * @param walks the walking engine
 * @param facts what each surface is made of and its condition, from whoever made the world
 */
public record Dispatcher(Core<?> core, Walks walks, Facts facts) {

    /** The whole a tick on from {@code before}, what is asked done first, with what happened and why. */
    public Given ticked(final Whole before, final Asking asking) {
        final Tick tick = before.things().tick();
        final Map<String, Outsider> standing = new TreeMap<>(asking.things().outsiders());
        before.walkers().forEach((name, walker) -> {
            final Standing stands = before.standings().get(name);
            standing.put(name, new Outsider(new Bulk(walker).whole(),
                    new Bulk(walker).stands(stands.feet(), stands.yaw()), stands.going()));
        });
        final Asked asked = asking.things();
        final Next next = core.ticked(before.things(),
                new Asked(asked.destroyed(), asked.taken(), asked.thrown(), asked.driven(), standing));
        final Footed under = new Footed(new Underfoot(new World(core, next.state()), next.state().ground(), facts),
                next.state());
        final List<Happened> happened = new ArrayList<>(next.stamped().happened());
        final Map<String, Standing> standings = new TreeMap<>();
        final Map<String, Stride> strides = new TreeMap<>();
        before.walkers().forEach((name, walker) -> {
            final Walking walking = new Walking(walker, before.standings().get(name), under,
                    next.state().gravity());
            final Explained<Walker, Walking, Stride> walked = walked(before, next, walking, tick,
                    asking.wishes().getOrDefault(name, new Wish()));
            standings.put(name, walked.state().standing());
            strides.put(name, walked.why());
            walked.said().stream().filter(Landing.class::isInstance).map(Landing.class::cast)
                    .forEach(landing -> happened.add(new Happened.Landed(name, landing.on())));
        });
        return new Given(new Whole(next.state(), before.walkers(), standings),
                new Stamped(tick, happened, next.stamped().why()), strides);
    }

    /** A walker stepped by the walking engine, and put right for what the core finds stood in its path. */
    private Explained<Walker, Walking, Stride> walked(final Whole before, final Next next, final Walking walking,
                                                      final Tick tick, final Wish wish) {
        final Explained<Walker, Walking, Stride> stepped = walks.simulated(walking, tick, List.of(wish));
        final Bulk bulk = new Bulk(walking.walker());
        final Standing from = walking.standing();
        final Standing to = stepped.state().standing();
        // against things with corners from a step above its feet, against round things as the whole of itself
        final List<Collision> met = new ArrayList<>();
        core.met(before.things(), next.state(), walking.walker().name(), bulk.box(),
                bulk.placed(from.feet(), to.yaw()), bulk.placed(to.feet(), to.yaw())).stream()
                .filter(collision -> !round(next, collision)).forEach(met::add);
        core.met(before.things(), next.state(), walking.walker().name(), bulk.whole(),
                bulk.stands(from.feet(), to.yaw()), bulk.stands(to.feet(), to.yaw())).stream()
                .filter(collision -> round(next, collision)).forEach(met::add);
        return met.isEmpty() ? stepped : walks.correctionS1(walking, tick, stepped, met);
    }

    /** Whether what was met is round: a thing a walker cannot step onto, however low. */
    private boolean round(final Next next, final Collision collision) {
        return next.state().things().get(collision.with()).shape() instanceof Ellipsoid;
    }
}
