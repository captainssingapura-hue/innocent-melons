package melons.innocent.building.walk;

import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.HouseSpec;
import com.example.fxsuite.existence.Elements;
import melons.innocent.building.generic.GenericHouse;
import melons.innocent.forms.Facets;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A building walked round. The house's door is among what the house says is in the way, and the avatar walking at it
 * from before its front is stopped there; with its doors left out, as a walk leaves them, it walks in through the
 * doorway and is stopped by the back wall, inside, and nothing but the door is left undrawn. The walk starts before
 * the middle of its front, facing it, on a map reaching well past it.
 */
class DoorlessTest {

    private final Structure house = new GenericHouse().build(new HouseSpec<>(900, 700, 2, 300, 3));

    @Test
    void theAvatarIsStoppedByTheHousesDoorAndWalksInOnceItsDoorsAreLeftOut() {
        final double front = house.waysIn().getFirst().along();
        final double door = house.waysIn().getFirst().across();
        final Avatar stopped = walked(house.shape(), door, front + 800);
        assertTrue(stopped.body().position().z() > front, "stopped before its front: " + stopped.body().position());
        final List<Volume> doors = StandIns.prisms(house.solids(new Elements.Door()));
        assertTrue(!doors.isEmpty() && house.shape().containsAll(doors), "its door is in the way, as built");
        final Doorless open = new Doorless(house);
        assertEquals(house.shape().size() - doors.size(), open.shape().size(), "only its doors are left out");
        assertEquals(house.solids().size() - doors.size(), open.solids().size(), "and only they are not drawn");
        final Avatar inside = walked(open.shape(), door, front + 800);
        assertTrue(inside.body().position().z() < front - 100, "inside: " + inside.body().position());
        assertTrue(inside.body().position().z() > -front, "and stopped by the back wall");
    }

    @Test
    void theWalkStartsBeforeTheMiddleOfItsFrontFacingItOnAMapReachingWellPastIt() {
        final WalkRound round = new WalkRound("The house", house, (Facets.Coat coat) -> null);
        assertEquals("0.00,11.80,180", round.start(), "eight metres before the furthest forward of it");
        assertEquals(60, round.map().metres(), "the least a map reaches, for a small house");
    }

    /** The avatar after eight seconds of walking back along the world from {@code x}, {@code z}, by the engine. */
    private Avatar walked(final List<Volume> shape, final double x, final double z) {
        final Obstacles inTheWay = Obstacles.NONE.with(shape, IntStream.range(0, shape.size()).boxed().toList());
        Avatar now = new Avatar(Body.standing(new Vec3(x, 0, z), 180), false);
        for (int step = 0; step < 960; step++) {
            now = now.step(new Controls(1, 0, 0, 0, true), Clock.STEP, inTheWay).avatar();
        }
        return now;
    }
}
