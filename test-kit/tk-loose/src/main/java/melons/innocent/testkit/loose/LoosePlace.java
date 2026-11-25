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
 * motion is not. A list in the walk's window names
 * every thing of the trial: the one chosen is taken away, and its last entry begins the trial again. The window
 * says how many things are at rest and in motion and how many the check has looked at, which stands still while
 * nothing happens.
 */
public final class LoosePlace implements Place {

    private static final String NOTHING = "nothing yet";
    private static final String AGAIN = "everything back: begin again";
    /** How many points go round a round thing's mesh. */
    private static final int ROUND = 48;

    private final Trial trial;
    private final Map<Stuff, Looked> looks = new EnumMap<>(Stuff.class);
    private final Map<String, Shaped> shapes = new TreeMap<>();
    /** The things that can be taken away, in the order the trial describes them. */
    private final List<String> names;
    /** The window's choice: nothing, then each thing by its name, then everything back again. */
    private final Dial taken;
    /** How steep the ramp is made when the trial is next begun again. */
    private final Dial slope;
    private int chosen;
    private long told;

    /** The {@code trial}'s things on {@code card}, their layouts as {@code laid} says. */
    public LoosePlace(final Card card, final Laid laid, final Trial trial) {
        this.trial = trial;
        this.names = trial.names();
        final List<String> choices = new ArrayList<>(List.of(NOTHING));
        choices.addAll(names);
        choices.add(AGAIN);
        this.taken = new Dial("Take away", choices, 0);
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
        return List.of(taken, slope);
    }

    /**
     * What the window's choice asks, done once each time the choice changes: the thing chosen taken away at the next
     * tick, if it is still there, or the trial begun again.
     */
    @Override
    public void readied(final Camera camera) {
        final int now = taken.step();
        if (now == chosen) {
            return;
        }
        if (now > names.size()) {
            trial.again(slope.value());
        } else if (now > 0) {
            trial.take(names.get(now - 1));
        }
        chosen = now;
    }

    /** Each thing at rest, in the way: as its own box where it stands square, as a ramp where it leans. */
    @Override
    public Obstacles inTheWay() {
        final World world = trial.world();
        final List<Block> blocks = new ArrayList<>();
        for (final String name : world.things()) {
            if (world.resting(name)) {
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
                world.things().size() - world.moving(), world.moving(), world.checks());
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
