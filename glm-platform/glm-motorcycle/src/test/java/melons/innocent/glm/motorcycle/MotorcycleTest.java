package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.existences.MotorcycleParts;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The motorcycle round its twin: its steering's trail as said from its rake, offset and front tyre, its wheels a
 * wheelbase apart, each on the ground; put together with every part met where it was made to, each of its own parts
 * of a motorcycle's kind; and each of them clear of the engine and of each other, but where they are joined.
 */
class MotorcycleTest {

    private final Motorcycle bike = new Motorcycle();
    private final BikeFigures figures = bike.figures();

    @Test
    void itsTrailAsSaidItsWheelsAWheelbaseApartOnTheGround() {
        assertEquals(BikeFigures.TRAIL, figures.trail(), 1e-9);
        assertEquals(4.7, figures.offset(), 0.05, "its fork's offset");
        assertEquals(BikeFigures.WHEELBASE, figures.frontAxle().x() - figures.rearAxle().x(), 1e-9);
        assertEquals(figures.ground(), figures.frontAxle().y() - BikeFigures.FRONT.radius(), 1e-9);
        assertEquals(figures.ground(), figures.rearAxle().y() - BikeFigures.REAR.radius(), 1e-6);
        assertTrue(figures.frontAxle().x() > 0 && figures.rearAxle().x() < 0, "the engine between its wheels");
    }

    @Test
    void putTogetherEveryPartMetEachOfAMotorcyclesKind() {
        final Platform platform = bike.platform();
        final Assembled built = new Assembly(platform).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        built.placed().keySet().forEach(p -> assertEquals(0, built.at(p).length(), 1e-6, p.said()));
        final MotorcycleParts kinds = new MotorcycleParts();
        final long own = ownParts(platform).stream()
                .filter(p -> p.kind().map(k -> kinds.tree().has(k)).orElse(false)).count();
        assertEquals(ownParts(platform).size(), own, "every one of its own parts of a motorcycle's kind");
        assertEquals(21, ownParts(platform).size());
    }

    @Test
    void itsPartsClearOfTheEngineAndEachOtherButWhereJoined() {
        final Platform platform = bike.platform();
        final Assembled built = new Assembly(platform).assembled();
        final List<Part> all = built.placed().keySet().stream().map(PartAt::part).toList();
        final List<Part> own = ownParts(platform);
        final TurnedTouching touching = new TurnedTouching();
        final List<String> touched = new ArrayList<>();
        for (final Part a : own) {
            for (final Part b : all) {
                if (a != b && !joined(platform, a, b) && !(own.contains(b) && all.indexOf(b) < all.indexOf(a))
                        && !touching.wheres(a.form(), new Mat4(), b.form(), new Mat4()).isEmpty()) {
                    touched.add(a.name() + " on " + b.name());
                }
            }
        }
        assertEquals(List.of(), touched);
    }

    /** Its own parts: those the engine does not have. */
    private List<Part> ownParts(final Platform platform) {
        final Set<String> engine = Set.of("V2");
        return platform.connections().stream().map(Connection::carried)
                .filter(p -> engine.stream().noneMatch(e -> p.name().startsWith(e))).distinct().toList();
    }

    /** Whether {@code a} and {@code b} are joined, one to the other. */
    private boolean joined(final Platform platform, final Part a, final Part b) {
        return platform.connections().stream().anyMatch(c -> c.carrier() == a && c.carried() == b
                || c.carrier() == b && c.carried() == a);
    }

}
