package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A terrain: the dry ground over a stretch of the map, made from its map and its landforms. The map is lines between
 * spots (a graph), each line straight or bowed along a curve; its landforms are laid over one another in the order
 * they are listed, the first over nothing, each shaped by its curves and, where it follows the map's lines, by them.
 * How high the ground is at a spot is what they make of it there, in turn: worked out, the same every time. Water
 * is no part of it: the sea's bed and a river's lie as dry ground, as deep as they are.
 *
 * <p>Made, it is checked: every bowed line meets its spots, its curve running the line's length and nothing at both
 * ends, and every path its landforms follow goes along the map's lines.
 */
public final class Terrain implements Ground {

    /** How near a bowed line's curve must come to its spots, in centimetres. */
    private static final double MEETS = 1;

    private final String name;
    private final Extent extent;
    private final Graph<Spot, Course> map;
    private final List<Landform> landforms;
    private final List<Landform.Shaping> shapings;

    /** {@code name}'s ground over {@code extent}, from {@code map}'s lines and {@code landforms}, laid in turn. */
    public Terrain(final String name, final Extent extent, final Graph<Spot, Course> map,
                   final List<Landform> landforms) {
        this.name = name;
        this.extent = extent;
        this.map = map;
        this.landforms = List.copyOf(landforms);
        if (this.landforms.isEmpty()) {
            throw new IllegalArgumentException(name + ": ground with no landform laid is no ground");
        }
        map.edges().listed().forEach(this::meets);
        final List<Landform.Shaping> shaped = new ArrayList<>();
        for (final Landform landform : this.landforms) {
            shaped.add(landform.shaping(map));
        }
        this.shapings = List.copyOf(shaped);
    }

    /** Its name. */
    public String name() {
        return name;
    }

    /** Its map: the lines between its spots. */
    public Graph<Spot, Course> map() {
        return map;
    }

    /** Its landforms, in the order they are laid. */
    public List<Landform> landforms() {
        return landforms;
    }

    @Override
    public Extent extent() {
        return extent;
    }

    @Override
    public double height(final double x, final double z) {
        double height = 0;
        for (final Landform.Shaping shaping : shapings) {
            height = shaping.laid(height, x, z);
        }
        return height;
    }

    /** {@code line}, if bowed, checked to meet its spots: its curve the line's length, nothing at both ends. */
    private void meets(final Edge<Spot, Course> line) {
        if (line.label() instanceof Course.Bowed(final var offset)) {
            final double length = line.one().to(line.other());
            if (Math.abs(offset.start()) > MEETS || Math.abs(offset.end() - length) > MEETS
                    || Math.abs(offset.at(offset.start())) > MEETS || Math.abs(offset.at(offset.end())) > MEETS) {
                throw new IllegalArgumentException(name + ": the line from " + line.one() + " to " + line.other()
                        + " bowed by a curve that does not meet both its spots");
            }
        }
    }
}
