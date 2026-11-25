package melons.innocent.doors.walk;

import com.example.fxsuite.vulkan.bench.Afoot;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Stepping;
import melons.innocent.testkit.TestKit;

import java.io.IOException;
import java.util.List;

/**
 * Swing doors, walked through on a test ground: a brick wall across the way with four doors in it, one of each
 * design, hung and opening different ways. Walk up to one and press T to open it or shut it; a leaf waits where you
 * stand in its way.
 */
public final class DoorWalk {

    private DoorWalk() {
    }

    public static void main(final String[] args) throws IOException {
        final Showing showing = new Showing();
        final Doors doors = new Doors(showing.doorways());
        final DoorHands hands = new DoorHands(doors);
        new TestKit("Swing doors: four designs in a wall", new BaseMap())
                .with((card, laid) -> new DoorsPlace(card, laid, doors, showing))
                .walked(new Stepping(new Afoot(), (step, going) -> hands.stepped(step, going.head(1), going.yaw(1)),
                        List.of("  T: open or shut the door at hand"), hands::hints));
    }
}
