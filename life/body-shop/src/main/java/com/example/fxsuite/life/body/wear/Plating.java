package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Blur;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.taxonomy.Leaf;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Hard pieces shaped over what is beneath them ({@link Plate}): a shell standing off its surface, how far told, as
 * thick as told, its small shapes smoothed away and its large ones kept, so that it follows the chest's and the
 * shoulder's swell and not each rib; as much of it as its edges leave. Shaped over the whole of what is beneath, one
 * closed surface, so that which side of it a point is on is always to be told.
 */
public record Plating() implements StatelessFunctionalObject {

    public static final Plating INSTANCE = new Plating();

    /**
     * How far from the surface beneath a point is found exactly at the least, cm: further than {@link #SURE}; and
     * beyond that, as far as smoothing reaches ({@link #within}).
     */
    private static final double LEAST = 3;
    /** How near the surface a point must be for the surface where it is nearest to say which side it is on, cm. */
    private static final double SURE = 2;
    /** How many of its steps apart a grid is first found on, coarsely, to know where the surface is not near. */
    private static final int COARSE = 4;
    /** How many steps either side each value is averaged over in smoothing it, and how many times. */
    private static final int REACH = 3;
    private static final int PASSES = 3;

    /**
     * A piece to be shaped: what kind it is; the part it moves with; how far out from the surface beneath its outside
     * stands and how thick it is, cm; how finely its shape is found and meshed, cm; the box it lies in; and its edges,
     * the shapes that cut it, each clipping or carving; in the body's frame as bound. For a piece round a limb, the
     * limb's line, which the surface beneath faces away from.
     *
     * @param kind   what it is
     * @param part   what it moves with
     * @param off    how far out its outside stands
     * @param thick  how thick it is
     * @param sample how finely its shape is found
     * @param cell   how finely it is meshed
     * @param box    where it lies: lowest x, y, z, then highest
     * @param edges  what cuts it
     * @param core   the limb's line, if it is round one
     */
    public record Cut(Leaf<?> kind, Part part, double off, double thick, double sample, double cell, double[] box,
                      List<Field.Element> edges, Optional<Core> core) {
    }

    /**
     * A limb's line: through {@code at}, along {@code way} (a unit direction), of {@code part}. Its own skin faces away
     * from it, so that where that skin is folded over on itself and faces back toward it, the way from it is taken for
     * the skin's way; not the skin of a hand or a foot beyond it, which may face back toward it as it is, a thumb's.
     */
    public record Core(V3 at, V3 way, Part part) {
    }

    /**
     * {@code cut} shaped over the surface {@code near} finds and what is worn over it, each of {@code over} finding
     * one: standing off whichever is further out, cloth stretched across a hollow of the skin as much as the skin.
     */
    public Plate of(final Nearness near, final List<Nearness> over, final Cut cut) {
        final List<Nearness> reached = over.stream().filter(o -> reaches(o.beneath(), cut)).toList();
        final Field.Sampled d = Blur.INSTANCE.smoothed(distance(near, reached, cut), REACH, PASSES);
        final List<Field.Element> e = new ArrayList<>(List.of(
                new Field.Element("outside", swollen(d, cut.off()), Field.Op.ADD, 0),
                new Field.Element("inside", swollen(d, cut.off() - cut.thick()), Field.Op.CARVE, 0)));
        e.addAll(cut.edges());
        final Field f = new Field(List.of(
                new Field.Element(cut.kind().title(), Field.Compound.of(e), Field.Op.ADD, 0)));
        return new Plate(cut.kind(), cut.part(), Mesher.of(f, cut.cell()));
    }

    /**
     * How far from the surface beneath {@code cut} needs it found exactly, cm: as far as its outside stands off it and
     * twice as far again as its smoothing spreads, beyond which the smoothing hardly reaches; {@link #LEAST} at least.
     */
    private double within(final Cut cut) {
        final double spread = cut.sample() * Math.sqrt(PASSES * (Math.pow(2 * REACH + 1, 2) - 1) / 12);
        return Math.max(LEAST, cut.off() + 2 * spread);
    }

    /**
     * Whether the box round {@code b} meets {@code cut}'s box, grown by as far as its shape is found ({@link #within}).
     */
    private boolean reaches(final Beneath b, final Cut cut) {
        final double[] box = cut.box();
        final double w = within(cut);
        final float[] p = b.points();
        boolean meets = true;
        for (int a = 0; a < 3; a++) {
            double lo = Double.MAX_VALUE;
            double hi = -Double.MAX_VALUE;
            for (int i = a; i < p.length; i += 3) {
                lo = Math.min(lo, p[i]);
                hi = Math.max(hi, p[i]);
            }
            meets &= hi > box[a] - w && lo < box[a + 3] + w;
        }
        return meets;
    }

    /** {@code shape} swollen all round by {@code by}. */
    private Field.Shape swollen(final Field.Shape shape, final double by) {
        return new Reshaped(shape, V3.ZERO, V3.ZERO, Rot.NONE, new V3(1, 1, 1), by);
    }

