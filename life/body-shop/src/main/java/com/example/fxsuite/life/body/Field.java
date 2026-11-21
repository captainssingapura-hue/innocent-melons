package com.example.fxsuite.life.body;

import java.util.Arrays;
import java.util.List;

/**
 * A shape as a field of distances: at any point, how far outside it the point is, less than nothing inside. Made of
 * simple shapes laid one after another ({@link Element}), each blended into what is before it smoothly, as bone grows
 * into bone; carved out of it, a hollow; or what is before it cut down to it, as a skull is cut along its sutures into
 * its bones. A shape may itself be such a list ({@link Compound}). Where the field is nothing is the surface
 * ({@link Mesher}).
 *
 * <p>Distances in cm. Near the surface as true as the shapes make them; further off, only so far as to say which side a
 * point is.
 */
public final class Field {

    /** A shape: how far outside it a point is, near enough; the box round it. */
    public sealed interface Shape
            permits Ellipsoid, Cone, Plate, Cylinder, Torus, Plane, Squash, Compound, Moved, Stack {

        double at(double x, double y, double z);

        /** Its box: lowest x, y, z, then highest. */
        double[] box();
    }

    /** What a shape does to what is laid before it. */
    public enum Op {
        /** Joined to it. */
        ADD,
        /** Carved out of it. */
        CARVE,
        /** It cut down to this shape. */
        CLIP
    }

    /**
     * One shape laid: what it is called, what it does, how far round it it blends with what is before it (cm; nothing a
     * hard edge).
     */
    public record Element(String name, Shape shape, Op op, double blend) {
    }

    /** No box at all, for a shape that goes on for ever. */
    static final double[] EVERYWHERE = {-1e9, -1e9, -1e9, 1e9, 1e9, 1e9};

    /**
     * An ellipsoid about {@code (cx, cy, cz)}, {@code m} (row by row) taking a point's offset from its middle to where
     * it would be on a sphere of radius 1; how far from its middle its nearest side is.
     */
    public record Ellipsoid(double cx, double cy, double cz, double[] m, double[] box, double least) implements Shape {

        /** The ellipsoid about {@code c} whose half axes are {@code a}, {@code b} and {@code d}, square to each other. */
        public static Ellipsoid of(final V3 c, final V3 a, final V3 b, final V3 d) {
            // its axes as the columns of a matrix taking the sphere to it; their inverse taking it back
            final double[] k = {a.x(), b.x(), d.x(), a.y(), b.y(), d.y(), a.z(), b.z(), d.z()};
            final double det = k[0] * (k[4] * k[8] - k[5] * k[7]) - k[1] * (k[3] * k[8] - k[5] * k[6])
                    + k[2] * (k[3] * k[7] - k[4] * k[6]);
            final double[] m = {
                (k[4] * k[8] - k[5] * k[7]) / det, (k[2] * k[7] - k[1] * k[8]) / det, (k[1] * k[5] - k[2] * k[4]) / det,
                (k[5] * k[6] - k[3] * k[8]) / det, (k[0] * k[8] - k[2] * k[6]) / det, (k[2] * k[3] - k[0] * k[5]) / det,
                (k[3] * k[7] - k[4] * k[6]) / det, (k[1] * k[6] - k[0] * k[7]) / det, (k[0] * k[4] - k[1] * k[3]) / det};
            final double ex = Math.sqrt(k[0] * k[0] + k[1] * k[1] + k[2] * k[2]);
            final double ey = Math.sqrt(k[3] * k[3] + k[4] * k[4] + k[5] * k[5]);
            final double ez = Math.sqrt(k[6] * k[6] + k[7] * k[7] + k[8] * k[8]);
            final double least = Math.min(a.length(), Math.min(b.length(), d.length()));
            return new Ellipsoid(c.x(), c.y(), c.z(), m,
                    new double[] {c.x() - ex, c.y() - ey, c.z() - ez, c.x() + ex, c.y() + ey, c.z() + ez}, least);
        }

        @Override
        public double at(final double x, final double y, final double z) {
            final double dx = x - cx;
            final double dy = y - cy;
            final double dz = z - cz;
            final double qx = m[0] * dx + m[1] * dy + m[2] * dz;
            final double qy = m[3] * dx + m[4] * dy + m[5] * dz;
            final double qz = m[6] * dx + m[7] * dy + m[8] * dz;
            final double k0 = Math.sqrt(qx * qx + qy * qy + qz * qz);
            // the way out of it, unscaled: its matrix's transpose on the sphere's point
            final double tx = m[0] * qx + m[3] * qy + m[6] * qz;
            final double ty = m[1] * qx + m[4] * qy + m[7] * qz;
            final double tz = m[2] * qx + m[5] * qy + m[8] * qz;
            final double k1 = Math.sqrt(tx * tx + ty * ty + tz * tz);
            return k1 < 1e-12 ? -least : k0 * (k0 - 1) / k1;
        }
    }

