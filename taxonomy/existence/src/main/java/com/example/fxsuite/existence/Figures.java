package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Figures: bodies that move themselves, stand and walk, each seen as one: an avatar, one of a crowd. A figure says what
 * it is, never how it is made nor what it is made of. Only the kinds something has so far been made as are here.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Figures.md}.
 */
public final class Figures {

    private Figures() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("figures", new Figure(), List.of(new FramedRobot(),
            new ShelledRobot()), Meanings.INSTANCE.beside(Figures.class));

    /** The root. A body that moves itself. */
    public record Figure() implements L0 {
    }

    // Robot: a machine built as a person is, and moving as one does
    public record Robot() implements L1<Figure> { public Figure parent() { return new Figure(); } }
    public record FramedRobot() implements Leaf<Robot> { public Robot parent() { return new Robot(); } }
    public record ShelledRobot() implements Leaf<Robot> { public Robot parent() { return new Robot(); } }
}
