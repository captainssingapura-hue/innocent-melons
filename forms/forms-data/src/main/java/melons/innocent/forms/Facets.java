package melons.innocent.forms;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Solids as the faces a renderer draws, whichever renderer: each solid's six faces, each two triangles wound to face
 * out of it, its corners its own so its edges stay sharp, every point facing as its face does; gathered a sheet to a
 * material, so a whole building is a handful of sheets. Solids no bigger every way than {@code finest} are left out, so
 * a building may be drawn at a coarser fineness than it is built. Each triangle remembers the solid it came from, by
 * its place in the list given. In centimetres, x across, y up, z along: a solid's across, up and along.
 */
public final class Facets {

    /** Each face's four corners, by their numbers in a solid: bit 0 across, bit 1 along, bit 2 up. */
    private static final int[][] FACES = {{0, 1, 3, 2}, {4, 5, 7, 6}, {0, 2, 6, 4}, {1, 3, 7, 5}, {0, 1, 5, 4},
        {2, 3, 7, 6}};

    private final Map<Material, Sheet> sheets = new EnumMap<>(Material.class);

    /**
     * A material's sheet of triangles.
     *
     * @param points  each point, x, y and z in turn
     * @param facings which way each point faces, a unit long, x, y and z in turn
     * @param corners each triangle's three points, by number, in turn
     * @param solids  the solid each triangle came from, by its place in the list given
     */
    public record Sheet(double[] points, double[] facings, int[] corners, int[] solids) {

        /** How many triangles it has. */
        public int triangles() {
            return corners.length / 3;
        }
    }

    /** {@code solids}' faces, but for those no bigger every way than {@code finest} centimetres. */
    public Facets(final List<Solid> solids, final double finest) {
        final Map<Material, List<double[]>> points = new EnumMap<>(Material.class);
        final Map<Material, List<Integer>> of = new EnumMap<>(Material.class);
        for (int s = 0; s < solids.size(); s++) {
            final Solid solid = solids.get(s);
            if (size(solid) > finest) {
                faced(solid, points.computeIfAbsent(solid.material(), m -> new ArrayList<>()));
                final List<Integer> from = of.computeIfAbsent(solid.material(), m -> new ArrayList<>());
                for (int t = 0; t < 2 * FACES.length; t++) {
                    from.add(s);
                }
            }
        }
        points.forEach((material, faces) -> sheets.put(material, sheet(faces, of.get(material))));
    }

    /** Its sheets, by material. */
    public Map<Material, Sheet> sheets() {
        return sheets;
    }

    /** How many triangles it has, all told. */
    public int triangles() {
        return sheets.values().stream().mapToInt(Sheet::triangles).sum();
    }

    /** How big {@code solid} is, its longest way across its corners. */
    private double size(final Solid solid) {
        double most = 0;
        for (final Point a : solid.corners()) {
            for (final Point b : solid.corners()) {
                most = Math.max(most, a.plus(b.times(-1)).length());
            }
        }
        return most;
    }

    /** {@code solid}'s faces added to {@code faces}: each its four corners and its facing, out of the solid. */
    private void faced(final Solid solid, final List<double[]> faces) {
        final double[] middle = new double[3];
        for (final Point p : solid.corners()) {
            middle[0] += p.across() / 8;
            middle[1] += p.up() / 8;
            middle[2] += p.along() / 8;
        }
        for (final int[] face : FACES) {
            final double[][] at = new double[4][];
            for (int k = 0; k < 4; k++) {
                final Point p = solid.corners().get(face[k]);
                at[k] = new double[] {p.across(), p.up(), p.along()};
            }
            final double[] facing = cross(minus(at[1], at[0]), minus(at[3], at[0]));
            final double[] out = minus(centre(at), middle);
            final boolean turned = facing[0] * out[0] + facing[1] * out[1] + facing[2] * out[2] < 0;
            final double[] unit = unit(turned ? new double[] {-facing[0], -facing[1], -facing[2]} : facing);
            faces.add(turned ? new double[] {at[0][0], at[0][1], at[0][2], at[3][0], at[3][1], at[3][2], at[2][0],
                at[2][1], at[2][2], at[1][0], at[1][1], at[1][2], unit[0], unit[1], unit[2]}
                : new double[] {at[0][0], at[0][1], at[0][2], at[1][0], at[1][1], at[1][2], at[2][0], at[2][1],
                    at[2][2], at[3][0], at[3][1], at[3][2], unit[0], unit[1], unit[2]});
        }
    }

    /** The sheet of {@code faces}, each two triangles, from the solids {@code of} says. */
    private Sheet sheet(final List<double[]> faces, final List<Integer> of) {
        final double[] points = new double[faces.size() * 12];
        final double[] facings = new double[faces.size() * 12];
        final int[] corners = new int[faces.size() * 6];
        for (int f = 0; f < faces.size(); f++) {
            final double[] face = faces.get(f);
            for (int k = 0; k < 4; k++) {
                System.arraycopy(face, 3 * k, points, 12 * f + 3 * k, 3);
                System.arraycopy(face, 12, facings, 12 * f + 3 * k, 3);
            }
            final int base = 4 * f;
            System.arraycopy(new int[] {base, base + 1, base + 2, base, base + 2, base + 3}, 0, corners, 6 * f, 6);
        }
        return new Sheet(points, facings, corners, of.stream().mapToInt(Integer::intValue).toArray());
    }

    private double[] minus(final double[] a, final double[] b) {
        return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }

    private double[] cross(final double[] a, final double[] b) {
        return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    private double[] centre(final double[][] at) {
        return new double[] {(at[0][0] + at[1][0] + at[2][0] + at[3][0]) / 4, (at[0][1] + at[1][1] + at[2][1]
            + at[3][1]) / 4, (at[0][2] + at[1][2] + at[2][2] + at[3][2]) / 4};
    }

    private double[] unit(final double[] v) {
        final double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return length == 0 ? new double[] {0, 1, 0} : new double[] {v[0] / length, v[1] / length, v[2] / length};
    }
}
