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
        assertEquals(List.of("shapes"), new Existences().trees().stream().map(Taxonomy::name).toList());
        assertEquals(16, shapes.leaves().size());
        assertEquals(23, shapes.nodes().size());
        assertEquals("Half of a ball or of an ellipsoid: the half melon a melon house is, a bowl turned over.",
                shapes.meaning(new Shapes.Dome()));
    }

    @Test
    void theShapesByHowTheyMeetWhatTheyStandOn() {
        assertEquals(List.of(new Shapes.Ellipsoid(), new Shapes.Rounded(), new Shapes.Shape()),
                new Shapes.Ellipsoid().lineage());
        assertEquals(List.of(new Shapes.Box(), new Shapes.Plate(), new Shapes.Prism(), new Shapes.Lump()),
                shapes.under(new Shapes.Faceted()));
        assertEquals(List.of(new Shapes.Drum(), new Shapes.Cone(), new Shapes.Capsule(), new Shapes.Ring()),
                shapes.under(new Shapes.Turned()));
        assertEquals(List.of(new Shapes.Level(), new Shapes.Slope(), new Shapes.Land(), new Shapes.Mesh()),
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
