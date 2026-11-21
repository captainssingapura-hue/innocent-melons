package com.example.fxsuite.life.body.fat;

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
 * it is on and whether it is wrapped round fingers or toes, as a mitten; and each point of it set on that outside, in
 * along its way out.
 */
public record Wrap() implements StatelessFunctionalObject {

    public static final Wrap INSTANCE = new Wrap();

    /**
     * A wrap as it starts: its cage as fitted, the surface smoothed from it, its points and triangles, what of the body
     * each point is on and whether it is a mitten's.
     */
    public record Shape(Cage cage, Subdivision.Level level, V3[] at, int[] triangles, FatTarget.Region[] regions,
                        boolean[] mitten) {
    }

    /** The wrap round {@code target}'s outside, its cage smoothed {@code steps} times. */
    public Shape start(final FatTarget target, final int steps) {
        final FatCage.Built built = FatCage.INSTANCE.of(target);
        final Cage fitted = Fitting.INSTANCE.fitted(built.cage(), 1);
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level start = s.of(fitted, 1);
        final Subdivision.Level shape = s.times(start, steps);
        final Subdivision.Level where = s.times(new Subdivision.Level(channels(built.regions()), start.faces(),
                start.sharp()), steps);
        final Subdivision.Level wraps = s.times(new Subdivision.Level(flags(built.wrapped()), start.faces(),
                start.sharp()), steps);
        final V3[] at = new V3[shape.size()];
        final boolean[] mitten = new boolean[at.length];
        final FatTarget.Region[] regions = new FatTarget.Region[at.length];
        for (int i = 0; i < at.length; i++) {
            at[i] = shape.point(i);
            mitten[i] = wraps.point(i).x() > 0.5;
            final V3 c = where.point(i);
            regions[i] = c.x() >= c.y() && c.x() >= c.z() ? FatTarget.Region.TRUNK
                    : c.y() >= c.z() ? FatTarget.Region.ARM : FatTarget.Region.LEG;
        }
        return new Shape(fitted, shape, at, s.mesh(shape).triangles(), regions, mitten);
    }

    /**
     * Every point of {@code s} set on {@code target}'s outside, in along its way out from {@code back} out, no further
     * than {@code far} from where its smoothed cage put it ({@code smooth}), how it follows the body into {@code w};
     * which found none and stayed, following what is nearest under them. A point of a mitten only ever set out, over
     * what bulges past it, the thumb's root; never in between the fingers or the toes.
     */
    public boolean[] onto(final FatTarget target, final Shape s, final V3[] smooth, final Weights[] w,
                          final double back, final double far) {
        final V3[] at = s.at();
        final V3[] out = Around.INSTANCE.normals(at, s.triangles());
        final boolean[] missed = new boolean[at.length];
        IntStream.range(0, at.length).parallel().forEach(i -> {
            final int side = at[i].x() < 0 ? -1 : 1;
            if (s.mitten()[i]) {
                final V3 from = at[i].plus(out[i].times(back));
                target.onto(from, out[i].times(-1), s.regions()[i], side, back)
                        .filter(h -> h.at().minus(at[i]).dot(out[i]) > 0).ifPresent(h -> at[i] = h.at());
                w[i] = w[i] != null ? w[i] : target.under(at[i]);
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
                w[i] = w[i] != null ? w[i] : target.under(at[i]);
            }
        });
        return missed;
    }

    /** Whether each point is wrapped, as a mitten, as the cage's points are: one number of three. */
    private double[] flags(final boolean[] wrapped) {
        final double[] c = new double[3 * wrapped.length];
        for (int i = 0; i < wrapped.length; i++) {
            c[3 * i] = wrapped[i] ? 1 : 0;
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
