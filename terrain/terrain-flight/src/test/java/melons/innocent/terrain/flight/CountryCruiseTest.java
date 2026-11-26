package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.afloat.Afloat;
import com.example.fxsuite.vulkan.afloat.Cruising;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.meadow.MeadowFeatures;
import melons.innocent.vehicles.drawn.BoatDrawn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cruising the meadow's river in the river boat, with no card. Where its river runs half way along, the water is deep
 * enough for the boat, a little way out from the bank if not in the very middle; and driven downstream at its cruising
 * throttle, steered back toward the river's course whenever it strays, it keeps on the water and goes on its way.
 */
class CountryCruiseTest {

    private static final double STEP = 1.0 / 120;
    private final LandFeatures meadow = MeadowFeatures.INSTANCE.features();
    private final Watered waters = new Watered(meadow);
    private final BoatDrawn boat = new BoatDrawn();

    @Test
    void halfWayAlongTheMeadowsRiverTheWaterIsDeepEnoughForTheBoat() {
        final Vec3 found = start();
        assertTrue(waters.deep(found.x(), found.z()) >= boat.draught() * 1.5,
                "deep enough: " + waters.deep(found.x(), found.z()));
    }

    @Test
    void drivenDownstreamAndSteeredAlongTheRiverItKeepsOnTheWaterAndGoesOnItsWay() {
        final List<Vec3> river = new Chosen().river();
        final Vec3 from = start();
        final Vec3 on = river.get(river.size() / 2 + 1).minus(river.get(river.size() / 2));
        Afloat now = new Afloat(new Vec3(from.x(), waters.top(from.x(), from.z()), from.z()),
                Math.toDegrees(Math.atan2(on.x(), on.z())));
        now = now.stepped(new Step(Set.of(), List.of("AHEAD"), 0, 0, false, 0, 0, false), STEP, boat, waters);
        double gone = 0;
        for (int step = 0; step < 120 * 40; step++) {
            final Vec3 was = now.at();
            now = now.stepped(new Step(Set.of(steering(now, river)), List.of(), 0, 0, false, 0, 0, false), STEP, boat,
                    waters);
            gone += now.at().minus(was).length();
            assertTrue(waters.deep(now.at().x(), now.at().z()) >= boat.draught(), "afloat at " + now.at());
        }
        assertTrue(gone > 10_000, "gone more than 100 m: " + gone / 100);
    }

    /** Where the boat starts: the water nearest half way along the river. */
    private Vec3 start() {
        final List<Vec3> river = new Chosen().river();
        final Vec3 here = river.get(river.size() / 2);
        return new Cruising(waters, "0,0,0", BoatDrawn::new).water(here, boat.draught() * 1.5);
    }

    /** The wheel put toward the river's course a little way ahead of {@code now}, or amidships if on it. */
    private String steering(final Afloat now, final List<Vec3> river) {
        Vec3 nearest = river.getFirst();
        int at = 0;
        for (int k = 0; k < river.size(); k++) {
            if (river.get(k).minus(now.at()).length() < nearest.minus(now.at()).length()) {
                nearest = river.get(k);
                at = k;
            }
        }
        final Vec3 aim = river.get(Math.min(river.size() - 1, at + 2)).minus(now.at());
        final double want = Math.toDegrees(Math.atan2(aim.x(), aim.z()));
        final double off = ((want - now.heading()) % 360 + 540) % 360 - 180;
        return off > 2 ? "D" : off < -2 ? "A" : "SPACE";
    }
}
