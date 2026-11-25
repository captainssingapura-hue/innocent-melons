package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.engine.EngineRange;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's own steel shell as the platform's root: its underbody in the cast body's place, pressed steel,
 * every mount the chassis is fixed by where the cast body has it; the body sides, the bulkhead, the tail panel and the
 * roof's two headers each a part of its own, welded on, the headers meeting the left body side again, closing the
 * cage; and the tyres and its engine, the V6, clear of all of it, the engine under the bonnet's line.
 */
class SantanaShellTest {

    private final Santana car = new Santana();
    private final Touching touching = new Touching();

    @Test
    void itsUnderbodyTheRootEveryMountWhereTheCastBodysIs() {
        final Assembled built = new Assembly(car.platform(null).platform()).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        final PartAt root = named(built, "underbody", Side.MIDDLE);
        assertEquals(new Vec3(0, 0, 0), built.at(root), "the root, where it was made");
        assertEquals(Material.STEEL, root.part().made().material());
        assertFalse(built.placed().keySet().stream().anyMatch(p -> p.part().name().equals("cast body")));
        final Part cast = new GlmPlatform(car.proportions(), car.figures()).platform().root();
        for (final Point mount : cast.points()) {
            if (!mount.name().startsWith("body side")) {
                assertEquals(mount.at(), root.part().point(mount.name()).at(), mount.name());
            }
        }
    }

    @Test
    void aCageOfPiecesEachAPartOfItsOwn() {
        final Assembled built = new Assembly(car.platform(null).platform()).assembled();
        final Set<String> shell = built.placed().keySet().stream()
                .filter(p -> p.part().group() == Group.BODYWORK || p.part().group() == Group.UNDERBODY)
                .filter(p -> !p.part().name().endsWith("door") && !p.part().name().endsWith("wing")
                        && !p.part().name().endsWith("lid") && !p.part().name().equals("bonnet"))
                .map(PartAt::said).collect(Collectors.toSet());
        assertEquals(Set.of("underbody", "right body side", "left body side", "bulkhead", "tail panel",
                "roof header, front", "roof header, rear"), shell);
        assertEquals(2, built.gaps().stream().filter(g -> g.connection().startsWith("roof header")).count(),
                "each header welded to the right body side and meeting the left again");
        final PartAt header = named(built, "roof header, rear", Side.MIDDLE);
        final PartAt left = named(built, "body side", Side.LEFT);
        assertTrue(header.point("body side, left").at().plus(built.at(header))
                .minus(left.point("header, rear").at().plus(built.at(left))).length() < 1e-6, "met");
    }

    @Test
    void theTyresAndTheEngineClearOfTheShellTheEngineUnderTheBonnetsLine() {
        final EngineBay bay = new EngineBay(new EngineRange().all().stream().filter(e -> e.name().equals("V6"))
                .findFirst().orElseThrow(), true);
        final Assembled built = new Assembly(car.platform(bay).platform()).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        final List<PartAt> shell = built.placed().keySet().stream()
                .filter(p -> p.part().group() == Group.BODYWORK || p.part().group() == Group.UNDERBODY).toList();
        final List<PartAt> engine = built.placed().keySet().stream()
                .filter(p -> (p.part().group() == Group.ENGINE || p.part().group() == Group.COVERS)
                        && !p.part().name().startsWith("engine mount")).toList();
        final List<PartAt> clear = new ArrayList<>(engine);
        built.placed().keySet().stream().filter(p -> p.part().group() == Group.TYRES).forEach(clear::add);
        for (final PartAt p : clear) {
            for (final PartAt q : shell) {
                assertFalse(touching.touch(p.shape().form(), built.at(p), q.shape().form(), built.at(q)),
                        p.said() + " meets " + q.said());
            }
        }
        final Loop wing = new Loop(new SantanaFront(new SantanaLines()).wing());
        for (final PartAt p : engine) {
            for (final Vec3 v : p.shape().form().points()) {
                final Vec3 q = v.plus(built.at(p));
                assertTrue(q.y() < wing.top(q.z()), p.said() + " over the bonnet's line at " + q);
            }
        }
    }

    private PartAt named(final Assembled built, final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}
