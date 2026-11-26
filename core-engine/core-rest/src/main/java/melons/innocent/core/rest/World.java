package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;

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
 * A world of things at rest and in motion, ticked. Nearly all of it is at rest, each thing standing on what holds it
 * ({@link Supports}), and costs nothing: a tick checks only the things something has happened to that could have
 * changed how they are held ({@link Reckoning}). A thing found no longer held is in motion from that tick, and so,
 * in turn and in the same tick, is whatever stood on it. What is in motion is stepped by the simulator the world is
 * given, the lowest first, until it has come down, been held without moving for a little while, and stands at rest
 * again on what it came down on.
 *
 * <p>A thing may be driven: it goes as it is told, whatever is on it or under it, and what rests on it rides it, at
 * rest in its frame and carried with it ({@link Carrying}). What leaves a driven thing, let go or thrown, keeps the
 * way it was being carried: so a thing thrown sideways from a train goes on along the train's way as it flies.
 *
 * <p>A thing may be taken up out of the world, into someone's hands, and stood in it again later, where it is put
 * down or as it is thrown: meanwhile it is nowhere in it, and holds nothing up.
 *
 * <p>A tick, in order: what is destroyed or taken up is taken away, what is driven otherwise is set going so, and
 * what is thrown is set going; the check runs over what all that touched, supporters before what they hold; the
 * driven things go on, and what rides them with them; the things in motion are stepped; and those that have settled
 * go to rest. What happened is given back. It is kept as it is ticked, and from the same beginning and the same
 * things asked of it at the same ticks it is the same world every time.
 *
 * <p>It counts its own ticks ({@link Tick}), each as long as its one ticker said when it was made: nobody who
 * ticks it says how long a tick is, and how long it has gone on is its count and nothing else.
 *
 * <p>For now every thing is looked at when one is asked what touches it, and things described as run into each
 * other are only said so, once, and left as they are.
 */
public final class World {

    /** How many ticks running a thing must be held without moving before it is at rest: a tenth of a second. */
    private static final int SETTLED = 12;

    private final Ground ground;
    private final Simulator loose;
    private final Touching touching = new Touching();
    private final Reckoning reckoning = new Reckoning();
    private final Map<String, Rigid> things = new TreeMap<>();
    private final Map<String, Placed> resting = new TreeMap<>();
    private final Map<String, Simulator.Mover> moving = new TreeMap<>();
    private final Supports supports = new Supports();
    private final Carrying carrying = new Carrying();
    /** The things at rest that something has happened to since they were last checked. */
    private final Set<String> dirty = new TreeSet<>();
    /** Whether the things have been looked over for any described as run into each other. */
    private boolean looked;
    /** How many things the check has looked at, since the world began. */
    private long checks;
    /** The tick to be taken next: its count is how many have been taken since the world began. */
    private Tick tick;

    /** A world on {@code ground}, whatever nothing drives stepped by {@code loose}, ticked as the game steps. */
    public World(final Ground ground, final Simulator loose) {
        this(ground, loose, new Ticker());
    }

    /** The same, ticked by {@code ticker}: how long its every tick is, said this once. */
    public World(final Ground ground, final Simulator loose, final Ticker ticker) {
        this.ground = ground;
        this.loose = loose;
        this.tick = ticker.first();
    }

    /**
     * {@code rigid} stood in the world as it is described: taken as at rest until the check says otherwise, which
     * of a thing whose shape never moves it never does.
     */
    public World stood(final Rigid rigid, final Placed placed) {
        if (things.putIfAbsent(rigid.name(), rigid) != null) {
            throw new IllegalArgumentException("there is already a thing called " + rigid.name());
        }
        resting.put(rigid.name(), placed);
        // what never moves is never checked: it is at rest, whatever is or is not under it
        if (!rigid.unmoving()) {
            dirty.add(rigid.name());
        }
        return this;
    }

    /**
     * {@code rigid} stood in the world as a driven thing, going as {@code going} says until it is driven otherwise:
     * never checked and never let go, and a frame for whatever rests on it.
     */
    public World driven(final Rigid rigid, final Placed placed, final Moved going) {
        stood(rigid, placed);
        dirty.remove(rigid.name());
        carrying.driven(rigid.name(), going);
        return this;
    }