    /** A cone rounded at both ends, from {@code a}, {@code r1} round, to {@code b}, {@code r2} round. */
    public record Cone(V3 a, V3 b, double r1, double r2, double[] box) implements Shape {

        public static Cone of(final V3 a, final V3 b, final double r1, final double r2) {
            final double l = b.minus(a).length();
            // its ends' spheres must not swallow each other
            final double s1 = Math.min(r1, r2 + l * 0.98);
            final double s2 = Math.min(r2, r1 + l * 0.98);
            final double r = Math.max(s1, s2);
            return new Cone(a, b, s1, s2, new double[] {Math.min(a.x(), b.x()) - r, Math.min(a.y(), b.y()) - r,
                Math.min(a.z(), b.z()) - r, Math.max(a.x(), b.x()) + r, Math.max(a.y(), b.y()) + r,
                Math.max(a.z(), b.z()) + r});
        }

        /** The round cone's distance, as Inigo Quilez works it. */
        @Override
        public double at(final double x, final double y, final double z) {
            final double bax = b.x() - a.x();
            final double bay = b.y() - a.y();
            final double baz = b.z() - a.z();
            final double l2 = bax * bax + bay * bay + baz * baz;
            final double rr = r1 - r2;
            final double a2 = l2 - rr * rr;
            final double il2 = 1 / l2;
            final double pax = x - a.x();
            final double pay = y - a.y();
            final double paz = z - a.z();
            final double yy = pax * bax + pay * bay + paz * baz;
            final double zz = yy - l2;
            final double qx = pax * l2 - bax * yy;
            final double qy = pay * l2 - bay * yy;
            final double qz = paz * l2 - baz * yy;
            final double x2 = qx * qx + qy * qy + qz * qz;
            final double y2 = yy * yy * l2;
            final double z2 = zz * zz * l2;
            final double k = Math.signum(rr) * rr * rr * x2;
            if (Math.signum(zz) * a2 * z2 > k) {
                return Math.sqrt(x2 + z2) * il2 - r2;
            }
            if (Math.signum(yy) * a2 * y2 < k) {
                return Math.sqrt(x2 + y2) * il2 - r1;
            }
            return (Math.sqrt(x2 * a2 * il2) + yy * rr) * il2 - r1;
        }
    }

    /** A flat triangle {@code a b c}, {@code thick} through, its edges rounded: a blade of bone. */
    public record Plate(V3 a, V3 b, V3 c, double thick, double[] box) implements Shape {

        public static Plate of(final V3 a, final V3 b, final V3 c, final double thick) {
            final double r = thick / 2;
            return new Plate(a, b, c, thick, new double[] {
                Math.min(a.x(), Math.min(b.x(), c.x())) - r, Math.min(a.y(), Math.min(b.y(), c.y())) - r,
                Math.min(a.z(), Math.min(b.z(), c.z())) - r, Math.max(a.x(), Math.max(b.x(), c.x())) + r,
                Math.max(a.y(), Math.max(b.y(), c.y())) + r, Math.max(a.z(), Math.max(b.z(), c.z())) + r});
        }

        /** How far from the triangle, as Inigo Quilez works it, less half its thickness. */
        @Override
        public double at(final double x, final double y, final double z) {
            final double bax = b.x() - a.x();
            final double bay = b.y() - a.y();
            final double baz = b.z() - a.z();
            final double cbx = c.x() - b.x();
            final double cby = c.y() - b.y();
            final double cbz = c.z() - b.z();
            final double acx = a.x() - c.x();
            final double acy = a.y() - c.y();
            final double acz = a.z() - c.z();
            final double pax = x - a.x();
            final double pay = y - a.y();
            final double paz = z - a.z();
            final double pbx = x - b.x();
            final double pby = y - b.y();
            final double pbz = z - b.z();
            final double pcx = x - c.x();
            final double pcy = y - c.y();
            final double pcz = z - c.z();
            // the triangle's normal
            final double nx = bay * acz - baz * acy;
            final double ny = baz * acx - bax * acz;
            final double nz = bax * acy - bay * acx;
            final double inside = Math.signum(side(bax, bay, baz, nx, ny, nz, pax, pay, paz))
                    + Math.signum(side(cbx, cby, cbz, nx, ny, nz, pbx, pby, pbz))
                    + Math.signum(side(acx, acy, acz, nx, ny, nz, pcx, pcy, pcz));
            final double d2;
            if (inside < 2) {
                d2 = Math.min(Math.min(edge(bax, bay, baz, pax, pay, paz), edge(cbx, cby, cbz, pbx, pby, pbz)),
                        edge(acx, acy, acz, pcx, pcy, pcz));
            } else {
                final double s = nx * pax + ny * pay + nz * paz;
                d2 = s * s / (nx * nx + ny * ny + nz * nz);
            }
            return Math.sqrt(d2) - thick / 2;
        }

