package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;

/**
 * The clay head on a body, to see how it suits it: made from a reference head on the machine ({@link Pieces}), every
 * piece on as the reference has it, meshed; made as big as the body's head, its crown as high above its head's joint as
 * the body's skin's is; the body's skin without its own head, which the clay head takes the place of.
 */
public record ClayHead() implements StatelessFunctionalObject {

    public static final ClayHead INSTANCE = new ClayHead();

    /** How finely the blank is made a solid, and the head's surface found, cm. */
    private static final double BLANK_CELL = 0.25;
    private static final double CELL = 0.2;

    /** The head of the reference at {@code reference}, in its head's frame, cm. */
    public Mesher.Mesh of(final Path reference) throws IOException {
        final ReferenceHead r = ReferenceReading.INSTANCE.of(reference);
        final Mesher.Mesh blank = BlankFit.INSTANCE.of(r, FeatureMasks.INSTANCE.of(r));
        final Field.Shape solid = MeshSolid.INSTANCE.closed(blank, BLANK_CELL);
        final Adjust plain = new Adjust(V3.ZERO, 0, 0, 1, new V3(1, 1, 1), 0);
        return Mesher.of(Pieces.INSTANCE.clay(r, solid).head(EnumSet.allOf(Piece.class), p -> plain), CELL);
    }

    /**
     * How many times as big as {@code head} the head of the body {@code skin} is: its crown's height above its head's
     * joint, its head's part of its skin as it was bound, over the clay head's.
     */
    public double scale(final Mesher.Mesh head, final Flesh skin) {
        final int part = skin.parts().indexOf(Part.HEAD);
        final Rig.Frame f = skin.bound().get(part);
        double crown = 0;
        for (int i = 0; i < skin.size(); i++) {
            if (skin.most(i) == Part.HEAD) {
                crown = Math.max(crown, f.turn().back().apply(skin.point(i).minus(f.at())).y());
            }
        }
        double top = 0;
        for (int i = 1; i < head.points().length; i += 3) {
            top = Math.max(top, head.points()[i]);
        }
        return crown / top;
    }

    /** {@code skin} without its head's part. */
    public Flesh headless(final Flesh skin) {
        return skin.patch(p -> p != Part.HEAD);
    }
}
