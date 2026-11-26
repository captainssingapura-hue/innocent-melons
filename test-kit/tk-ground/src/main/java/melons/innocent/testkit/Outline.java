package melons.innocent.testkit;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The outline of a base map, as common shapes, each given by its corners round the map's middle: a circle and an
 * oval by many, a square by four, a star by ten. From any of them the middle sees the whole edge, so the ground
 * within is a fan of triangles from the middle and the avatar, who starts there, starts within.
 */
public enum Outline {
    /** A circle: as far every way. */
    CIRCLE("a circle", 180, 0),
    /** A square, its sides twice as long as it is said to reach. */
    SQUARE("a square", 4, 45),
    /** An oval: as far as said across, and 0.6 as far along. */
    OVAL("an oval", 180, 0),
    /** A star of five points, a point straight along; its inner corners 0.45 as far out. */
    STAR("a star", 10, 0);

    private static final double FLAT = 0.6;
    private static final double INNER = 0.45;
    private static final double TURN = 360;

    private final String called;
    private final int corners;
    private final double first;

    Outline(final String called, final int corners, final double first) {
        this.called = called;
        this.corners = corners;
        this.first = first;
    }

    /** What it is called, in a line of words. */
    public String called() {
        return called;
    }

    /** Its corners on the ground, in order round the middle, for a map reaching {@code reach} centimetres. */
    public List<Vec3> edge(final double reach) {
        final List<Vec3> edge = new ArrayList<>();
        for (int corner = 0; corner < corners; corner++) {
            final double angle = Math.toRadians(first + corner * TURN / corners);
            final double out = reach * out(corner, angle);
            edge.add(new Vec3(out * Math.sin(angle), 0, out * Math.cos(angle)));
        }
        return edge;
    }

    /** How far out the corner numbered {@code corner} is, the way {@code angle} points, of how far it reaches. */
    private double out(final int corner, final double angle) {
        final double across = Math.sin(angle);
        final double along = Math.cos(angle);
        return switch (this) {
            case CIRCLE -> 1;
            case SQUARE -> 1 / Math.max(Math.abs(across), Math.abs(along));
            case OVAL -> 1 / Math.sqrt(across * across + along * along / (FLAT * FLAT));
            case STAR -> corner % 2 == 0 ? 1 : INNER;
        };
    }
}
