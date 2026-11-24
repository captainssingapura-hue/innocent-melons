package com.example.fxsuite.texture.materials;

import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.texture.stone.CoursedStone;
import com.example.fxsuite.texture.stone.RubbleWall;
import com.example.fxsuite.vulkan.bench.subject.FlakedLacquer;
import com.example.fxsuite.vulkan.bench.subject.Tone;
import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.term.Surface;

/**
 * How each material is drawn: for every leaf of the tree of materials, its surface. Whatever is in the tree is
 * already shaded, and this is where: a thing says what it is made of by the material's type and asks here for the
 * surface, never making one of its own. A test goes through the tree's leaves and fails on one with no surface, so
 * a material cannot be added to the tree without being added here.
 *
 * <p>The surfaces themselves are where they were made: the metals and what gives light with the avatar's tones, the
 * walls in the texture lab, the rind with the watermelon, the water with the village's lake.
 */
public record Shades() {

    /** {@code material}'s surface. */
    public Surface of(final Material material) {
        return switch (material) {
            case Materials.Gold gold -> Tone.GOLD.surface();
            case Materials.Bronze bronze -> Tone.BRONZE.surface();
            case Materials.Titanium titanium -> Tone.TITANIUM.surface();
            case Materials.Gunmetal gunmetal -> Tone.GUNMETAL.surface();
            case Materials.Steel steel -> Tone.STEEL.surface();
            case Materials.CommonRedBrick brick -> Brickwork.COMMON_RED.wall().surface();
            case Materials.ChineseGreyBrick brick -> Brickwork.CHINESE_GREY.wall().surface();
            case Materials.Adobe adobe -> Brickwork.ADOBE.wall().surface();
            case Materials.Rubble rubble -> new RubbleWall().surface();
            case Materials.DressedStone stone -> new CoursedStone().wall().surface();
            case Materials.Concrete concrete -> new CastConcrete().surface();
            case Materials.Enamel enamel -> Tone.SHELL.surface();
            case Materials.Rubber rubber -> Tone.JOINT.surface();
            case Materials.GlossPaint paint -> new Lacquer().surface();
            case Materials.MetallicPaint paint -> new FlakedLacquer().surface();
            case Materials.Glass glass -> new SheetGlass().surface();
            case Materials.LitGlass glass -> Tone.VISOR.surface();
            case Materials.Flame flame -> Tone.FLAME.surface();
            case Materials.LitPaper paper -> new PaperLantern().surface();
            case Materials.Water water -> new StillWater().surface();
            case Materials.Turf turf -> Grounds.TURF.surface();
            case Materials.Topsoil soil -> Grounds.TOPSOIL.surface();
            case Materials.Sand sand -> Grounds.SAND.surface();
            case Materials.Gravel gravel -> Grounds.GRAVEL.surface();
            case Materials.Bedrock rock -> Grounds.BEDROCK.surface();
            case Materials.Mud mud -> Grounds.MUD.surface();
            case Materials.Skin skin -> Tissues.SKIN.surface();
            case Materials.Lip lip -> Tissues.LIP.surface();
            case Materials.Nail nail -> Tissues.NAIL.surface();
            case Materials.Hair hair -> Tissues.HAIR.surface();
            case Materials.Sclera sclera -> Tissues.SCLERA.surface();
            case Materials.Iris iris -> Tissues.IRIS.surface();
            case Materials.Cornea cornea -> new ClearCornea().surface();
            case Materials.ToothEnamel enamel -> Tissues.TOOTH_ENAMEL.surface();
            case Materials.Bone bone -> Tissues.BONE.surface();
            case Materials.Cartilage cartilage -> Tissues.CARTILAGE.surface();
            case Materials.Muscle muscle -> Tissues.MUSCLE.surface();
            case Materials.Tendon tendon -> Tissues.TENDON.surface();
            case Materials.Fat fat -> Tissues.FAT.surface();
            default -> grown(material);
        };
    }

    /** {@code material}'s surface, where it is something grown: a rind, timber, or a tree's bark. */
    private Surface grown(final Material material) {
        return switch (material) {
            case Materials.Rind rind -> new Watermelon().rind();
            case Materials.Timber timber -> new VarnishedTimber().surface();
            case Materials.OakBark bark -> new RoughBark().surface();
            case Materials.WalnutBark bark -> new FurrowedBark().surface();
            case Materials.BirchBark bark -> new PaperBark().surface();
            case Materials.PoplarBark bark -> new PaleBark().surface();
            // a willow's old bark is furrowed as a walnut's is, browner; its young wood is golden
            case Materials.WillowBark bark -> new FurrowedBark("#6f6557", "#857b6a", "#544b40", "#2b2520").surface()
                    .marked(new Slender("#c9a53a", 0.5, 2.2).term());
            case Materials.PlaneBark bark -> new MottledBark().surface()
                    .marked(new Slender("#7c7f52", 0.6, 2.5).term());
            default -> throw new IllegalArgumentException(material.title() + " is in no tree of materials that is"
                    + " shaded here");
        };
    }
}
