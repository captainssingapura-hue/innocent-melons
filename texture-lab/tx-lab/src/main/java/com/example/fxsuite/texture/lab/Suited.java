package com.example.fxsuite.texture.lab;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * What suits a material on the bench: the shapes it is best looked at on, and the colour the shapes are to begin
 * with, for a material that takes its colour from the thing. Either may be said of any node of the tree of
 * materials, a leaf or a branch or the root. A material that has none said of itself has what is said of the branch
 * it is under, or of the branch that is under, and so on up to the root, which says something of both: so every
 * material has shapes and a colour, and a new leaf has its branch's until it is given its own.
 *
 * <p>What is said, today: everything, a wall, a ball and a block, in a deep red; metals, a ball, a block and a
 * curved sheet; masonry, a wall and a block; the two paints, the car body and a curved sheet; glass, a pane and a
 * ball; the ground, the knoll; what a body is made of, a limb and a ball, and what is under its skin, a limb and
 * a block; hair and the parts of the eye, a ball. Skin, hair and the iris each begin in a colour a person might
 * have.
 */
public record Suited(Material material) {

    /** The shapes the material is best looked at on, as the nearest node that says so has them. */
    public List<Shape> shapes() {
        return material.lineage().stream().map(this::shapes).flatMap(Optional::stream).findFirst()
                .orElseThrow(() -> new IllegalStateException("the root of the tree says what suits everything"));
    }

    /** The colour the shapes begin in, as the nearest node that says so has it. */
    public Vec3 colour() {
        return material.lineage().stream().map(this::colour).flatMap(Optional::stream).findFirst()
                .map(written -> new Called(written).colour())
                .orElseThrow(() -> new IllegalStateException("the root of the tree says what suits everything"));
    }

    /** The shapes said of {@code node} itself, where any are. */
    private Optional<List<Shape>> shapes(final Node node) {
        return switch (node) {
            case Materials.Matter all -> Optional.of(List.of(Shape.WALL, Shape.BALL, Shape.BLOCK));
            case Materials.Metal metal -> Optional.of(List.of(Shape.BALL, Shape.BLOCK, Shape.CURVED));
            case Materials.Masonry masonry -> Optional.of(List.of(Shape.WALL, Shape.BLOCK));
            case Materials.GlossPaint paint -> Optional.of(List.of(Shape.CAR, Shape.CURVED));
            case Materials.MetallicPaint paint -> Optional.of(List.of(Shape.CAR, Shape.CURVED));
            case Materials.Glass glass -> Optional.of(List.of(Shape.PANE, Shape.BALL));
            case Materials.Ground ground -> Optional.of(List.of(Shape.KNOLL));
            case Materials.Bark bark -> Optional.of(List.of(Shape.LIMB, Shape.BLOCK));
            case Materials.Bodily body -> Optional.of(List.of(Shape.LIMB, Shape.BALL));
            case Materials.Inward inward -> Optional.of(List.of(Shape.LIMB, Shape.BLOCK));
            case Materials.Hair hair -> Optional.of(List.of(Shape.BALL));
            case Materials.Sclera sclera -> Optional.of(List.of(Shape.BALL));
            case Materials.Iris iris -> Optional.of(List.of(Shape.BALL));
            case Materials.Cornea cornea -> Optional.of(List.of(Shape.BALL));
            default -> Optional.empty();
        };
    }

    /** The colour said of {@code node} itself, where one is: written as a page of the web writes a colour. */
    private Optional<String> colour(final Node node) {
        return switch (node) {
            case Materials.Matter all -> Optional.of("#a3151c");
            case Materials.Skin skin -> Optional.of("#c69a7b");
            case Materials.Hair hair -> Optional.of("#3b2a20");
            case Materials.Iris iris -> Optional.of("#4f7f8f");
            default -> Optional.empty();
        };
    }
}
