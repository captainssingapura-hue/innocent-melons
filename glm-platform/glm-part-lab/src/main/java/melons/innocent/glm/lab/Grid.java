package melons.innocent.glm.lab;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * A grid as a drawing office rules one, round what is looked at: a floor under it and two walls, one behind it and
 * one to its left, meeting in a corner; ruled as {@code spacing} says, light lines and heavy, over the box from
 * {@code low} to {@code high} widened out to whole heavy steps; and three axes through {@code origin}, across, up and
 * along, each its own colour, as long as the grid. Each line a thin bar, so it is seen from either side. In
 * centimetres.
 *
 * @param low     the box's near lower left corner
 * @param high    its far upper right corner
 * @param origin  where the axes cross
 * @param spacing how finely it is ruled
 */
public record Grid(Vec3 low, Vec3 high, Vec3 origin, Spacing spacing) {

    /** How many lines a wall may have each way, at most: wider than that, it is ruled no finer than so many. */
    private static final int MOST = 240;
    /**
     * How wide a light line is, as a share of the grid's box, so it is seen whether the box is a part's or a car's; a
     * heavy one, as many times; an axis, as many again.
     */
    private static final double LIGHT = 0.0018;
    private static final double HEAVY = 2;
    private static final double AXIS = 1.5;

    /** Its light lines. */
    public Mesh light() {
        return ruled(step(), false);
    }

    /** Its heavy lines. */
    public Mesh heavy() {
        return ruled(spacing.major(), true);
    }

    /** Its axis across, up or along: {@code way} 0, 1 or 2. */
    public Mesh axis(final int way) {
        final Vec3 a = corner(true);
        final Vec3 b = corner(false);
        final double wide = width(true) * AXIS;
        final Vec3 from = switch (way) {
            case 0 -> new Vec3(a.x(), origin.y(), origin.z());
            case 1 -> new Vec3(origin.x(), a.y(), origin.z());
            default -> new Vec3(origin.x(), origin.y(), a.z());
        };
        final Vec3 to = switch (way) {
            case 0 -> new Vec3(b.x(), origin.y(), origin.z());
            case 1 -> new Vec3(origin.x(), b.y(), origin.z());
            default -> new Vec3(origin.x(), origin.y(), b.z());
        };
        return new Shapes(8).drum(from, to, wide);
    }

    /** The box it rules, widened out to whole heavy steps: its low corner, or its high. */
    public Vec3 corner(final boolean lower) {
        final double m = spacing.major();
        return lower ? new Vec3(Math.floor(low.x() / m) * m, Math.floor(low.y() / m) * m, Math.floor(low.z() / m) * m)
                : new Vec3(Math.ceil(high.x() / m) * m, Math.ceil(high.y() / m) * m, Math.ceil(high.z() / m) * m);
    }

    /** How far apart its light lines are: as the spacing says, or no finer than keeps it to so many lines. */
    public double step() {
        final Vec3 a = corner(true);
        final Vec3 b = corner(false);
        final double widest = Math.max(b.x() - a.x(), Math.max(b.y() - a.y(), b.z() - a.z()));
        double step = spacing.minor();
        while (widest / step > MOST) {
            step *= 10;
        }
        return step;
    }

    /** Lines every {@code step} on the floor and the two walls; heavy ones, or light ones but where a heavy one is. */
    private Mesh ruled(final double step, final boolean heavy) {
        final Vec3 a = corner(true);
        final Vec3 b = corner(false);
        final double wide = width(heavy);
        final List<Mesh> lines = new ArrayList<>();
        final Shapes shapes = new Shapes(4);
        for (double x = a.x(); x <= b.x() + 1e-6; x += step) {
            if (heavy || !onHeavy(x)) {
                lines.add(bar(shapes, new Vec3(x, a.y(), a.z()), new Vec3(x, a.y(), b.z()), wide));
                lines.add(bar(shapes, new Vec3(x, a.y(), a.z()), new Vec3(x, b.y(), a.z()), wide));
            }
        }
        for (double z = a.z(); z <= b.z() + 1e-6; z += step) {
            if (heavy || !onHeavy(z)) {
                lines.add(bar(shapes, new Vec3(a.x(), a.y(), z), new Vec3(b.x(), a.y(), z), wide));
                lines.add(bar(shapes, new Vec3(a.x(), a.y(), z), new Vec3(a.x(), b.y(), z), wide));
            }
        }
        for (double y = a.y(); y <= b.y() + 1e-6; y += step) {
            if (heavy || !onHeavy(y)) {
                lines.add(bar(shapes, new Vec3(a.x(), y, a.z()), new Vec3(b.x(), y, a.z()), wide));
                lines.add(bar(shapes, new Vec3(a.x(), y, a.z()), new Vec3(a.x(), y, b.z()), wide));
            }
        }
        return shapes.joined(lines);
    }

    private boolean onHeavy(final double at) {
        final double m = spacing.major();
        return Math.abs(at / m - Math.rint(at / m)) < 1e-6;
    }

    /** How wide a line is: a share of the grid's box, the heavy ones so many times as wide. */
    private double width(final boolean heavy) {
        final Vec3 d = corner(false).minus(corner(true));
        return Math.max(d.x(), Math.max(d.y(), d.z())) * LIGHT * (heavy ? HEAVY : 1);
    }

    /** A thin bar from {@code from} to {@code to}, {@code wide} across. */
    private Mesh bar(final Shapes shapes, final Vec3 from, final Vec3 to, final double wide) {
        return shapes.drum(from, to, wide / 2);
    }
}
