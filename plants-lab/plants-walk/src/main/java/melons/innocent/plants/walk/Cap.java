package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Dial;
import melons.innocent.plants.tree.Stage;

import java.util.Arrays;
import java.util.Locale;

/**
 * The finest level any plant of a place may be drawn at, whatever its distance, the eye's speed or any other rule
 * would have: a cap over them all, for finding out by eye which change of level it is that is seen. Set at twigs
 * gone, nothing has every twig however near, so the change from the finest is nowhere to be seen; set at boughs,
 * the change to twigs gone is gone too; and so down to trunks alone. What a rule already draws coarser than the cap
 * is as the rule has it: the cap coarsens, and never refines.
 *
 * <p>It is a dial with a step to each level, by the level's name, chosen from a list in the walk's window while
 * walking. To begin with it is at the finest, which caps nothing, unless {@code -Dforest.cap=} names another level.
 */
public final class Cap {

    private final Dial dial;

    /** A cap as the run says it, or capping nothing. */
    public Cap() {
        this.dial = new Dial("Plants no finer than", Arrays.stream(Stage.values())
                .map(stage -> stage.name().toLowerCase(Locale.ROOT).replace('_', ' ')).toList(),
                new WoodDrawn.Held(System.getProperty("forest.cap", "")).stage().orElse(Stage.FINE).ordinal());
    }

    /** Its dial, for a place to say among its own. */
    public Dial dial() {
        return dial;
    }

    /** The finest level anything is drawn at, as it now stands. */
    public Stage stage() {
        return Stage.values()[dial.step()];
    }

    /** {@code looking} with nothing finer than the cap now allows. */
    public Looking over(final Looking looking) {
        return new Capped(looking, stage());
    }
}
