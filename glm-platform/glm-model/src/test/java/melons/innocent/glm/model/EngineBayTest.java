package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engine in its bay: the V6 put in, covered, on its two mounts, met everywhere it was made to; its crank down the
 * car's middle 52 cm up, its block's rear face 10 cm ahead of the front axle, its front forward; covered or not, the
 * covers the only parts between; and every engine of the range clear of the subframe, the steering gear, the links and
 * the wheels.
 */
class EngineBayTest {

    private final Proportions suv = new Proportions();
    private final Touching touching = new Touching();

    @Test
    void theV6PutInCoveredOnItsTwoMounts() {
        final Assembled bare = new Assembly(new GlmPlatform(suv, new BodyFigures()).platform()).assembled();
        final Assembled fitted = new Assembly(new GlmPlatform().platform()).assembled();
        assertEquals(List.of(), fitted.problems(1e-6));
        final long engine = fitted.placed().keySet().stream().filter(p -> p.part().group() == Group.ENGINE
                || p.part().group() == Group.COVERS).count();
        assertEquals(fitted.placed().size() - bare.placed().size(), engine, "the engine and its mounts, all of it");
        final PartAt block = fitted.placed().keySet().stream().filter(p -> p.part().name().equals("V6 block"))
                .findFirst().orElseThrow();
        final Vec3 rear = fitted.at(block).plus(block.point("crank, rear").at());
        final Vec3 front = fitted.at(block).plus(block.point("crank, front").at());
        assertEquals(0, rear.minus(new Vec3(0, 52, 10)).length(), 1e-9, "its rear face 10 cm ahead of the front axle");
        assertEquals(0, front.minus(new Vec3(0, 52, 10 + new EngineRange().v6().length())).length(), 1e-9,
                "its timing end forward");
    }

    @Test
    void coveredOrNotTheCoversTheOnlyPartsBetween() {
        final EngineFigures v6 = new EngineRange().v6();
        final int covered = new Assembly(new GlmPlatform(suv, new BodyFigures(), new EngineBay(v6, true)).platform())
                .assembled().placed().size();
        final Assembled open = new Assembly(new GlmPlatform(suv, new BodyFigures(), new EngineBay(v6, false))
                .platform()).assembled();
        assertEquals(List.of(), open.problems(1e-6));
        assertEquals(2 + 2 + 1 + 1, covered - open.placed().size(), "two head covers, two coil sets, the timing and"
                + " the trim covers");
    }

    @Test
    void everyEngineClearOfTheSubframeSteeringLinksAndWheels() {
        // the boxers wait for a place of their own: low and wide, their heads lie where this bay's links run
        for (final EngineFigures e : new EngineRange().all().stream().filter(e -> !e.boxer()).toList()) {
            final Assembled built = new Assembly(new GlmPlatform(suv, new BodyFigures(), new EngineBay(e, true))
                    .platform()).assembled();
            assertEquals(List.of(), built.problems(1e-6), e.name());
            final List<PartAt> engine = built.placed().keySet().stream()
                    .filter(p -> (p.part().group() == Group.ENGINE || p.part().group() == Group.COVERS)
                            && !p.part().name().startsWith("engine mount"))
                    .toList();
            final List<PartAt> around = built.placed().keySet().stream()
                    .filter(p -> p.part().group() != Group.ENGINE && p.part().group() != Group.COVERS
                            && p.part().group() != Group.UNDERBODY).toList();
            for (final PartAt p : engine) {
                for (final PartAt q : around) {
                    assertTrue(!touching.touch(p.shape().form(), built.at(p), q.shape().form(), built.at(q)),
                            e.name() + ": " + p.said() + " meets " + q.said());
                }
            }
        }
    }
}
