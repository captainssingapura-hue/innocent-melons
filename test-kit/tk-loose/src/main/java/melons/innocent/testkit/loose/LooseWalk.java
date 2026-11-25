package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.Afoot;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.TestKit;
import melons.innocent.testkit.Ticking;

import java.io.IOException;

/**
 * The trial of the core engine's world at rest and its loose body, walked round on a test ground: a crate that
 * falls, a stack that comes down when its pillar goes, and a beam that tips when one of its two pillars goes. The
 * supports are taken away from the walk's window, or of themselves {@code -Dloose.after=} seconds in.
 */
public final class LooseWalk {

    private LooseWalk() {
    }

    public static void main(final String[] args) throws IOException {
        final Trial trial = new Trial();
        new TestKit("Loose bodies: the world at rest, and what falls", new BaseMap())
                .with((card, laid) -> new LoosePlace(card, laid, trial))
                .walked(new Ticking(new Afoot(), trial::ticked));
    }
}
