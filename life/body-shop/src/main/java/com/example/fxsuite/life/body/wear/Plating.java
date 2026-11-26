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

    /** How far from the surface beneath it is found exactly, cm. */
    private static final double WITHIN = 4.5;
    /** How near the surface a point must be for the surface where it is nearest to say which side it is on, cm. */
    private static final double SURE = 2;
    /** How many of its steps apart a grid is first found on, coarsely, to know where the surface is not near. */
    private static final int COARSE = 4;
    /** How far round toward a limb's line its skin may face, a cosine, before it is taken to be folded over. */
    private static final double BACK = -0.2;
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

    /** {@code cut} shaped over the surface {@code near} finds. */
    public Plate of(final Nearness near, final Cut cut) {
        final Field.Sampled d = Blur.INSTANCE.smoothed(distance(near, cut), REACH, PASSES);
        final List<Field.Element> e = new ArrayList<>(List.of(
                new Field.Element("outside", swollen(d, cut.off()), Field.Op.ADD, 0),
                new Field.Element("inside", swollen(d, cut.off() - cut.thick()), Field.Op.CARVE, 0)));
        e.addAll(cut.edges());
        final Field f = new Field(List.of(
                new Field.Element(cut.kind().title(), Field.Compound.of(e), Field.Op.ADD, 0)));
        return new Plate(cut.kind(), cut.part(), Mesher.of(f, cut.cell()));
    }

    /** {@code shape} swollen all round by {@code by}. */
    private Field.Shape swollen(final Field.Shape shape, final double by) {
        return new Reshaped(shape, V3.ZERO, V3.ZERO, Rot.NONE, new V3(1, 1, 1), by);
    }

    /**
     * How far outside the surface {@code near} finds each point of a grid over {@code cut}'s box is,
     * {@code cut.sample()} apart, behind it less than nothing: as far as it is, within {@link #WITHIN}; beyond, as far
     * as that. Which side it is on: within {@link #SURE} of it, as the surface faces where it is nearest, its
     * corners' ways out blended there; further off, as most of the points further off joined to it say
     * ({@link #sided}), so that where the surface is folded over on itself and faces in, a point far off it is not
     * taken to be inside.
     */
    Field.Sampled distance(final Nearness near, final Cut cut) {
        final Beneath b = near.beneath();
        final float[] out = outward(b);
        final double[] box = cut.box();
        final double h = cut.sample();
        final int[] n = {(int) Math.ceil((box[3] - box[0]) / h) + 1, (int) Math.ceil((box[4] - box[1]) / h) + 1,
            (int) Math.ceil((box[5] - box[2]) / h) + 1};
        final int[] m = {n[0] / COARSE + 1, n[1] / COARSE + 1, n[2] / COARSE + 1};
        final float[] coarse = new float[m[0] * m[1] * m[2]];
        final double reach = WITHIN + 2 * COARSE * h;
        IntStream.range(0, coarse.length).parallel().forEach(at -> coarse[at] = (float) near.nearest(
                point(box, h, at % m[0] * COARSE, at / m[0] % m[1] * COARSE, at / (m[0] * m[1]) * COARSE), reach,
                k -> true).distance());
        final float[] v = new float[n[0] * n[1] * n[2]];
        final float[] far = new float[v.length];
        IntStream.range(0, v.length).parallel().forEach(at -> {
            final int[] c = {at % n[0], at / n[0] % n[1], at / (n[0] * n[1])};
            final int[] q = {coarsely(c[0], m[0]), coarsely(c[1], m[1]), coarsely(c[2], m[2])};
            final double r = h * Math.sqrt(Math.pow(c[0] - q[0] * COARSE, 2) + Math.pow(c[1] - q[1] * COARSE, 2)
                    + Math.pow(c[2] - q[2] * COARSE, 2));
            final double d = coarse[(q[2] * m[1] + q[1]) * m[0] + q[0]];
            final V3 p = point(box, h, c[0], c[1], c[2]);
            final Nearness.Near found = d - r >= WITHIN ? null
                    : near.nearest(p, Math.min(WITHIN, d + r + h), k -> true);
            far[at] = found == null || found.triangle() < 0 ? (float) WITHIN : (float) found.distance();
            v[at] = found == null || found.triangle() < 0 ? 0
                    : (float) (behind(b, out, found, p, cut.core()) ? -found.distance() : found.distance());
        });
        sided(v, far, n);
        return new Field.Sampled(new double[] {box[0], box[1], box[2]}, h, n, v, box);
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
    /** Each of {@code b}'s points' way out, its triangles' summed, each as large as it is: three numbers each. */
    private float[] outward(final Beneath b) {
        final int[] t = b.triangles();
        final float[] out = new float[b.points().length];
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = b.point(t[i]);
            final V3 n = b.point(t[i + 1]).minus(a).cross(b.point(t[i + 2]).minus(a));
            for (int c = 0; c < 3; c++) {
                out[3 * t[i + c]] += (float) n.x();
                out[3 * t[i + c] + 1] += (float) n.y();
                out[3 * t[i + c] + 2] += (float) n.z();
            }
        }
        return out;
    }

    /**
     * Whether {@code p}, nearest {@code m} on {@code b}, is behind it, as its corners' ways out {@code out} blend;
     * where {@code core} says the surface lies round a line and it faces back toward the line, as the way from it.
     */
    private boolean behind(final Beneath b, final float[] out, final Nearness.Near m, final V3 p,
                           final Optional<Core> core) {
        final int[] t = b.triangles();
        final double[] w = {m.a(), m.b(), m.c()};
        V3 at = V3.ZERO;
        V3 way = V3.ZERO;
        for (int c = 0; c < 3; c++) {
            final int i = t[3 * m.triangle() + c];
            at = at.plus(b.point(i).times(w[c]));
            way = way.plus(new V3(out[3 * i], out[3 * i + 1], out[3 * i + 2]).unit().times(w[c]));
        }
        if (core.isPresent() && b.main()[t[3 * m.triangle()]] == core.get().part()) {
            final V3 from = at.minus(core.get().at());
            final V3 off = from.minus(core.get().way().times(from.dot(core.get().way()))).unit();
            way = way.unit().dot(off) < BACK ? off : way;
        }
        return p.minus(at).dot(way) < 0;
    }
}
