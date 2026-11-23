package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.CageView;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.MuscleView;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * What the soft bench shows ({@link SoftBench}): the muscles a region takes in, red, their parts beyond it darker; those
 * held round them as pale lines; the bones; the region, its edges; where the taken are cut, blue; where held to their
 * bones, yellow. And the taken, listed by how deep they lie.
 */
public record SoftScene() implements StatelessFunctionalObject {

    public static final SoftScene INSTANCE = new SoftScene();

    /** How thick the region's edges are drawn, and how big a cut and a hold, cm. */
    private static final double EDGE = 0.12;
    private static final double CUT = 0.18;
    private static final double HOLD = 0.45;

    /** The scene of {@code region} and what it takes in, {@code taken}; the held if {@code rest}; {@code bones}, if any. */
    public Group world(final SoftRegion region, final RegionPieces.Taken taken, final boolean rest, final Group bones) {
        final List<Node> nodes = new ArrayList<>();
        for (final RegionPieces.Piece p : taken.taken()) {
            nodes.add(MuscleView.INSTANCE.node(beyond(region, p.surface()), "#c8463a", "#5a1f1a",
                    CageView.INSTANCE::scene, true));
        }
        if (rest) {
            for (final RegionPieces.Piece p : taken.held()) {
                final MeshView v = MuscleView.INSTANCE.node(p.surface(), "#bcbcbc", "#bcbcbc", CageView.INSTANCE::scene,
                        true);
                v.setDrawMode(DrawMode.LINE);
                nodes.add(v);
            }
        }
        if (bones != null) {
            nodes.add(bones);
        }
        nodes.addAll(edges(region));
        for (final V3 c : taken.cuts()) {
            nodes.add(dot(c, CUT, "#2f7fd6"));
        }
        for (final V3 a : taken.attached()) {
            nodes.add(dot(a, HOLD, "#f2c230"));
        }
        return new Group(nodes);
    }

    /** {@code taken} listed by how deep each lies, the outermost first: its depth, its name and side, how much is in. */
    public String listed(final RegionPieces.Taken taken, final RegionPieces.Filled filled) {
        final List<Integer> order = new ArrayList<>();
        for (int i = 0; i < taken.taken().size(); i++) {
            order.add(i);
        }
        order.sort(Comparator.comparingInt((Integer i) -> taken.taken().get(i).muscle().depth())
                .thenComparing(i -> taken.taken().get(i).muscle().name()));
        final StringBuilder s = new StringBuilder();
        for (final int i : order) {
            final RegionPieces.Piece p = taken.taken().get(i);
            s.append(String.format(Locale.ROOT, "%d  %s%s  %.1f cm3%n", p.muscle().depth(), p.muscle().name(),
                    p.muscle().paired() ? p.left() ? ", left" : ", right" : "", filled.each()[i]));
        }
        return s.toString();
    }

    /** {@code s}, its points beyond {@code region} marked as its tendon is, to be drawn darker. */
    private MuscleSurface.Surface beyond(final SoftRegion region, final MuscleSurface.Surface s) {
        final float[] p = s.mesh().points();
        final float[] out = new float[p.length / 3];
        for (int i = 0; i < out.length; i++) {
            out[i] = region.has(new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2])) ? 0 : 1;
        }
        return new MuscleSurface.Surface(s.mesh(), out, s.parts(), s.shares(), s.depth());
    }

    /** The region's twelve edges. */
    private List<Node> edges(final SoftRegion region) {
        final double[] lo = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] hi = {region.hi().x(), region.hi().y(), region.hi().z()};
        final List<Node> edges = new ArrayList<>();
        for (int along = 0; along < 3; along++) {
            for (int k = 0; k < 4; k++) {
                final double[] a = new double[3];
                final double[] b = new double[3];
                int bit = 0;
                for (int c = 0; c < 3; c++) {
                    if (c == along) {
                        a[c] = lo[c];
                        b[c] = hi[c];
                    } else {
                        a[c] = (k >> bit & 1) == 0 ? lo[c] : hi[c];
                        b[c] = a[c];
                        bit++;
                    }
                }
                edges.add(edge(new V3(a[0], a[1], a[2]), new V3(b[0], b[1], b[2])));
            }
        }
        return edges;
    }

    /** An edge from {@code a} to {@code b}, along one of the body's ways. */
    private Node edge(final V3 a, final V3 b) {
        final V3 p = CageView.INSTANCE.scene(a);
        final V3 q = CageView.INSTANCE.scene(b);
        final Box box = new Box(Math.max(EDGE, Math.abs(q.x() - p.x())), Math.max(EDGE, Math.abs(q.y() - p.y())),
                Math.max(EDGE, Math.abs(q.z() - p.z())));
        box.setTranslateX((p.x() + q.x()) / 2);
        box.setTranslateY((p.y() + q.y()) / 2);
        box.setTranslateZ((p.z() + q.z()) / 2);
        box.setMaterial(new PhongMaterial(Color.web("#3a3a3a")));
        return box;
    }

    /** A dot at {@code at}, so big, so coloured. */
    private Node dot(final V3 at, final double r, final String colour) {
        final V3 p = CageView.INSTANCE.scene(at);
        final Sphere s = new Sphere(r, 6);
        s.setTranslateX(p.x());
        s.setTranslateY(p.y());
        s.setTranslateZ(p.z());
        s.setMaterial(new PhongMaterial(Color.web(colour)));
        return s;
    }
}
