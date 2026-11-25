package melons.innocent.doors.walk;

import com.example.fxsuite.existence.Things;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Handled;
import melons.innocent.core.interaction.Hands;
import melons.innocent.core.interaction.Offer;
import melons.innocent.core.interaction.Relation;
import melons.innocent.core.interaction.Use;
import melons.innocent.core.motion.Spot;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The hands of whoever walks up to the doors. Each step it is told the keys pressed, where the walker's eye is and
 * which way they face. The door at hand is the nearest whose leaf is within reach before them, from either side;
 * what can be done with it is what the core engine binds to the kind of thing a door is, which is to open it or shut
 * it; and T does that. Then the doors are a tick on, each leaf waiting where the walker stands in its way.
 */
public final class DoorHands {

    /** How far under the eye the hands are, and the feet. */
    private static final double HANDS = 55;
    private static final double FEET = 160;

    private final Doors doors;
    private final List<Offer> offers = new Handled().bindings().offers(new Things.Door(), Relation.NEAR).offered();
    private Optional<String> atHand = Optional.empty();
    private Optional<Use> last = Optional.empty();

    /** Hands on {@code doors}. */
    public DoorHands(final Doors doors) {
        this.doors = doors;
    }

    /** A step: told what {@code step} was, with the walker's eye at {@code head}, facing {@code yaw} degrees. */
    public void stepped(final Step step, final Vec3 head, final double yaw) {
        final Map<String, Spot> leaves = new TreeMap<>();
        for (int door = 0; door < doors.doorways().size(); door++) {
            final Vec3 leaf = doors.doorways().get(door).stood().reached(doors.swung(door));
            leaves.put(doors.doorways().get(door).name(), new Spot(leaf.x(), leaf.y(), leaf.z()));
        }
        atHand = new Hands(new Spot(head.x(), head.y() - HANDS, head.z()), yaw).atHand(leaves);
        if (step.pressed().contains("T") && atHand.isPresent() && !offers.isEmpty()) {
            final Use use = new Use(atHand.get(), offers.getFirst().action());
            if (use.action() == Action.OPEN) {
                doors.worked(use.target());
            }
            last = Optional.of(use);
        }
        doors.ticked(new Vec3(head.x(), head.y() - FEET, head.z()));
    }

    /** The name of the door at hand; none, where none is within reach. */
    public Optional<String> atHand() {
        return atHand;
    }

    /** What was last done; nothing, where nothing has been. */
    public Optional<Use> last() {
        return last;
    }

    /** What is written over the picture: the door at hand, and what T does with it. */
    public List<String> hints() {
        return atHand.filter(name -> !offers.isEmpty())
                .map(name -> List.of(name, "[T] " + offers.getFirst().label())).orElse(List.of());
    }
}
