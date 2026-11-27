package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.hand.MechanicalHand;
import com.example.fxsuite.life.body.hand.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A robot's hands, mechanical ({@link MechanicalHand}), the same for every robot: on each hand its palm, moving with
 * the hand, and its fingers' links and barrels, each moving with its segment of the hand rig. Made on the body as it
 * is bound, in the body's frame, the left the right's mirror. What a robot is built of first left without hands, all
 * but the housing at the wrist. Its fingers' pieces kept said of by their hand, their paint and their segment, by
 * their names; read back as they were.
 */
public final class RobotHands {

    private static final int HAND = 0;
    private static final int PAINT = 1;
    private static final int SEGMENT = 2;
    private static final Set<Part> HANDS = Set.of(Part.RIGHT_HAND, Part.LEFT_HAND);

    /**
     * A piece of a finger: the hand it is on, the segment it moves with, what it is painted, its surface in the body's
     * frame as bound.
     *
     * @param hand    the hand it is on
     * @param segment the segment it moves with
     * @param paint   what it is painted
     * @param mesh    its surface
     */
    public record Finger(Part hand, Segment segment, Paint paint, Mesher.Mesh mesh) {
    }

    /**
     * A robot's hands made: each palm, a piece moving with its hand; each finger's pieces.
     *
     * @param palms   each palm
     * @param fingers each finger's pieces
     */
    public record Made(List<RobotParts.Piece> palms, List<Finger> fingers) {
    }

    /** Both hands, on the body {@code bound} holds, their palms and links in {@code paint}. */
    public Made of(final Rig bound, final Paint paint) {
        final List<MechanicalHand.Piece> hand = new MechanicalHand().of(paint);
        final List<RobotParts.Piece> palms = new ArrayList<>();
        final List<Finger> fingers = new ArrayList<>();
        for (final Part h : List.of(Part.RIGHT_HAND, Part.LEFT_HAND)) {
            final Rig.Frame f = bound.frame(h);
            for (final MechanicalHand.Piece p : hand) {
                final Mesher.Mesh placed = placed(f, p.mesh());
                if (p.segment().isPresent()) {
                    fingers.add(new Finger(h, p.segment().get(), p.paint(), placed));
                } else {
                    palms.add(new RobotParts.Piece(h, p.paint(), placed));
                }
            }
        }
        return new Made(palms, fingers);
    }

    /** {@code made} without its hands but for the housing at each wrist. */
    public List<RobotParts.Piece> handless(final List<RobotParts.Piece> made) {
        return made.stream().filter(p -> !HANDS.contains(p.part()) || p.paint() == Paint.HOUSING).toList();
    }

    /** {@code fingers}, each piece as it is kept. */
    public List<Piece> kept(final List<Finger> fingers) {
        return fingers.stream().map(f -> new Piece(List.of(f.hand().name(), f.paint().name(), f.segment().name()),
                f.mesh().points(), f.mesh().triangles())).toList();
    }

    /** The fingers' pieces of {@code kept}, each as it was made. */
    public List<Finger> fingers(final List<Piece> kept) {
        return kept.stream().filter(p -> p.labels().size() == 3).map(p -> new Finger(
                Part.valueOf(p.labels().get(HAND)), Segment.valueOf(p.labels().get(SEGMENT)),
                Paint.valueOf(p.labels().get(PAINT)), new Mesher.Mesh(p.points(), p.triangles()))).toList();
    }

    /** {@code mesh}, in {@code f}'s own frame, in the body's. */
    private Mesher.Mesh placed(final Rig.Frame f, final Mesher.Mesh mesh) {
        final float[] at = mesh.points().clone();
        for (int i = 0; i < at.length; i += 3) {
            final V3 v = f.place(new V3(at[i], at[i + 1], at[i + 2]));
            at[i] = (float) v.x();
            at[i + 1] = (float) v.y();
            at[i + 2] = (float) v.z();
        }
        return new Mesher.Mesh(at, mesh.triangles());
    }
}
