package melons.innocent.testkit.loose;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Bindings;
import melons.innocent.core.interaction.Handled;
import melons.innocent.core.interaction.Hands;
import melons.innocent.core.interaction.Menu;
import melons.innocent.core.interaction.Mode;
import melons.innocent.core.interaction.Offer;
import melons.innocent.core.interaction.Offers;
import melons.innocent.core.interaction.Relation;
import melons.innocent.core.interaction.Strength;
import melons.innocent.core.interaction.Use;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The hands of whoever walks the trial. Each step it is told the keys pressed and held, where the walker's eye is,
 * which way they face and how far up they look. What is at hand is the nearest thing before them that anything can
 * be done with; what can be done with it is what is bound to its kind ({@link Handled}), offered as they stand to
 * it, near or holding it, and barred where the thing is too heavy for them.
 *
 * <p>T does the first thing offered: near a thing that is carried, takes it up; holding one, puts it down. G opens
 * a menu of whatever else is offered, the arrows move its mark, T does what is marked and G shuts it. An action
 * either does its work at once, or leads into a state that lasts: thrown, a thing is first readied, held up, and
 * from then the trigger held gathers power and let go throws it, the way the walker faces and as far up as they
 * look and a little more, with whatever way they are going themselves; G or T leaves the state with the thing
 * still held. Every doing is first a {@link Use}, this action with this thing, and only that reaches the trial.
 *
 * <p>A thing held is out of the trial's world, and goes where the hands go. Crushed or eaten, a thing is gone; cut,
 * a melon is in slices where it lay. Nothing in the trial opens yet.
 */
public final class Handling {

    /** How far under the eye the hands are, and the feet; and how far before the hands a thing is held. */
    private static final double HANDS = 55;
    private static final double FEET = 160;
    private static final double BEFORE = 30;
    /** How far over the hands a thing readied to be thrown is held up, and how far over the look it is thrown. */
    private static final double RAISED = 45;
    private static final double LOFT = 20;

    private final Trial trial;
    private final Bindings bindings = new Handled().bindings();
    private final Strength strength = new Strength();
    private final Windup windup = new Windup();
    private Optional<Lifted> held = Optional.empty();
    private Optional<String> near = Optional.empty();
    private Optional<Hands> hands = Optional.empty();
    private Optional<Menu> menu = Optional.empty();
    private Optional<Mode> mode = Optional.empty();
    /** How the walker is going, from their last two steps; and how far up they look. */
    private Spot walking = new Spot(0, 0, 0);
    private double tilt;
    /** What the window's list has asked for since the last step. */
    private final List<Action> asked = new ArrayList<>();
    private String last = "";
    private long begun;
    private double joules = Double.parseDouble(System.getProperty("loose.joules", "500"));

    /** Hands on {@code trial}. */
    public Handling(final Trial trial) {
        this.trial = trial;
        this.begun = trial.begun();
    }

    /**
     * A step: told what {@code step} was, with the walker's eye at {@code head}, facing {@code yaw} degrees and
     * looking {@code looked} degrees up from level.
     */
    public void stepped(final Step step, final Spot head, final double yaw, final double looked) {
        if (trial.begun() != begun) {
            begun = trial.begun();
            emptied();
            last = "";
        }
        final Hands now = new Hands(head.plus(new Spot(0, -HANDS, 0)), yaw);
        final Spot went = hands.map(was -> now.at().minus(was.at()).times(trial.world().tick().rate()))
                .orElse(new Spot(0, 0, 0));
        // set down somewhere else all at once, it was not going there
        walking = went.length() > 20_000 ? new Spot(0, 0, 0) : went;
        hands = Optional.of(now);
        tilt = looked;
        final Optional<String> was = target();
        near = held.isPresent() ? Optional.empty() : now.atHand(offered());
        if (!was.equals(target())) {
            menu = Optional.empty();
        }
        final List<Action> wanted = new ArrayList<>();
        step.pressed().forEach(key -> pressed(key, step.held().contains("SHIFT")).ifPresent(wanted::add));
        wanted.addAll(asked);
        asked.clear();
        // one thing a step: what it does is not done until the trial's next tick
        if (!wanted.isEmpty()) {
            wanted(wanted.getFirst());
        }
        if (mode.equals(Optional.of(Mode.THROWING)) && held.isPresent()) {
            windup.stepped(step.triggered(), trial.world().tick().seconds()).ifPresent(this::thrown);
        } else {
            windup.clear();
        }
    }

