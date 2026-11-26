package melons.innocent.doors.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.places.yard.Wall;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The doors in their wall, on the card: the wall of common red brick as one mesh, and each door part by part, its
 * leaf turned as far as it has swung. The wall, the frames and the leaves are in the way, the leaves where they are
 * at the moment, so a door is walked through only once it is open. Glass is drawn after all else, the furthest door's
 * first. A list in the walk's window names every door: the one chosen is opened, or shut. The window says how each
 * stands.
 */
public final class DoorsPlace implements Place {

    private final Doors doors;
    private final List<Wall> walls;
    private final Map<Material, Looked> looks = new LinkedHashMap<>();
    private final List<DoorDrawn> drawn = new ArrayList<>();
    private Optional<Shaped> wall = Optional.empty();
    /** The window's list of doors: the one chosen is worked. */
    private final Dial working;
    private long worked;
    private long told;

    /** {@code doors} in the wall {@code showing} builds round them, on {@code card}. */
    public DoorsPlace(final Card card, final Laid laid, final Doors doors, final Showing showing) {
        this.doors = doors;
        this.walls = showing.walls();
        this.working = new Dial("Open or shut", doors.doorways().stream().map(Doors.Doorway::name).toList());
        final Shades shades = new Shades();
        try {
            final Forming forming = new Forming(showing.middle());
            walls.forEach(run -> run.formed(forming));
            wall = Optional.of(new Shaped(card, laid, forming.formed()));
            looked(card, laid, shades, new Materials.CommonRedBrick());
            for (final Doors.Doorway doorway : doors.doorways()) {
                drawn.add(new DoorDrawn(card, laid, doorway.stood().door(),
                        material -> looked(card, laid, shades, material)));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
        this.told = doors.changes();
    }

    /** The look of {@code material}, made the first time it is asked for. */
    private Looked looked(final Card card, final Laid laid, final Shades shades, final Material material) {
        return looks.computeIfAbsent(material, of -> new Looked(card, new Look(laid, shades.of(of))));
    }

    @Override
    public List<Dial> dials() {
        return List.of(working);
    }

    /** What the window's list asks, each asking done once: the door chosen opened, or shut. */
    @Override
    public void readied(final Camera camera) {
        if (working.asks() != worked) {
            worked = working.asks();
            doors.worked(doors.doorways().get(working.step()).name());
        }
    }

    @Override
    public Obstacles inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        walls.forEach(run -> blocks.add(run.inTheWay()));
        blocks.addAll(doors.inTheWay());
        return new Obstacles(blocks);
    }

    @Override
    public Optional<Obstacles> rearranged() {
        if (doors.changes() == told) {
            return Optional.empty();
        }
        told = doors.changes();
        return Optional.of(inTheWay());
    }

    @Override
    public String said() {
        return IntStream.range(0, doors.doorways().size()).mapToObj(doors::said)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        looks.get(new Materials.CommonRedBrick()).drawn(pass, wall.orElseThrow(), new Mat4(), camera, 1);
        for (int door = 0; door < drawn.size(); door++) {
            drawn.get(door).drawn(pass, camera, doors.doorways().get(door).stood(), doors.swung(door), false);
        }
    }

    /** The doors' glass, the furthest door's first. */
    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        IntStream.range(0, drawn.size()).boxed()
                .sorted(Comparator.comparingDouble((Integer door) -> -doors.doorways().get(door).stood()
                        .reached(doors.swung(door)).minus(camera.eye()).length()))
                .forEach(door -> drawn.get(door).drawn(pass, camera, doors.doorways().get(door).stood(),
                        doors.swung(door), true));
    }

    @Override
    public void close() {
        drawn.forEach(DoorDrawn::close);
        drawn.clear();
        wall.ifPresent(Shaped::close);
        wall = Optional.empty();
        looks.values().forEach(Looked::close);
        looks.clear();
    }
}
