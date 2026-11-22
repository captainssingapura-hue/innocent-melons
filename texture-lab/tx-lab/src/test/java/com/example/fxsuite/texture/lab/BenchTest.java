package com.example.fxsuite.texture.lab;

import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bench, with no card and no window. Its shapes: each as wide as it says, standing on the ground when it is
 * lifted as it says, every face of it turned outward. Its stand: the shapes on show in a row in the order given,
 * the row's middle over the world's, none touching another; one shape alone at the middle; none, and only the
 * ground; a shape on show once, and one not on show not asked after. A material and shapes found by what they are
 * called, and what is not known refused. What is on show seeming no nearer than it is. And what a material is,
 * said in words: its line in the tree, its meaning, whether it mirrors or gives light, and what its surface is
 * made of.
 */
class BenchTest {

    @Test
    void eachShapeIsAsWideAsItSaysStandsOnTheGroundWhenLiftedAndHasEveryFaceTurnedOutward() {
        for (final Shape shape : Shape.values()) {
            final Mesh formed = shape.formed();
            double left = Double.MAX_VALUE;
            double right = -Double.MAX_VALUE;
            double lowest = Double.MAX_VALUE;
            for (final Vec3 point : formed.points()) {
                left = Math.min(left, point.x());
                right = Math.max(right, point.x());
                lowest = Math.min(lowest, point.y());
            }
            // what stands on the ground may be what the shape is fitted with: a car stands on its wheels
            for (final Mesh part : shape.fitted().values()) {
                for (final Vec3 point : part.points()) {
                    lowest = Math.min(lowest, point.y());
                }
            }
            // to a fifth of a millimetre: a bowed edge cut into straight pieces falls a hair short of its bow
            assertEquals(shape.across(), right - left, 0.02, shape + " as wide as it says");
            assertEquals(0, lowest + shape.lifted(), 1e-9, shape + " on the ground");
            // somewhere inside it: its frame's middle, or just above the ground for what stands on its foot
            final Vec3 inside = new Vec3(0, shape.lifted() > 0 ? 0 : 10, 0);
            for (int triangle = 0; triangle < formed.triangles(); triangle++) {
                final Vec3 centre = formed.corner(triangle, 0).plus(formed.corner(triangle, 1))
                        .plus(formed.corner(triangle, 2)).times(1.0 / 3);
                assertTrue(formed.crossed(triangle).dot(centre.minus(inside)) > 0, shape + " faces outward");
            }
        }
    }

    @Test
    void theShapesOnShowStandInARowInTheOrderGivenItsMiddleOverTheWorldsAndNoneTouchingAnother() {
        final Stand stand = new Stand();
        assertEquals(List.of(Shape.WALL, Shape.BALL, Shape.BLOCK), stand.shown());
        assertEquals(300 + 100 + 100 + 2 * 60, stand.across(), 1e-9);
        assertEquals(new Vec3(-160, 120, 0), stand.stands(Shape.WALL));
        assertEquals(new Vec3(100, 50, 0), stand.stands(Shape.BALL));
        assertEquals(new Vec3(260, 50, 0), stand.stands(Shape.BLOCK));
        assertEquals(new Vec3(0, 120, 0), stand.middle(), "half as high as the wall, mid-row");
        final Stand turned = new Stand(List.of(Shape.PANE, Shape.CURVED, Shape.WALL));
        double edge = -turned.across() / 2;
        for (final Shape shape : turned.shown()) {
            assertEquals(edge + shape.across() / 2, turned.stands(shape).x(), 1e-9, shape + " after the last");
            edge += shape.across() + 60;
        }
        assertEquals(turned.across() / 2 + 60, edge, 1e-9, "the row's ends as far from the middle as each other");
    }

