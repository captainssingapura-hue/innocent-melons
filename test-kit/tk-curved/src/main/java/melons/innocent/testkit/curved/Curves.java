package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Pavement;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.World;
import melons.innocent.core.sheet.Arched;
import melons.innocent.core.sheet.Banked;
import melons.innocent.core.sheet.Round;
import melons.innocent.core.sheet.Straight;
import melons.innocent.core.sheet.Swept;
import melons.innocent.core.surface.Facts;
import melons.innocent.physics.existences.Conditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * The trial of curved surfaces: three shells standing in a world at rest, ahead of where the walk starts. A spiral
 * ramp of concrete, nine metres out to its middle line and four across, two turns to the right climbing 3.6 m a
 * turn, banked a little toward its inside, its foot before the walker; to the right a vault of timber sixteen metres
 * across, three high and fourteen long, open at both ends and high enough to walk through; to the left a dome of bronze seven metres out and 2.2 m
 * high. Each is one sheet, and everything said or drawn of it comes from that sheet. Their sizes are their
 * author's.
 */
final class Curves implements Facts {

    static final String RAMP = "the spiral ramp";
    static final String VAULT = "the vault";
    static final String DOME = "the dome";

    private final World world;
    private final Map<String, Material> made = new LinkedHashMap<>();

    /** The three as they stand. */
    Curves() {
        final Simulator still = (rigid, now, around, tick) -> new Simulator.Stepped(now, Optional.empty());
        final Shell ramp = new Shell(new Swept(new Round(new Spot(0, 6, 0), 900, 270, 2, 360),
                new Banked(-200, 200, -0.03)), 30);
        final Shell vault = new Shell(new Swept(new Straight(new Spot(0, 0, -700), 0, 1_400),
                new Arched(800, 300)), 30);
        final Shell dome = new Shell(new Swept(new Round(new Spot(0, 0, 0), 700, 0, 1, 0),
                new Arched(0, 700, 700, (700.0 * 700 + 220.0 * 220) / (2 * 220), 220)), 30);
        this.world = new World(new Ground(), still)
                .stood(new Rigid(RAMP, ramp, 2_400), new Placed(new Spot(300, 0, 1_500)))
                .stood(new Rigid(VAULT, vault, 600), new Placed(new Spot(2_700, 0, 1_200)))
                .stood(new Rigid(DOME, dome, 8_700), new Placed(new Spot(-2_000, 0, 1_000)));
        made.put(RAMP, new Materials.Concrete());
        made.put(VAULT, new Materials.Timber());
        made.put(DOME, new Materials.Bronze());
    }

    /** The world at rest they stand in. */
    World world() {
        return world;
    }

    /** What each is called, in the order they were stood. */
    List<String> names() {
        return List.copyOf(made.keySet());
    }

    /** The shell called {@code name}. */
    Shell shell(final String name) {
        return (Shell) world.thing(name).shape();
    }

    /** Where the thing called {@code name} stands. */
    Placed placed(final String name) {
        return world.placed(name).orElseThrow();
    }

    /** What the thing called {@code name} is made of. */
    Material made(final String name) {
        return made.get(name);
    }

    /**
     * The three as the game engine has them in its avatar's way: the engine's own pavement, told each sheet's
     * height at every spot, in bands of height where a shell passes over itself, to be stood on; and each shell's
     * sides and underside ({@link Sided}), to be run into.
     */
    Obstacles inTheWay() {
        final List<Volume> volumes = new ArrayList<>();
        for (final String name : names()) {
            new Decked(shell(name), placed(name)).banded().forEach(band -> volumes.add(new Pavement(band)));
            volumes.addAll(new Sided(shell(name), placed(name)).walls());
        }
        return Obstacles.NONE.with(volumes, IntStream.range(0, volumes.size()).boxed().toList());
    }

    @Override
    public Node material(final String provider, final Spot spot) {
        return made.containsKey(provider) ? made.get(provider) : new Materials.Turf();
    }

    @Override
    public Node condition(final String provider, final Spot spot) {
        return new Conditions.Dry();
    }
}
