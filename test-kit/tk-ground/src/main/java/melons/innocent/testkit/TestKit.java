package melons.innocent.testkit;

import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Walk;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.shade.Laid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The test kit: a base map and the few things a bench stands on it, walked round with the test engine, which is the
 * game engine as it is on level ground. A bench says its map, adds its things, and walks:
 *
 * <pre>{@code
 * new TestKit("The house, by itself", new BaseMap(Outline.CIRCLE, 1000, Grounds.TURF))
 *         .with(Homestead::new)
 *         .walked();
 * }</pre>
 *
 * <p>A thing is whatever makes a {@link Place} on the card it is given: what is drawn of it and what is in the way
 * of it, standing wherever it says it stands. The avatar starts at the map's middle, facing along it. Every option
 * a walk has, the kit's walk has: scripts, kept runs, replays and films, the bot, the sky and the shadows. A bench
 * that opens the walk some other way of its own takes {@link #placing()} and hands it to whatever it opens.
 */
public record TestKit(String title, BaseMap map, List<BiFunction<Card, Laid, Place>> standing) {

    public TestKit {
        standing = List.copyOf(standing);
    }

    /** A kit called {@code title}, with nothing yet stood on {@code map}. */
    public TestKit(final String title, final BaseMap map) {
        this(title, map, List.of());
    }

    /** The same kit with what {@code thing} makes stood on its map besides. */
    public TestKit with(final BiFunction<Card, Laid, Place> thing) {
        final List<BiFunction<Card, Laid, Place>> more = new ArrayList<>(standing);
        more.add(thing);
        return new TestKit(title, map, more);
    }

    /** What makes its place on a card: the map and everything stood on it, for a walk to be handed. */
    public BiFunction<Card, Laid, Place> placing() {
        return (card, laid) -> new TestGround(card, laid, map, standing);
    }

    /** Walked round on foot and in the air, until a window is closed or the run is up. */
    public void walked() throws IOException {
        new Walk(title, placing()).run();
    }

    /** The same, its figures moved as {@code roamer} moves them. */
    public void walked(final Roamer roamer) throws IOException {
        new Walk(title, placing(), roamer).run();
    }
}
