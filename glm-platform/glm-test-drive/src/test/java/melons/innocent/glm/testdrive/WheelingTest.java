package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Vehicle;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.model.BodyFigures;
import melons.innocent.glm.model.GlmPlatform;
import melons.innocent.glm.model.Proportions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The wheels as the car is driven: each front tyre steered about its own middle, which stays put, its front turned to
 * the right as the wheel is; a rear tyre not steered; every tyre turning as it rolls, its top going ahead; a part away
 * from the wheels not moved; and the gearbox that makes the engine's revs, changing up as the car goes faster, never
 * past the red line, idling at a stop.
 */
class WheelingTest {

    private final Proportions suv = new Proportions();
    private final Wheeling wheeling = new Wheeling(suv);
    private final Assembled built = new Assembly(new GlmPlatform(suv, new BodyFigures()).platform()).assembled();

    @Test
    void eachFrontTyreSteeredAboutItsOwnMiddleAndRearNot() {
        for (final Side side : new Side[] {Side.RIGHT, Side.LEFT}) {
            final double x = (side == Side.LEFT ? -1 : 1) * suv.halfTrack();
            final Vec3 middle = new Vec3(x, suv.tyre(), 0);
            final Mat4 m = wheeling.moved(part("front tyre", side), 20, 0);
            assertEquals(0, moved(m, middle).minus(middle).length(), 1e-9, side + " its middle stays put");
            assertTrue(moved(m, middle.plus(new Vec3(0, 0, 30))).x() > middle.x() + 5, side + " its front to the right");
        }
        final Mat4 rear = wheeling.moved(part("tyre", Side.RIGHT), 20, 0);
        final Vec3 back = new Vec3(suv.halfTrack(), suv.tyre(), suv.back() + 30);
        assertEquals(0, moved(rear, back).minus(back).length(), 1e-9, "a rear tyre not steered");
    }

    @Test
    void everyTyreTurningAsItRollsAndTheRestStill() {
        final Mat4 m = wheeling.moved(part("tyre", Side.LEFT), 0, 20);
        final Vec3 top = new Vec3(-suv.halfTrack(), 2 * suv.tyre(), suv.back());
        assertTrue(moved(m, top).z() > suv.back() + 5, "its top going ahead");
        final Mat4 still = wheeling.moved(part("front control arm", Side.RIGHT), 30, 100);
        assertEquals(0, moved(still, top).minus(top).length(), 1e-9, "a link not moved");
    }

    @Test
    void theGearboxChangingUpAsItGoesFasterNeverPastTheRedLine() {
        final AutoBox box = new AutoBox(Vehicle.SALOON.with(Engine.V6));
        box.step(0, 0);
        assertEquals(Engine.V6.idle(), box.rpm(), 1e-9, "idling at a stop");
        int last = 1;
        for (double speed = 1; speed < 65; speed += 1) {
            box.step(speed, 1);
            assertTrue(box.gear() >= last, "never down while speeding up, at " + speed);
            assertTrue(box.rpm() <= Engine.V6.redline() + 1e-9);
            last = box.gear();
        }
        assertTrue(last >= 4, "up through its gears: " + last);
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }

    private Vec3 moved(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }
}