    /**
     * The world a tick on, what is {@code asked} of it done first: the tick is the world's own next one, as long
     * as every other, and what happened is given back stamped with it.
     */
    public Stamped ticked(final Asked asked) {
        final Tick taken = tick;
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
        checked(taken.seconds(), happened);
        // who rides what, now that what is held is known; and then the driven things go on, their riders with them
        dirty.addAll(carrying.ridden(supports, resting));
        carrying.carried(resting, taken.seconds());
        moved(asked.outsiders(), taken, happened);
        rested(happened);
        tick = taken.next();
        return new Stamped(taken, happened);
    }

    /** The tick to be taken next: its count is how many the world has taken since it began. */
    public Tick tick() {
        return tick;
    }

    /**
     * The things in motion a tick on. First the regular tick: each is stepped by its engine, the lowest first.
     * Then, every one of them having been stepped, what stood in the path each took is looked for, all against
     * the one tick, and each one's engine corrects what it worked out for what was found. Last, a thing at rest
     * that one of them struck is let go, going as the blow leaves it.
     */
    private void moved(final Map<String, Outsider> outsiders, final Tick taken, final List<Happened> happened) {
        final Map<String, Simulator.Mover> before = new TreeMap<>(moving);
        final Map<String, Simulator.Stepped> regular = new TreeMap<>();
        for (final String name : lowestFirst(moving.keySet())) {
            regular.put(name, loose.stepped(things.get(name), moving.get(name),
                    (rigid, placed) -> touches(name, rigid, placed, true), taken));
            moving.put(name, regular.get(name).next());
        }
        final Others others = new Others(things, resting, carrying::going, before, regular, outsiders);
        final Map<String, List<Collision>> found = new TreeMap<>();
        regular.forEach((name, stepped) -> found.put(name, new InThePath(touching).of(name,
                things.get(name).shape(), before.get(name).placed(), stepped.next().placed(), others.round(name))));
        found.forEach((name, collisions) -> {
            final Simulator.Stepped stepped = collisions.isEmpty() ? regular.get(name)
                    : loose.correctionS1(things.get(name), before.get(name),
                            (rigid, placed) -> touches(name, rigid, placed, true), taken, regular.get(name),
                            collisions);
            moving.put(name, stepped.next());
            stepped.on().ifPresent(on -> happened.add(new Happened.Landed(name, on)));
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
            if (reckoning.of(things.get(name), resting.get(name), touches, seconds).held()) {
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

    /** The names of every thing in it. */
    public Set<String> things() {
        return Set.copyOf(things.keySet());
    }

    /** The thing called {@code name}. */
    public Rigid thing(final String name) {
        return Optional.ofNullable(things.get(name)).orElseThrow(
                () -> new IllegalArgumentException("nothing is called " + name));
    }

    /** Where the thing called {@code name} is, at rest or in motion; nowhere, where there is none. */
    public Optional<Placed> placed(final String name) {
        return Optional.ofNullable(resting.get(name))
                .or(() -> Optional.ofNullable(moving.get(name)).map(Simulator.Mover::placed));
    }

    /**
     * How the thing called {@code name} is moving over the ground: as it goes, where it is in motion; as it is
     * driven or carried, where it is driven or rides; not at all, where it rests on what does not move.
     */
    public Moved moved(final String name) {
        return Optional.ofNullable(moving.get(name)).map(Simulator.Mover::moved)
                .orElseGet(() -> new Moved(carrying.going(name), new Spot(0, 0, 0)));
    }

    /** Whether the thing called {@code name} is at rest: on the ground, or in the frame of what carries it. */
    public boolean resting(final String name) {
        return resting.containsKey(name);
    }

    /** What the thing called {@code name} stands on; nothing, where it is not at rest. */
    public Set<String> under(final String name) {
        return supports.under(name);
    }

    /** How many things the check has looked at since the world began: none in a tick nothing happened in. */
    public long checks() {
        return checks;
    }

    /** How many things are in motion. */
    public int moving() {
        return moving.size();
    }
}
