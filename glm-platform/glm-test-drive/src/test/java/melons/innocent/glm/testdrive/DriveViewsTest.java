package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.flight.Chasing;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.glm.model.Proportions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The views: C going through them in turn and round again; V to the driver's seat and back to the view it was in;
 * from the hood, looking ahead as the car heads, over its front, and R held looking back from over its tail; and the
 * straight's start, in its lane short of the start line, heading along it.
 */
class DriveViewsTest {

    private final GlmChassis glm = new GlmChassis(new Proportions());

    @Test
    void cInTurnAndVToTheSeatAndBack() {
        final DriveViews views = new DriveViews(glm, DriveViews.View.CHASE_FAR);
        for (final DriveViews.View expected : List.of(DriveViews.View.CHASE_NEAR, DriveViews.View.HOOD,
                DriveViews.View.COCKPIT, DriveViews.View.BUMPER, DriveViews.View.CHASE_FAR)) {
            views.told(pressed("C"));
            assertEquals(expected, views.view());
        }
        views.told(pressed("C"));
        views.told(pressed("V"));
        assertEquals(DriveViews.View.COCKPIT, views.view());
        views.told(pressed("V"));
        assertEquals(DriveViews.View.CHASE_NEAR, views.view(), "back to the view it was in");
    }

    @Test
    void fromTheHoodAheadAndRHeldBack() {
        final DriveViews views = new DriveViews(glm, DriveViews.View.HOOD);
        final Place at = new Place(0, 0, glm.ride(), 90);
        final Camera ahead = views.camera(new Chasing(), new Vec3(0, 0, 0), at, 0, 0);
        assertTrue(ahead.ahead().x() > 0.99, "looking as it heads: " + ahead.ahead());
        views.told(new Step(Set.of("R"), List.of(), 0, 0, false, 0, 0, false));
        final Camera back = views.camera(new Chasing(), new Vec3(0, 0, 0), at, 0, 0);
        assertTrue(back.ahead().x() < -0.99, "looking back: " + back.ahead());
        assertTrue(back.eye().x() < ahead.eye().x() - 200, "from over its tail");
    }

    @Test
    void theStraightsStartInItsLaneShortOfTheLine() {
        final Place start = new StraightTrack().start(glm.ride());
        assertEquals(0, start.yaw(), 1e-9);
        assertTrue(start.x() > 0 && start.x() < 600, "in the right hand lane");
        assertTrue(start.z() < new Strip.Lines().start(), "short of the start line");
        assertEquals(1_900, (new Strip.Lines().finish() - new Strip.Lines().start()) / 100, 1e-9, "1.9 km to run");
    }

    private Step pressed(final String key) {
        return new Step(Set.of(), List.of(key), 0, 0, false, 0, 0, false);
    }
}
