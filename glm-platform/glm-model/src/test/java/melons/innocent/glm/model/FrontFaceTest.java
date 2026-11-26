package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.engine.EngineRange;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's front face: its grille's panel, its grille and the chrome bar over it, a headlamp with its lens
 * and an indicator each side; all of it inside the wings and between the bumper and the bonnet's edge, behind the
 * bumper's face; clear of the bumper and of the V6 behind it.
 */
class FrontFaceTest {

    private final SantanaLines lines = new SantanaLines();
    private final Assembled built = new Assembly(new Santana().platform(new EngineBay(new EngineRange().all()
            .stream().filter(e -> e.name().equals("V6")).findFirst().orElseThrow(), true)).platform()).assembled();
    private final Touching touching = new Touching();
    private final Set<String> face = Set.of("grille panel", "grille", "grille bar", "right headlamp",
            "left headlamp", "right headlamp lens", "left headlamp lens", "right indicator", "left indicator");

    @Test
    void itsPartsEachWhereItBelongs() {
        assertEquals(List.of(), built.problems(1e-6));
        assertEquals(face, built.placed().keySet().stream().map(PartAt::said).filter(face::contains)
                .collect(Collectors.toSet()));
        final double k = lines.scale();
        for (final PartAt p : parts()) {
            for (final Vec3 v : p.shape().form().points()) {
                final Vec3 q = v.plus(built.at(p));
                assertTrue(Math.abs(q.x()) < lines.half(q.z(), q.y()), p.said() + " outside the wings at " + q);
                assertTrue(q.y() > 530 * k / 10 && q.y() < 760 * k / 10, p.said() + " out of the face at " + q);
                assertTrue(q.z() < 928 * k / 10 - 8, p.said() + " ahead of the bumper at " + q);
            }
        }
    }

    @Test
    void clearOfTheBumperAndTheEngine() {
        final List<PartAt> around = built.placed().keySet().stream().filter(p -> p.part().name().equals("front bumper")
                || p.part().group() == Group.ENGINE || p.part().group() == Group.COVERS).toList();
        for (final PartAt p : parts()) {
            for (final PartAt q : around) {
                assertFalse(touching.touch(p.shape().form(), built.at(p), q.shape().form(), built.at(q)),
                        p.said() + " meets " + q.said());
            }
        }
    }

    private List<PartAt> parts() {
        return built.placed().keySet().stream().filter(p -> face.contains(p.said())).toList();
    }
}
