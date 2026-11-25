package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A flat plate cut from sheet, as a bracket's cheek is or a pressing's web: its outline and the holes punched in it
 * drawn flat, then set where it is, the drawing's along {@code u} and its up {@code v} from {@code origin}; as thick as
 * the sheet, half each side of the drawing. Its two faces flat, its edges and the holes' sides square to them.
 *
 * @param outline its outline, drawn
 * @param holes   the holes punched in it, drawn
 * @param origin  where the drawing's nought is
 * @param u       the way the drawing's along runs
 * @param v       the way the drawing's up runs, square to {@code u}
 * @param thick   how thick the sheet is
 */
public record Plate(List<Spot> outline, List<List<Spot>> holes, Vec3 origin, Vec3 u, Vec3 v, double thick) {

    public Plate {
        outline = List.copyOf(outline);
        holes = holes.stream().map(List::copyOf).toList();
        if (outline.size() < 3 || thick <= 0) {
            throw new IllegalArgumentException("a plate has an outline and is some thickness");
        }
        u = u.unit();
        v = v.unit();
    }

    /** It. */
    public Mesh mesh() {
        final Forming forming = new Forming(origin);
        final EarClipping cut = new EarClipping(outline, holes);
        final List<Spot> spots = cut.spots();
        final List<Integer> triangles = cut.triangles();
        final Vec3 face = u.cross(v).unit();
        for (final int side : List.of(1, -1)) {
            final Vec3 out = face.times(side);
            final int[] at = new int[spots.size()];
            for (int k = 0; k < spots.size(); k++) {
                at[k] = forming.point(where(spots.get(k), side), out);
            }
            for (int k = 0; k + 2 < triangles.size(); k += 3) {
                forming.faced(at[triangles.get(k)], at[triangles.get(k + 1)], at[triangles.get(k + 2)]);
            }
        }
        int from = 0;
        final List<Integer> sizes = new ArrayList<>();
        sizes.add(outline.size());
        holes.forEach(h -> sizes.add(h.size()));
        for (final int size : sizes) {
            edged(forming, spots.subList(from, from + size));
            from += size;
        }
        return forming.formed();
    }

    /** The side of a loop drawn round to the left, or to the right for a hole: square to the faces, facing out. */
    private void edged(final Forming forming, final List<Spot> loop) {
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            final Spot way = a.to(b);
            final Vec3 out = u.times(way.v()).minus(v.times(way.u()));
            final int p = forming.point(where(a, 1), out);
            final int q = forming.point(where(b, 1), out);
            final int r = forming.point(where(b, -1), out);
            final int s = forming.point(where(a, -1), out);
            forming.faced(p, q, r);
            forming.faced(p, r, s);
        }
    }

    /** Where {@code spot} is, on the face toward {@code side}. */
    private Vec3 where(final Spot spot, final int side) {
        return origin.plus(u.times(spot.u())).plus(v.times(spot.v())).plus(u.cross(v).unit().times(side * thick / 2));
    }
}
