package com.example.fxsuite.texture.lab;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.texture.materials.Glowing;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Mirroring;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * What a material is, said in words for the bench's figures: its name and the line it is of in the tree; what it
 * means, as the tree has it; whether it mirrors or gives light; and what its surface is made of, term by term, each
 * with the smallest thing it adds, which is what says how far off it is before it fades to its average.
 */
public record Told(Material material) {

    private static final double MILLIMETRES = 10;
    private static final double HUNDRED = 100;

    /** All of it, a line to each. */
    public String said() {
        final Surface surface = new Shades().of(material);
        final List<Node> line = material.lineage();
        final StringBuilder said = new StringBuilder(material.title()).append(": ")
                .append(line.subList(1, line.size()).stream().map(Node::title).collect(Collectors.joining(", of ")))
                .append('\n').append(new Materials().tree().meaning(material).replaceAll("\\s+", " "));
        if (material.answer(Mirroring.class).isPresent()) {
            said.append("\nIt mirrors what is about it.");
        }
        if (material.answer(Glowing.class).isPresent()) {
            said.append("\nIt gives light of its own.");
        }
        if (surface.seenThrough()) {
            said.append(String.format(Locale.ENGLISH, "\nIt is seen through: %.0f parts in a hundred of what is"
                    + " behind it show, seen square on.", HUNDRED * surface.through()));
        }
        surface.flakes().ifPresent(flakes -> said.append(String.format(Locale.ENGLISH,
                "\nFlakes of metal lie in it, %.1f mm apart: told apart seen close, one soft shine further off.",
                MILLIMETRES * flakes.size())));
        if (surface.terms().isEmpty()) {
            return said.append("\nIts surface is one colour all over.").toString();
        }
        said.append("\nOver its colour, its surface has:");
        for (final Term term : surface.terms()) {
            said.append("\n  ").append(term.name())
                    .append(String.format(Locale.ENGLISH, ", nothing smaller than %.1f cm", term.smallest()));
        }
        return said.toString();
    }
}
