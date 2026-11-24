package com.example.fxsuite.texture.materials;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The tree of materials, which the lab owns: forty-two leaves under eight branches, two of them in parts, every
 * node with its meaning; each
 * leaf under the branch it is of, and its line going up to what all things are made of; every leaf to be asked for
 * as a material, and nothing that is not a leaf; the metals, both paints, glass and water mirroring, and only what
 * gives light glowing.
 */
class MaterialsTest {

    private final Taxonomy tree = new Materials().tree();

    @Test
    void fortyTwoLeavesUnderEightBranchesEveryNodeWithItsMeaning() {
        assertEquals(List.of(), tree.problems());
        assertEquals("materials", tree.name());
        assertEquals(47, tree.leaves().size());
        assertEquals(59, tree.nodes().size(), "the leaves, their eight branches, the three parts of two, and the root");
        for (final Node node : tree.nodes()) {
            assertFalse(tree.meaning(node).isBlank(), node.title() + " has its meaning");
        }
        assertEquals("Stone cut square, to lie in courses.", tree.meaning(new Materials.DressedStone()));
        assertEquals("What rain is, lying still where the ground or a rim holds it.",
                tree.meaning(new Materials.Water()));
    }

    @Test
    void eachLeafIsUnderTheBranchItIsOfAndItsLineGoesUpToWhatAllThingsAreMadeOf() {
        assertEquals(List.of(new Materials.Gold(), new Materials.Metal(), new Materials.Matter()),
                new Materials.Gold().lineage());
        assertEquals(List.of(new Materials.CommonRedBrick(), new Materials.ChineseGreyBrick(),
                new Materials.Adobe(), new Materials.Rubble(),
                new Materials.DressedStone(), new Materials.Concrete()), tree.under(new Materials.Masonry()));
        assertEquals(List.of(new Materials.Water()), tree.under(new Materials.Liquid()));
    }

    @Test
    void everyLeafIsAMaterialAndNothingElseIsTheMetalsAndWaterMirroringAndOnlyWhatGivesLightGlowing() {
        for (final Node node : tree.nodes()) {
            assertEquals(tree.leaves().contains(node), node instanceof Material, node.title());
        }
        assertEquals(List.of(new Materials.Gold(), new Materials.Bronze(), new Materials.Titanium(),
                        new Materials.Gunmetal(), new Materials.Steel(), new Materials.GlossPaint(),
                        new Materials.MetallicPaint(),
                        new Materials.Glass(), new Materials.Timber(), new Materials.Water(), new Materials.Cornea()),
                tree.leaves().stream().filter(leaf -> leaf.answer(Mirroring.class).isPresent()).toList());
        assertEquals(List.of(new Materials.LitGlass(), new Materials.Flame(), new Materials.LitPaper()),
                tree.leaves().stream().filter(leaf -> leaf.answer(Glowing.class).isPresent()).toList());
        assertTrue(tree.has(new Materials.Rind()));
    }
}