    @Test
    void oneShapeStandsAtTheMiddleNoneLeavesOnlyTheGroundAndAShapeIsOnShowOnce() {
        final Stand one = new Stand(List.of(Shape.CURVED));
        assertEquals(new Vec3(0, 0, 0), one.stands(Shape.CURVED));
        assertEquals(180, one.across(), 1e-9);
        final Stand none = new Stand(List.of());
        assertEquals(0, none.across(), 1e-9);
        assertEquals(new Vec3(0, 50, 0), none.middle());
        assertThrows(IllegalArgumentException.class, () -> one.stands(Shape.WALL), "not on show");
        assertThrows(IllegalArgumentException.class, () -> new Stand(List.of(Shape.BALL, Shape.BALL)));
    }

    @Test
    void aMaterialAndShapesAreFoundByWhatTheyAreCalledAndWhatIsNotKnownIsRefused() {
        assertEquals(new Materials.ChineseGreyBrick(), new Called("ChineseGreyBrick").material());
        assertEquals(new Materials.ChineseGreyBrick(), new Called(" chinese-grey-brick ").material());
        assertEquals(new Materials.Water(), new Called("WATER").material());
        assertThrows(IllegalArgumentException.class, () -> new Called("Marble").material());
        assertThrows(IllegalArgumentException.class, () -> new Called("Masonry").material(), "a branch is no material");
        assertEquals(List.of(Shape.WALL, Shape.CURVED, Shape.PANE), new Called("wall, Curved sheet ,PANE").shapes());
        assertEquals(List.of(), new Called(" ").shapes());
        assertThrows(IllegalArgumentException.class, () -> new Called("wall, cone").shapes());
    }

    @Test
    void whatIsOnShowSeemsNoNearerThanItIsAndStandsOnAWallABallAndABlockUnlessOthersAreSaid() {
        assertThrows(IllegalArgumentException.class, () -> new OnShow(new Materials.Gold(), 0.5));
        assertEquals(new Stand().shown(), new OnShow(new Materials.Gold(), 2).shapes());
        assertEquals(List.of(Shape.PANE), new OnShow(new Materials.Gold(), 2, List.of(Shape.PANE)).shapes());
    }

    @Test
    void aColourIsReadAsItIsWrittenAnythingElseIsRefusedAndTheThingIsADeepRedUnlessAnotherIsSaid() {
        assertEquals(new Vec3(1, 0, 128 / 255.0), new Called(" #FF0080 ").colour());
        assertEquals(new Vec3(0, 0, 0), new Called("#000000").colour());
        assertThrows(IllegalArgumentException.class, () -> new Called("red").colour());
        assertThrows(IllegalArgumentException.class, () -> new Called("#a31").colour());
        assertThrows(IllegalArgumentException.class, () -> new Called("#a3151g").colour());
        assertEquals(new Called("#a3151c").colour(), new OnShow(new Materials.GlossPaint(), 1).tint());
        final Vec3 blue = new Called("#1c3f8f").colour();
        assertEquals(blue, new OnShow(new Materials.GlossPaint(), 1, List.of(Shape.CURVED), blue).tint());
    }

    @Test
    void whatAMaterialIsIsSaidInWordsItsLineItsMeaningAndWhatItsSurfaceIsMadeOf() {
        final String brick = new Told(new Materials.ChineseGreyBrick()).said();
        assertTrue(brick.startsWith("ChineseGreyBrick: Masonry, of Matter\nClay fired and then starved of air"), brick);
        assertTrue(brick.contains("\nOver its colour, its surface has:\n  "), brick);
        assertTrue(brick.contains(", nothing smaller than "), brick);
        final String gold = new Told(new Materials.Gold()).said();
        assertTrue(gold.contains("\nIt mirrors what is about it.") && !gold.contains("gives light"), gold);
        assertTrue(gold.endsWith("\nIts surface is one colour all over."), gold);
        final String flame = new Told(new Materials.Flame()).said();
        assertTrue(flame.contains("\nIt gives light of its own.") && !flame.contains("mirrors"), flame);
    }
}
