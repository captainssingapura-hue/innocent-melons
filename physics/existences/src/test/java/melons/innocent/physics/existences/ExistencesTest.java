package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists in physics: nothing wrong with its tree, every node with its meaning. The shapes by how they meet what
 * they stand on; and resting on a face, tipping, rolling one way or any way, rocking and never moving, as their
 * branches and leaves say: a box rests and tips and never rolls, a watermelon rolls any way and rests on no face, a
 * drum does all three one way or another, a capsule only rolls, a dome rocks, and the ground never moves.
 */
class ExistencesTest {

    private final Taxonomy shapes = new Shapes().tree();

    @Test
    void nothingWrongWithItsTreeEveryNodeWithItsMeaning() {
        assertEquals(List.of(), new Existences().problems());
        assertEquals(List.of("shapes", "movers", "surfaces", "conditions"),
                new Existences().trees().stream().map(Taxonomy::name).toList());
        assertEquals(17, shapes.leaves().size());
        assertEquals(24, shapes.nodes().size());
        assertEquals("Half of a ball or of an ellipsoid: the half melon a melon house is, a bowl turned over.",
                shapes.meaning(new Shapes.Dome()));
    }

    @Test
    void moversByHowTheyMeetASurfaceAndWhetherTheyDriveThemselves() {
        final Taxonomy movers = new Movers().tree();
        assertEquals(11, movers.leaves().size());
        assertEquals(18, movers.nodes().size());
        assertEquals(List.of(new Movers.Walker(), new Movers.Stepping(), new Movers.Mover()),
                new Movers.Walker().lineage());
        assertEquals(List.of(new Movers.Car(), new Movers.Motorcycle(), new Movers.Wagon()),
                movers.under(new Movers.Wheeled()));
        assertTrue(new Movers.Walker().answer(DrivesItself.class).isPresent(), "said once, on its branch");
        assertTrue(new Movers.Car().answer(DrivesItself.class).isPresent());
        assertTrue(new Movers.Wagon().answer(OnlyPushed.class).isPresent(), "a wagon goes as it is pulled");
        assertFalse(new Movers.Wagon().answer(DrivesItself.class).isPresent());
        assertTrue(new Movers.Roller().answer(OnlyPushed.class).isPresent(), "a melon goes as the slope sends it");
        assertTrue(new Movers.Dragged().answer(OnlyPushed.class).isPresent());
        assertTrue(new Movers.Boat().answer(DrivesItself.class).isPresent()
                && new Movers.Raft().answer(OnlyPushed.class).isPresent());
        // every mover is one or the other, and none both
        for (final Node mover : movers.leaves()) {
            assertTrue(mover.answer(DrivesItself.class).isPresent() != mover.answer(OnlyPushed.class).isPresent(),
                    mover.name());
        }
    }

    @Test
    void aSurfacesFormAndItsConditionAreToldApart() {
        final Taxonomy surfaces = new Surfaces().tree();
        assertEquals(6, surfaces.leaves().size());
        assertEquals(10, surfaces.nodes().size());
        assertEquals(List.of(new Surfaces.Level(), new Surfaces.Slope(), new Surfaces.Stepped(), new Surfaces.Land()),
                surfaces.under(new Surfaces.Firm()));
        assertEquals(List.of(new Surfaces.Liquid(), new Surfaces.Yielding(), new Surfaces.Surface()),
                new Surfaces.Liquid().lineage());
        final Taxonomy conditions = new Conditions().tree();
        assertEquals(5, conditions.leaves().size());
        assertEquals(9, conditions.nodes().size());
        assertEquals(List.of(new Conditions.Wet(), new Conditions.Icy()), conditions.under(new Conditions.Slick()));
        assertEquals("Under ice or frost, which very little grips.", conditions.meaning(new Conditions.Icy()));
        // a slope among the shapes is the shape of a thing; a slope among the surfaces is a form to move over
        assertFalse(new Surfaces.Slope().equals(new Shapes.Slope()));
    }

    @Test
    void theShapesByHowTheyMeetWhatTheyStandOn() {
        assertEquals(List.of(new Shapes.Ellipsoid(), new Shapes.Rounded(), new Shapes.Shape()),
                new Shapes.Ellipsoid().lineage());
        assertEquals(List.of(new Shapes.Box(), new Shapes.Plate(), new Shapes.Prism(), new Shapes.Lump()),
                shapes.under(new Shapes.Faceted()));
        assertEquals(List.of(new Shapes.Drum(), new Shapes.Cone(), new Shapes.Capsule(), new Shapes.Ring()),
                shapes.under(new Shapes.Turned()));
        assertEquals(List.of(new Shapes.Level(), new Shapes.Slope(), new Shapes.Land(), new Shapes.Shell(),
                new Shapes.Mesh()),
                shapes.under(new Shapes.Fixed()));
        assertEquals("ellipsoid", new Shapes.Ellipsoid().name());
    }

    @Test
    void restingTippingRollingRockingAndNeverMovingAsTheirBranchesAndLeavesSay() {
        final Node box = new Shapes.Box();
        assertTrue(box.answer(RestsOnAFace.class).isPresent() && box.answer(Tips.class).isPresent());
        assertFalse(box.answer(Rolls.class).isPresent(), "a box never rolls");
        final Node melon = new Shapes.Ellipsoid();
        assertTrue(melon.answer(RollsAnyWay.class).isPresent() && melon.answer(Rolls.class).isPresent());
        assertFalse(melon.answer(RestsOnAFace.class).isPresent() || melon.answer(Tips.class).isPresent(),
                "a watermelon has no face to rest on and no edge to tip over");
        final Node drum = new Shapes.Drum();
        assertTrue(drum.answer(Rolls.class).isPresent() && drum.answer(RestsOnAFace.class).isPresent()
                && drum.answer(Tips.class).isPresent(), "on its side a drum rolls, on its end it stands and tips");
        assertFalse(drum.answer(RollsAnyWay.class).isPresent(), "but it rolls one way only");
        final Node capsule = new Shapes.Capsule();
        assertTrue(capsule.answer(Rolls.class).isPresent());
        assertFalse(capsule.answer(RestsOnAFace.class).isPresent(), "a capsule has no end to stand on");
        assertTrue(new Shapes.Dome().answer(Rocks.class).isPresent()
                && new Shapes.Dome().answer(RestsOnAFace.class).isPresent());
        assertFalse(new Shapes.Ball().answer(Rocks.class).isPresent());
        assertFalse(new Shapes.Joined().answer(Rolls.class).isPresent(), "what is joined is as its parts are");
        assertEquals(List.of(new Shapes.Ball(), new Shapes.Ellipsoid(), new Shapes.Drum(), new Shapes.Cone(),
                new Shapes.Capsule(), new Shapes.Ring()), shapes.leaves().stream()
                .filter(leaf -> leaf.answer(Rolls.class).isPresent()).toList());
        assertEquals(shapes.under(new Shapes.Fixed()), shapes.leaves().stream()
                .filter(leaf -> leaf.answer(Unmoving.class).isPresent()).toList());
    }
}
