package com.example.fxsuite.life.body.hand;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A robot's hand, a machine built as a hand is, turning as the hand rig turns ({@link HandRig}): a palm, a plate over
 * the metacarpals with a bar across the knuckles, moving with the hand; each finger's phalanges and the thumb's, its
 * metacarpal too, each a tapered link from its joint to short of the next, the last with a pad at its tip; and at each
 * joint a barrel across the finger, the hinge it turns on, moving with the link it carries. Each piece made in the
 * hand's frame as on the right side as the hand hangs at rest, the papers' centimetres; the links and the palm in the
 * paint asked, the barrels in the housings'.
 */
public record MechanicalHand() {

    /** How thick a link is at its joint and at its far end, and a barrel, as against its finger's bones. */
    private static final double ROOT = 0.78;
    private static final double TIP = 0.64;
    private static final double BARREL = 0.55;
    private static final double BARREL_WIDE = 0.72;
    /** How far short of its next joint a link stops; how big a fingertip's pad is. */
    private static final double SHORT = 0.25;
    private static final double PAD = 0.55;
    /** The palm: its plate's middle, half-sizes and how thick, and its knuckle bar's radius. */
    private static final double PALM_DOWN = -6.2;
    private static final double PALM_OUT = 0.15;
    private static final double PALM_ACROSS = 3.9;
    private static final double PALM_ALONG = 3.6;
    private static final double PALM_THICK = 1.15;
    private static final double KNUCKLES = 0.72;
    /** How much its shapes blend where they meet, and how finely it is meshed, cm. */
    private static final double BLEND = 0.35;
    private static final double CELL = 0.12;

    /**
     * A piece of it: the segment it moves with, or none, the palm, moving with the hand; what it is painted; its
     * surface, in the hand's frame at rest.
     *
     * @param segment what it moves with, if not the palm
     * @param paint   what it is painted
     * @param mesh    its surface
     */
    public record Piece(Optional<Segment> segment, Paint paint, Mesher.Mesh mesh) {
    }

    /** Its pieces, its palm and links in {@code paint}, its barrels in the housings' paint. */
    public List<Piece> of(final Paint paint) {
        final List<Piece> pieces = new ArrayList<>(List.of(new Piece(Optional.empty(), paint, palm())));
        for (final Segment s : Segment.values()) {
            pieces.add(new Piece(Optional.of(s), paint, link(s)));
            pieces.add(new Piece(Optional.of(s), Paint.HOUSING, barrel(s)));
        }
        return pieces;
    }

    /** The palm: a plate over the metacarpals, from the wrist to the knuckles, and a bar across the knuckles. */
    private Mesher.Mesh palm() {
        final List<Field.Element> made = new ArrayList<>(List.of(new Field.Element("plate", Field.Ellipsoid.of(
                new V3(0, PALM_DOWN, PALM_OUT), V3.X.times(PALM_ACROSS), V3.Y.times(PALM_ALONG),
                V3.Z.times(PALM_THICK)), Field.Op.ADD, 0)));
        for (final Digit d : new Digit[] {Digit.INDEX, Digit.MIDDLE, Digit.RING, Digit.LITTLE}) {
            made.add(new Field.Element(d.said(), Field.Cone.of(d.base(), d.head(), KNUCKLES * d.thick(),
                    KNUCKLES * d.thick()), Field.Op.ADD, BLEND));
        }
        return Mesher.of(new Field(made), CELL);
    }

    /** {@code s}'s link: tapered from its joint to short of its far end; the last of a finger with a pad at its tip. */
    private Mesher.Mesh link(final Segment s) {
        final double k = s.digit().thick();
        final V3 from = s.joint();
        final V3 to = s.end().minus(s.way().times(SHORT));
        final List<Field.Element> made = new ArrayList<>(List.of(new Field.Element("link",
                Field.Cone.of(from, to, ROOT * k, TIP * k), Field.Op.ADD, 0)));
        if (s.phalanx() == s.digit().phalanges() - 1) {
            made.add(new Field.Element("pad", Field.Ellipsoid.of(to.plus(V3.Z.times(0.15 * k)), V3.X.times(PAD * k),
                    s.way().times(PAD * k), V3.Z.times(0.8 * PAD * k)), Field.Op.ADD, BLEND));
        }
        return Mesher.of(new Field(made), CELL);
    }

    /** {@code s}'s barrel: across its joint, the line it bends about, wider than the link. */
    private Mesher.Mesh barrel(final Segment s) {
        final double k = s.digit().thick();
        final V3 across = s.way().cross(V3.Z).unit();
        return Mesher.of(new Field(List.of(new Field.Element("barrel", Field.Cylinder.of(s.joint(), across,
                BARREL * k, BARREL_WIDE * k, 0.15 * k), Field.Op.ADD, 0))), CELL);
    }
}
