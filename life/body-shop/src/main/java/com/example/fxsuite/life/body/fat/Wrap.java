package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.cage.Fitting;
import com.example.fxsuite.life.body.cage.Subdivision;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A surface wrapped round a body, every body's the same points and faces, the fat's or the skin's: its cage found on
 * the outside asked for ({@link FatCage}, {@link FatTarget}), moved so that its smoothed surface passes through its
 * points ({@link Fitting}), smoothed as many times as asked ({@link Subdivision}), each point knowing what of the body
 * it is on and how far it is held as the cage makes it, a hand's or a foot's wholly; and each point of it set on
 * that outside, in along its way out, as far as it is free to be.
 */
public record Wrap() implements StatelessFunctionalObject {

    public static final Wrap INSTANCE = new Wrap();

    /** How far held a point is that is held wholly, near enough. */
    public static final double HELD = 0.99;

    /**
     * A wrap as it starts: its cage as fitted, the surface smoothed from it, its points and triangles, what of the body
     * each point is on, how far it is held as the cage makes it, from none to wholly, a hand's or a foot's wholly;
     * and the part each face of its surface is of, as the cage's face it comes of is.
     */
    public record Shape(Cage cage, Subdivision.Level level, V3[] at, int[] triangles, FatTarget.Region[] regions,
                        double[] held, Part[] parts) {
    }

    /** The wrap round {@code target}'s outside, its cage smoothed {@code steps} times. */
    public Shape start(final FatTarget target, final int steps) {
        final FatCage.Built built = FatCage.INSTANCE.of(target);
        final Cage fitted = Fitting.INSTANCE.fitted(built.cage(), 1);
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level start = s.of(fitted, 1);
        Subdivision.Level shape = start;
        Part[] parts = built.parts();
        for (int k = 0; k < steps; k++) {
            parts = carried(parts, s.parents(shape));
            shape = s.once(shape);
        }
        final Subdivision.Level where = s.times(new Subdivision.Level(channels(built.regions()), start.faces(),
                start.sharp()), steps);
        final Subdivision.Level wraps = s.times(new Subdivision.Level(flags(built.held()), start.faces(),
                start.sharp()), steps);
        final V3[] at = new V3[shape.size()];
        final double[] held = new double[at.length];
        final FatTarget.Region[] regions = new FatTarget.Region[at.length];
        for (int i = 0; i < at.length; i++) {
            at[i] = shape.point(i);
            held[i] = Math.clamp(wraps.point(i).x(), 0, 1);
            final V3 c = where.point(i);
            regions[i] = c.x() >= c.y() && c.x() >= c.z() ? FatTarget.Region.TRUNK
                    : c.y() >= c.z() ? FatTarget.Region.ARM : FatTarget.Region.LEG;
        }
        return new Shape(fitted, shape, at, s.mesh(shape).triangles(), regions, held, parts);
    }

    /**
     * Every point of {@code s} set on {@code target}'s outside, in along its way out from {@code back} out, no further
     * than {@code far} from where its smoothed cage put it ({@code smooth}), how it follows the body into {@code w};
     * which found none and stayed, following what is nearest under them. A point held set only as far as it is free,
     * one held wholly, a hand's or a foot's, left as it is.
     */
    public boolean[] onto(final FatTarget target, final Shape s, final V3[] smooth, final Weights[] w,
                          final double back, final double far) {
        final V3[] at = s.at();
        final V3[] out = Around.INSTANCE.normals(at, s.triangles());
        final boolean[] missed = new boolean[at.length];
        IntStream.range(0, at.length).parallel().forEach(i -> {
            final int side = at[i].x() < 0 ? -1 : 1;
            if (s.held()[i] > 0) {
                w[i] = under(target, at[i], w[i]);
                return;
            }
            Optional<FatTarget.Hit> hit = Optional.empty();
            // from further out each time the line starts inside the body, met as soon as it starts
            for (double b = back; b <= 4 * back && hit.isEmpty(); b *= 2) {
                final V3 from = at[i].plus(out[i].times(b));
                hit = target.onto(from, out[i].times(-1), s.regions()[i], side, 2 * b)
                        .filter(h -> h.at().minus(from).length() > 1e-6);
            }
            hit = hit.filter(h -> h.at().minus(smooth[i]).length() <= far);
            if (hit.isPresent()) {
                at[i] = hit.get().at();
                w[i] = hit.get().weights();
            } else {
                missed[i] = true;
                w[i] = under(target, at[i], w[i]);
            }
        });
        return missed;
    }

    /** How {@code at} follows the body: as {@code was}, if it is known, else as what is nearest under it. */
    private Weights under(final FatTarget target, final V3 at, final Weights was) {
        return was != null ? was : target.under(at);
    }

    /** The part each face of a step is of, {@code parts} the step before's, each of the face {@code from} says. */
    public Part[] carried(final Part[] parts, final int[] from) {
        final Part[] next = new Part[from.length];
        for (int i = 0; i < next.length; i++) {
            next[i] = parts[from[i]];
        }
        return next;
    }

    /** How far each point is held, as the cage's points are: one number of three. */
    private double[] flags(final double[] held) {
        final double[] c = new double[3 * held.length];
        for (int i = 0; i < held.length; i++) {
            c[3 * i] = held[i];
        }
        return c;
    }

    /** What each point is on, one number each for the trunk, an arm and a leg, as the cage's points are. */
    private double[] channels(final FatTarget.Region[] regions) {
        final double[] c = new double[3 * regions.length];
        for (int i = 0; i < regions.length; i++) {
            c[3 * i + regions[i].ordinal()] = 1;
        }
        return c;
    }
}
