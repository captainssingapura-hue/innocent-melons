package melons.innocent.testkit.loose;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Relation;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.ShapeImpl;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The trial's things on the card: each a box of its own size, in the lab's material it is made of, drawn where the
 * core's world has it at the moment, at rest or falling or tipping. What is at rest is in the avatar's way, as a box
 * where it stands square and as a ramp where it leans, so a leaning beam is walked up and not through; what is in
 * motion is not. A list in the walk's window names every thing of the trial: the one chosen is taken away, and
 * its last entry begins the trial again. Another names every thing that moves: the one chosen is thrown, with the
 * energy, the way up and the way round the sliders under it say, and what came of the last throw is said. A
 * third sets the wagon going, as fast as its slider says, or stops it. The window says how many things are at rest
 * and in motion and how many the check has looked at, which stands still while
 * nothing happens.
 *
 * <p>What the walker holds is drawn before their hands, held up where it is readied to be thrown, and over what is
 * at hand a small light hangs. While a throw is wound up, a row of lights shows the way the thing would fly if it
 * were let go now. A fourth list asks for anything that can be done with what is held or at hand, by its name
 * ({@link Handling}); and what is thrown from the hands has, at full power, the energy the throwing slider says.
 */
public final class LoosePlace implements Place {

    private static final String AGAIN = "everything back: begin again";
    /** How many points go round a round thing's mesh. */
    private static final int ROUND = 48;

    /** How big the light over what is at hand is, and how far over the thing it hangs. */
    private static final double LIGHT = 7;
    private static final double OVER = 14;

    private final Card card;
    private final Laid laid;
    private final Trial trial;
    private final Handling handling;
    private Optional<Looked> light = Optional.empty();
    private Optional<Shaped> lamp = Optional.empty();
    private final Map<Stuff, Looked> looks = new EnumMap<>(Stuff.class);
    private final Map<String, Shaped> shapes = new TreeMap<>();
    /** The things that can be taken away, in the order the trial describes them. */
    private final List<String> names;
    /** The window's list to take away from: each thing by its name, then everything back again. */
    private final Dial taken;
    /** The window's list to throw from, each thing that moves by its name; and how it is thrown. */
    private final Dial thrown;
    private final Dial joules = new Dial("Thrown with, joules", 50, 5_000,
            Double.parseDouble(System.getProperty("loose.joules", "500")));
    private final Dial upward = new Dial("Thrown so far up from level, degrees", 0, 85,
            Double.parseDouble(System.getProperty("loose.up", "45")));
    private final Dial round = new Dial("Thrown which way round, degrees from ahead", 0, 360,
            Double.parseDouble(System.getProperty("loose.round", "90")));
    /** The window's list that sets the wagon going or stops it, and how fast it is set going. */
    private final Dial wagon = new Dial("The wagon", List.of("set going", "stopped"));
    private final Dial speed = new Dial("The wagon's speed when set going, km an hour", 5, 100,
            Double.parseDouble(System.getProperty("loose.wagon", "36")));
    /** How steep the ramp is made when the trial is next begun again. */
    private final Dial slope;
    /** The window's list of what can be done, with what is held or at hand. */
    private final Dial doing = new Dial("Do, with what is held or at hand", java.util.Arrays.stream(Action.values())
            .map(action -> action.offered(Relation.NEAR).map(near -> action.offered(Relation.HELD)
                    .filter(held -> !held.equals(near)).map(held -> near + " / " + held).orElse(near))
                    .orElseGet(() -> action.offered(Relation.HELD).orElse(action.id()))).toList());
    /** What the walker stands on, asked as any mover asks; and the window's list of states to put the ramp in. */
    private final Footing footing;
    private final Dial ramped;
    /** How many askings of each list have been done. */
    private long did;
    private long took;
    private long threw;
    private long drove;
    private long told;

    /** The {@code trial}'s things on {@code card}, their layouts as {@code laid} says, handled by nobody. */
    public LoosePlace(final Card card, final Laid laid, final Trial trial) {
        this(card, laid, trial, new Handling(trial));
    }

