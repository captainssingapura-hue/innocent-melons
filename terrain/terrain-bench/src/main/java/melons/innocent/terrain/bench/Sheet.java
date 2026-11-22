package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The ground as the card draws it, from its heights already worked out ({@link Grid}): a sheet of triangles, each
 * point as high as the ground is there and facing as the sheet slopes there, so it is lit smooth; two triangles to a
 * square, each facing up out of the ground. Its frame is the map's own; its heights may be drawn so many times as
 * high, so a shallow bed shows beside a high mountain. It may be cut into parts by the layer each point is coloured
 * in, each square going with the layer at its near left corner, each part a sheet of its own, drawn in its colour.
 */
public record Sheet() implements StatelessFunctionalObject {

    public static final Sheet INSTANCE = new Sheet();

    /** {@code ground} as one sheet, its points about {@code every} centimetres apart, {@code up} times as high. */
    public Mesh of(final Ground ground, final double every, final double up) {
        final Grid grid = new Grid(ground, every);
        return parts(grid, up, new int[grid.across() * grid.along()]).get(0);
    }

    /**
     * {@code grid}'s ground as sheets, {@code up} times as high, one for each layer its squares are coloured in, as
     * {@code layers} says of each point, row by row from the nearest: in the order the layers are first come to.
     */
    public Map<Integer, Mesh> parts(final Grid grid, final double up, final int[] layers) {
        if (!(up > 0)) {
            throw new IllegalArgumentException("drawn some times as high: " + up);
        }
        final Map<Integer, List<Integer>> squares = new LinkedHashMap<>();
        for (int j = 0; j + 1 < grid.along(); j++) {
            for (int i = 0; i + 1 < grid.across(); i++) {
                final int near = j * grid.across() + i;
                squares.computeIfAbsent(layers[near], k -> new ArrayList<>()).add(near);
            }
        }
        final Map<Integer, Mesh> parts = new LinkedHashMap<>();
        squares.forEach((layer, nears) -> parts.put(layer, part(grid, up, nears)));
        return parts;
    }

    /** The sheet over the squares whose near left corners are {@code nears}, its points only those they use. */
    private Mesh part(final Grid grid, final double up, final List<Integer> nears) {
        final Map<Integer, Integer> own = new HashMap<>();
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>(nears.size() * 6);
        final int across = grid.across();
        for (final int near : nears) {
            final int far = near + across;
            for (final int corner : List.of(near, far, near + 1, near + 1, far, far + 1)) {
                corners.add(own.computeIfAbsent(corner, c -> {
                    final int i = c % across;
                    final int j = c / across;
                    final double[] rise = grid.rise(i, j);
                    points.add(new Vec3(grid.x(i), grid.height(i, j) * up, grid.z(j)));
                    facings.add(new Vec3(-rise[0] * up, 1, -rise[1] * up).unit());
                    return points.size() - 1;
                }));
            }
        }
        return new Mesh(points, facings, corners);
    }
}
