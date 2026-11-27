package com.example.fxsuite.baked;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;
import java.util.Map;

/** A small tree for the tests: things; machines among them, a robot one; and a rock. And a piece or two of them. */
final class Things {

    /** The tree. */
    final Taxonomy tree = new Taxonomy("things", new Thing(), List.of(new Robot(), new Rock()), Map.of(
            "Thing", "Anything.", "Machine", "A thing that works.", "Robot", "A machine built as a person is.",
            "Rock", "A thing that lies there."));

    /** The root. */
    record Thing() implements L0 {
    }

    /** A branch. */
    record Machine() implements L1<Thing> {
        @Override
        public Thing parent() {
            return new Thing();
        }
    }

    /** A leaf two down. */
    record Robot() implements Leaf<Machine> {
        @Override
        public Machine parent() {
            return new Machine();
        }
    }

    /** A leaf one down. */
    record Rock() implements Leaf<Thing> {
        @Override
        public Thing parent() {
            return new Thing();
        }
    }

    /** Two pieces: a triangle, labelled; a square, in two triangles. */
    List<Piece> pieces() {
        return List.of(new Piece(List.of("HEAD", "SHELL"), new float[] {0, 0, 0, 1, 0, 0, 0, 1, 0},
                        new int[] {0, 1, 2}),
                new Piece(List.of("TRUNK"), new float[] {0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1},
                        new int[] {0, 1, 2, 0, 2, 3}));
    }
}
