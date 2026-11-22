package com.example.fxsuite.math.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Curves: the kinds of curve, each a way of giving one value for each x over a span, the same every time. A kind is
 * told by how its value is worked out from what it is given: by a polynomial, along a conic, by a transcendental
 * function, piece by piece between points, or by curves of other kinds joined end to end. Whether it is
 * {@link Continuous}, {@link Smooth} or {@link Interpolating} is said across the tree. A closed curve, a circle, is
 * two of them over one parameter. A curve is nobody's making and of no era; nothing in the world is one, but follows
 * one: a bridge's deck a parabola, a roof a fold.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Curves.md}.
 */
public final class Curves {

    private Curves() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = new Taxonomy("curves", new Curve(), List.of(new Constant(), new Linear(),
            new Parabolic(), new Cubic(), new CircularArc(), new EllipticArc(), new Sinusoidal(), new Catenary(),
            new Clothoid(), new Stepped(), new Folded(), new MonotoneCubic(), new NaturalCubic(), new Chained()),
            Meanings.INSTANCE.beside(Curves.class));

    /** The root: a curve, one value for each x over its span. */
    public record Curve() implements L0 {
    }

    // Polynomial: worked out as a sum of powers of x
    public record Polynomial() implements L1<Curve>, Smooth { public Curve parent() { return new Curve(); } }
    public record Constant() implements Leaf<Polynomial> { public Polynomial parent() { return new Polynomial(); } }
    public record Linear() implements Leaf<Polynomial> { public Polynomial parent() { return new Polynomial(); } }
    public record Parabolic() implements Leaf<Polynomial> { public Polynomial parent() { return new Polynomial(); } }
    public record Cubic() implements Leaf<Polynomial> { public Polynomial parent() { return new Polynomial(); } }

    // Conic: along a cut through a cone, as far as it gives one value for each x
    public record Conic() implements L1<Curve>, Smooth { public Curve parent() { return new Curve(); } }
    public record CircularArc() implements Leaf<Conic> { public Conic parent() { return new Conic(); } }
    public record EllipticArc() implements Leaf<Conic> { public Conic parent() { return new Conic(); } }

    // Transcendental: worked out by a function no polynomial is
    public record Transcendental() implements L1<Curve>, Smooth { public Curve parent() { return new Curve(); } }
    public record Sinusoidal() implements Leaf<Transcendental> {
        public Transcendental parent() { return new Transcendental(); }
    }
    public record Catenary() implements Leaf<Transcendental> {
        public Transcendental parent() { return new Transcendental(); }
    }
    public record Clothoid() implements Leaf<Transcendental> {
        public Transcendental parent() { return new Transcendental(); }
    }

    // Piecewise: through points given in order, a piece between each two
    public record Piecewise() implements L1<Curve>, Interpolating { public Curve parent() { return new Curve(); } }
    public record Stepped() implements Leaf<Piecewise> { public Piecewise parent() { return new Piecewise(); } }
    public record Folded() implements Leaf<Piecewise>, Continuous {
        public Piecewise parent() { return new Piecewise(); }
    }
    public record Spline() implements L2<Piecewise>, Smooth { public Piecewise parent() { return new Piecewise(); } }
    public record MonotoneCubic() implements Leaf<Spline> { public Spline parent() { return new Spline(); } }
    public record NaturalCubic() implements Leaf<Spline> { public Spline parent() { return new Spline(); } }

    // Composite: curves of other kinds made into one
    public record Composite() implements L1<Curve> { public Curve parent() { return new Curve(); } }
    public record Chained() implements Leaf<Composite> { public Composite parent() { return new Composite(); } }
}