    /** The {@code trial}'s things on {@code card}, and what {@code handling} holds and has at hand. */
    public LoosePlace(final Card card, final Laid laid, final Trial trial, final Handling handling) {
        this.card = card;
        this.laid = laid;
        this.trial = trial;
        this.handling = handling;
        this.footing = new Footing(trial);
        this.ramped = new Dial("The ramp's surface is", footing.conditions().stream().map(Node::name).toList(), 0);
        this.names = trial.names();
        final List<String> choices = new ArrayList<>(names);
        choices.add(AGAIN);
        this.taken = new Dial("Take away", choices);
        this.thrown = new Dial("Throw", trial.movable());
        this.slope = new Dial("The ramp's slope when begun again, degrees", 0, 30, trial.slope());
        final Shades shades = new Shades();
        try {
            for (final Stuff stuff : Stuff.values()) {
                looks.put(stuff, new Looked(card, new Look(laid, shades.of(stuff.material()))));
            }
            light = Optional.of(new Looked(card, new Look(laid, shades.of(new Materials.LitPaper()))));
            lamp = Optional.of(new Shaped(card, laid,
                    new com.example.fxsuite.vulkan.form.Ellipsoid(LIGHT, LIGHT, LIGHT).formed(16, 8)));
            shaped();
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
        this.told = trial.changes();
    }

    /** Every thing now in the world that has no shape on the card yet, given one: the slices of a melon cut. */
    private void shaped() {
        for (final String name : trial.world().things()) {
            if (!shapes.containsKey(name)) {
                shapes.put(name, new Shaped(card, laid, meshed(trial.world().thing(name).shape())));
            }
        }
    }

    /** A thing's shape as a mesh: a box or a slab its six faces, a round thing round. */
    private Mesh meshed(final ShapeImpl<?> shape) {
        return switch (shape) {
            case Box box -> new Cuboid(box.across(), box.up(), box.along()).formed();
            case Slab slab -> new Cuboid(slab.box().across(), slab.box().up(), slab.box().along()).formed();
            case Ellipsoid round -> new com.example.fxsuite.vulkan.form.Ellipsoid(round.across(), round.up(),
                    round.along()).formed(ROUND, ROUND / 2);
            case Shell shell -> throw new IllegalArgumentException("the trial has no shells: " + shell);
        };
    }

    @Override
    public List<Dial> dials() {
        return List.of(doing, taken, slope, ramped, thrown, joules, upward, round, wagon, speed);
    }

    /**
     * What the window's lists ask, each asking done once: the thing chosen taken away at the next tick, if it is
     * still there, or the trial begun again; and the thing chosen thrown, as the sliders then stand.
     */
    @Override
    public void readied(final Camera camera) {
        shaped();
        handling.full(joules.value());
        footing.conditioned(footing.conditions().get(ramped.step()));
        if (doing.asks() != did) {
            did = doing.asks();
            handling.asked(Action.values()[doing.step()]);
        }
        if (taken.asks() != took) {
            took = taken.asks();
            if (taken.step() >= names.size()) {
                trial.again(slope.value());
            } else {
                trial.take(names.get(taken.step()));
            }
        }
        if (thrown.asks() != threw) {
            threw = thrown.asks();
            trial.thrown(trial.movable().get(thrown.step()),
                    new Throw(joules.value(), round.value(), upward.value()));
        }
        if (wagon.asks() != drove) {
            drove = wagon.asks();
            trial.driven(wagon.step() == 0 ? speed.value() : 0);
        }
    }

    /** Each thing at rest, in the way: as its own box where it stands square, as a ramp where it leans. */
    @Override
    public Obstacles inTheWay() {
        final World world = trial.world();
        final List<Block> blocks = new ArrayList<>();
        for (final String name : world.things()) {
            // what is going, the wagon and what rides it, is in nobody's way until it stops
            if (world.resting(name) && world.moved(name).velocity().length() == 0) {
                blocks.add(new StandIn().of(world.thing(name).shape(), world.placed(name).orElseThrow()));
            }
        }
        return new Obstacles(blocks);
    }


    @Override
    public Optional<Obstacles> rearranged() {
        if (trial.changes() == told) {
            return Optional.empty();
        }
        told = trial.changes();
        return Optional.of(inTheWay());
    }

    @Override
    public String said() {
        final World world = trial.world();
        final String going = names.stream().filter(name -> world.things().contains(name) && !world.resting(name))
                .reduce((one, other) -> one + ", " + other).map(moving -> " (" + moving + ")").orElse("");
        return String.format(Locale.ENGLISH, "Loose bodies: %d at rest, %d in motion%s; %d checked since it began",
                world.things().size() - world.moving(), world.moving(), going, world.checks())
                + System.lineSeparator() + footing.said(handling.feet())
                + System.lineSeparator() + handling.said()
                + trial.flown().map(flown -> System.lineSeparator() + flown.said()).orElse("")
                + trial.struck().map(struck -> System.lineSeparator() + struck).orElse("");
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        final World world = trial.world();
        final Map<String, Stuff> made = trial.made();
        for (final String name : world.things()) {
            if (shapes.containsKey(name)) {
                looks.get(made.get(name)).drawn(pass, shapes.get(name),
                        stands(world.placed(name).orElseThrow()), camera, 1, made.get(name).tint());
            }
        }
        // what is held, before the hands; and a light over what is at hand
        handling.held().filter(held -> shapes.containsKey(held.name())).ifPresent(held ->
                looks.get(held.stuff()).drawn(pass, shapes.get(held.name()),
                        stands(handling.holding().orElseThrow()), camera, 1, held.stuff().tint()));
        // the way what is being wound up would fly, a light each twentieth of a second
        handling.curve().forEach(spot -> light.orElseThrow().drawn(pass, lamp.orElseThrow(),
                new Mat4(new Vec3(spot.across(), spot.up(), spot.along())), camera, 1, new Vec3(1, 0.95, 0.8)));
        handling.atHand().flatMap(world::placed).ifPresent(placed -> light.orElseThrow().drawn(pass,
                lamp.orElseThrow(), new Mat4(new Vec3(placed.at().across(), placed.at().up()
                        + world.thing(handling.atHand().orElseThrow()).shape().reaches() + OVER,
                        placed.at().along())), camera, 1, new Vec3(1, 0.72, 0.2)));
    }

    /** Where a thing placed so stands on the card: turned as it is, and moved to where its middle is. */
    private Mat4 stands(final Placed placed) {
        final Turn back = placed.turned().back();
        final Spot across = back.turned(new Spot(1, 0, 0));
        final Spot up = back.turned(new Spot(0, 1, 0));
        final Spot along = back.turned(new Spot(0, 0, 1));
        return new Mat4(new Vec3(placed.at().across(), placed.at().up(), placed.at().along())).times(new Mat4(
                new Vec3(across.across(), across.up(), across.along()), new Vec3(up.across(), up.up(), up.along()),
                new Vec3(along.across(), along.up(), along.along())));
    }

    @Override
    public void close() {
        shapes.values().forEach(Shaped::close);
        shapes.clear();
        looks.values().forEach(Looked::close);
        looks.clear();
        lamp.ifPresent(Shaped::close);
        lamp = Optional.empty();
        light.ifPresent(Looked::close);
        light = Optional.empty();
    }
}
