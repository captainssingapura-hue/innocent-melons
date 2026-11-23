package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.tiles.Sampled;
import melons.innocent.terrain.tiles.Triangles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * The ground as the card draws it, from its heights already worked out ({@link Grid}): a sheet of triangles, each
 * point as high as the ground is there and facing as the sheet slopes there, so it is lit smooth; two triangles to a
 * square, each facing up out of the ground. Its frame is the map's own; its heights may be drawn so many times as
 * high, so a shallow bed shows beside a high mountain. It may be cut into parts by the layer each point is coloured
 * in, a triangle whose corners lie in different layers cut where they part, as a tile's are ({@link Triangles}), so
 * one layer ends where it ends on the land and not along the squares; each part a sheet of its own, in its colour.
 */
public record Sheet() implements StatelessFunctionalObject {

    public static final Sheet INSTANCE = new Sheet();

    /** {@code ground} as one sheet, its points about {@code every} centimetres apart, {@code up} times as high. */
    public Mesh of(final Ground ground, final double every, final double up) {
        final Grid grid = new Grid(ground, every);
        return parts(grid, up, new int[grid.across() * grid.along()], at -> 0).get(0);
    }

    /**
     * {@code grid}'s ground as sheets, {@code up} times as high, one for each layer it is coloured in, as
     * {@code layers} says of each point, row by row from the nearest, and as {@code sorted} says of any spot between
     * them: in the order the layers are first come to.
     */
    public Map<Integer, Mesh> parts(final Grid grid, final double up, final int[] layers,
                                    final ToIntFunction<Sample> sorted) {
        if (!(up > 0)) {
            throw new IllegalArgumentException("drawn some times as high: " + up);
        }
        final Layered layered = new Layered(grid, layers);
        final Triangles cut = new Triangles(layered, sorted);
        final Map<Integer, List<Integer>> triangles = new LinkedHashMap<>();
        for (int t = 0; t < cut.count(); t++) {
            triangles.computeIfAbsent(cut.sort(t), k -> new ArrayList<>()).add(t);
        }
        final Map<Integer, Mesh> parts = new LinkedHashMap<>();
        triangles.forEach((layer, those) -> parts.put(layer, part(layered, cut, up, those)));
        return parts;
    }

    /** The sheet of {@code those} of the cut triangles, its points only those they use. */
    private Mesh part(final Layered grid, final Triangles cut, final double up, final List<Integer> those) {
        final Map<Integer, Integer> own = new HashMap<>();
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>(those.size() * 3);
        for (final int triangle : those) {
            for (int k = 0; k < 3; k++) {
                corners.add(own.computeIfAbsent(cut.corner(triangle, k), c -> {
                    final double[] at = point(grid, cut, c);
                    points.add(new Vec3(at[0], at[2] * up, at[1]));
                    facings.add(new Vec3(-at[3] * up, 1, -at[4] * up).unit());
                    return points.size() - 1;
                }));
            }
        }
        return new Mesh(points, facings, corners);
    }

    /**
     * Point {@code c} of the cut triangles: where on the map, across and along, how high, and how it rises across and
     * along; one a cut added made of the grid's own points, each in its share, so it lies on the edge it cuts.
     */
    private double[] point(final Layered grid, final Triangles cut, final int c) {
        final int n = grid.columns();
        if (c < cut.own()) {
            final int i = c % n;
            final int j = c / n;
            return new double[] {grid.x(i), grid.z(j), grid.height(i, j), grid.across(i, j), grid.along(i, j)};
        }
        final double[] sum = new double[5];
        for (int k = 0; k < 3; k++) {
            final double share = cut.share(c - cut.own(), k);
            final double[] part = point(grid, cut, cut.from(c - cut.own(), k));
            for (int q = 0; q < sum.length; q++) {
                sum[q] += share * part[q];
            }
        }
        return sum;
    }

    /**
     * A grid's points, each in the layer {@code layers} says.
     *
     * @param grid   the ground's heights
     * @param layers each point's layer, row by row from the nearest
     */
    private record Layered(Grid grid, int[] layers) implements Sampled {

        @Override
        public int columns() {
            return grid.across();
        }

        @Override
        public int rows() {
            return grid.along();
        }

        @Override
        public double x(final int i) {
            return grid.x(i);
        }

        @Override
        public double z(final int j) {
            return grid.z(j);
        }

        @Override
        public double height(final int i, final int j) {
            return grid.height(i, j);
        }

        @Override
        public double across(final int i, final int j) {
            return grid.rise(i, j)[0];
        }

        @Override
        public double along(final int i, final int j) {
            return grid.rise(i, j)[1];
        }

        @Override
        public int sort(final int i, final int j) {
            return layers[j * grid.across() + i];
        }
    }
}
