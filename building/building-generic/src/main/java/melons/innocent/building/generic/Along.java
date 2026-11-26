package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.ArrayList;
import java.util.List;

/**
 * Boxes along a line in plan, whichever way it runs: so far along it from its beginning to so far, so far off its
 * middle line across it and so thick, so high; each a box of its own eight corners, along the line (bit 0), across it
 * from one face to the other (bit 1) and up (bit 2). In centimetres.
 */
final class Along {

    /**
     * A box along {@code line}, from {@code from} to {@code to} along it, its middle {@code off} across it from the
     * line's and {@code thick} across, from {@code y0} up to {@code y1}.
     */
    Solid box(final String name, final Role role, final Material material, final FloorPlan.Wall line,
              final double from, final double to, final double off, final double thick, final double y0,
              final double y1) {
        final double ux = (line.x1() - line.x0()) / line.length();
        final double uz = (line.z1() - line.z0()) / line.length();
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            final double along = (k & 1) == 0 ? from : to;
            final double across = off + ((k >> 1 & 1) == 0 ? -thick / 2 : thick / 2);
            corners.add(new Point(line.x0() + ux * along - uz * across, line.z0() + uz * along + ux * across,
                    (k >> 2 & 1) == 0 ? y0 : y1));
        }
        return new Solid(name, role, material, corners);
    }

    /** {@code line} whole, as thick as it is, from {@code y0} up to {@code y1}. */
    Solid whole(final String name, final Role role, final Material material, final FloorPlan.Wall line,
                final double y0, final double y1) {
        return box(name, role, material, line, 0, line.length(), 0, line.thick(), y0, y1);
    }
}
