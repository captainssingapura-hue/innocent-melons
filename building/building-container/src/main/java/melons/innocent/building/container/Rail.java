package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A railing along the edge of what is walked on, straight, across or along: from ({@code x0}, {@code z0}) to
 * ({@code x1}, {@code z1}), standing on what is {@code level} high there. Posts {@link #BAR} square no more than
 * {@link #APART} apart, a top rail {@link #HIGH} over the floor and a rail halfway, all finished in {@code frame}.
 *
 * @param x0    where it starts, across
 * @param z0    and along
 * @param x1    where it ends, across
 * @param z1    and along
 * @param level how high what it stands on is
 * @param frame what it is finished in
 */
record Rail(double x0, double z0, double x1, double z1, double level, Finish frame) {

    /** How square its posts and rails are; how far apart its posts are, at most; how high it is. */
    static final double BAR = 5;
    static final double APART = 120;
    static final double HIGH = 100;

    /** Its posts and rails. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final double length = Math.hypot(x1 - x0, z1 - z0);
        final int bays = (int) Math.max(1, Math.ceil(length / APART));
        for (int k = 0; k <= bays; k++) {
            final double x = x0 + (x1 - x0) * k / bays;
            final double z = z0 + (z1 - z0) * k / bays;
            parts.add(bar(x - BAR / 2, x + BAR / 2, z - BAR / 2, z + BAR / 2, level, level + HIGH));
        }
        for (final double y : List.of(HIGH / 2, HIGH - BAR)) {
            parts.add(bar(Math.min(x0, x1) - BAR / 2, Math.max(x0, x1) + BAR / 2, Math.min(z0, z1) - BAR / 2,
                    Math.max(z0, z1) + BAR / 2, level + y, level + y + BAR));
        }
        return parts;
    }

    private Part bar(final double x0, final double x1, final double z0, final double z1, final double y0,
                     final double y1) {
        return new Part(new Elements.Railing(), frame.on(Timbers.INSTANCE.block("railing", Role.FRAME,
                frame.material(), x0, x1, z0, z1, y0, y1)));
    }
}
