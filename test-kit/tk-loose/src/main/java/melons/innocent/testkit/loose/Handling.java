package melons.innocent.testkit.loose;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Bindings;
import melons.innocent.core.interaction.Handled;
import melons.innocent.core.interaction.Hands;
import melons.innocent.core.interaction.Offer;
import melons.innocent.core.interaction.Relation;
import melons.innocent.core.interaction.Strength;
import melons.innocent.core.interaction.Use;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The hands of whoever walks the trial. Each step it is told the keys pressed, where the walker's eye is and which
 * way they face. What is at hand is the nearest thing before them that anything can be done with; what can be done
 * with it is what is bound to its kind ({@link Handled}), offered as they stand to it, near or holding it, and
 * barred where the thing is too heavy for them. T does the first thing offered: near a thing that is carried, takes
 * it up; holding one, puts it down. X throws what is held, the way they face, with the energy and the way up it is
 * told, and with whatever way they are going themselves. Anything else offered is asked for by name, from the
 * window's list. Every doing is first a {@link Use}, this action with this thing, and only that reaches the trial.
 *
 * <p>A thing held is out of the trial's world, and goes where the hands go. Crushed or eaten, a thing is gone; cut,
 * a melon is in slices where it lay. Nothing in the trial opens yet.
 */
public final class Handling {

    private static final double TICK = 1.0 / 120;
    /** How far under the eye the hands are, and the feet; and how far before the hands a thing is held. */
    private static final double HANDS = 55;
    private static final double FEET = 160;
    private static final double BEFORE = 30;

    private final Trial trial;
    private final Bindings bindings = new Handled().bindings();
    private final Strength strength = new Strength();
    private Optional<Lifted> held = Optional.empty();
    private Optional<String> near = Optional.empty();
    private Optional<Hands> hands = Optional.empty();
    /** How the walker is going, from their last two steps. */
    private Spot walking = new Spot(0, 0, 0);
    /** What the window's list has asked for since the last step. */
    private final List<Action> asked = new ArrayList<>();
    private String last = "";
    private long begun;
    private double joules = Double.parseDouble(System.getProperty("loose.joules", "500"));
    private double upward = Double.parseDouble(System.getProperty("loose.up", "45"));

    /** Hands on {@code trial}. */
    public Handling(final Trial trial) {
        this.trial = trial;
        this.begun = trial.begun();
    }

    /** A step: told what {@code step} was, with the walker's eye at {@code head}, facing {@code yaw} degrees. */
    public void stepped(final Step step, final Spot head, final double yaw) {
        if (trial.begun() != begun) {
            begun = trial.begun();
            held = Optional.empty();
            last = "";
        }
        final Hands now = new Hands(head.plus(new Spot(0, -HANDS, 0)), yaw);
        final Spot went = hands.map(was -> now.at().minus(was.at()).times(1 / TICK)).orElse(new Spot(0, 0, 0));
        // set down somewhere else all at once, it was not going there
        walking = went.length() > 20_000 ? new Spot(0, 0, 0) : went;
        hands = Optional.of(now);
        near = held.isPresent() ? Optional.empty() : now.atHand(offered());
        final List<Action> wanted = new ArrayList<>();
        for (final String key : step.pressed()) {
            if (key.equals("T")) {
                offers().stream().findFirst().ifPresent(offer -> wanted.add(offer.action()));
            } else if (key.equals("X")) {
                wanted.add(Action.THROW);
            }
        }
        wanted.addAll(asked);
        asked.clear();
        // one thing a step: what it does is not done until the trial's next tick
        if (!wanted.isEmpty()) {
            wanted(wanted.getFirst());
        }
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
            done(new Use(target.get(), action));
            last = offer.get().label() + ": " + target.get();
        }
    }

    /** A use, done: all that reaches the trial of what the walker's hands do. */
    private void done(final Use use) {
        switch (use.action()) {
            case CARRY -> {
                if (held.isPresent()) {
                    put(HANDS - FEET + held.get().rigid().shape().reaches() + 2, 10);
                } else {
                    held = Optional.of(trial.lifted(use.target()));
                }
            }
            case THROW -> {
                final String name = put(15, 0);
                trial.thrown(name, new Throw(joules, hands.orElseThrow().facing(), upward), walking);
            }
            case CUT -> trial.cut(use.target());
            case CRUSH, EAT -> {
                if (held.isEmpty()) {
                    trial.take(use.target());
                }
                held = Optional.empty();
            }
            case OPEN -> last = "";
        }
        near = Optional.empty();
    }

    /** What is held put back into the trial, so far up from the hands and so much further before them: its name. */
    private String put(final double up, final double further) {
        final Lifted lifted = held.orElseThrow();
        trial.put(lifted, new Placed(hands.orElseThrow().before(
                BEFORE + further + lifted.rigid().shape().reaches(), up), lifted.turned()));
        held = Optional.empty();
        return lifted.name();
    }

    /** The name of what is held, or of what is at hand and still there; nothing, where there is neither. */
    public Optional<String> target() {
        return held.map(Lifted::name).or(() -> near.filter(name -> trial.world().things().contains(name)));
    }

    /** What is offered of what is held or at hand, each with why it cannot be done just now, where it cannot. */
    public List<Offer> offers() {
        return target().map(name -> {
            final Node kind = held.map(Lifted::kind).orElseGet(() -> trial.kind(name));
            final double kilograms = held.map(lifted -> lifted.rigid().kilograms())
                    .orElseGet(() -> trial.world().thing(name).kilograms());
            return bindings.offers(kind, held.isPresent() ? Relation.HELD : Relation.NEAR)
                    .offered(action -> strength.barred(action, kilograms));
        }).orElse(List.of());
    }

    /** The window's list asks for {@code action}, of what is held or at hand at the next step. */
    public void asked(final Action action) {
        asked.add(action);
    }

    /** What is held is thrown with so many {@code joules}, so many degrees {@code up} from level, from now on. */
    public void throwing(final double energy, final double up) {
        joules = energy;
        upward = up;
    }

    /** What is held; nothing, where the hands are empty. */
    public Optional<Lifted> held() {
        return held;
    }

    /** Where what is held is, before the hands. */
    public Optional<Placed> holding() {
        return held.flatMap(lifted -> hands.map(now -> new Placed(
                now.before(BEFORE + lifted.rigid().shape().reaches(), 0), lifted.turned())));
    }

    /** The name of what is at hand, where nothing is held and it is still there. */
    public Optional<String> atHand() {
        return held.isPresent() ? Optional.empty() : target();
    }

    /** What is written over the picture: what is held or at hand, and what is offered of it, by its keys. */
    public List<String> hints() {
        return target().map(name -> {
            final List<String> said = new ArrayList<>();
            for (final Offer offer : offers()) {
                final String key = offer.primary() ? "[T] " : offer.action() == Action.THROW ? "[X] " : "";
                said.add(key + offer.barred().map(why -> offer.label() + " - " + why).orElse(offer.label()));
            }
            return List.of((held.isPresent() ? "HOLDING " : "") + name, String.join("   ", said));
        }).orElse(List.of());
    }

    /** What the window says of it: what is held or at hand and what is offered of it, and what was last done. */
    public String said() {
        final String at = target().map(name -> (held.isPresent() ? "In hand: " : "At hand: ") + name + ", a "
                + held.map(Lifted::kind).orElseGet(() -> trial.kind(name)).name().replace('-', ' ') + ": "
                + offers().stream().map(Offer::said).collect(Collectors.joining(", "))).orElse("Nothing at hand");
        return last.isEmpty() ? at : at + System.lineSeparator() + "Last: " + last;
    }
}
