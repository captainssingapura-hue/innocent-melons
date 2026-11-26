package melons.innocent.building.walk;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.building.spec.Walling;
import melons.innocent.building.generic.GenericPointBlock;
import melons.innocent.building.generic.GenericSlabBlock;
import melons.innocent.forms.Facets;
import melons.innocent.forms.Tint;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The housing board's blocks walked into, as a walk has them: started before the first way in, the avatar walking
 * straight on goes in, into the slab block's core off the corridor and up its stair's first flight to the half
 * landing, and under the point block's north flat along its porch into the lift lobby; and from the lobby, up the
 * point block's stair's first flight to its half landing.
 */
class StairsWalkTest {

    private final Structure slab = new GenericSlabBlock().build(new SlabBlockSpec<>(13_440, 1_205, 180, 12, 360, 280,
            new Finish(Walling.BRICK, Optional.empty(), new Tint("#3c3f44"), new Tint("#6f8fa3"))));
    private final Structure point = new GenericPointBlock().build(new PointBlockSpec<>(20, 900));

    @Test
    void theWalkStartsBeforeTheFirstWayInFacingIt() {
        final Structure.WayIn core = slab.waysIn().getFirst();
        assertEquals(String.format(Locale.ROOT, "%.2f", core.across() / 100),
                new WalkRound("slab", slab, (Facets.Coat coat) -> null).start().split(",")[0], "before a core");
        assertEquals("2.95", new WalkRound("point", point, (Facets.Coat coat) -> null).start().split(",")[0],
                "before the north flat's porch");
    }

    @Test
    void intoTheSlabBlocksCoreAndUpItsStairToTheHalfLanding() {
        final Structure.Stair stair = slab.stairs().getFirst();
        final Avatar in = walked(slab, stair.foot().across(), 1_000, 180, 8);
        assertTrue(in.body().position().z() < stair.foot().along(), "in, and onto the stair: " + in.body().position());
        assertTrue(up(in) > 170, "up to its half landing, 1.8 m: " + up(in));
    }

    @Test
    void underThePointBlocksNorthFlatIntoItsLobbyAndUpItsStair() {
        final Structure.WayIn way = point.waysIn().getFirst();
        final Avatar in = walked(point, way.across(), way.along() + 800, 180, 12);
        assertTrue(Math.abs(in.body().position().z()) < 450, "in the lobby: " + in.body().position());
        final Structure.Stair stair = point.stairs().getFirst();
        final Avatar up = walked(point, stair.foot().across() + 100, stair.foot().along(), 270, 6);
        assertTrue(up(up) > 170, "up to its half landing, 1.8 m: " + up(up) + " at " + up.body().position());
    }

    /** How high the avatar stands. */
    private double up(final Avatar avatar) {
        return -avatar.body().position().y();
    }

    /** The avatar after {@code seconds} walking on from ({@code x}, {@code z}), facing {@code yaw}, by the engine. */
    private Avatar walked(final Structure building, final double x, final double z, final double yaw,
                          final int seconds) {
        final var shape = new Doorless(building).shape();
        final Obstacles inTheWay = Obstacles.NONE.with(shape, IntStream.range(0, shape.size()).boxed().toList());
        Avatar now = new Avatar(Body.standing(new Vec3(x, 0, z), yaw), false);
        for (int step = 0; step < seconds * 120; step++) {
            now = now.step(new Controls(1, 0, 0, 0, true), Clock.STEP, inTheWay).avatar();
        }
        return now;
    }
}
