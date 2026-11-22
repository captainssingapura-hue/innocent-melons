package com.example.fxsuite.texture.lab;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Things asked for by what they are called: what a list to choose from hands back, and what a run with nobody at
 * it is told. A material, as the tree's meanings head it ({@code ChineseGreyBrick}) or as its name is written
 * ({@code chinese-grey-brick}); shapes, several with commas between ({@code wall, curved sheet}); or a colour
 * ({@code #a3151c}). Capitals and small letters are all one.
 */
public record Called(String name) {

    private static final double FULL = 255;

    /** The material so called; one the tree has not is refused, by name. */
    public Material material() {
        return new Materials().tree().leaves().stream()
                .filter(leaf -> leaf.title().equalsIgnoreCase(name.trim()) || leaf.name().equalsIgnoreCase(name.trim()))
                .map(Material.class::cast).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("the tree of materials has nothing called " + name));
    }

    /**
     * The colour so written, as a page of the web writes one: a hash and six figures, two each for how much red,
     * green and blue, from 00 for none to ff for all. Anything else is refused.
     */
    public Vec3 colour() {
        final String written = name.trim();
        if (!written.matches("#[0-9a-fA-F]{6}")) {
            throw new IllegalArgumentException("a colour is written as #a3151c, not " + name);
        }
        return new Vec3(Integer.parseInt(written.substring(1, 3), 16) / FULL,
                Integer.parseInt(written.substring(3, 5), 16) / FULL,
                Integer.parseInt(written.substring(5, 7), 16) / FULL);
    }

    /** The shapes so called, in the order they are named; a blank names none, and one unknown is refused. */
    public List<Shape> shapes() {
        final List<Shape> named = new ArrayList<>();
        for (final String one : name.isBlank() ? new String[0] : name.split(",")) {
            named.add(Arrays.stream(Shape.values())
                    .filter(shape -> shape.name().equalsIgnoreCase(one.trim())
                            || shape.title().equalsIgnoreCase(one.trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("the bench has no shape called " + one.trim())));
        }
        return named;
    }
}