    /**
     * How far outside the surface {@code near} finds each point of a grid over {@code cut}'s box is,
     * {@code cut.sample()} apart, behind it less than nothing: as far as it is, within {@link #within}; beyond, as far
     * as that. Which side it is on: within {@link #SURE} of it, as the surface faces where it is nearest, its
     * corners' ways out blended there; further off, as most of the points further off joined to it say
     * ({@link #sided}), so that where the surface is folded over on itself and faces in, a point far off it is not
     * taken to be inside. A point outside it as far as it is from the nearest of what {@code over} finds, if that is
     * nearer: what is worn over the surface lies outside it, and what lies between the two is taken to be within.
     */
    Field.Sampled distance(final Nearness near, final List<Nearness> over, final Cut cut) {
        final Beneath b = near.beneath();
        final float[] out = Facing.INSTANCE.outward(b);
        final List<float[]> outs = over.stream().map(o -> Facing.INSTANCE.outward(o.beneath())).toList();
        final double[] box = cut.box();
        final double h = cut.sample();
        final int[] n = {(int) Math.ceil((box[3] - box[0]) / h) + 1, (int) Math.ceil((box[4] - box[1]) / h) + 1,
            (int) Math.ceil((box[5] - box[2]) / h) + 1};
        final int[] m = {n[0] / COARSE + 1, n[1] / COARSE + 1, n[2] / COARSE + 1};
        final float[] coarse = new float[m[0] * m[1] * m[2]];
        final double within = within(cut);
        final double reach = within + 2 * COARSE * h;
        IntStream.range(0, coarse.length).parallel().forEach(at -> {
            final V3 p = point(box, h, at % m[0] * COARSE, at / m[0] % m[1] * COARSE, at / (m[0] * m[1]) * COARSE);
            coarse[at] = (float) Math.abs(worn(over, outs, p, near.nearest(p, reach, k -> true).distance()));
        });
        final float[] v = new float[n[0] * n[1] * n[2]];
        final float[] far = new float[v.length];
        IntStream.range(0, v.length).parallel().forEach(at -> {
            final int[] c = {at % n[0], at / n[0] % n[1], at / (n[0] * n[1])};
            final int[] q = {coarsely(c[0], m[0]), coarsely(c[1], m[1]), coarsely(c[2], m[2])};
            final double r = h * Math.sqrt(Math.pow(c[0] - q[0] * COARSE, 2) + Math.pow(c[1] - q[1] * COARSE, 2)
                    + Math.pow(c[2] - q[2] * COARSE, 2));
            final double d = coarse[(q[2] * m[1] + q[1]) * m[0] + q[0]];
            final V3 p = point(box, h, c[0], c[1], c[2]);
            final double look = Math.min(within, d + r + h);
            final boolean skip = d - r >= within;
            final Nearness.Near found = skip ? null : near.nearest(p, look, k -> true);
            final boolean met = found != null && found.triangle() >= 0;
            final boolean in = met && Facing.INSTANCE.behind(b, out, found, p, cut.core());
            final double worn = skip || in ? look : worn(over, outs, p, look);
            final double apart = in ? -found.distance() : worn < 0 ? worn : Math.min(met ? found.distance() : look,
                    worn);
            final boolean known = met || worn < look;
            far[at] = known ? (float) Math.abs(apart) : (float) within;
            v[at] = known ? (float) apart : 0;
        });
        sided(v, far, n);
        return new Field.Sampled(new double[] {box[0], box[1], box[2]}, h, n, v, box);
    }

    /**
     * How far {@code p} is outside the nearest of what {@code over} finds, behind it less than nothing, as each faces
     * ({@code outs}); looked for no further than {@code reach}, and that if none is nearer.
     */
    private double worn(final List<Nearness> over, final List<float[]> outs, final V3 p, final double reach) {
        double d = reach;
        for (int i = 0; i < over.size(); i++) {
            final Nearness.Near m = over.get(i).nearest(p, Math.abs(d), k -> true);
            if (m.triangle() >= 0) {
                d = Facing.INSTANCE.behind(over.get(i).beneath(), outs.get(i), m, p, Optional.empty()) ? -m.distance()
                        : m.distance();
            }
        }
        return d;
    }

    /** The coarse grid's step nearest the fine grid's {@code c}, of the {@code m} along it. */
    private int coarsely(final int c, final int m) {
        return Math.min(Math.round((float) c / COARSE), m - 1);
    }

    /** The point of a grid over {@code box}, {@code h} apart, {@code i}, {@code j}, {@code k} along it. */
    private V3 point(final double[] box, final double h, final int i, final int j, final int k) {
        return new V3(box[0] + i * h, box[1] + j * h, box[2] + k * h);
    }
    /**
     * {@code v}'s points further than {@link #SURE} off the surface, {@code far} says, each on the side most of those
     * joined to it, as far off, are on: in a run of such points, side by side along the grid {@code n} across, the
     * points behind the surface counted against those before it, any with none near not counted; before it, if as
     * many.
     */
    private void sided(final float[] v, final float[] far, final int[] n) {
        final int[] run = new int[v.length];
        java.util.Arrays.fill(run, -1);
        final List<Integer> sides = new ArrayList<>();
        final int[] queue = new int[v.length];
        final int[] step = {1, n[0], n[0] * n[1]};
        for (int s = 0; s < v.length; s++) {
            if (run[s] >= 0 || far[s] <= SURE) {
                continue;
            }
            int tail = 0;
            int side = 0;
            queue[tail++] = s;
            run[s] = sides.size();
            for (int head = 0; head < tail; head++) {
                final int i = queue[head];
                side += (int) Math.signum(v[i]);
                final int[] c = {i % n[0], i / n[0] % n[1], i / (n[0] * n[1])};
                for (int axis = 0; axis < 3; axis++) {
                    for (int d = -1; d <= 1; d += 2) {
                        final int j = i + d * step[axis];
                        if (c[axis] + d >= 0 && c[axis] + d < n[axis] && run[j] < 0 && far[j] > SURE) {
                            run[j] = run[s];
                            queue[tail++] = j;
                        }
                    }
                }
            }
            sides.add(side);
        }
        for (int i = 0; i < v.length; i++) {
            v[i] = run[i] < 0 ? v[i] : (sides.get(run[i]) < 0 ? -far[i] : far[i]);
        }
    }
}
