package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.vehiclelab.forms.Coat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The GLM platform put together, both axles: the front underbody welded to the rear's where it was made to be, the
 * front subframe's pieces each met where they were made to, its castings bare aluminium and its strut tubes coated;
 * the front subframe clear of the front tyres' insides; and titanium put wherever aluminium is, seen on the bare
 * parts only, and a metal refused in rubber's place.
 */
class GlmPlatformTest {

    private final Assembled built = new Assembly(new GlmPlatform().platform()).assembled();

    @Test
    void bothAxlesPutTogetherEveryPieceMetWhereItWasMadeTo() {
        assertEquals(List.of(), built.problems(1e-6));
        assertEquals(50 + 1 + 1 + 2 * 3, built.placed().size(), "the rear axle; the front underbody and beam once,"
                + " and each side the front subframe's node, strut and hook");
        assertEquals(16 + 3, built.gaps().size(), "the rear axle's, a front node's weld to the beam and both"
                + " front hooks' bolts");
        assertEquals(new Vec3(0, 0, 0), built.at(part("front underbody", Side.MIDDLE)), "made where it stands");
    }

    @Test
    void itsCastingsBareAluminiumItsStrutsCoatedAndClearOfTheTyres() {
        assertTrue(part("front node", Side.RIGHT).part().made().bare());
        assertEquals(Material.ALUMINIUM, part("front hook", Side.LEFT).part().made().material());
        assertTrue(!part("front strut", Side.RIGHT).part().made().bare());
        final Proportions suv = new Proportions();
        final double inner = suv.halfTrack() - suv.width() * 0.1 / 2;
        for (final PartAt p : built.placed().keySet()) {
            if (p.part().name().startsWith("front ") && p.part().group() == Group.SUBFRAMES) {
                final double out = p.shape().form().points().stream().mapToDouble(v -> v.x() + built.at(p).x())
                        .max().orElseThrow();
                assertTrue(out < inner, p.said() + " reaches " + out);
            }
        }
    }

    @Test
    void titaniumWhereverAluminiumIsSeenOnTheBarePartsOnly() {
        final Finishes finishes = new Finishes();
        final MadeOf titanium = new MadeOf().with(Material.ALUMINIUM, Material.TITANIUM);
        assertEquals(Coat.Kind.ALUMINIUM, finishes.of(part("front node", Side.RIGHT).part(), new MadeOf()).kind());
        assertEquals(Coat.Kind.TITANIUM, finishes.of(part("front node", Side.RIGHT).part(), titanium).kind());
        assertEquals(Coat.Kind.TITANIUM, finishes.of(part("wheel carrier", Side.LEFT).part(), titanium).kind());
        assertEquals(finishes.of(Group.SUBFRAMES), finishes.of(part("front strut", Side.RIGHT).part(), titanium),
                "coated, its coat seen");
        assertEquals(Material.ALUMINIUM, titanium.with(Material.ALUMINIUM, Material.ALUMINIUM)
                .of(Material.ALUMINIUM), "back as designed");
        assertThrows(IllegalArgumentException.class, () -> new MadeOf().with(Material.RUBBER, Material.STEEL));
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}
