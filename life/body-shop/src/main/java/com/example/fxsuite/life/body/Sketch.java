package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A shape drawn shape by shape ({@link Field}), as a bone is: balls, eggs, rods, plates, discs and rings, each laid on
 * what is drawn before it, blended into it as far as is set, or carving it or cutting it down. Drawn in a frame of its
 * own: from points given in the body's frame, a part's own, made so by taking away where the part is; and as on the
 * right side, or mirrored to the left.
 */
public final class Sketch {

    private final List<Field.Element> laid = new ArrayList<>();
    private final V3 origin;
    private final double side;
    private double blend = 0.3;
    private Field.Op next = Field.Op.ADD;

    /** Drawn as given, on the right side. */
    public Sketch() {
        this(V3.ZERO, 1);
    }

    /** Drawn as given less {@code origin}, mirrored to the left if {@code side} is less than nothing. */
    public Sketch(final V3 origin, final double side) {
        this.origin = origin;
        this.side = side;
    }

    public static V3 v(final double x, final double y, final double z) {
        return new V3(x, y, z);
    }

    /** {@code p} as it is drawn: from its origin, on its side. */
    private V3 at(final V3 p) {
        return new V3(side * (p.x() - origin.x()), p.y() - origin.y(), p.z() - origin.z());
    }

    /** {@code w}, a way, as it is drawn: on its side. */
    private V3 way(final V3 w) {
        return new V3(side * w.x(), w.y(), w.z());
    }

    /** What follows blended {@code k} into what is before it. */
    public Sketch blend(final double k) {
        blend = k;
        return this;
    }

    /** The next shape carved out of what is before it. */
    public Sketch carve() {
        next = Field.Op.CARVE;
        return this;
    }

    /** What is before the next shape cut down to it. */
    public Sketch clip() {
        next = Field.Op.CLIP;
        return this;
    }

    /** {@code shape}, as drawn already, laid. */
    public Sketch shape(final String name, final Field.Shape shape) {
        laid.add(new Field.Element(name, shape, next, blend));
        next = Field.Op.ADD;
        return this;
    }

    public Sketch ball(final V3 c, final double r) {
        return egg(c, r, r, r);
    }

    /** An egg about {@code c}, so far each way. */
    public Sketch egg(final V3 c, final double rx, final double ry, final double rz) {
        return shape("egg", Field.Ellipsoid.of(at(c), way(new V3(rx, 0, 0)), new V3(0, ry, 0), new V3(0, 0, rz)));
    }

    /**
     * An egg about {@code c}, so far each way, turned {@code ax} about its x, then {@code ay} about y, then {@code az}
     * about z, degrees.
     */
    public Sketch egg(final V3 c, final double rx, final double ry, final double rz, final double ax, final double ay,
               final double az) {
        final Rot r = Rot.aboutZ(az).then(Rot.aboutY(ay)).then(Rot.aboutX(ax));
        return shape("egg", Field.Ellipsoid.of(at(c), way(r.apply(new V3(rx, 0, 0))), way(r.apply(new V3(0, ry, 0))),
                way(r.apply(new V3(0, 0, rz)))));
    }

    /**
     * An egg about {@code c} lying along {@code along}, {@code ra} each way along it, {@code rb} toward {@code facing}
     * (square to it) and {@code rc} square to both.
     */
    public Sketch egg(final V3 c, final V3 along, final V3 facing, final double ra, final double rb, final double rc) {
        final V3 u = along.unit();
        V3 f = facing.minus(u.times(facing.dot(u)));
        if (f.length() < 1e-9) {
            f = u.cross(Math.abs(u.z()) < 0.9 ? V3.Z : V3.X);
        }
        f = f.unit();
        final V3 g = u.cross(f).unit();
        return shape("egg", Field.Ellipsoid.of(at(c), way(u.times(ra)), way(f.times(rb)), way(g.times(rc))));
    }

    /** A rod from {@code a}, {@code r1} round, to {@code b}, {@code r2} round, its ends rounded. */
    public Sketch rod(final V3 a, final V3 b, final double r1, final double r2) {
        return shape("rod", Field.Cone.of(at(a), at(b), r1, r2));
    }

    /** A rod as {@link #rod}, but {@code ratio} as thick along {@code way}: flattened. */
    public Sketch flat(final V3 a, final V3 b, final double r1, final double r2, final V3 way, final double ratio) {
        final V3 mid = at(a.plus(b).times(0.5));
        return shape("flat rod", Field.Squash.of(Field.Cone.of(at(a), at(b), r1, r2), mid, way(way), ratio));
    }

    /** Rods through {@code points} in turn, from {@code r1} round at the first to {@code r2} at the last. */
    public Sketch tube(final double r1, final double r2, final V3... points) {
        for (int k = 0; k + 1 < points.length; k++) {
            final double t0 = (double) k / (points.length - 1);
            final double t1 = (double) (k + 1) / (points.length - 1);
            rod(points[k], points[k + 1], r1 + (r2 - r1) * t0, r1 + (r2 - r1) * t1);
        }
        return this;
    }

