package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

import java.util.function.UnaryOperator;

/**
 * A muscle's surface drawn ({@link MuscleShape.Surface}): its belly one colour, its tendons another, pale, the one
 * becoming the other where the surface does; each point coloured as far tendon as it is, from a strip of colour from
 * belly to tendon, sharp in its middle. Into a scene by a map from the body's frame; its triangles turned round if the
 * map is a mirror's.
 */
public record MuscleView() implements StatelessFunctionalObject {

    public static final MuscleView INSTANCE = new MuscleView();

    /** How many steps the strip of colour has from belly to tendon. */
    private static final int STRIP = 64;

    /** {@code s} drawn, its belly {@code belly}, its tendons {@code tendon}, each point where {@code map} puts it. */
    public MeshView node(final MuscleShape.Surface s, final String belly, final String tendon,
                         final UnaryOperator<V3> map, final boolean mirrors) {
        final float[] p = s.mesh().points().clone();
        for (int i = 0; i < p.length; i += 3) {
            final V3 v = map.apply(new V3(p[i], p[i + 1], p[i + 2]));
            p[i] = (float) v.x();
            p[i + 1] = (float) v.y();
            p[i + 2] = (float) v.z();
        }
        final int[] t = s.mesh().triangles().clone();
        if (mirrors) {
            for (int i = 0; i < t.length; i += 3) {
                final int b = t[i + 1];
                t[i + 1] = t[i + 2];
                t[i + 2] = b;
            }
        }
        final TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        mesh.getPoints().setAll(p);
        mesh.getNormals().setAll(normals(p, t));
        final float[] uv = new float[2 * s.tendon().length];
        for (int i = 0; i < s.tendon().length; i++) {
            uv[2 * i] = Math.clamp(s.tendon()[i], 0.01f, 0.99f);
            uv[2 * i + 1] = 0.5f;
        }
        mesh.getTexCoords().setAll(uv);
        final int[] faces = new int[3 * t.length];
        for (int i = 0; i < t.length; i++) {
            faces[3 * i] = t[i];
            faces[3 * i + 1] = t[i];
            faces[3 * i + 2] = t[i];
        }
        mesh.getFaces().setAll(faces);
        final MeshView view = new MeshView(mesh);
        view.setCullFace(CullFace.BACK);
        final PhongMaterial m = new PhongMaterial(Color.WHITE);
        m.setDiffuseMap(strip(Color.web(belly), Color.web(tendon)));
        m.setSpecularColor(Color.web("#5a4a44"));
        m.setSpecularPower(18);
        view.setMaterial(m);
        return view;
    }

    /** A strip from {@code belly} to {@code tendon}: the one to two fifths along, the other from three, blended between. */
    private WritableImage strip(final Color belly, final Color tendon) {
        final WritableImage image = new WritableImage(STRIP, 2);
        final PixelWriter w = image.getPixelWriter();
        for (int x = 0; x < STRIP; x++) {
            final double u = (x + 0.5) / STRIP;
            final double f = Math.clamp((u - 0.4) / 0.2, 0, 1);
            final Color c = belly.interpolate(tendon, f * f * (3 - 2 * f));
            w.setColor(x, 0, c);
            w.setColor(x, 1, c);
        }
        return image;
    }

    /** Every point's normal: its triangles', {@code t} as they go round, by size, facing out. */
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
