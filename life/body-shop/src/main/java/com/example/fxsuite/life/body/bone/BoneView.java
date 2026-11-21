package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

import java.util.function.UnaryOperator;

/**
 * A bone's surface drawn on its own, standing on the ground over the scene's origin: turned into the scene's frame,
 * where up is {@code -y} and the bone faces the viewer from the bench's front ({@code x} and {@code z} the other way),
 * each point with its own normal, facing out.
 */
public record BoneView() implements StatelessFunctionalObject {

    public static final BoneView INSTANCE = new BoneView();

    /** {@code mesh}, coloured {@code paint}, standing on the ground, the middle of its box over the origin. */
    public Group node(final Mesher.Mesh mesh, final Paint paint) {
        final double[] mid = middle(mesh.points());
        return drawn(mesh, paint, new double[] {mid[0], mid[3], mid[2]}, false);
    }

    /**
     * {@code mesh}, coloured {@code paint}, as it is in its bone's frame, to be put where its bone is: its point
     * {@code p} at {@code -p} in the scene, so its bone's place {@code (R, t)} is the scene's {@code (R, -t)}.
     */
    public Group inItsFrame(final Mesher.Mesh mesh, final Paint paint) {
        return drawn(mesh, paint, new double[] {0, 0, 0}, false);
    }

    /** As {@link #inItsFrame}, but the bone's mirror across the middle: a bone on the left drawn as on the right. */
    public Group mirrored(final Mesher.Mesh mesh, final Paint paint) {
        return drawn(mesh, paint, new double[] {0, 0, 0}, true);
    }

    /**
     * {@code mesh}, coloured {@code paint}, each point where {@code map} takes it; {@code mirrors} if the map is a
     * mirror's, turning the surface inside out, which is turned back.
     */
    public Group mapped(final Mesher.Mesh mesh, final Paint paint, final UnaryOperator<V3> map, final boolean mirrors) {
        final float[] p = mesh.points().clone();
        for (int i = 0; i < p.length; i += 3) {
            final V3 v = map.apply(new V3(p[i], p[i + 1], p[i + 2]));
            p[i] = (float) v.x();
            p[i + 1] = (float) v.y();
            p[i + 2] = (float) v.z();
        }
        return made(p, mesh.triangles().clone(), paint, mirrors);
    }

    private Group drawn(final Mesher.Mesh mesh, final Paint paint, final double[] from, final boolean mirrored) {
        final float[] p = mesh.points().clone();
        // into the scene's frame, turned and turned over: a mirror, so the triangles go round the other way; and
        // mirrored again for a bone on the left, which turns them back
        for (int i = 0; i < p.length; i += 3) {
            p[i] = (float) ((mirrored ? 1 : -1) * (p[i] - from[0]));
            p[i + 1] = (float) -(p[i + 1] - from[1]);
            p[i + 2] = (float) -(p[i + 2] - from[2]);
        }
        return made(p, mesh.triangles().clone(), paint, !mirrored);
    }

    /** Points {@code p} and triangles {@code t} made a surface, the triangles turned round first if {@code over}. */
    private Group made(final float[] p, final int[] t, final Paint paint, final boolean over) {
        if (over) {
            for (int i = 0; i < t.length; i += 3) {
                final int b = t[i + 1];
                t[i + 1] = t[i + 2];
                t[i + 2] = b;
            }
        }
        final TriangleMesh m = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        m.getPoints().setAll(p);
        m.getNormals().setAll(normals(p, t));
        m.getTexCoords().addAll(0, 0);
        final int[] faces = new int[t.length * 3];
        for (int i = 0; i < t.length; i += 3) {
            final int[] corner = {t[i], t[i + 1], t[i + 2]};
            for (int k = 0; k < 3; k++) {
                faces[3 * (i + k)] = corner[k];
                faces[3 * (i + k) + 1] = corner[k];
            }
        }
        m.getFaces().setAll(faces);
        final MeshView view = new MeshView(m);
        view.setCullFace(CullFace.BACK);
        final PhongMaterial material = new PhongMaterial(Color.web(paint.colour()));
        material.setSpecularColor(Color.web(paint.finish() == Paint.Finish.GLOSS ? "#9a968c" : "#3a3934"));
        material.setSpecularPower(paint.finish() == Paint.Finish.GLOSS ? 30 : 12);
        view.setMaterial(material);
        return new Group(view);
    }

    /** How big {@code mesh} is each way, cm: its box's width, height and depth. */
    public double[] size(final Mesher.Mesh mesh) {
        final double[] b = box(mesh.points());
        return new double[] {b[3] - b[0], b[4] - b[1], b[5] - b[2]};
    }

    private double[] middle(final float[] p) {
        final double[] b = box(p);
        return new double[] {(b[0] + b[3]) / 2, (b[1] + b[4]) / 2, (b[2] + b[5]) / 2, b[1]};
    }

    private double[] box(final float[] p) {
        final double[] b = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE,
            -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (int i = 0; i < p.length; i++) {
            b[i % 3] = Math.min(b[i % 3], p[i]);
            b[i % 3 + 3] = Math.max(b[i % 3 + 3], p[i]);
        }
        return p.length == 0 ? new double[6] : b;
    }

    /** Every point's normal, in the scene: its triangles', {@code t} as they go round there, by size, facing out. */
    private float[] normals(final float[] p, final int[] t) {
        final float[] n = new float[p.length];
        for (int i = 0; i < t.length; i += 3) {
            final int a = 3 * t[i];
            final int b = 3 * t[i + 1];
            final int c = 3 * t[i + 2];
            final float ux = p[b] - p[a];
            final float uy = p[b + 1] - p[a + 1];
            final float uz = p[b + 2] - p[a + 2];
            final float vx = p[c] - p[a];
            final float vy = p[c + 1] - p[a + 1];
            final float vz = p[c + 2] - p[a + 2];
            for (final int q : new int[] {a, b, c}) {
                n[q] += uy * vz - uz * vy;
                n[q + 1] += uz * vx - ux * vz;
                n[q + 2] += ux * vy - uy * vx;
            }
        }
        for (int i = 0; i < n.length; i += 3) {
            final float l = (float) Math.sqrt(n[i] * n[i] + n[i + 1] * n[i + 1] + n[i + 2] * n[i + 2]);
            final float k = l > 0 ? 1 / l : 0;
            n[i] *= k;
            n[i + 1] *= k;
            n[i + 2] *= k;
        }
        return n;
    }
}
