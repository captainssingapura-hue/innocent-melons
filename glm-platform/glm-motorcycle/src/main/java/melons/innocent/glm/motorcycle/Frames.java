package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.engine.Hull;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A motorcycle's frames, which its twin carries the loads between ({@link Mounts}): the trellis, steel tube in
 * triangles, a side either side of the heads, outside the timing cover, from behind the steering head, welded to
 * its back clear of the fork's legs as they turn, down to the front head's node and back over the V to the back
 * head's, tubes across at each, a lug to the front head's face; the mid-structure, forged aluminium, a plate either
 * side outside the clutch cover and the belt, from the crankcase's mount behind the gearbox up to the back head's
 * node, a lug to its face, carrying the swingarm's pivot and the subframe; and the subframe, back from the
 * mid-structure over the back wheel to the tail, carrying the seat and the shocks' tops ({@link Subframe}).
 *
 * @param figures the bike's figures
 * @param mounts  where they are bolted to the engine
 * @param beneath how high the engine reaches under the seat, which the subframe's rails carry the seat over
 */
record Frames(BikeFigures figures, Mounts mounts, double beneath) {

    private static final int ROUND = 16;
    /** How far round its tubes are: the trellis's, a boss. */
    private static final double TUBE = 1.4;
    private static final double BOSS = 1.6;
    /** How far out from the middle the mid-structure's plates are, and the swingarm's arms. */
    static final double PLATES = 20.5;
    static final double ARMS = 18.5;
    /** How thick a plate is. */
    private static final double PLATE = 1;
    /** How far behind the steering head the trellis's sides come in, and how far round the head is. */
    private static final double BEHIND = 9;
    private static final double HEAD = 2.4;

    /** The trellis. */
    Part trellis() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        final double out = mounts.sides();
        final Vec3 front = mounts.node(mounts.front());
        final Vec3 rear = mounts.node(mounts.rear());
        for (final int side : List.of(-1, 1)) {
            final Vec3 across = new Vec3(0, 0, side * out);
            final Vec3 high = node(true).plus(across);
            final Vec3 low = node(false).plus(across);
            final Vec3 f = front.plus(across);
            final Vec3 r = rear.plus(across);
            final Vec3 m = high.plus(r).times(0.5);
            for (final Vec3[] tube : List.of(new Vec3[] {high, m}, new Vec3[] {m, r}, new Vec3[] {low, f},
                    new Vec3[] {high, f}, new Vec3[] {m, f}, new Vec3[] {f, r}, new Vec3[] {high, low},
                    new Vec3[] {high, head(true)}, new Vec3[] {low, head(false)})) {
                made.add(shapes.drum(tube[0], tube[1], TUBE));
            }
        }
        for (final Vec3 node : List.of(front, rear, mid())) {
            made.add(shapes.drum(across(node, -out), across(node, out), TUBE));
        }
        made.add(lug(mounts.front()));
        return new Part("trellis frame", Group.FRAME, false, List.of(
                new Point("front head", mounts.front().point("top").at()), new Point("rear node", rear),
                new Point("top rail", topRail()),
                new Point("steering head", figures.stem((BikeFigures.LOW_YOKE + BikeFigures.HIGH_YOKE) / 2))),
                shapes.joined(made), new Made(Material.STEEL, false)).as(new MotorcycleParts.TrellisFrame());
    }

    /**
     * The mid-structure: its two plates, from the crankcase's mount up to the back head's node, the swingarm's pivot
     * and the subframe's front between them; bolts through at each, and a lug to the back head's face.
     */
    Part midStructure() {
        final Shapes shapes = new Shapes(ROUND);
        final Outlines outlines = new Outlines(ROUND);
        final Vec3 rear = mounts.node(mounts.rear());
        final Vec3 pivot = figures.pivot();
        final Vec3 mount = mounts.rearMount();
        final Vec3 top = subframe().front();
        final List<Spot> round = new ArrayList<>(outlines.circle(spot(mount), 3));
        round.addAll(outlines.circle(spot(pivot), 3.5));
        round.addAll(outlines.circle(spot(rear), 3));
        round.addAll(outlines.circle(spot(top), 3));
        final List<Spot> outline = new Hull(round).outline();
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            made.add(new Plate(outline, List.of(), new Vec3(0, 0, figures.centre() + side * PLATES),
                    new Vec3(1, 0, 0), new Vec3(0, 1, 0), PLATE).mesh());
        }
        for (final Vec3 through : List.of(mount, pivot, top, rear)) {
            made.add(shapes.drum(across(through, -PLATES), across(through, PLATES), through == pivot ? 1.2 : 1.0));
        }
        made.add(lug(mounts.rear()));
        return new Part("mid-structure", Group.FRAME, false, List.of(new Point("crankcase", mount),
                new Point("pivot", pivot), new Point("subframe", top), new Point("trellis", rear),
                new Point("back head", mounts.rear().point("top").at())), shapes.joined(made),
                new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.MidStructure());
    }

    /** The subframe, bolted to the mid-structure a little behind and under the back head's node ({@link Subframe}). */
    Subframe subframe() {
        final Vec3 rear = mounts.node(mounts.rear());
        return new Subframe(figures, new Vec3(rear.x() - 4, rear.y() - 6, figures.centre()), beneath);
    }

    /** A lug from head {@code head}'s outer face out to its node, in the middle. */
    private Mesh lug(final Part head) {
        return new Shapes(ROUND).drum(mounts.face(head), mounts.node(head), BOSS);
    }

    /**
     * The node behind the steering head, its top one or its bottom, in the middle across, where the trellis's side
     * comes in: behind the head, out of the way of the fork's legs as the bars turn them.
     */
    private Vec3 node(final boolean top) {
        return head(top).minus(figures.ahead().times(BEHIND));
    }

    /** Where the trellis is welded to the back of the steering head, near its top or its bottom. */
    private Vec3 head(final boolean top) {
        return figures.stem(top ? BikeFigures.HIGH_YOKE - 4 : BikeFigures.LOW_YOKE + 4).minus(figures.ahead()
                .times(HEAD));
    }

    /** The middle of the trellis's top rails, in the middle across, where its tube across is. */
    Vec3 topRail() {
        return mid();
    }

    /** How high the trellis's top rails are at {@code x} along the bike, from the steering head to the back head. */
    double topRail(final double x) {
        final Vec3 high = node(true);
        final Vec3 rear = mounts.node(mounts.rear());
        return high.y() + (x - high.x()) * (rear.y() - high.y()) / (rear.x() - high.x());
    }

    /** The trellis's middle, where its tube across is. */
    private Vec3 mid() {
        final Vec3 high = node(true);
        return high.plus(mounts.node(mounts.rear())).times(0.5);
    }

    /** {@code p} moved to {@code out} from the middle along the crank. */
    private Vec3 across(final Vec3 p, final double out) {
        return new Vec3(p.x(), p.y(), figures.centre() + out);
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }
}
