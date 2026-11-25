package melons.innocent.testkit;

import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;

import java.util.List;
import java.util.Optional;

/**
 * A way of going about a place with something else kept in step with it: whatever a bench has that changes by the
 * simulation's own steps and is no part of what goes about, as things that fall and come to rest are. For every
 * step the going takes, {@code tick} is run once, so the other thing is as far on as the run is, however the frames
 * fall, in a film as in a window.
 *
 * @param roamer how the place is gone about
 * @param tick   what is run for each step it takes
 */
public record Ticking(Roamer roamer, Runnable tick) implements Roamer {

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        return new Ticked(roamer.roaming(source, keeping, skies), tick);
    }

    @Override
    public List<String> keys() {
        return roamer.keys();
    }
}
