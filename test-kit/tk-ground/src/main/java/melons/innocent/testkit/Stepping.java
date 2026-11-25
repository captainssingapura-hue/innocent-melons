package melons.innocent.testkit;

import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A way of going about a place with something else kept in step with it, and told what each step was told: the keys
 * held and pressed in it, with the going as that step left it, so that where the avatar stands and which way it
 * faces can be asked. For every step the going takes, {@code tick} is run once, so the other thing is as far on as
 * the run is, however the frames fall, in a film as in a window, and a run kept and played back tells it the same.
 * It may add keys of its own to those the window says, and lines written over the picture, low in its middle.
 *
 * @param roamer how the place is gone about
 * @param tick   what is run for each step it takes, told the step and the going
 * @param keys   the keys it answers to besides the going's own, as the window says them
 * @param hints  what is written over the picture, line by line, asked each frame
 */
public record Stepping(Roamer roamer, BiConsumer<Step, Roaming> tick, List<String> keys,
                       Supplier<List<String>> hints) implements Roamer {

    public Stepping {
        keys = List.copyOf(keys);
    }

    /** A going with something kept in step with it, with no keys of its own and nothing written over the picture. */
    public Stepping(final Roamer roamer, final BiConsumer<Step, Roaming> tick) {
        this(roamer, tick, List.of(), List::of);
    }

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final Told told = new Told(source, tick);
        final Roaming going = roamer.roaming(told, keeping, skies);
        told.going(going);
        return new Ticked(going, told, hints);
    }

    @Override
    public List<String> keys() {
        final List<String> all = new ArrayList<>(roamer.keys());
        all.addAll(keys);
        return all;
    }
}
