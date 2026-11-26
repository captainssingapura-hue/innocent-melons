package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The whole state of a world at a tick, as a value: every thing in it, where each stands or how each moves, what
 * stands on what, what is driven and what rides, what is still to be checked, and which tick is to be taken next.
 * Nothing in it changes. A tick gives the next state from this one and what is asked ({@link Core#ticked}), so
 * a state can be kept, compared with another, handed on, or begun from again, and from the same state and the same
 * things asked the next is always the same.
 *
 * <p>Whatever keeps a state between ticks keeps only this; no engine keeps any of it.
 *
 * @param ground  the ground the world is on
 * @param tick    the tick to be taken next: its count is how many have been taken since the world began
 * @param things  every thing in the world, as it is described, by its name
 * @param resting where each thing at rest stands
 * @param moving  how each thing in motion stands and moves
 * @param on      what each thing at rest stands on
 * @param holds   what each thing holds up: the other way round
 * @param driving how each driven thing is going
 * @param riding  what each thing that rides is carried by
 * @param dirty   the things at rest that something has happened to since they were last checked
 * @param looked  whether the things have been looked over for any described as run into each other
 * @param checks  how many things the check has looked at since the world began
 */
public record State(Ground ground, Tick tick, Map<String, Rigid> things, Map<String, Placed> resting,
                    Map<String, Simulator.Mover> moving, Map<String, Set<String>> on,
                    Map<String, Set<String>> holds, Map<String, Moved> driving, Map<String, String> riding,
                    Set<String> dirty, boolean looked, long checks) {

    public State {
        things = Collections.unmodifiableSortedMap(new TreeMap<>(things));
        resting = Collections.unmodifiableSortedMap(new TreeMap<>(resting));
        moving = Collections.unmodifiableSortedMap(new TreeMap<>(moving));
        on = new Sets(on).frozen();
        holds = new Sets(holds).frozen();
        driving = Collections.unmodifiableSortedMap(new TreeMap<>(driving));
        riding = Collections.unmodifiableSortedMap(new TreeMap<>(riding));
        dirty = Collections.unmodifiableSortedSet(new TreeSet<>(dirty));
    }

    /** A world with nothing in it yet, on {@code ground}, its first tick still to be taken. */
    public State(final Ground ground, final Ticker ticker) {
        this(ground, ticker.first(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Set.of(),
                false, 0);
    }

    /**
     * This state with {@code rigid} stood in it as it is described: taken as at rest until the check says
     * otherwise, which of a thing whose shape never moves it never does.
     */
    public State stood(final Rigid rigid, final Placed placed) {
        if (things.containsKey(rigid.name())) {
            throw new IllegalArgumentException("there is already a thing called " + rigid.name());
        }
        final Map<String, Rigid> all = new TreeMap<>(things);
        all.put(rigid.name(), rigid);
        final Map<String, Placed> stands = new TreeMap<>(resting);
        stands.put(rigid.name(), placed);
        final Set<String> unchecked = new TreeSet<>(dirty);
        // what never moves is never checked: it is at rest, whatever is or is not under it
        if (!rigid.unmoving()) {
            unchecked.add(rigid.name());
        }
        return new State(ground, tick, all, stands, moving, on, holds, driving, riding, unchecked, looked, checks);
    }

    /**
     * This state with {@code rigid} stood in it as a driven thing, going as {@code going} says until it is driven
     * otherwise: never checked and never let go, and a frame for whatever rests on it.
     */
    public State driven(final Rigid rigid, final Placed placed, final Moved going) {
        final State stood = stood(rigid, placed);
        final Set<String> unchecked = new TreeSet<>(stood.dirty);
        unchecked.remove(rigid.name());
        final Map<String, Moved> driven = new TreeMap<>(driving);
        driven.put(rigid.name(), going);
        return new State(ground, tick, stood.things, stood.resting, moving, on, holds, driven, riding, unchecked,
                looked, checks);
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
        return Optional.ofNullable(moving.get(name)).map(Simulator.Mover::moved).orElseGet(
                () -> new Moved(new Carrying(driving, riding).going(name), new Spot(0, 0, 0)));
    }

    /** What the thing called {@code name} stands on; nothing, where it is not at rest. */
    public Set<String> under(final String name) {
        return Set.copyOf(on.getOrDefault(name, Set.of()));
    }
}
