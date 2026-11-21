package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.BoneView;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

import java.util.List;
import java.util.Set;

/**
 * A body's cage drawn, and what it is smoothed into ({@link Cage}): the skin; the cage's edges as fine rods, its creases
 * coloured as sharp as they are, one chosen brighter; its points as beads, each its number to be picked by; the bones
 * under it all. Each in the scene's frame, where up is {@code -y} and the body faces the viewer from the bench's front.
 */
public record CageView() implements StatelessFunctionalObject {

    public static final CageView INSTANCE = new CageView();

    /** How thick the edges are drawn, and how big the points, cm. */
    private static final double ROD = 0.12;
    private static final double BEAD = 0.32;

    /** {@code v}, in the body's frame, in the scene's. */
    public V3 scene(final V3 v) {
        return new V3(-v.x(), -v.y(), -v.z());
    }

    /** The skin, {@code mesh} in the body's frame, coloured {@code colour}. */
    public Node skin(final Mesher.Mesh mesh, final String colour) {
        final Group g = BoneView.INSTANCE.mapped(mesh, Paint.SKIN, this::scene, true);
        final PhongMaterial m = (PhongMaterial) ((MeshView) g.getChildren().getFirst()).getMaterial();
        m.setDiffuseColor(Color.web(colour));
        m.setSpecularColor(Color.web("#3a3530"));
        m.setSpecularPower(14);
        return g;
    }

    /** The edges from {@code points} to {@code points}, each pair one, as rods coloured {@code colour}. */
    public Node edges(final List<V3> points, final List<int[]> edges, final String colour, final double thick) {
        final TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        mesh.getTexCoords().addAll(0, 0);
        final float[] p = new float[edges.size() * 18];
        final int[] f = new int[edges.size() * 36];
        for (int e = 0; e < edges.size(); e++) {
            final V3 a = scene(points.get(edges.get(e)[0]));
            final V3 b = scene(points.get(edges.get(e)[1]));
            final V3 d = b.minus(a).unit();
            final V3 u = (Math.abs(d.y()) < 0.9 ? d.cross(V3.Y) : d.cross(V3.X)).unit().times(ROD * thick);
            final V3 w = d.cross(u).unit().times(ROD * thick);
            final V3[] round = {u, w.times(-0.5).plus(u.times(-0.87)), w.times(0.5).plus(u.times(-0.87))};
            for (int k = 0; k < 3; k++) {
                put(p, 6 * e + k, a.plus(round[k]));
                put(p, 6 * e + 3 + k, b.plus(round[k]));
            }
            for (int k = 0; k < 3; k++) {
                final int n = (k + 1) % 3;
                final int[] t = {6 * e + k, 6 * e + 3 + k, 6 * e + 3 + n, 6 * e + k, 6 * e + 3 + n, 6 * e + n};
                for (int q = 0; q < 6; q++) {
                    f[36 * e + 12 * k + 2 * q] = t[q];
                }
            }
        }
        mesh.getPoints().setAll(p);
        mesh.getFaces().setAll(f);
        final MeshView view = new MeshView(mesh);
        view.setCullFace(CullFace.NONE);
        view.setMaterial(new PhongMaterial(Color.web(colour)));
        return view;
    }

    /** The cage's points as beads, each its number as its user data; {@code chosen} bigger and red. */
    public Group points(final List<V3> points, final Set<Integer> chosen) {
        final Group g = new Group();
        final PhongMaterial plain = new PhongMaterial(Color.web("#2b2f3a"));
        final PhongMaterial picked = new PhongMaterial(Color.web("#e0322a"));
        for (int i = 0; i < points.size(); i++) {
            final boolean c = chosen.contains(i);
            final Sphere s = new Sphere(c ? BEAD * 2 : BEAD, 6);
            final V3 v = scene(points.get(i));
            s.setTranslateX(v.x());
            s.setTranslateY(v.y());
            s.setTranslateZ(v.z());
            s.setMaterial(c ? picked : plain);
            s.setUserData(i);
            g.getChildren().add(s);
        }
        return g;
    }

    /**
     * The bones of the body {@code bound} holds, of the parts {@code which} says, as far a woman's as {@code female},
     * as finely as {@code detail}.
     */
    public Group bones(final Rig bound, final double female, final Detail detail,
                       final java.util.function.Predicate<Part> which) {
        final Group g = new Group();
        for (final Part p : Part.values()) {
            if (!which.test(p)) {
                continue;
            }
            final Part right = p.side() < 0 ? p.other() : p;
            final Rig.Frame f = bound.frame(p);
            for (final PartBones.Piece piece : PartBones.INSTANCE.pieces(right, detail, female)) {
                g.getChildren().add(BoneView.INSTANCE.mapped(piece.mesh(), piece.paint(), v -> scene(f.place(v)),
                        p.side() >= 0));
            }
        }
        g.setMouseTransparent(true);
        return g;
    }

    private void put(final float[] p, final int i, final V3 v) {
        p[3 * i] = (float) v.x();
        p[3 * i + 1] = (float) v.y();
        p[3 * i + 2] = (float) v.z();
    }
}