        /** Which side of edge {@code e} a point {@code p} (from its start) is, in the triangle's plane. */
        private static double side(final double ex, final double ey, final double ez, final double nx,
                                   final double ny, final double nz, final double px, final double py,
                                   final double pz) {
            return (ey * nz - ez * ny) * px + (ez * nx - ex * nz) * py + (ex * ny - ey * nx) * pz;
        }

        /** How far, squared, a point {@code p} (from an edge's start) is from the edge {@code e}. */
        private static double edge(final double ex, final double ey, final double ez, final double px,
                                   final double py, final double pz) {
            final double t = Math.clamp((ex * px + ey * py + ez * pz) / (ex * ex + ey * ey + ez * ez), 0, 1);
            final double dx = ex * t - px;
            final double dy = ey * t - py;
            final double dz = ez * t - pz;
            return dx * dx + dy * dy + dz * dz;
        }
    }

    /**
     * A cylinder about {@code c} along {@code axis} (a unit direction), {@code r} round and {@code half} long each way
     * from its middle, its edges rounded {@code round}.
     */
    public record Cylinder(V3 c, V3 axis, double r, double half, double round, double[] box) implements Shape {

        public static Cylinder of(final V3 c, final V3 axis, final double r, final double half, final double round) {
            final V3 u = axis.unit();
            final double e = Math.max(r, half);
            return new Cylinder(c, u, r, half, Math.min(round, Math.min(r, half)),
                    new double[] {c.x() - e, c.y() - e, c.z() - e, c.x() + e, c.y() + e, c.z() + e});
        }

        @Override
        public double at(final double x, final double y, final double z) {
            final double px = x - c.x();
            final double py = y - c.y();
            final double pz = z - c.z();
            final double along = px * axis.x() + py * axis.y() + pz * axis.z();
            final double ox = px - axis.x() * along;
            final double oy = py - axis.y() * along;
            final double oz = pz - axis.z() * along;
            final double dx = Math.sqrt(ox * ox + oy * oy + oz * oz) - r + round;
            final double dy = Math.abs(along) - half + round;
            final double mx = Math.max(dx, 0);
            final double my = Math.max(dy, 0);
            return Math.min(Math.max(dx, dy), 0) + Math.sqrt(mx * mx + my * my) - round;
        }
    }

    /** A ring about {@code c}, round {@code axis}, {@code big} from its middle to its tube's, its tube {@code small} round. */
    public record Torus(V3 c, V3 axis, double big, double small, double[] box) implements Shape {

        public static Torus of(final V3 c, final V3 axis, final double big, final double small) {
            final double e = big + small;
            return new Torus(c, axis.unit(), big, small,
                    new double[] {c.x() - e, c.y() - e, c.z() - e, c.x() + e, c.y() + e, c.z() + e});
        }

        @Override
        public double at(final double x, final double y, final double z) {
            final double px = x - c.x();
            final double py = y - c.y();
            final double pz = z - c.z();
            final double along = px * axis.x() + py * axis.y() + pz * axis.z();
            final double ox = px - axis.x() * along;
            final double oy = py - axis.y() * along;
            final double oz = pz - axis.z() * along;
            final double q = Math.sqrt(ox * ox + oy * oy + oz * oz) - big;
            return Math.sqrt(q * q + along * along) - small;
        }
    }

    /** Everything on the far side of a plane through {@code at} from {@code out} (a unit direction): what it cuts to. */
    public record Plane(V3 at, V3 out) implements Shape {

        public static Plane of(final V3 at, final V3 out) {
            return new Plane(at, out.unit());
        }

        @Override
        public double at(final double x, final double y, final double z) {
            return (x - at.x()) * out.x() + (y - at.y()) * out.y() + (z - at.z()) * out.z();
        }

        @Override
        public double[] box() {
            return EVERYWHERE;
        }
    }

    /**
     * {@code inner} squashed {@code ratio} as thick along {@code way} (a unit direction) through {@code at}: an ellipse
     * of a circle, a flat bone of a round one.
     */
    public record Squash(Shape inner, V3 at, V3 way, double ratio, double[] box) implements Shape {

