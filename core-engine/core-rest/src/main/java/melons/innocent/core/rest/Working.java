package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explanation;
import melons.innocent.core.tick.Tick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * One tick of a world, being worked out: the state before it taken apart, changed in the tick's own order, and put
 * together again as the state after ({@link Core#ticked}). It is made for one tick and thrown away with it, so
 * nothing of it is kept from one tick to the next: all that lasts is the state it gives back.
 *
 * <p>A tick, in order: what is destroyed or taken up is taken away, what is driven otherwise is set going so, and
 * what is thrown is set going; the check runs over what all that touched, supporters before what they hold; the
 * driven things go on, and what rides them with them; the things in motion are stepped, each by its engine handed
 * the tick, and corrected for what stood in their paths; and those that have settled go to rest.
 *
 * <p>For now every thing is looked at when one is asked what touches it, and things described as run into each
 * other are only said so, once, and left as they are.
 */
final class Working<E extends Explanation<Rigid>> {

    /** How many ticks running a thing must be held without moving before it is at rest: a tenth of a second. */
    private static final int SETTLED = 12;

    private final Ground ground;
    private final Gravity gravity;
    private final Simulator<E> loose;
    private final Touching touching;
    private final Reckoning reckoning;
    private final Tick tick;
    private final Map<String, Rigid> things;
    private final Map<String, Placed> resting;
    private final Map<String, Simulator.Mover> moving;
    private final Supports supports;
    private final Carrying carrying;
    /** The things at rest that something has happened to since they were last checked. */
    private final Set<String> dirty;
    /** Whether the things have been looked over for any described as run into each other. */
    private boolean looked;
    /** How many things the check has looked at, since the world began. */
    private long checks;
    /** What the engine worked out for each thing it stepped in this tick. */
    private final Map<String, Explanation<Rigid>> why = new TreeMap<>();

    /** The tick {@code core} is to work out from {@code before}. */
    Working(final Core<E> core, final State before) {
        this.ground = before.ground();
        this.gravity = before.gravity();
        this.loose = core.loose();
        this.touching = core.touching();
        this.reckoning = core.reckoning();
        this.tick = before.tick();
        this.things = new TreeMap<>(before.things());
        this.resting = new TreeMap<>(before.resting());
        this.moving = new TreeMap<>(before.moving());
        this.supports = new Supports(before.on(), before.holds());
        this.carrying = new Carrying(before.driving(), before.riding());
        this.dirty = new TreeSet<>(before.dirty());
        this.looked = before.looked();
        this.checks = before.checks();
    }

    /** The state a tick on, what is {@code asked} done first, with what happened stamped with the tick taken. */
    Next ticked(final Asked asked) {
        final List<Happened> happened = new ArrayList<>();
        dirty.addAll(carrying.ridden(supports, resting));
        new TreeSet<>(asked.destroyed()).forEach(gone -> gone(gone, happened, new Happened.Destroyed(gone)));
        new TreeSet<>(asked.taken()).forEach(gone -> gone(gone, happened, new Happened.Taken(gone)));
        if (!looked) {
            overlapping(happened);
            looked = true;
        }
        new TreeMap<>(asked.driven()).forEach((name, going) -> redriven(name, going, happened));
        new TreeMap<>(asked.thrown()).forEach((name, with) -> thrown(name, with, happened));
        checked(tick.seconds(), happened);
        // who rides what, now that what is held is known; and then the driven things go on, their riders with them
        dirty.addAll(carrying.ridden(supports, resting));
        carrying.carried(resting, tick.seconds());
        moved(asked.outsiders(), tick, happened);
        rested(happened);
        return new Next(new State(ground, gravity, tick.next(), things, resting, moving, supports.on(),
                supports.holds(), carrying.driving(), carrying.riding(), dirty, looked, checks),
                new Stamped(tick, happened, why));
    }


    /**
     * The things in motion a tick on. First the regular tick: each is stepped by its engine, the lowest first.
     * Then, every one of them having been stepped, what stood in the path each took is looked for, all against
     * the one tick, and each one's engine corrects what it worked out for what was found. Last, a thing at rest
     * that one of them struck is let go, going as the blow leaves it.
     */
    private void moved(final Map<String, Outsider> outsiders, final Tick taken, final List<Happened> happened) {
        final Map<String, Simulator.Mover> before = new TreeMap<>(moving);
        final Map<String, Body> handed = new TreeMap<>();
        final Map<String, Explained<Rigid, Body, E>> regular = new TreeMap<>();
        for (final String name : lowestFirst(moving.keySet())) {
            handed.put(name, new Body(things.get(name), moving.get(name),
                    (rigid, placed) -> touches(name, rigid, placed, true), gravity));
            regular.put(name, loose.simulated(handed.get(name), taken, List.of()));
            moving.put(name, regular.get(name).state().mover());
        }
        final Others others = new Others(things, resting, carrying::going, before, new TreeMap<>(moving),
                outsiders, taken.seconds());
        final Map<String, List<Collision>> found = new TreeMap<>();
        regular.forEach((name, stepped) -> found.put(name, new InThePath(touching).of(name,
                things.get(name).shape(), before.get(name).placed(), stepped.state().mover().placed(),
                others.round(name))));
        found.forEach((name, collisions) -> {
            final Explained<Rigid, Body, E> stepped = collisions.isEmpty() ? regular.get(name)
                    : loose.correctionS1(handed.get(name), taken, regular.get(name), collisions);
            moving.put(name, stepped.state().mover());
            why.put(name, stepped.why());
            stepped.said().stream().filter(Landing.class::isInstance).map(Landing.class::cast)
                    .forEach(landing -> happened.add(new Happened.Landed(name, landing.on())));
        });
        others.struck(found).forEach((name, collision) -> {
            final Spot pushes = collision.touch().pushes();
            final Spot more = pushes.times(collision.touch().going().minus(carrying.going(name)).dot(pushes));
            letGo(name);
            moving.put(name, new Simulator.Mover(moving.get(name).placed(),
                    new Moved(moving.get(name).moved().velocity().plus(more), new Spot(0, 0, 0)), false, 0));
            happened.add(new Happened.Struck(name, collision.thing()));
        });
    }

    /** A thing out of the world, destroyed or taken up: what rode it let go, what stood on it to be checked. */
    private void gone(final String gone, final List<Happened> happened, final Happened how) {
        if (!things.containsKey(gone)) {
            throw new IllegalArgumentException("nothing is called " + gone);
        }
        unridden(gone, happened);
        things.remove(gone);
        dirty.addAll(supports.over(gone));
        supports.gone(gone);
        resting.remove(gone);
        moving.remove(gone);
        dirty.remove(gone);
        carrying.left(gone);
        happened.add(how);
    }

    /** A driven thing set going otherwise: what rode it let go first, keeping the way it was being carried. */
    private void redriven(final String name, final Moved going, final List<Happened> happened) {
        if (!carrying.drives(name)) {
            throw new IllegalArgumentException("nothing driven is called " + name);
        }
        unridden(name, happened);
        carrying.driven(name, going);
        happened.add(new Happened.Driven(name));
    }

    /** Whatever rides the driven thing called {@code name} let go, each going on as it was being carried. */
    private void unridden(final String name, final List<Happened> happened) {
        if (carrying.drives(name)) {
            for (final String rider : carrying.riders(name, supports, resting)) {
                letGo(rider);
                happened.add(new Happened.Unsettled(rider));
            }
        }
    }

    /** A thing given a motion: let go of what held it, if it was at rest, and what stood on it to be checked. */
    private void thrown(final String name, final Moved with, final List<Happened> happened) {
        if (!things.containsKey(name) || things.get(name).unmoving() || carrying.drives(name)) {
            throw new IllegalArgumentException("nothing that can be thrown is called " + name);
        }
        if (resting.containsKey(name)) {
            letGo(name);
        }
        final Simulator.Mover was = moving.get(name);
        moving.put(name, new Simulator.Mover(was.placed(), new Moved(
                was.moved().velocity().plus(with.velocity()), was.moved().spin().plus(with.spin())), false, 0));
        happened.add(new Happened.Thrown(name));
    }

    /**
     * A thing at rest let go: in motion from now, going as whatever carried it was going, and what stood on it to
     * be checked.
     */
    private void letGo(final String name) {
        final Moved going = new Moved(carrying.going(name), new Spot(0, 0, 0));
        moving.put(name, new Simulator.Mover(resting.remove(name), going, false, 0));
        supports.over(name).stream().filter(resting::containsKey).forEach(dirty::add);
        supports.left(name);
        dirty.remove(name);
        carrying.left(name);
    }

    /** Every pair of things run well into each other where they stand, said once. */
    private void overlapping(final List<Happened> happened) {
        final List<String> names = List.copyOf(resting.keySet());
        for (int one = 0; one < names.size(); one++) {
            for (int other = one + 1; other < names.size(); other++) {
                if (touching.overlapping(things.get(names.get(one)).shape(), resting.get(names.get(one)),
                        things.get(names.get(other)).shape(), resting.get(names.get(other)))) {
                    happened.add(new Happened.Overlapping(names.get(one), names.get(other)));
                }
            }
        }
    }

    /** The check, over what is dirty: the lowest first, and whatever stands on a thing let go checked in its turn. */
    private void checked(final double seconds, final List<Happened> happened) {
        dirty.removeIf(name -> !resting.containsKey(name) || carrying.drives(name));
        while (!dirty.isEmpty()) {
            final String name = lowestFirst(dirty).getFirst();
            dirty.remove(name);
            checks++;
            final List<Touch> touches = touches(name, things.get(name), resting.get(name), false);
            if (reckoning.of(things.get(name), resting.get(name), touches, gravity, seconds).held()) {
                supports.stands(name, touches.stream().map(Touch::on).collect(Collectors.toSet()));
            } else {
                letGo(name);
                happened.add(new Happened.Unsettled(name));
            }
        }
    }

    /** The things in motion that have settled, gone to rest: each once all it stands on is at rest. */
    private void rested(final List<Happened> happened) {
        for (final String name : lowestFirst(moving.keySet())) {
            final Simulator.Mover mover = moving.get(name);
            if (!mover.landed() || mover.still() < SETTLED) {
                continue;
            }
            final Set<String> under = touches(name, things.get(name), mover.placed(), true).stream()
                    .map(Touch::on).collect(Collectors.toCollection(TreeSet::new));
            if (!under.isEmpty() && under.stream().allMatch(on -> on.equals(ground.name())
                    || resting.containsKey(on))) {
                moving.remove(name);
                resting.put(name, mover.placed());
                supports.stands(name, under);
                happened.add(new Happened.Rested(name, under));
            }
        }
    }

    /**
     * Where {@code rigid}, called {@code name} and placed so, is touched: by the ground, by the things at rest and,
     * where {@code landedToo}, by the things in motion that have come down and stopped; each touch with how what
     * touches it there is going, driven, riding or still.
     */
    private List<Touch> touches(final String name, final Rigid rigid, final Placed placed,
                                final boolean landedToo) {
        final List<Touch> touches = new ArrayList<>(touching.grounded(rigid.shape(), placed, ground));
        resting.forEach((other, stands) -> {
            if (!other.equals(name)) {
                touching.held(rigid.shape(), placed, other, things.get(other).shape(), stands)
                        .forEach(touch -> touches.add(touch.carried(carrying.going(other))));
            }
        });
        moving.forEach((other, mover) -> {
            if (landedToo && mover.landed() && !other.equals(name)) {
                touching.held(rigid.shape(), placed, other, things.get(other).shape(), mover.placed())
                        .forEach(touch -> touches.add(touch.carried(mover.moved().velocity())));
            }
        });
        return touches;
    }

    /** {@code names}, the lowest thing first, and by name where two are as low. */
    private List<String> lowestFirst(final Collection<String> names) {
        return names.stream().sorted(Comparator.<String>comparingDouble(name -> placed(name).orElseThrow().at().up())
                .thenComparing(Comparator.naturalOrder())).toList();
    }

    /** Where the thing called {@code name} is, at rest or in motion. */
    private Optional<Placed> placed(final String name) {
        return Optional.ofNullable(resting.get(name))
                .or(() -> Optional.ofNullable(moving.get(name)).map(Simulator.Mover::placed));
    }
}
