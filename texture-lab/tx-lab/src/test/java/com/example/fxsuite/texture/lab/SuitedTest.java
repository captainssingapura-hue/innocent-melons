package com.example.fxsuite.texture.lab;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * What suits a material on the bench, looked up the tree. A leaf with shapes said of itself has those; one with
 * none has its branch's; one whose branch is a part of a branch has the part's, and failing that the branch's; and
 * one with nothing said anywhere above it has the root's. The colour is looked up the same way, apart from the
 * shapes: a leaf may have its own colour and its branch's shapes. And every leaf of the tree has both.
 */
class SuitedTest {

    private final Vec3s colours = new Vec3s();

    @Test
    void aLeafHasItsOwnShapesOrItsBranchsOrThePartsOfItsBranchOrTheRoots() {
        assertEquals(List.of(Shape.PANE, Shape.BALL), new Suited(new Materials.Glass()).shapes(), "its own");
        assertEquals(List.of(Shape.WALL, Shape.BALL, Shape.BLOCK), new Suited(new Materials.Enamel()).shapes(),
                "beside glass under one branch, with none of its own nor its branch's: the root's");
        assertEquals(List.of(Shape.KNOLL), new Suited(new Materials.Turf()).shapes(), "its branch's");
        assertEquals(List.of(Shape.BALL, Shape.BLOCK, Shape.CURVED), new Suited(new Materials.Steel()).shapes());
        assertEquals(List.of(Shape.LIMB, Shape.BLOCK), new Suited(new Materials.Muscle()).shapes(),
                "the part of its branch it is under");
        assertEquals(List.of(Shape.LIMB, Shape.BALL), new Suited(new Materials.Skin()).shapes(),
                "its part says none: the branch's");
        assertEquals(List.of(Shape.BALL), new Suited(new Materials.Iris()).shapes(), "its own, over the branch's");
    }

    @Test
    void theColourIsLookedUpTheSameWayAndApartFromTheShapes() {
        assertEquals(colours.of("#c69a7b"), new Suited(new Materials.Skin()).colour(), "its own colour");
        assertEquals(List.of(Shape.LIMB, Shape.BALL), new Suited(new Materials.Skin()).shapes(),
                "with its branch's shapes");
        assertEquals(colours.of("#a3151c"), new Suited(new Materials.GlossPaint()).colour(), "the root's");
        assertEquals(colours.of("#a3151c"), new Suited(new Materials.Bone()).colour());
    }

    @Test
    void everyLeafOfTheTreeHasShapesAndAColour() {
        for (final Leaf<?> leaf : new Materials().tree().leaves()) {
            final Suited suited = new Suited((Material) leaf);
            assertFalse(suited.shapes().isEmpty(), leaf.title());
            assertEquals(suited.shapes().size(), suited.shapes().stream().distinct().count(), leaf.title());
            assertEquals(3, List.of(suited.colour().x(), suited.colour().y(), suited.colour().z()).size());
        }
    }

    /** A colour as it is written. */
    private record Vec3s() {

        com.example.fxsuite.vulkan.space.Vec3 of(final String written) {
            return new Called(written).colour();
        }
    }
}
