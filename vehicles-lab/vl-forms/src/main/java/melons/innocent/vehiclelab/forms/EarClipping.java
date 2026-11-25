package melons.innocent.vehiclelab.forms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A flat outline with holes in it cut into triangles, as a pressing's face is: each hole first joined to the outline
 * by a cut from its furthest point to the nearest point of the outline it can see, so outline and holes run as one,
 * then a triangle cut off at a time where a corner turns in and holds no other point, until none is left. The
 * outline runs round to the left, the holes to the right, whichever way they were drawn.
 *
 * @param outline the outline, round
 * @param holes   the holes in it, each round, none touching another or the outline
 */
public record EarClipping(List<Spot> outline, List<List<Spot>> holes) {

    public EarClipping {
        outline = List.copyOf(outline);
        holes = holes.stream().map(List::copyOf).toList();
    }

    /** Every spot, the outline's first, then each hole's in turn: what the triangles' numbers count. */
    public List<Spot> spots() {
        final List<Spot> all = new ArrayList<>(left(outline, true));
        holes.forEach(h -> all.addAll(left(h, false)));
        return all;
    }

    /** The triangles, three numbers to each, each running round to the left. */
    public List<Integer> triangles() {
        final List<Spot> all = spots();
        final List<Integer> loop = new ArrayList<>();
        for (int k = 0; k < outline.size(); k++) {
            loop.add(k);
        }
        final List<List<Integer>> rings = new ArrayList<>();
        int from = outline.size();
        for (final List<Spot> h : holes) {
            final List<Integer> ring = new ArrayList<>();
            for (int k = 0; k < h.size(); k++) {
                ring.add(from + k);
            }
            rings.add(ring);
            from += h.size();
        }
        rings.sort(Comparator.comparingDouble((List<Integer> r) -> r.stream()
                .mapToDouble(k -> all.get(k).u()).max().orElseThrow()).reversed());
        for (final List<Integer> ring : rings) {
            joined(all, loop, ring);
        }
        return clipped(all, loop);
    }

    /** {@code loop} drawn round to the left if {@code left}, else to the right. */
    private List<Spot> left(final List<Spot> loop, final boolean left) {
        double area = 0;
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            area += a.u() * b.v() - b.u() * a.v();
        }
        return area > 0 == left ? loop : loop.reversed();
    }

    /** {@code ring} joined into {@code loop} by a cut from its furthest point to the nearest it can see. */
    private void joined(final List<Spot> all, final List<Integer> loop, final List<Integer> ring) {
        int far = 0;
        for (int k = 1; k < ring.size(); k++) {
            if (all.get(ring.get(k)).u() > all.get(ring.get(far)).u()) {
                far = k;
            }
        }
        final Spot m = all.get(ring.get(far));
        int best = -1;
        for (int k = 0; k < loop.size(); k++) {
            final Spot p = all.get(loop.get(k));
            if ((best < 0 || m.distance(p) < m.distance(all.get(loop.get(best)))) && clear(all, loop, m, p)) {
                best = k;
            }
        }
        final List<Integer> spliced = new ArrayList<>();
        for (int k = 0; k <= ring.size(); k++) {
            spliced.add(ring.get((far + k) % ring.size()));
        }
        spliced.add(loop.get(best));
        loop.addAll(best + 1, spliced);
    }

    /** Whether the cut from {@code a} to {@code b} crosses no edge of the outline or any hole. */
    private boolean clear(final List<Spot> all, final List<Integer> loop, final Spot a, final Spot b) {
        for (int k = 0; k < loop.size(); k++) {
            if (crosses(a, b, all.get(loop.get(k)), all.get(loop.get((k + 1) % loop.size())))) {
                return false;
            }
        }
        int from = outline.size();
        for (final List<Spot> h : holes) {
            for (int k = 0; k < h.size(); k++) {
                if (crosses(a, b, all.get(from + k), all.get(from + (k + 1) % h.size()))) {
                    return false;
                }
            }
            from += h.size();
        }
        return true;
    }

    /** Whether {@code a}–{@code b} and {@code c}–{@code d} cross, not merely meet at an end. */
    private boolean crosses(final Spot a, final Spot b, final Spot c, final Spot d) {
        final double e = 1e-9;
        if (a.equals(c) || a.equals(d) || b.equals(c) || b.equals(d)) {
            return false;
        }
        final double d1 = a.turn(b, c);
        final double d2 = a.turn(b, d);
        final double d3 = c.turn(d, a);
        final double d4 = c.turn(d, b);
        return (d1 > e && d2 < -e || d1 < -e && d2 > e) && (d3 > e && d4 < -e || d3 < -e && d4 > e);
    }

    /** {@code loop} cut into triangles, an ear at a time. */
    private List<Integer> clipped(final List<Spot> all, final List<Integer> loop) {
        final List<Integer> left = new ArrayList<>(loop);
        final List<Integer> made = new ArrayList<>();
        int stuck = 0;
        int k = 0;
        while (left.size() > 3 && stuck <= left.size()) {
            final int n = left.size();
            final int a = left.get((k + n - 1) % n);
            final int b = left.get(k % n);
            final int c = left.get((k + 1) % n);
            if (ear(all, left, a, b, c)) {
                made.addAll(List.of(a, b, c));
                left.remove(k % n);
                stuck = 0;
            } else {
                k++;
                stuck++;
            }
            k = k % left.size();
        }
        if (left.size() == 3) {
            made.addAll(left);
        }
        return made;
    }

    /** Whether the corner {@code a}, {@code b}, {@code c} turns in and holds no other spot of {@code left}. */
    private boolean ear(final List<Spot> all, final List<Integer> left, final int a, final int b, final int c) {
        final Spot pa = all.get(a);
        final Spot pb = all.get(b);
        final Spot pc = all.get(c);
        if (pa.turn(pb, pc) <= 1e-12) {
            return false;
        }
        for (final int k : left) {
            final Spot p = all.get(k);
            if (!p.equals(pa) && !p.equals(pb) && !p.equals(pc) && pa.turn(pb, p) >= 0 && pb.turn(pc, p) >= 0
                    && pc.turn(pa, p) >= 0) {
                return false;
            }
        }
        return true;
    }
}
