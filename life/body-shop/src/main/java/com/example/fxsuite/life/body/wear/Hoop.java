package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A ring of stretched knit round a body, as a band drawn tight round anything lies: across its hollows, on its
 * bulges, round what is beneath it in one plane as a hull is ({@link Beneath}); its points found where lines out from
 * the middle of that hull, every way asked, meet it, a little out from it, as thick as the knit; each following the
 * body as the points beneath it under the band do, between the two it spans as far as it lies between them.
 */
public record Hoop() implements StatelessFunctionalObject {

    public static final Hoop INSTANCE = new Hoop();

    /** A plane through {@code at}, its two ways {@code u} and {@code w}, a ring in it read round from {@code u}. */
    public record Plane(V3 at, V3 u, V3 w) {
        V3 normal() {
            return u.cross(w).unit();
        }

        V3 place(final double x, final double y) {
            return at.plus(u.times(x)).plus(w.times(y));
        }
    }

    /** A ring: its points and how each follows the body; the middle it was found from. */
    public record Ring(List<V3> points, List<Weights> weights, V3 middle) {
    }

    /** A point where the body crosses a plane, in the plane's own two ways, and how it follows the body. */
    private record Crossing(double x, double y, Weights weights) {
    }

    /**
     * The ring round what of {@code beneath}'s triangles {@code among} cross {@code plane} where {@code keep} says, at
     * {@code angles} round from the plane's {@code u} toward its {@code w}, {@code ease} out from it; none if nothing
     * is there.
     */
    public Ring ring(final Beneath beneath, final int[] among, final Plane plane, final Predicate<V3> keep,
                     final double[] angles, final double ease) {
        final List<Crossing> hull = hull(crossings(beneath, among, plane, keep));
        if (hull.size() < 3) {
            return null;
        }
        final double[] c = middle(hull);
        final List<V3> points = new ArrayList<>();
        final List<Weights> weights = new ArrayList<>();
        for (final double a : angles) {
            final double dx = Math.cos(a);
            final double dy = Math.sin(a);
            for (int k = 0; k < hull.size(); k++) {
                final Crossing p = hull.get(k);
                final Crossing q = hull.get((k + 1) % hull.size());
                final double ex = q.x() - p.x();
                final double ey = q.y() - p.y();
                // c + t d = p + s e
                final double den = dx * ey - dy * ex;
                if (Math.abs(den) < 1e-12) {
                    continue;
                }
                final double rx = p.x() - c[0];
                final double ry = p.y() - c[1];
                final double s = (dy * rx - dx * ry) / den;
                final double t = (rx * ey - ex * ry) / den;
                if (s >= -1e-9 && s <= 1 + 1e-9 && t > 0) {
                    final double l = Math.hypot(ex, ey);
                    points.add(plane.place(c[0] + t * dx + ease * ey / l, c[1] + t * dy - ease * ex / l));
                    weights.add(p.weights().toward(q.weights(), Math.clamp(s, 0, 1)));
                    break;
                }
            }
        }
        return new Ring(points, weights, plane.place(c[0], c[1]));
    }

    /** Where {@code among}'s triangles' edges cross {@code plane}, where {@code keep} says. */
    private List<Crossing> crossings(final Beneath b, final int[] among, final Plane plane, final Predicate<V3> keep) {
        final V3 n = plane.normal();
        final double off = n.dot(plane.at());
        final float[] p = b.points();
        final int[] t = b.triangles();
        final List<Crossing> out = new ArrayList<>();
        for (final int tri : among) {
            for (int e = 0; e < 3; e++) {
                final int i = t[3 * tri + e];
                final int j = t[3 * tri + (e + 1) % 3];
                final double di = n.x() * p[3 * i] + n.y() * p[3 * i + 1] + n.z() * p[3 * i + 2] - off;
                final double dj = n.x() * p[3 * j] + n.y() * p[3 * j + 1] + n.z() * p[3 * j + 2] - off;
                if (di < 0 == dj < 0) {
                    continue;
                }
                final double s = di / (di - dj);
                final V3 at = b.point(i).plus(b.point(j).minus(b.point(i)).times(s));
                if (keep.test(at)) {
                    final V3 r = at.minus(plane.at());
                    out.add(new Crossing(r.dot(plane.u()), r.dot(plane.w()), b.weights()[i].toward(b.weights()[j], s)));
                }
            }
        }
        return out;
    }

    /** The hull round {@code all}, its points in order round from the plane's {@code u} toward its {@code w}. */
    private List<Crossing> hull(final List<Crossing> all) {
        final List<Crossing> sorted = new ArrayList<>(all);
        sorted.sort((a, b) -> a.x() != b.x() ? Double.compare(a.x(), b.x()) : Double.compare(a.y(), b.y()));
        final List<Crossing> h = new ArrayList<>();
        for (int pass = 0; pass < 2; pass++) {
            final int start = h.size();
            for (final Crossing c : sorted) {
                while (h.size() >= start + 2 && turn(h.get(h.size() - 2), h.getLast(), c) <= 0) {
                    h.removeLast();
                }
                h.add(c);
            }
            h.removeLast();
            java.util.Collections.reverse(sorted);
        }
        return h;
    }

    private double turn(final Crossing o, final Crossing a, final Crossing b) {
        return (a.x() - o.x()) * (b.y() - o.y()) - (a.y() - o.y()) * (b.x() - o.x());
    }

    /** The middle of the area {@code hull} closes round. */
    private double[] middle(final List<Crossing> hull) {
        double area = 0;
        double cx = 0;
        double cy = 0;
        for (int k = 0; k < hull.size(); k++) {
            final Crossing p = hull.get(k);
            final Crossing q = hull.get((k + 1) % hull.size());
            final double cross = p.x() * q.y() - q.x() * p.y();
            area += cross;
            cx += (p.x() + q.x()) * cross;
            cy += (p.y() + q.y()) * cross;
        }
        return new double[] {cx / (3 * area), cy / (3 * area)};
    }
}