    /** What {@code key} pressed asks to be done, where it asks for an action; and what it does to the menu. */
    private Optional<Action> pressed(final String key, final boolean shifted) {
        if (key.equals("T") && menu.isPresent()) {
            final Optional<Action> chosen = menu.get().chosen().map(Offer::action);
            menu = Optional.empty();
            return chosen;
        } else if (key.equals("T") && mode.isEmpty()) {
            return offers().stream().findFirst().map(Offer::action);
        } else if (key.equals("G") && menu.isPresent()) {
            menu = Optional.empty();
        } else if ((key.equals("G") || key.equals("T")) && mode.isPresent()) {
            last = "No longer " + mode.get().label().toLowerCase(Locale.ENGLISH) + ": " + target().orElse("");
            mode = Optional.empty();
        } else if (key.equals("G")) {
            final Menu others = new Menu(offered(target()).others(this::barred));
            menu = Optional.of(others).filter(listed -> !listed.empty());
            last = menu.isPresent() ? last : target().map(name -> "nothing more to do with " + name)
                    .orElse("nothing at hand");
        } else if (!shifted && (key.equals("UP") || key.equals("DOWN"))) {
            menu = menu.map(listed -> listed.moved(key.equals("UP") ? -1 : 1));
        }
        return Optional.empty();
    }

    /** Every thing in the trial that something is offered of to one near it, by name, and where it is. */
    private Map<String, Spot> offered() {
        final World world = trial.world();
        final Map<String, Spot> things = new TreeMap<>();
        for (final String name : world.things()) {
            if (bindings.offers(trial.kind(name), Relation.NEAR).primary().isPresent()) {
                things.put(name, world.placed(name).orElseThrow().at());
            }
        }
        return things;
    }

    /** {@code action} wanted of what is held or at hand: done, if it is offered and nothing bars it. */
    private void wanted(final Action action) {
        final Optional<String> target = target();
        final Optional<Offer> offer = offers().stream().filter(one -> one.action() == action).findFirst();
        if (target.isEmpty()) {
            last = "nothing at hand to " + action.id();
        } else if (offer.isEmpty()) {
            last = target.get() + " is not to " + action.id() + (held.isPresent() ? " while it is held" : " as it lies");
        } else if (offer.get().barred().isPresent()) {
            last = offer.get().said() + ": " + target.get();
        } else {
            last = offer.get().label() + ": " + target.get();
            done(new Use(target.get(), action));
        }
    }

    /** A use, done: all that reaches the trial of what the walker's hands do, at once or by a state it leads into. */
    private void done(final Use use) {
        menu = Optional.empty();
        near = Optional.empty();
        if (use.action().leads().isPresent()) {
            mode = use.action().leads();
            windup.clear();
            return;
        }
        mode = Optional.empty();
        switch (use.action()) {
            case CARRY -> {
                if (held.isPresent()) {
                    put(HANDS - FEET + held.get().rigid().shape().reaches() + 2, 10);
                } else {
                    held = Optional.of(trial.lifted(use.target()));
                }
            }
            case CUT -> trial.cut(use.target());
            case CRUSH, EAT -> {
                if (held.isEmpty()) {
                    trial.take(use.target());
                }
                held = Optional.empty();
            }
            default -> {
            }
        }
    }

    /** What is held, let go with so great a {@code share} of all its power: put back into the trial, and thrown. */
    private void thrown(final double share) {
        final Throw thrown = aimed(share);
        final String name = put(RAISED, 0);
        trial.thrown(name, thrown, walking);
        mode = Optional.empty();
        last = String.format(Locale.ENGLISH, "Thrown with %.0f J, %.0f degrees up: %s", thrown.joules(),
                thrown.upward(), name);
    }

    /** The throw a {@code share} of all the power makes, the way the walker faces and looks now. */
    private Throw aimed(final double share) {
        return new Throw(share * joules, hands.orElseThrow().facing(), Math.max(-60, Math.min(85, tilt + LOFT)));
    }

