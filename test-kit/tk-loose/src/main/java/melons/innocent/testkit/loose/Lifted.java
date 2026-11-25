package melons.innocent.testkit.loose;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Rigid;

/**
 * A thing taken up out of the trial's world, in someone's hands: what it is, what it is made of, which kind of thing
 * it is, and how it was turned when it was taken up, which is how it is held and put down again.
 *
 * @param rigid  the thing
 * @param stuff  what it is made of
 * @param kind   which kind of thing it is
 * @param turned how it was turned when it was taken up
 */
public record Lifted(Rigid rigid, Stuff stuff, Node kind, Turn turned) {

    /** What it is called. */
    public String name() {
        return rigid.name();
    }
}
