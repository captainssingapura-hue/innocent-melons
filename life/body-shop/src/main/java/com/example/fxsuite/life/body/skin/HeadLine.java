package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.wear.Sheet;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * Where the skin of the head ends and the neck's begins, as anatomy draws it, not level: from under the chin along the
 * lower border of the jaw to its angle, up behind the ear to the mastoid's tip, then along the superior nuchal line to
 * the external occipital protuberance behind; the chin and the jaw's edge the head's, the nape up to the occiput the
 * neck's. Each of the skin's triangles of the head or the neck given to the one its middle is on, the line at each way
 * round the head's upright found between its points either side; the skin under the chin and the jaw lying a little
 * below the bone, {@link #UNDER}.
 */
public record HeadLine() implements StatelessFunctionalObject {

    public static final HeadLine INSTANCE = new HeadLine();

    /** How far under the bone of the chin and the jaw the skin there is, cm. */
    private static final double UNDER = 0.6;

    /**
     * The parts of {@code sheet}'s {@code triangles}, {@code patches} as they were, but those of the head or the neck
     * given to the one each is of by the line, the body bound as {@code bind} holds it.
     */
    public Part[] split(final Part[] patches, final Sheet sheet, final int[] triangles, final Rig bind) {
        final Rig.Frame head = bind.frame(Part.HEAD);
        final ToDoubleFunction<V3> above = above();
        final Part[] split = patches.clone();
        for (int t = 0; t < split.length; t++) {
            if (split[t] != Part.HEAD && split[t] != Part.NECK) {
                continue;
            }
            final V3 middle = sheet.point(triangles[3 * t]).plus(sheet.point(triangles[3 * t + 1]))
                    .plus(sheet.point(triangles[3 * t + 2])).times(1.0 / 3);
            final V3 p = head.local(middle);
            split[t] = above.applyAsDouble(p) > 0 ? Part.HEAD : Part.NECK;
        }
        whole(split, triangles, Part.NECK);
        whole(split, triangles, Part.HEAD);
        return split;
    }

    /**
     * How far above the line a point is, cm, the point in the head's frame as the body was bound: up, not square to
     * it; the line found once, for many points.
     */
    public ToDoubleFunction<V3> above() {
        final List<V3> line = line();
        return p -> p.y() - height(line, round(p));
    }

    /**
     * {@code split} made one piece of {@code part}: what of it is apart from its largest piece given to the other of
     * the head and the neck, as a fold of the skin deep into the mouth would be, lying deep enough in the head to be
     * under the line round it.
     */
    private void whole(final Part[] split, final int[] triangles, final Part part) {
        final int[][] next = adjacent(triangles, split.length);
        final int[] piece = new int[split.length];
        Arrays.fill(piece, -1);
        final List<Integer> sizes = new ArrayList<>();
        for (int t = 0; t < split.length; t++) {
            if (split[t] == part && piece[t] < 0) {
                sizes.add(fill(split, next, piece, t, sizes.size()));
            }
        }
        int largest = 0;
        for (int k = 1; k < sizes.size(); k++) {
            largest = sizes.get(k) > sizes.get(largest) ? k : largest;
        }
        for (int t = 0; t < split.length; t++) {
            if (piece[t] >= 0 && piece[t] != largest) {
                split[t] = part == Part.HEAD ? Part.NECK : Part.HEAD;
            }
        }
    }

    /** Each of the {@code n} {@code triangles}' neighbours, those sharing an edge with it. */
    private int[][] adjacent(final int[] triangles, final int n) {
        final Map<Long, Integer> first = new HashMap<>();
        final List<List<Integer>> next = new ArrayList<>();
        for (int t = 0; t < n; t++) {
            next.add(new ArrayList<>());
        }
        for (int t = 0; t < n; t++) {
            for (int e = 0; e < 3; e++) {
                final int a = triangles[3 * t + e];
                final int b = triangles[3 * t + (e + 1) % 3];
                final long key = (long) Math.min(a, b) << 32 | Math.max(a, b);
                final Integer other = first.putIfAbsent(key, t);
                if (other != null) {
                    next.get(t).add(other);
                    next.get(other).add(t);
                }
            }
        }
        return next.stream().map(l -> l.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new);
    }

    /** The triangles of {@code split}'s part joined to {@code from}, marked piece {@code k}: how many. */
    private int fill(final Part[] split, final int[][] next, final int[] piece, final int from, final int k) {
        final ArrayDeque<Integer> open = new ArrayDeque<>(List.of(from));
        piece[from] = k;
        int n = 0;
        while (!open.isEmpty()) {
            final int t = open.pop();
            n++;
            for (final int u : next[t]) {
                if (piece[u] < 0 && split[u] == split[from]) {
                    piece[u] = k;
                    open.push(u);
                }
            }
        }
        return n;
    }

    /**
     * The line's points, front to back, on the right, in the head's frame as the skull is bent: under the chin; under
     * the jaw's angle; the mastoid's tip, about a centimetre and a half under the condyle's top, behind it, as wide as
     * the skull is across the mastoids, a man's ten; the external occipital protuberance, where the trapezius arises.
     */
    private List<V3> line() {
        final SkullForm s = SkullForm.INSTANCE;
        final V3 down = new V3(0, -UNDER, 0);
        return List.of(s.apply(Landmark.GNATHION.drawn()).plus(down), s.apply(Landmark.GONION.drawn()).plus(down),
                s.apply(new V3(5.0, -0.5, -1.6)), s.apply(new V3(0, 3.96, -9.2)));
    }

    /** How high the line is at {@code round}, the way round the head's upright from straight ahead, either side. */
    private double height(final List<V3> line, final double round) {
        for (int i = 1; i < line.size(); i++) {
            final double from = round(line.get(i - 1));
            final double to = round(line.get(i));
            if (round <= to || i == line.size() - 1) {
                final double t = Math.max(0, Math.min(1, (round - from) / (to - from)));
                return line.get(i - 1).y() + (line.get(i).y() - line.get(i - 1).y()) * t;
            }
        }
        return line.getLast().y();
    }

    /** The way round the head's upright {@code p} is from straight ahead, either side alike, 0 to pi. */
    private double round(final V3 p) {
        return Math.atan2(Math.abs(p.x()), p.z());
    }
}