        public static Squash of(final Shape inner, final V3 at, final V3 way, final double ratio) {
            // the inner shape's box squashed toward at along the way, near enough: kept as it is, which holds it
            return new Squash(inner, at, way.unit(), ratio, inner.box());
        }

        @Override
        public double at(double x, double y, double z) {
            final double s = ((x - at.x()) * way.x() + (y - at.y()) * way.y() + (z - at.z()) * way.z()) * (1 / ratio - 1);
            x += way.x() * s;
            y += way.y() * s;
            z += way.z() * s;
            return inner.at(x, y, z) * ratio;
        }
    }

    /** Shapes laid in turn ({@link Element}), a shape of their own; its box round what is joined, cut to what clips it. */
    public record Compound(List<Element> elements, double[] box) implements Shape {

        public static Compound of(final List<Element> elements) {
            final double[] box = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
            for (final Element e : elements) {
                if (e.op() == Op.ADD) {
                    final double[] b = grown(e.shape().box(), e.blend());
                    for (int k = 0; k < 3; k++) {
                        box[k] = Math.min(box[k], b[k]);
                        box[k + 3] = Math.max(box[k + 3], b[k + 3]);
                    }
                }
            }
            for (final Element e : elements) {
                if (e.op() == Op.CLIP && e.shape().box() != EVERYWHERE) {
                    final double[] b = e.shape().box();
                    for (int k = 0; k < 3; k++) {
                        box[k] = Math.max(box[k], b[k]);
                        box[k + 3] = Math.min(box[k + 3], b[k + 3]);
                    }
                }
            }
            return new Compound(List.copyOf(elements), box);
        }

        @Override
        public double at(final double x, final double y, final double z) {
            double d = FAR;
            for (final Element e : elements) {
                d = apply(d, e, e.shape().at(x, y, z));
            }
            return d;
        }
    }

    private static double[] grown(final double[] b, final double by) {
        return new double[] {b[0] - by, b[1] - by, b[2] - by, b[3] + by, b[4] + by, b[5] + by};
    }

    /** {@code d}, what is laid so far, with {@code e} laid on it, {@code s} its own distance. */
    static double apply(final double d, final Element e, final double s) {
        return switch (e.op()) {
            case ADD -> blend(d, s, e.blend());
            case CARVE -> -blend(-d, s, e.blend());
            case CLIP -> -blend(-d, -s, e.blend());
        };
    }

    private final List<Element> elements;
    private final double[] box;

    public Field(final List<Element> elements) {
        this.elements = List.copyOf(elements);
        this.box = Compound.of(this.elements).box();
    }

    public List<Element> elements() {
        return elements;
    }

    /** The box round it, lowest x, y, z then highest. */
    public double[] box() {
        return box.clone();
    }

    /**
     * Which of its shapes can matter anywhere in the box {@code lo} to {@code hi} within {@code reach} of its surface,
     * by their indices, in order: those that come within their blend and {@code reach} of it, and whatever cuts.
     */
    public int[] near(final double[] lo, final double[] hi, final double reach) {
        final int[] found = new int[elements.size()];
        int n = 0;
        for (int k = 0; k < elements.size(); k++) {
            final Element e = elements.get(k);
            if (e.op() == Op.CLIP || gap(e.shape().box(), lo, hi) <= e.blend() + reach) {
                found[n++] = k;
            }
        }
        return Arrays.copyOf(found, n);
    }

    /** How far apart the box {@code b} and the box {@code lo} to {@code hi} are; nothing if they meet. */
    static double gap(final double[] b, final double[] lo, final double[] hi) {
        double s = 0;
        for (int k = 0; k < 3; k++) {
            final double d = Math.max(0, Math.max(b[k] - hi[k], lo[k] - b[k + 3]));
            s += d * d;
        }
        return Math.sqrt(s);
    }

    /** The field at a point, of the shapes {@code only} (indices, in order); far outside if none. */
    public double at(final double x, final double y, final double z, final int[] only) {
        double d = FAR;
        for (final int k : only) {
            final Element e = elements.get(k);
            d = apply(d, e, e.shape().at(x, y, z));
        }
        return d;
    }

    /** Far outside anything. */
    static final double FAR = 1e3;

    /** {@code a} and {@code b} together, blended smoothly within {@code k} of where they meet (a polynomial smooth min). */
    static double blend(final double a, final double b, final double k) {
        if (k <= 0) {
            return Math.min(a, b);
        }
        final double h = Math.max(k - Math.abs(a - b), 0) / k;
        return Math.min(a, b) - h * h * k * 0.25;
    }
}
