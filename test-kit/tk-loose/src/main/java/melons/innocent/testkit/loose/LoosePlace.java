package melons.innocent.testkit.loose;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
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
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.ShapeImpl;
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
 */
public final class LoosePlace implements Place {

    private static final String AGAIN = "everything back: begin again";
    /** How many points go round a round thing's mesh. */
    private static final int ROUND = 48;

    private final Trial trial;
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
    /** How many askings of each list have been done. */
    private long took;
    private long threw;
    private long drove;
    private long told;

    /** The {@code trial}'s things on {@code card}, their layouts as {@code laid} says. */
    public LoosePlace(final Card card, final Laid laid, final Trial trial) {
        this.trial = trial;
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
            for (final String name : trial.world().things()) {
                shapes.put(name, new Shaped(card, laid, meshed(trial.world().thing(name).shape())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
        this.told = trial.changes();
    }

    /** A thing's shape as a mesh: a box or a slab its six faces, a round thing round. */
    private Mesh meshed(final ShapeImpl<?> shape) {
        return switch (shape) {
            case Box box -> new Cuboid(box.across(), box.up(), box.along()).formed();
            case Slab slab -> new Cuboid(slab.box().across(), slab.box().up(), slab.box().along()).formed();
            case Ellipsoid round -> new com.example.fxsuite.vulkan.form.Ellipsoid(round.across(), round.up(),
                    round.along()).formed(ROUND, ROUND / 2);
        };
    }

    @Override
    public List<Dial> dials() {
        return List.of(taken, slope, thrown, joules, upward, round, wagon, speed);
    }

    /**
     * What the window's lists ask, each asking done once: the thing chosen taken away at the next tick, if it is
     * still there, or the trial begun again; and the thing chosen thrown, as the sliders then stand.
     */
    @Override
    public void readied(final Camera camera) {
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
        return String.format(Locale.ENGLISH, "Loose bodies: %d at rest, %d in motion; %d checked since it began",
                world.things().size() - world.moving(), world.moving(), world.checks())
                + trial.flown().map(flown -> System.lineSeparator() + flown.said()).orElse("");
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        final World world = trial.world();
        final Map<String, Stuff> made = trial.made();
        for (final String name : world.things()) {
            looks.get(made.get(name)).drawn(pass, shapes.get(name), stands(world.placed(name).orElseThrow()),
                    camera, 1);
        }
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
    }
}
