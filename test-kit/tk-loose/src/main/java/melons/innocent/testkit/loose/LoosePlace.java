package melons.innocent.testkit.loose;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
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
import melons.innocent.core.rest.Placed;
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

    private final Trial trial;
    private final Map<Stuff, Looked> looks = new EnumMap<>(Stuff.class);
    private final Map<String, Shaped> shapes = new TreeMap<>();
    /** The things that can be taken away, in the order the trial describes them. */
    private final List<String> names;
    /** The window's choice: nothing, then each thing by its name, then everything back again. */
    private final Dial taken;
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
        final Shades shades = new Shades();
        try {
            for (final Stuff stuff : Stuff.values()) {
                looks.put(stuff, new Looked(card, new Look(laid, shades.of(stuff.material()))));
            }
            for (final String name : trial.world().things()) {
                final Box box = trial.world().thing(name).box();
                shapes.put(name, new Shaped(card, laid, new Cuboid(box.across(), box.up(), box.along()).formed()));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
        this.told = trial.changes();
    }

    @Override
    public List<Dial> dials() {
        return List.of(taken);
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
            trial.again();
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
                blocks.add(standsIn(world.thing(name).box(), world.placed(name).orElseThrow()));
            }
        }
        return new Obstacles(blocks);
    }

    /**
     * What stands in for a box placed so, to the avatar. Standing square it is itself. Leaning, it is a ramp along
     * its longest way, from its lower end up to its higher, as wide as it is and solid down to the ground under it,
     * so it is walked up and not through. Standing on end, it is the box round its corners.
     */
    private Block standsIn(final Box box, final Placed placed) {
        if (Math.abs(placed.turned().w()) > 0.999_999) {
            return new Block(placed.at().across(), placed.at().along(), box.across() / 2, box.along() / 2, 0,
                    placed.at().up() - box.up() / 2, placed.at().up() + box.up() / 2);
        }
        final int longest = box.across() >= box.up() && box.across() >= box.along() ? 0
                : box.up() >= box.along() ? 1 : 2;
        final Spot reach = placed.turned().turned(new Spot(longest == 0 ? 1 : 0, longest == 1 ? 1 : 0,
                longest == 2 ? 1 : 0)).times(reach(box, longest) / 2);
        final Spot upper = reach.up() >= 0 ? reach : reach.times(-1);
        final List<Spot> corners = placed.corners(box);
        final double high = corners.stream().filter(corner -> corner.minus(placed.at()).dot(upper) > 0)
                .mapToDouble(Spot::up).max().orElseThrow();
        final double low = corners.stream().filter(corner -> corner.minus(placed.at()).dot(upper) < 0)
                .mapToDouble(Spot::up).max().orElseThrow();
        if (Math.hypot(upper.across(), upper.along()) < 1) {
            final double across = corners.stream().mapToDouble(corner -> Math.abs(corner.across()
                    - placed.at().across())).max().orElseThrow();
            final double along = corners.stream().mapToDouble(corner -> Math.abs(corner.along()
                    - placed.at().along())).max().orElseThrow();
            return new Block(placed.at().across(), placed.at().along(), across, along, 0, 0, Math.max(high, low));
        }
        final Spot foot = placed.at().minus(upper);
        final Spot head = placed.at().plus(upper);
        return Block.ramp(foot.across(), foot.along(), low, head.across(), head.along(), high,
                Math.max(reach(box, (longest + 1) % 3), reach(box, (longest + 2) % 3)));
    }

    /** How far a box reaches the way numbered: 0 across, 1 up, 2 along. */
    private double reach(final Box box, final int way) {
        return way == 0 ? box.across() : way == 1 ? box.up() : box.along();
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
