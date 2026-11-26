package melons.innocent.core.rest;

import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;

import java.util.Optional;
import java.util.Set;

/**
 * A world of things at rest and in motion, ticked: the keeper of a world's state between its ticks, and nothing
 * more. The state is a value ({@link State}); a tick is worked out by the core ({@link Core}), which is handed the
 * state as it is and what is asked and gives the state a tick on; and the world then keeps that one in place of the
 * one before. So this is the one place a world changes, and all it changes is which state it keeps.
 *
 * <p>Nearly all of a world is at rest, each thing standing on what holds it, and costs nothing: a tick checks only
 * the things something has happened to that could have changed how they are held ({@link Reckoning}). A thing found
 * no longer held is in motion from that tick, and so, in turn and in the same tick, is whatever stood on it. What is
 * in motion is stepped by the simulator the world is given, the lowest first, until it has come down, been held
 * without moving for a little while, and stands at rest again on what it came down on.
 *
 * <p>A thing may be driven: it goes as it is told, whatever is on it or under it, and what rests on it rides it, at
 * rest in its frame and carried with it. What leaves a driven thing, let go or thrown, keeps the way it was being
 * carried: so a thing thrown sideways from a train goes on along the train's way as it flies.
 *
 * <p>A thing may be taken up out of the world, into someone's hands, and stood in it again later, where it is put
 * down or as it is thrown: meanwhile it is nowhere in it, and holds nothing up.
 *
 * <p>It counts its own ticks ({@link Tick}), each as long as its one ticker said when it was made: nobody who
 * ticks it says how long a tick is, and how long it has gone on is its count and nothing else. From the same
 * beginning and the same things asked of it at the same ticks it is the same world every time.
 */
public final class World {

    private final Core<?> core;
    private State state;

    /** A world on {@code ground}, whatever nothing drives stepped by {@code loose}, ticked as the game steps. */
    public World(final Ground ground, final Simulator<?> loose) {
        this(ground, loose, new Ticker());
    }

    /** The same, ticked by {@code ticker}: how long its every tick is, said this once. */
    public World(final Ground ground, final Simulator<?> loose, final Ticker ticker) {
        this(ground, new Gravity(), loose, ticker);
    }

    /** The same, pulling as {@code gravity} says: a world on the Moon, or on Mars. */
    public World(final Ground ground, final Gravity gravity, final Simulator<?> loose, final Ticker ticker) {
        this(new Core<>(loose), new State(ground, gravity, ticker));
    }

    /** A world that goes by {@code core}, as {@code state} says it is: one begun anew, or one kept and taken up. */
    public World(final Core<?> core, final State state) {
        this.core = core;
        this.state = state;
    }

    /**
     * {@code rigid} stood in the world as it is described: taken as at rest until the check says otherwise, which
     * of a thing whose shape never moves it never does.
     */
    public World stood(final Rigid rigid, final Placed placed) {
        state = state.stood(rigid, placed);
        return this;
    }

    /**
     * {@code rigid} stood in the world as a driven thing, going as {@code going} says until it is driven otherwise:
     * never checked and never let go, and a frame for whatever rests on it.
     */
    public World driven(final Rigid rigid, final Placed placed, final Moved going) {
        state = state.driven(rigid, placed, going);
        return this;
    }

    /**
     * The world a tick on, what is {@code asked} of it done first: the tick is the world's own next one, as long
     * as every other, and what happened is given back stamped with it.
     */
    public Stamped ticked(final Asked asked) {
        final Next next = core.ticked(state, asked);
        state = next.state();
        return next.stamped();
    }

    /** The whole state of the world as it is now: a value, which a later tick does not change. */
    public State state() {
        return state;
    }

    /** The core the world goes by. */
    public Core<?> core() {
        return core;
    }

    /** How hard the world pulls everything in it down. */
    public Gravity gravity() {
        return state.gravity();
    }

    /** The tick to be taken next: its count is how many the world has taken since it began. */
    public Tick tick() {
        return state.tick();
    }

    /** The names of every thing in it. */
    public Set<String> things() {
        return Set.copyOf(state.things().keySet());
    }

    /** The thing called {@code name}. */
    public Rigid thing(final String name) {
        return state.thing(name);
    }

    /** Where the thing called {@code name} is, at rest or in motion; nowhere, where there is none. */
    public Optional<Placed> placed(final String name) {
        return state.placed(name);
    }

    /**
     * How the thing called {@code name} is moving over the ground: as it goes, where it is in motion; as it is
     * driven or carried, where it is driven or rides; not at all, where it rests on what does not move.
     */
    public Moved moved(final String name) {
        return state.moved(name);
    }

    /** Whether the thing called {@code name} is at rest: on the ground, or in the frame of what carries it. */
    public boolean resting(final String name) {
        return state.resting().containsKey(name);
    }

    /** What the thing called {@code name} stands on; nothing, where it is not at rest. */
    public Set<String> under(final String name) {
        return state.under(name);
    }

    /** How many things the check has looked at since the world began: none in a tick nothing happened in. */
    public long checks() {
        return state.checks();
    }

    /** How many things are in motion. */
    public int moving() {
        return state.moving().size();
    }
}
