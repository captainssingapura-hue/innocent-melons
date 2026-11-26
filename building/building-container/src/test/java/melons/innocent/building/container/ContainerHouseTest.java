package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A container house: three containers, each a room come into at its door; pillars under each wherever nothing else
 * holds it up, each from the ground up to what it holds, and none where one container bears on another; its stairs
 * risers each no higher than the walker steps; its finishes as asked, titanium and gun metal among them; nothing wrong
 * with it as a thing built; and what is not one refused.
 */
class ContainerHouseTest {

    private final ContainerHouseSpec<Civilization, CommonEras.Contemporary> spec = new ContainerHouseSpec<>(
            Size.FORTY, 250, 500, Finish.CREAM, Finish.GUN_METAL, Finish.TITANIUM);

    @Test
    void threeContainersEachARoomComeIntoAtItsDoor() {
        final Structure built = new ContainerHouse().build(spec);
        assertEquals(List.of(), built.problems());
        assertEquals(3, built.rooms().size());
        assertEquals(3, built.reached().size(), "each come into");
        assertEquals(0, built.waysIn().getFirst().level(), 1e-9, "come onto from the ground");
    }

    @Test
    void pillarsWhereverNothingElseHoldsItUp() {
        final Structure built = new ContainerHouse().build(spec);
        final List<Solid> pillars = built.solids(new Elements.Column()).stream()
                .filter(s -> s.name().equals("pillar")).toList();
        // six under each container, four under the deck, four under the landing
        assertEquals(3 * 6 + 4 + 4, pillars.size());
        assertTrue(pillars.stream().allMatch(p -> low(p) == 0), "each on the ground");
        final double upper = 250 + Size.HIGH;
        assertEquals(6 + 4, pillars.stream().filter(p -> high(p) == upper).count(),
                "six up to the upper one, and four to the landing level with its floor");
        final Layout stacked = new Layout(spec);
        final Container lower = stacked.boxes().getFirst();
        final Container over = new Container(Size.TWENTY, new Placing(lower.at().x(), 0, lower.top(), true), 1, 1,
                Finish.CREAM, Finish.STEEL);
        assertTrue(new Pillars(List.of(lower, over), Finish.STEEL).under(over.bearings(10), over.under()).isEmpty(),
                "none where one bears on another");
    }

    @Test
    void itsStairsStepsTheWalkerTakes() {
        final Layout layout = new Layout(spec);
        for (final Flight f : List.of(layout.first(), layout.second())) {
            assertTrue((f.head() - f.foot()) / f.risers() <= Flight.RISER + 1e-9);
        }
        assertTrue(layout.second().start() <= Layout.DECK / 2, "the second flight's foot on the deck");
    }

    @Test
    void itsFinishesAsAsked() {
        final Structure built = new ContainerHouse().build(spec);
        assertTrue(built.solids(new Elements.Column()).stream().allMatch(s -> s.material() == Material.TITANIUM));
        assertTrue(built.solids(new Elements.Roof()).stream().anyMatch(s -> s.material() == Material.GUN_METAL));
        assertTrue(built.solids(new Elements.Roof()).stream().anyMatch(s -> s.tint().isPresent()), "cream, painted");
        assertTrue(!built.solids(new Elements.Window()).isEmpty());
    }

    @Test
    void whatIsNotOneRefused() {
        assertThrows(IllegalArgumentException.class, () -> new ContainerHouseSpec<>(Size.FORTY, 50, 500,
                Finish.CREAM, Finish.CREAM, Finish.STEEL));
        assertThrows(IllegalArgumentException.class, () -> new ContainerHouseSpec<>(Size.FORTY, 250, 300,
                Finish.CREAM, Finish.CREAM, Finish.STEEL));
        assertThrows(IllegalArgumentException.class, () -> new ContainerHouseSpec<>(Size.FORTY, 250, 500,
                null, Finish.CREAM, Finish.STEEL));
    }

    private double low(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).min().orElseThrow();
    }

    private double high(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
