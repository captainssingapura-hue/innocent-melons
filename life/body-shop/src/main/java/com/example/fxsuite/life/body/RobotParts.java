package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A robot's frame on a body as it is bound, for what draws it elsewhere, as the card does: each piece in the body's
 * frame, moving with its part. Its waist, chest, neck and head its own, as its design draws them ({@link RobotFrame});
 * every other part the lab's bones ({@link PartBones}) in metal, their cartilage and teeth left out, a housing round
 * its joint.
 */
public record RobotParts() implements StatelessFunctionalObject {

    public static final RobotParts INSTANCE = new RobotParts();

    /** A piece of {@code part}, painted {@code paint}, its surface {@code mesh}, in the body's frame as bound. */
    public record Piece(Part part, Paint paint, Mesher.Mesh mesh) {
    }

    /**
     * The frame of the robot {@code design} draws, on the body {@code bound} holds, its bones as finely as
     * {@code detail}; its helmet in {@code head}.
     */
    public List<Piece> of(final Rig bound, final RobotDesign design, final Paint head, final Detail detail) {
        final List<Piece> made = new ArrayList<>();
        for (final Part p : Part.values()) {
            final Part right = p.side() < 0 ? p.other() : p;
            final Rig.Frame f = bound.frame(p);
            if (RobotFrame.designed(p)) {
                RobotFrame.part(right, design, head).forEach(s -> made.add(new Piece(p, s.paint(), placed(f, s))));
                continue;
            }
            for (final PartBones.Piece bone : PartBones.INSTANCE.pieces(right, detail)) {
                if (bone.paint() != Paint.CARTILAGE && bone.paint() != Paint.TEETH) {
                    made.add(new Piece(p, Paint.FRAME, placed(f, bone.mesh())));
                }
            }
            final double r = Bones.HOUSING.getOrDefault(right.name().replace("RIGHT_", ""), 0.0);
            if (r > 0) {
                made.add(new Piece(p, Paint.HOUSING, placed(f, Shapes.ellipsoid(p.said() + " housing", Paint.HOUSING,
                        V3.ZERO, r, r, r, 16, 10))));
            }
        }
        return made;
    }

    /** {@code mesh}, in {@code f}'s own frame, in the body's. */
    private Mesher.Mesh placed(final Rig.Frame f, final Mesher.Mesh mesh) {
        final float[] at = new float[mesh.points().length];
        for (int i = 0; i < at.length; i += 3) {
            put(at, i / 3, f.place(new V3(mesh.points()[i], mesh.points()[i + 1], mesh.points()[i + 2])));
        }
        return new Mesher.Mesh(at, mesh.triangles());
    }

    /**
     * {@code s}, in {@code f}'s own frame, in the body's: seen smooth, its points shared by its triangles; flat-faced,
     * each triangle's its own, so that each face takes the light as one.
     */
    private Mesher.Mesh placed(final Rig.Frame f, final Surface s) {
        final List<V3> points = s.points();
        final List<int[]> triangles = s.triangles();
        if (s.smooth()) {
            final float[] at = new float[3 * points.size()];
            for (int i = 0; i < points.size(); i++) {
                put(at, i, f.place(points.get(i)));
            }
            final int[] t = new int[3 * triangles.size()];
            for (int k = 0; k < triangles.size(); k++) {
                System.arraycopy(triangles.get(k), 0, t, 3 * k, 3);
            }
            return new Mesher.Mesh(at, t);
        }
        final float[] at = new float[9 * triangles.size()];
        final int[] t = new int[3 * triangles.size()];
        for (int k = 0; k < triangles.size(); k++) {
            for (int c = 0; c < 3; c++) {
                put(at, 3 * k + c, f.place(points.get(triangles.get(k)[c])));
                t[3 * k + c] = 3 * k + c;
            }
        }
        return new Mesher.Mesh(at, t);
    }

    /** {@code v} put into {@code at} as its {@code i}th point. */
    private void put(final float[] at, final int i, final V3 v) {
        at[3 * i] = (float) v.x();
        at[3 * i + 1] = (float) v.y();
        at[3 * i + 2] = (float) v.z();
    }
}
