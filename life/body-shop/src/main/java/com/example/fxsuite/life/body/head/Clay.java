package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * What a head is made of, cut once from its reference ({@link Pieces}): its blank made a solid; its nose, its mouth and
 * its ears cut; each eye's layers and its middle. A head of it made as often as its pieces are chosen and adjusted,
 * each adjusted as cheaply as a shape is wrapped, none cut again.
 *
 * @param blank   the blank, made a solid
 * @param cuts    the nose, the mouth and each ear, cut
 * @param eyes    each eye's layers
 * @param middles each eye's middle, what it is adjusted about
 */
public record Clay(Field.Shape blank, Map<Feature, Pieces.Cut> cuts, Map<Feature, List<Field.Element>> eyes,
        Map<Feature, V3> middles) {

    /** The head with the pieces {@code on} put on its blank, each adjusted as {@code adjust} says. */
    public Field head(final Set<Piece> on, final Function<Piece, Adjust> adjust) {
        Field.Shape base = blank;
        if (on.contains(Piece.EARS)) {
            for (final Feature f : Piece.EARS.features()) {
                base = Pieces.INSTANCE.grafted(cuts.get(f), adjust.apply(Piece.EARS), f.left(), base);
            }
        }
        final List<Field.Shape> pieces = new ArrayList<>();
        for (final Piece p : List.of(Piece.NOSE, Piece.MOUTH)) {
            if (on.contains(p)) {
                pieces.add(Pieces.INSTANCE.piece(cuts.get(p.features().get(0)), adjust.apply(p), false));
            }
        }
        final List<Field.Element> then = new ArrayList<>();
        if (on.contains(Piece.EYES)) {
            final Adjust a = adjust.apply(Piece.EYES);
            for (final Feature f : Piece.EYES.features()) {
                for (final Field.Element e : eyes.get(f)) {
                    then.add(new Field.Element(e.name(), a.on(e.shape(), middles.get(f), f.left()), e.op(), e.blend()));
                }
            }
        }
        return Pieces.INSTANCE.head(base, pieces, then);
    }
}
