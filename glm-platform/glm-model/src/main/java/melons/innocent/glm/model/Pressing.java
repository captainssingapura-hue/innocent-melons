package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.EarClipping;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A panel pressed from sheet steel: its outline and the openings cut in it drawn flat, along {@code u} and up
 * {@code v}, then laid on the curved face the press gives it, {@code laid} saying where each spot of the drawing comes
 * to. Its outer face that face, facing {@code outward}'s way; its inner face a sheet's thickness in; its outline's edge
 * turned in as deep as {@code rim}, as a door's skin is folded over its frame; round each opening its edge turned in, a
 * flange as deep as {@code flange}, as a body side's is round its door openings. Its faces cut finer than the
 * drawing's own corners, at every {@code along} along and every {@code up} up, so they follow the curve.
 *
 * @param outline its outline, drawn
 * @param holes   the openings cut in it, drawn
 * @param laid    where each spot of the drawing comes to
 * @param outward roughly the way its outer face faces
 * @param thick   how thick the sheet is
 * @param rim     how deep its outline's edge is turned in, no deeper than the sheet is thick for a bare edge
 * @param flange  how deep the openings' edges are turned in
 * @param along   how finely its faces are cut along
 * @param up      how finely its faces are cut up
 */
record Pressing(List<Spot> outline, List<List<Spot>> holes, Laid laid, Vec3 outward, double thick, double rim,
                double flange, double along, double up) {

    /** Where each spot of a drawing comes to, laid on a curved face. */
    interface Laid {
        /** Where {@code spot} comes to. */
        Vec3 at(Spot spot);
    }

    private static final double NUDGE = 1e-3;

    Pressing {
        outline = List.copyOf(outline);
        holes = holes.stream().map(List::copyOf).toList();
    }

    /** It. */
    Mesh mesh() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final EarClipping cut = new EarClipping(outline, holes);
        final List<Spot> spots = cut.spots();
        final List<Integer> triangles = cut.triangles();
        for (int k = 0; k + 2 < triangles.size(); k += 3) {
            final List<Spot> triangle = List.of(spots.get(triangles.get(k)), spots.get(triangles.get(k + 1)),
                    spots.get(triangles.get(k + 2)));
            new Slicing(along, up).sliced(triangle).forEach(piece -> faced(forming, piece));
        }
        int from = 0;
        final List<Integer> sizes = new ArrayList<>();
        sizes.add(outline.size());
        holes.forEach(h -> sizes.add(h.size()));
        for (int k = 0; k < sizes.size(); k++) {
            edged(forming, spots.subList(from, from + sizes.get(k)), k == 0 ? rim : flange);
            from += sizes.get(k);
        }
        return forming.formed();
    }

    /** A piece of its faces, a flat drawing's small convex patch laid on the curve, outer and inner. */
    private void faced(final Forming forming, final List<Spot> piece) {
        for (final int side : List.of(1, -1)) {
            final int[] at = new int[piece.size()];
            for (int k = 0; k < piece.size(); k++) {
                final Vec3 n = facing(piece.get(k));
                final Vec3 where = laid.at(piece.get(k));
                at[k] = forming.point(side > 0 ? where : where.minus(n.times(thick)), n.times(side));
            }
            for (int k = 1; k + 1 < at.length; k++) {
                forming.faced(at[0], at[k], at[k + 1]);
            }
        }
    }

    /**
     * The edge round a loop drawn with the sheet on its left, turned in {@code deep}: a strip square to the faces,
     * facing out of the sheet, and, where it is deeper than the sheet is thick, its back a sheet's thickness behind.
     */
    private void edged(final Forming forming, final List<Spot> loop, final double deep) {
        final List<Spot> fine = finer(loop);
        for (int k = 0; k < fine.size(); k++) {
            final Spot a = fine.get(k);
            final Spot b = fine.get((k + 1) % fine.size());
            final Spot way = a.to(b);
            final double length = Math.hypot(way.u(), way.v());
            if (length < 1e-9) {
                continue;
            }
            final Spot middle = new Spot((a.u() + b.u()) / 2, (a.v() + b.v()) / 2);
            final Spot off = new Spot(way.v() / length * NUDGE, -way.u() / length * NUDGE);
            final Vec3 n = facing(middle);
            final Vec3 raw = laid.at(new Spot(middle.u() + off.u(), middle.v() + off.v())).minus(laid.at(middle));
            final Vec3 out = raw.minus(n.times(raw.dot(n))).unit();
            strip(forming, a, b, deep, out, new Vec3(0, 0, 0));
            if (deep > thick * 1.5) {
                strip(forming, a, b, deep, out.times(-1), out.times(-thick));
            }
        }
    }

    /** One strip of an edge, from {@code a} to {@code b}, {@code deep} in from the outer face, facing {@code out}. */
    private void strip(final Forming forming, final Spot a, final Spot b, final double deep, final Vec3 out,
                       final Vec3 shift) {
        final Vec3 pa = laid.at(a).plus(shift);
        final Vec3 pb = laid.at(b).plus(shift);
        final int p = forming.point(pa, out);
        final int q = forming.point(pb, out);
        final int r = forming.point(pb.minus(facing(b).times(deep)), out);
        final int s = forming.point(pa.minus(facing(a).times(deep)), out);
        forming.faced(p, q, r);
        forming.faced(p, r, s);
    }

    /** {@code loop} with spots put in along its sides, none further apart than its faces are cut. */
    private List<Spot> finer(final List<Spot> loop) {
        final double step = Math.min(along, up);
        final List<Spot> out = new ArrayList<>();
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            final int n = Math.max(1, (int) Math.ceil(a.distance(b) / step));
            for (int i = 0; i < n; i++) {
                out.add(new Spot(a.u() + (b.u() - a.u()) * i / n, a.v() + (b.v() - a.v()) * i / n));
            }
        }
        return out;
    }

    /** The way its outer face faces at {@code spot}. */
    private Vec3 facing(final Spot spot) {
        final Vec3 du = laid.at(new Spot(spot.u() + NUDGE, spot.v())).minus(laid.at(new Spot(spot.u() - NUDGE,
                spot.v())));
        final Vec3 dv = laid.at(new Spot(spot.u(), spot.v() + NUDGE)).minus(laid.at(new Spot(spot.u(),
                spot.v() - NUDGE)));
        final Vec3 n = du.cross(dv).unit();
        return n.dot(outward) < 0 ? n.times(-1) : n;
    }
}
