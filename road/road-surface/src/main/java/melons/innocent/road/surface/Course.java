package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * The line a road is laid along, from its start, nought along it, to its end, {@link #length()} along: where its line
 * is so far along it, how high what the road is laid on is there, and which way it heads. What carries the road works
 * its course out, the ground or a bridge's deck, straight or curving, level or climbing; the road's surface only
 * follows it, laid along it in pieces no longer than {@link #piece()}. In centimetres: across, along, up.
 */
public interface Course {

    /** How long it is, along its line, seen from above. */
    double length();

    /** Where its line is {@code s} along it, as high as what the road is laid on is there. */
    Point at(double s);

    /** Which way it heads {@code s} along it: so far round from along toward across, in radians. */
    double heading(double s);

    /**
     * How long a piece laid along it may be, at most, and still lie on it: short enough to follow its curve, and to
     * rise so little that one is stepped up onto from the one before it.
     */
    double piece();

    /** Where a point is {@code x} across from its line, left negative, {@code s} along it, {@code y} over it. */
    default Point beside(final double s, final double x, final double y) {
        final Point line = at(s);
        final double h = heading(s);
        return new Point(line.across() + x * Math.cos(h), line.along() - x * Math.sin(h), line.up() + y);
    }

    /**
     * Where its pieces begin and end, so far along it, from its start to its end: as many alike as it takes for none
     * to be longer than {@code most}, nor than {@link #piece()}.
     */
    default List<Double> cuts(final double most) {
        final int n = (int) Math.max(1, Math.ceil(length() / Math.min(most, piece()) - 1e-9));
        final List<Double> cuts = new ArrayList<>(n + 1);
        for (int k = 0; k <= n; k++) {
            cuts.add(length() * k / n);
        }
        return cuts;
    }
}