    /** A blade of bone, the triangle {@code a b c}, {@code thick} through. */
    public Sketch plate(final V3 a, final V3 b, final V3 c, final double thick) {
        return shape("plate", Field.Plate.of(at(a), at(b), at(c), thick));
    }

    /** A blade of bone, the four-sided {@code a b c d}, {@code thick} through. */
    public Sketch plate(final V3 a, final V3 b, final V3 c, final V3 d, final double thick) {
        plate(a, b, c, thick);
        return plate(a, c, d, thick);
    }

    /** A disc about {@code c} along {@code axis}, {@code r} round, {@code half} thick each way, its edges rounded. */
    public Sketch disc(final V3 c, final V3 axis, final double r, final double half, final double round) {
        return shape("disc", Field.Cylinder.of(at(c), way(axis), r, half, round));
    }

    /** A disc as {@link #disc}, {@code ratio} as deep along {@code flat}: an oval one. */
    public Sketch disc(final V3 c, final V3 axis, final double r, final double half, final double round, final V3 flat,
                final double ratio) {
        return shape("oval disc", Field.Squash.of(Field.Cylinder.of(at(c), way(axis), r, half, round), at(c), way(flat),
                ratio));
    }

    /** A ring about {@code c}, round {@code axis}, {@code big} to its tube's middle, its tube {@code small} round. */
    public Sketch ring(final V3 c, final V3 axis, final double big, final double small) {
        return shape("ring", Field.Torus.of(at(c), way(axis), big, small));
    }

    /** Everything on the far side of the plane through {@code at} from {@code out}. */
    public Sketch plane(final V3 at, final V3 out) {
        return shape("plane", Field.Plane.of(at(at), way(out)));
    }

    /**
     * The side of the plane through {@code at} away from {@code out}, as drawn, set in {@code inset} from it: not laid,
     * but to cut by, as a bone is cut from its neighbour along a suture.
     */
    public Field.Shape half(final V3 at, final V3 out, final double inset) {
        final V3 u = way(out).unit();
        return Field.Plane.of(at(at).minus(u.times(inset)), u);
    }

    /**
     * A curved blade of bone {@code thick} through, spanned between two edges, each a smooth line through its points
     * ({@link #smooth}): {@code across} by {@code down} plates from one edge to the other, bowed {@code depth} toward
     * {@code bow} in its middle.
     */
    public Sketch sheet(final V3[] one, final V3[] other, final int across, final int down, final double thick,
                 final V3 bow, final double depth) {
        final java.util.List<V3> a = smooth(java.util.List.of(one), across + 1);
        final java.util.List<V3> b = smooth(java.util.List.of(other), across + 1);
        final V3[][] grid = new V3[across + 1][down + 1];
        for (int i = 0; i <= across; i++) {
            for (int j = 0; j <= down; j++) {
                final double u = (double) i / across;
                final double w = (double) j / down;
                final double bulge = depth * Math.sin(Math.PI * u) * Math.sin(Math.PI * w);
                grid[i][j] = a.get(i).times(1 - w).plus(b.get(i).times(w)).plus(bow.times(bulge));
            }
        }
        // the plates joined without blending, which would ridge them where they meet; the sheet blended as one
        final Sketch inner = fresh();
        inner.blend(0);
        for (int i = 0; i < across; i++) {
            for (int j = 0; j < down; j++) {
                inner.plate(grid[i][j], grid[i + 1][j], grid[i + 1][j + 1], grid[i][j + 1], thick);
            }
        }
        return shape("sheet", inner.done());
    }

    /** {@code keys} joined smoothly (Catmull-Rom), {@code n} points in all, from the first to the last. */
    public static java.util.List<V3> smooth(final java.util.List<V3> keys, final int n) {
        final java.util.List<V3> out = new java.util.ArrayList<>();
        final int segments = keys.size() - 1;
        for (int k = 0; k < n; k++) {
            final double u = (double) k / (n - 1) * segments;
            final int j = Math.min((int) u, segments - 1);
            final double t = u - j;
            final V3 p0 = keys.get(Math.max(j - 1, 0));
            final V3 p1 = keys.get(j);
            final V3 p2 = keys.get(j + 1);
            final V3 p3 = keys.get(Math.min(j + 2, keys.size() - 1));
            final double t2 = t * t;
            final double t3 = t2 * t;
            out.add(p1.times(2).plus(p2.minus(p0).times(t)).plus(p0.times(2).minus(p1.times(5)).plus(p2.times(4))
                    .minus(p3).times(t2)).plus(p3.minus(p0).plus(p1.times(3)).minus(p2.times(3)).times(t3)).times(0.5));
        }
        return out;
    }

    /** What {@code inner} draws, laid as one shape. */
    public Sketch group(final Sketch inner) {
        return shape("group", inner.done());
    }

    /** A sketch drawn in the same frame, on the same side, to be laid as one shape ({@link #group}). */
    public Sketch fresh() {
        return new Sketch(origin, side);
    }

    /** What is drawn, as one shape. */
    public Field.Compound done() {
        return Field.Compound.of(laid);
    }
}
