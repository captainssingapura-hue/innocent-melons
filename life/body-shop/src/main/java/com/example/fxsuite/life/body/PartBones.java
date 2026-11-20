package com.example.fxsuite.life.body;

import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Drawn;
import com.example.fxsuite.life.body.bone.Moves;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.bone.Skeleton;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The skeleton as the game draws it: the bones of each of the rig's parts, the lab's bone by bone ({@link Skeleton}),
 * as one rigid shape in the part's frame as on the right side, a part down the middle with its paired bones on both
 * sides; its surface found once, as finely as asked ({@link Detail}), a piece for each colour (the bones', the
 * cartilages'). Each bone as it stands at rest in its part: the radius turned across the ulna as far as the puppet's
 * forearms are ({@link Puppet#TWIST}), the kneecap on the front of the knee, the jaw shut, the fingers and the toes as
 * they hang. The middle ear's bones left out, too small to be seen.
 */
public record PartBones() implements StatelessFunctionalObject {

    public static final PartBones INSTANCE = new PartBones();

    /** A part's bones of one colour, their surface found. */
    public record Piece(Paint paint, Mesher.Mesh mesh) {
    }

    /** {@code p}'s bones (as on the right side, {@code p} a right part or one down the middle), as finely as asked. */
    public List<Piece> pieces(final Part p, final Detail detail) {
        final Map<Paint, List<Field.Element>> by = new LinkedHashMap<>();
        final double swell = detail.swell(p);
        for (final Bone b : Skeleton.INSTANCE.bones()) {
            if (b.part() == p && b.region() != Region.EAR) {
                final List<Field.Element> mine = by.computeIfAbsent(b.paint(), k -> new ArrayList<>());
                final Field.Shape shape = Drawn.INSTANCE.shape(b, 0);
                mine.add(new Field.Element(b.name(), placed(b, shape, swell), Field.Op.ADD, 0));
                if (p.side() == 0 && b.paired()) {
                    mine.add(new Field.Element("left " + b.name(),
                            new Moved(shape, Rot.NONE, V3.ZERO, true, swell), Field.Op.ADD, 0));
                }
            }
        }
        final List<Piece> pieces = new ArrayList<>();
        by.forEach((paint, elements) -> pieces.add(new Piece(paint, Mesher.of(new Field(elements), detail.cell(p)))));
        return pieces;
    }

    /** {@code shape}, bone {@code b}'s, where it stands in its part at rest, swollen {@code swell}. */
    private Field.Shape placed(final Bone b, final Field.Shape shape, final double swell) {
        if (b.moves() != Moves.RADIUS) {
            return new Moved(shape, Rot.NONE, V3.ZERO, false, swell);
        }
        final Rot turn = Rot.about(Rig.PRONATION, Puppet.TWIST);
        return new Moved(shape, turn, Rig.RADIUS_TOP.minus(turn.apply(Rig.RADIUS_TOP)), false, swell);
    }
}
