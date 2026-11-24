package melons.innocent.testkit;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Gate;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A base map on the card with whatever is stood on it: the place a test walks. It draws its own ground, which ends
 * at the map's edge, and then each thing stood on it; what is in the way is the wall round its edge and whatever
 * each thing says is in the way of it. A thing stood on it is a place itself, made on the same card, so anything
 * that can be walked round by itself can be stood here with nothing changed. Closed, the things go from the card,
 * the last made first, and then its ground.
 */
public final class TestGround implements Place {

    private final BaseMap map;
    private final Formed ground;
    private final List<Place> stood = new ArrayList<>();
    private final List<Obstacles> inTheWay = new ArrayList<>();

    /** {@code map} on {@code card}, and on it what each of {@code standing} makes, in their order. */
    public TestGround(final Card card, final Laid laid, final BaseMap map,
                      final List<BiFunction<Card, Laid, Place>> standing) {
        this.map = map;
        this.ground = new Formed(card, new Look(laid, map.surface()), map.formed());
        try {
            standing.forEach(placing -> stood.add(placing.apply(card, laid)));
            inTheWay.add(together());
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The map's wall and what is in the way of each thing stood on it, as one. */
    private Obstacles together() {
        final List<Block> blocks = new ArrayList<>(map.inTheWay());
        final List<Gate> gates = new ArrayList<>();
        final List<Volume> volumes = new ArrayList<>();
        for (final Place place : stood) {
            final Obstacles its = place.inTheWay();
            blocks.addAll(its.blocks());
            gates.addAll(its.gates());
            volumes.addAll(its.volumes());
        }
        return new Obstacles(blocks, gates, IntStream.range(0, blocks.size()).boxed().toList(), volumes,
                IntStream.range(0, volumes.size()).map(volume -> blocks.size() + volume).boxed().toList());
    }

    /** The base map it stands on. */
    public BaseMap map() {
        return map;
    }

    @Override
    public Obstacles inTheWay() {
        return inTheWay.getFirst();
    }

    @Override
    public boolean onTheFlat() {
        return false;
    }

    @Override
    public void readied(final Camera camera) {
        stood.forEach(place -> place.readied(camera));
    }

    @Override
    public boolean settled() {
        return stood.stream().allMatch(Place::settled);
    }

    @Override
    public String said() {
        return IntStream.rangeClosed(0, stood.size()).mapToObj(line -> line == 0 ? map.said()
                : stood.get(line - 1).said()).filter(line -> !line.isBlank())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        ground.drawn(pass, new Mat4(), camera, 1);
        stood.forEach(place -> place.drawn(pass, camera));
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        stood.forEach(place -> place.seenThrough(pass, camera));
    }

    @Override
    public void close() {
        for (int place = stood.size() - 1; place >= 0; place--) {
            stood.get(place).close();
        }
        stood.clear();
        ground.close();
    }
}
