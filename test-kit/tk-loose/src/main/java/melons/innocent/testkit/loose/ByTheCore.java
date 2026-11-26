package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import melons.innocent.core.motion.Spot;

import java.util.List;
import java.util.Optional;

/**
 * A walk of the loose bodies' trial whose figure goes by the core: the core's walker, stepped by the walking engine
 * with the trial's things in one tick, in place of the old engine's avatar. It begins where the run says by
 * {@code -Dbench.at=x,z,yaw}, in metres and degrees, at the middle facing ahead unless said; and is drawn as the bot
 * the run names by {@code -Dbench.bot=}.
 *
 * @param trial the trial it walks
 */
record ByTheCore(Trial trial) implements Roamer {

    private static final double METRE = 100;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final String[] numbers = System.getProperty("bench.at", "0,0,0").split(",");
        if (numbers.length < 3) {
            throw new IllegalArgumentException("a walk starts so far across and along, in metres, and so far round,"
                    + " in degrees: " + System.getProperty("bench.at"));
        }
        return new Walked(source, keeping, skies, trial,
                new Bot(new Afoot.Said(System.getProperty("bench.bot", "man")).build()),
                new Spot(Double.parseDouble(numbers[0].trim()) * METRE, 0,
                        Double.parseDouble(numbers[1].trim()) * METRE), Double.parseDouble(numbers[2].trim()));
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, walking by the core's own engine:",
                "  mouse: turn, and look up or down (Esc lets it go)", "  W, S: ahead and back", "  A, D: sideways",
                "  Shift: walk, not run", "  Space: jump, as high as the world's pull lets it",
                "  Q, E: swing the view round; C: back behind", "  scroll: nearer or further",
                "  no jets and no sound yet: the old avatar has them, by -Dloose.avatar=old");
    }
}
