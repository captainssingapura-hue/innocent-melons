package melons.innocent.glm.lab;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.model.Finishes;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.ArrayList;
import java.util.List;

/**
 * What the lab draws, worked out from what it shows: the part looked at, in its finish, alone in its own frame, or
 * standing where it was put together with the rest, the rest as glass, finished or left out; the part's points, where
 * they stand; and the box the grid rules round it, round what is shown or, ruled fine, round the part alone. Its axes
 * cross at nought, which alone is the part's own frame's middle and put together the platform's.
 *
 * @param built    the platform put together
 * @param show     what is shown
 * @param finishes what the parts are seen in
 */
public record LabScene(Assembled built, LabShow show, Finishes finishes) {

    /** A part drawn: where its own frame stands, and what it is finished in. */
    public record Placement(PartAt part, Vec3 at, Coat coat) {
    }

    /**
     * The scene worked out once, as it is drawn frame after frame until it changes: what is shown, its parts, its
     * points, the grid round it, where its middle is, lifted, and how big it is; and how far all of it is lifted.
     */
    public record Settled(LabShow show, List<Placement> placements, List<Point> points, Grid grid, Vec3 middle,
                          double size, Vec3 lift) {
    }

    /** It worked out once. */
    public Settled settled() {
        final Grid grid = grid();
        final Vec3 lift = lift(grid);
        return new Settled(show, placements(), points(), grid, middle().plus(lift), size(), lift);
    }

    /**
     * How far everything is lifted where it is drawn: a part alone, in its own frame, as far as puts the grid's floor
     * on the ground, so none of it is under the ground; put together, nowhere, the grid's floor the ground.
     */
    public Vec3 lift(final Grid grid) {
        return show.level() == LabShow.Level.PART ? new Vec3(0, -grid.corner(true).y(), 0) : new Vec3(0, 0, 0);
    }

    /** How much room the grid leaves round what it rules, as a share of its size. */
    private static final double ROOM = 0.15;

    /** Every part drawn. */
    public List<Placement> placements() {
        final PartAt looked = show.part();
        final Coat own = finishes.of(looked.part(), show.madeOf());
        if (show.level() == LabShow.Level.PART) {
            return List.of(new Placement(looked, new Vec3(0, 0, 0), own));
        }
        final List<Placement> all = new ArrayList<>();
        built.placed().forEach((part, at) -> {
            if (part.equals(looked)) {
                all.add(new Placement(part, at, own));
            } else if (show.others() == LabShow.Others.GHOST) {
                all.add(new Placement(part, at, new Coat(Coat.Kind.GLASS)));
            } else if (show.others() == LabShow.Others.FINISHED
                    || show.others() == LabShow.Others.CHASSIS && part.part().group() != Group.UNDERBODY
                    || show.others() == LabShow.Others.GROUP && part.part().group() == looked.part().group()) {
                all.add(new Placement(part, at, finishes.of(part.part(), show.madeOf())));
            }
        });
        return all;
    }

    /** Where the part's points are, named, where they stand. */
    public List<Point> points() {
        final Vec3 at = show.level() == LabShow.Level.PART ? new Vec3(0, 0, 0) : built.at(show.part());
        return show.part().shape().points().stream().map(p -> new Point(p.name(), p.at().plus(at))).toList();
    }

    /** The grid's box, what it rules round: its low corner, or its high. */
    public Vec3 corner(final boolean low) {
        final List<Placement> round = show.spacing() == Spacing.FINE || show.level() == LabShow.Level.PART
                ? placements().stream().filter(p -> p.part().equals(show.part())).toList() : placements();
        double[] least = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        double[] most = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (final Placement p : round) {
            for (final Vec3 v : p.part().shape().form().points()) {
                final Vec3 w = v.plus(p.at());
                least = new double[] {Math.min(least[0], w.x()), Math.min(least[1], w.y()), Math.min(least[2], w.z())};
                most = new double[] {Math.max(most[0], w.x()), Math.max(most[1], w.y()), Math.max(most[2], w.z())};
            }
        }
        final double room = ROOM * Math.max(most[0] - least[0], Math.max(most[1] - least[1], most[2] - least[2]));
        final double floor = show.level() == LabShow.Level.ASSEMBLY ? 0 : least[1] - room;
        return low ? new Vec3(least[0] - room, floor, least[2] - room)
                : new Vec3(most[0] + room, most[1] + room, most[2] + room);
    }

    /** The middle of the grid's box, to look at. */
    public Vec3 middle() {
        return corner(true).plus(corner(false)).times(0.5);
    }

    /** How big the grid's box is, its longest way. */
    public double size() {
        final Vec3 d = corner(false).minus(corner(true));
        return Math.max(d.x(), Math.max(d.y(), d.z()));
    }

    /** The grid round it, ruled as shown. */
    public Grid grid() {
        return new Grid(corner(true), corner(false), new Vec3(0, 0, 0), show.spacing());
    }
}
