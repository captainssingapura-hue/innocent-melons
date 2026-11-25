package melons.innocent.core.rest;

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
 * <p>A tick, in order: what is destroyed is taken away; the check runs over what that touched, supporters before what
 * they hold; the things in motion are stepped; and those that have settled go to rest. What happened is given back.
 * It is kept as it is ticked, and from the same beginning and the same things destroyed at the same ticks it is the
 * same world every time.
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
    /** The things at rest that something has happened to since they were last checked. */
    private final Set<String> dirty = new TreeSet<>();
    /** Whether the things have been looked over for any described as run into each other. */
    private boolean looked;
    /** How many things the check has looked at, since the world began. */
    private long checks;

    /** A world on {@code ground}, whatever nothing drives stepped by {@code loose}. */
    public World(final Ground ground, final Simulator loose) {
        this.ground = ground;
        this.loose = loose;
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

    /** The world a tick of so many {@code seconds} on, the things named {@code destroyed} first taken out of it. */
    public List<Happened> ticked(final Collection<String> destroyed, final double seconds) {
        final List<Happened> happened = new ArrayList<>();
        new TreeSet<>(destroyed).forEach(gone -> destroyed(gone, happened));
        if (!looked) {
            overlapping(happened);
            looked = true;
        }
        checked(seconds, happened);
        for (final String name : lowestFirst(moving.keySet())) {
            final Simulator.Stepped stepped = loose.stepped(things.get(name), moving.get(name),
                    (rigid, placed) -> touches(name, rigid, placed, true), seconds);
            moving.put(name, stepped.next());
            stepped.on().ifPresent(on -> happened.add(new Happened.Landed(name, on)));
        }
        rested(happened);
        return happened;
    }

    private void destroyed(final String gone, final List<Happened> happened) {
        if (things.remove(gone) == null) {
            throw new IllegalArgumentException("nothing is called " + gone);
        }
        dirty.addAll(supports.over(gone));
        supports.gone(gone);
        resting.remove(gone);
        moving.remove(gone);
        dirty.remove(gone);
        happened.add(new Happened.Destroyed(gone));
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
        dirty.retainAll(resting.keySet());
        while (!dirty.isEmpty()) {
            final String name = lowestFirst(dirty).getFirst();
            dirty.remove(name);
            checks++;
            final List<Touch> touches = touches(name, things.get(name), resting.get(name), false);
            if (reckoning.of(things.get(name), resting.get(name), touches, seconds).held()) {
                supports.stands(name, touches.stream().map(Touch::on).collect(Collectors.toSet()));
            } else {
                moving.put(name, new Simulator.Mover(resting.remove(name)));
                supports.over(name).stream().filter(resting::containsKey).forEach(dirty::add);
                supports.left(name);
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
     * Where {@code rigid}, called {@code name} and placed so, is touched from below: by the ground, by the things at
     * rest and, where {@code landedToo}, by the things in motion that have come down and stopped.
     */
    private List<Touch> touches(final String name, final Rigid rigid, final Placed placed,
                                final boolean landedToo) {
        final List<Touch> touches = new ArrayList<>(touching.grounded(rigid.shape(), placed, ground));
        resting.forEach((other, stands) -> {
            if (!other.equals(name)) {
                touches.addAll(touching.held(rigid.shape(), placed, other, things.get(other).shape(), stands));
            }
        });
        moving.forEach((other, mover) -> {
            if (landedToo && mover.landed() && !other.equals(name)) {
                touches.addAll(touching.held(rigid.shape(), placed, other, things.get(other).shape(), mover.placed()));
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

    /** How the thing called {@code name} is moving: not at all, where it is at rest. */
    public Moved moved(final String name) {
        return Optional.ofNullable(moving.get(name)).map(Simulator.Mover::moved).orElseGet(Moved::new);
    }

    /** Whether the thing called {@code name} is at rest. */
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
