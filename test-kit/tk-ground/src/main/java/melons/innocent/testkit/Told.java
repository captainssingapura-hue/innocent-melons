package melons.innocent.testkit;

import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.play.Where;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A walk's source of steps, listened in on: every step it tells is told on to something else, once the going has
 * taken it. A step is asked for just before the going takes it; so when the next one is asked for, or the frame's
 * last has been taken, the one before is told on, with the going as that step left it. So whatever is told goes
 * step for step with the walk, however the steps fall into frames, and the same for the same steps.
 */
final class Told implements Source {

    private final Source source;
    private final BiConsumer<Step, Roaming> tick;
    private Optional<Roaming> going = Optional.empty();
    private Optional<Step> last = Optional.empty();

    Told(final Source source, final BiConsumer<Step, Roaming> tick) {
        this.source = source;
        this.tick = tick;
    }

    /** The going its steps are told to, once it is made. */
    void going(final Roaming made) {
        going = Optional.of(made);
    }

    /** The last step told, told on, if it has not been yet. */
    void caught() {
        final Optional<Step> taken = last;
        last = Optional.empty();
        if (going.isPresent()) {
            taken.ifPresent(step -> tick.accept(step, going.get()));
        }
    }

    @Override
    public Step step(final long step, final int left) {
        caught();
        final Step told = source.step(step, left);
        last = Optional.of(told);
        return told;
    }

    @Override
    public boolean over(final long steps) {
        return source.over(steps);
    }

    @Override
    public long length() {
        return source.length();
    }

    @Override
    public boolean ends() {
        return source.ends();
    }

    @Override
    public Optional<Where> check(final long step) {
        return source.check(step);
    }
}
