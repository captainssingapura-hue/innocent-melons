package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.life.body.soft.Lattice;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A lattice's elements as the card draws them ({@link Lattice}): each tetrahedron drawn shrunk toward its middle, so
 * that each is seen apart from its neighbours and the lattice's make can be read; its four faces flat, each turned
 * away from its middle. Those that touch a fixed node apart from those that do not, to be drawn in another colour.
 */
public final class ElementsDrawn {

    /** How big each is drawn, of its size. */
    private static final double SHRUNK = 0.7;
    /** Each face's corners, of a tetrahedron's four. */
    private static final int[][] FACES = {{0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}};

    /** {@code l}'s elements that touch a fixed node, if {@code fixed}; else those that do not. */
    public Mesh of(final Lattice l, final boolean fixed) {
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>();
        for (int t = 0; t < l.tetCount(); t++) {
            boolean touches = false;
            final Vec3[] c = new Vec3[4];
            Vec3 middle = new Vec3(0, 0, 0);
            for (int k = 0; k < 4; k++) {
                final int n = l.tets()[4 * t + k];
                touches |= l.fixed()[n];
                c[k] = new Vec3(l.nodes()[3 * n], l.nodes()[3 * n + 1], l.nodes()[3 * n + 2]);
                middle = middle.plus(c[k].times(0.25));
            }
            if (touches != fixed) {
                continue;
            }
            for (int k = 0; k < 4; k++) {
                c[k] = middle.plus(c[k].minus(middle).times(SHRUNK));
            }
            for (final int[] f : FACES) {
                face(c[f[0]], c[f[1]], c[f[2]], middle, points, facings, corners);
            }
        }
        return new Mesh(points, facings, corners);
    }

    /** A face of corners {@code a}, {@code b} and {@code c}, flat, turned away from {@code middle}. */
    private void face(final Vec3 a, final Vec3 b, final Vec3 c, final Vec3 middle, final List<Vec3> points,
                      final List<Vec3> facings, final List<Integer> corners) {
        Vec3 out = b.minus(a).cross(c.minus(a));
        final boolean turned = out.dot(a.minus(middle)) < 0;
        out = out.times(turned ? -1 : 1).unit();
        final int first = points.size();
        points.add(a);
        points.add(turned ? c : b);
        points.add(turned ? b : c);
        for (int k = 0; k < 3; k++) {
            facings.add(out);
            corners.add(first + k);
        }
    }
}