    /** What is held put back into the trial, so far up from the hands and so much further before them: its name. */
    private String put(final double up, final double further) {
        final Lifted lifted = held.orElseThrow();
        trial.put(lifted, new Placed(hands.orElseThrow().before(
                BEFORE + further + lifted.rigid().shape().reaches(), up), lifted.turned()));
        held = Optional.empty();
        return lifted.name();
    }

    /** Nothing held, nothing listed, and no state. */
    private void emptied() {
        held = Optional.empty();
        menu = Optional.empty();
        mode = Optional.empty();
        windup.clear();
    }

    /** What is written over the picture: what is held or at hand, the menu, or the throw being wound up. */
    public List<String> hints() {
        return new Told(this).hints();
    }

    /** What the window says of it: what is held or at hand, what is offered of it, and what was last done. */
    public String said() {
        return new Told(this).said();
    }

    /** The name of what is held, or of what is at hand and still there; nothing, where there is neither. */
    public Optional<String> target() {
        return held.map(Lifted::name).or(() -> near.filter(name -> trial.world().things().contains(name)));
    }

    /** What can be offered of the thing called {@code name}, as the walker stands to it: nothing, of nothing. */
    private Offers offered(final Optional<String> name) {
        final Relation relation = held.isPresent() ? Relation.HELD : Relation.NEAR;
        return name.map(thing -> bindings.offers(kind(thing), relation)).orElse(new Offers(List.of(), relation));
    }

    /** Which kind of thing the one called {@code name} is, held or in the trial. */
    Node kind(final String name) {
        return held.map(Lifted::kind).orElseGet(() -> trial.kind(name));
    }

    /** Why {@code action} cannot be done with what is held or at hand just now; nothing, where it can. */
    private Optional<String> barred(final Action action) {
        return target().flatMap(name -> strength.barred(action, held.map(lifted -> lifted.rigid().kilograms())
                .orElseGet(() -> trial.world().thing(name).kilograms())));
    }

    /** What is offered of what is held or at hand, each with why it cannot be done just now, where it cannot. */
    public List<Offer> offers() {
        return offered(target()).offered(this::barred);
    }

    /** The window's list asks for {@code action}, of what is held or at hand at the next step. */
    public void asked(final Action action) {
        asked.add(action);
    }

    /** What is held is thrown with so many {@code joules} when all its power has gathered, from now on. */
    public void full(final double energy) {
        joules = energy;
    }

    /** What is held; nothing, where the hands are empty. */
    public Optional<Lifted> held() {
        return held;
    }

    /** Where what is held is: before the hands, and held up where it is readied to be thrown. */
    public Optional<Placed> holding() {
        return held.flatMap(lifted -> hands.map(now -> new Placed(now.before(
                BEFORE + lifted.rigid().shape().reaches(), mode.isPresent() ? RAISED : 0), lifted.turned())));
    }

    /** Where the walker's feet are, once they have taken a step. */
    public Optional<Spot> feet() {
        return hands.map(now -> now.at().plus(new Spot(0, HANDS - FEET, 0)));
    }

    /** The name of what is at hand, where nothing is held and it is still there. */
    public Optional<String> atHand() {
        return held.isPresent() ? Optional.empty() : target();
    }

    /** The menu of what else can be done, where it is open. */
    public Optional<Menu> menu() {
        return menu;
    }

    /** The state what is held is in, where it is in one. */
    public Optional<Mode> mode() {
        return mode;
    }

    /** How much of all the power has gathered for a throw: 0 to 1. */
    public double power() {
        return windup.share();
    }

    /** What was last done, or why it was not, in words: nothing, where nothing has been. */
    public String last() {
        return last;
    }

    /**
     * The way what is held would fly if it were let go now, a spot each twentieth of a second until it is down at
     * the walker's feet: nothing, unless power is gathering for a throw.
     */
    public List<Spot> curve() {
        return mode.isPresent() && held.isPresent() && hands.isPresent() && windup.share() > 0
                ? new Arc(holding().orElseThrow().at(), aimed(windup.share()).of(held.get().rigid()).velocity()
                        .plus(walking), hands.get().at().up() + HANDS - FEET,
                        trial.world().gravity().pull()).spots() : List.of();
    }
}
