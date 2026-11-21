package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;

import java.util.List;

/**
 * A body's flesh as one surface, bound to the parts it moves with: each of its points as it was found, with the body
 * held to be bound ({@link Fleshing}), and how much it follows each of up to four parts, the shares adding to one.
 * Posed, each point is moved by its parts' moves blended by its shares, each move a turn and a shift taken together
 * (dual quaternion skinning, Kavan's), so that the flesh round a bending knee or elbow turns round it, keeping its
 * bulk, rather than its corners pinching in as places blended would; and its way out turned the same.
 */
public final class Flesh {

    /** How many parts a point may follow. */
    public static final int PARTS = 4;

    private final float[] points;
    private final float[] normals;
    private final int[] triangles;
    private final List<Part> parts;
    private final List<Rig.Frame> bound;
    private final int[] which;
    private final float[] shares;

    /**
     * Points and their ways out, as found; its triangles; the parts it follows and where each was as it was bound; for
     * each point, which of them it follows (by number, {@code -1} none) and how much, {@link #PARTS} a point.
     */
    public Flesh(final float[] points, final float[] normals, final int[] triangles, final List<Part> parts,
                 final List<Rig.Frame> bound, final int[] which, final float[] shares) {
        this.points = points.clone();
        this.normals = normals.clone();
        this.triangles = triangles.clone();
        this.parts = List.copyOf(parts);
        this.bound = List.copyOf(bound);
        this.which = which.clone();
        this.shares = shares.clone();
    }

    /** How many points it has. */
    public int size() {
        return points.length / 3;
    }

    /** Its triangles, three points' numbers each, facing out. */
    public int[] triangles() {
        return triangles.clone();
    }

    /** How many triangles it has. */
    public int triangleCount() {
        return triangles.length / 3;
    }

    /** Its points' ways out, as found, three a point: for what is cut from it. */
    float[] normals() {
        return normals.clone();
    }

    /** The parts it follows, and where each was bound. */
    List<Part> parts() {
        return parts;
    }

    List<Rig.Frame> bound() {
        return bound;
    }

    /** Which parts each point follows, by number, and how much: {@link #PARTS} a point. */
    int[] which() {
        return which.clone();
    }

    float[] shares() {
        return shares.clone();
    }

    /** Point {@code i} as it was found. */
    public V3 point(final int i) {
        return new V3(points[3 * i], points[3 * i + 1], points[3 * i + 2]);
    }

    /** How much point {@code i} follows part {@code p}. */
    public double share(final int i, final Part p) {
        double s = 0;
        for (int k = 0; k < PARTS; k++) {
            final int q = which[PARTS * i + k];
            if (q >= 0 && parts.get(q) == p) {
                s += shares[PARTS * i + k];
            }
        }
        return s;
    }

    /** Every point and its way out posed as {@code rig} holds the body, into {@code at} and {@code out}, three each. */
    public void posed(final Rig rig, final float[] at, final float[] out) {
        final double[][] moves = new double[parts.size()][];
        for (int q = 0; q < moves.length; q++) {
            final Rig.Frame now = rig.frame(parts.get(q));
            final Rig.Frame then = bound.get(q);
            final Rot turn = now.turn().then(then.turn().back());
            moves[q] = dual(turn, now.at().minus(turn.apply(then.at())));
        }
        final double[] b = new double[8];
        for (int i = 0; i < size(); i++) {
            java.util.Arrays.fill(b, 0);
            double[] first = null;
            for (int k = 0; k < PARTS; k++) {
                final int q = which[PARTS * i + k];
                if (q >= 0) {
                    final double[] m = moves[q];
                    first = first == null ? m : first;
                    final double dot = m[0] * first[0] + m[1] * first[1] + m[2] * first[2] + m[3] * first[3];
                    final double w = shares[PARTS * i + k] * (dot < 0 ? -1 : 1);
                    for (int c = 0; c < 8; c++) {
                        b[c] += w * m[c];
                    }
                }
            }
            moved(b, i, at, out);
        }
    }

    /** Point {@code i} and its way out moved as the blended dual quaternion {@code b} says, into the arrays. */
    private void moved(final double[] b, final int i, final float[] at, final float[] out) {
        final double l = Math.sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2] + b[3] * b[3]);
        final double rw = b[0] / l;
        final double rx = b[1] / l;
        final double ry = b[2] / l;
        final double rz = b[3] / l;
        final double dw = b[4] / l;
        final double dx = b[5] / l;
        final double dy = b[6] / l;
        final double dz = b[7] / l;
        final double tx = 2 * (rw * dx - dw * rx + ry * dz - rz * dy);
        final double ty = 2 * (rw * dy - dw * ry + rz * dx - rx * dz);
        final double tz = 2 * (rw * dz - dw * rz + rx * dy - ry * dx);
        for (int part = 0; part < 2; part++) {
            final float[] src = part == 0 ? points : normals;
            final float[] dst = part == 0 ? at : out;
            final double px = src[3 * i];
            final double py = src[3 * i + 1];
            final double pz = src[3 * i + 2];
            final double cx = ry * pz - rz * py;
            final double cy = rz * px - rx * pz;
            final double cz = rx * py - ry * px;
            dst[3 * i] = (float) (px + 2 * (rw * cx + ry * cz - rz * cy) + (part == 0 ? tx : 0));
            dst[3 * i + 1] = (float) (py + 2 * (rw * cy + rz * cx - rx * cz) + (part == 0 ? ty : 0));
            dst[3 * i + 2] = (float) (pz + 2 * (rw * cz + rx * cy - ry * cx) + (part == 0 ? tz : 0));
        }
    }

    /** The turn {@code r} and then the shift {@code t}, as a dual quaternion: its turn's four, then its shift's. */
    private double[] dual(final Rot r, final V3 t) {
        final double tr = r.xx() + r.yy() + r.zz();
        final double w;
        final double x;
        final double y;
        final double z;
        if (tr > 0) {
            final double s = Math.sqrt(tr + 1) * 2;
            w = 0.25 * s;
            x = (r.zy() - r.yz()) / s;
            y = (r.xz() - r.zx()) / s;
            z = (r.yx() - r.xy()) / s;
        } else if (r.xx() > r.yy() && r.xx() > r.zz()) {
            final double s = Math.sqrt(1 + r.xx() - r.yy() - r.zz()) * 2;
            w = (r.zy() - r.yz()) / s;
            x = 0.25 * s;
            y = (r.xy() + r.yx()) / s;
            z = (r.xz() + r.zx()) / s;
        } else if (r.yy() > r.zz()) {
            final double s = Math.sqrt(1 + r.yy() - r.xx() - r.zz()) * 2;
            w = (r.xz() - r.zx()) / s;
            x = (r.xy() + r.yx()) / s;
            y = 0.25 * s;
            z = (r.yz() + r.zy()) / s;
        } else {
            final double s = Math.sqrt(1 + r.zz() - r.xx() - r.yy()) * 2;
            w = (r.yx() - r.xy()) / s;
            x = (r.xz() + r.zx()) / s;
            y = (r.yz() + r.zy()) / s;
            z = 0.25 * s;
        }
        return new double[] {w, x, y, z, -0.5 * (t.x() * x + t.y() * y + t.z() * z),
            0.5 * (w * t.x() + t.y() * z - t.z() * y), 0.5 * (w * t.y() + t.z() * x - t.x() * z),
            0.5 * (w * t.z() + t.x() * y - t.y() * x)};
    }
}
