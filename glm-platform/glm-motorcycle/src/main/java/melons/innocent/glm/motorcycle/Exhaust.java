package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.List;

/**
 * A motorcycle's exhaust, two into one, low on its left, as the reference bike's is: the front header out of the
 * front head's exhaust port, down ahead of the crankcase, across to the left and back low, outside the clutch cover;
 * the back header out of the back head's, over the clutch cover to the left and down outside the mid-structure's plate,
 * welded into the front one where they meet; the
 * collector where they meet, behind the engine; and the muffler on back from it, low beside the back wheel, under
 * the swingarm and outside the shock, a heat shield over its front. Its places estimates, as the frame leaves room.
 *
 * @param figures the bike's figures
 * @param front   the front cylinder's head
 * @param rear    the back cylinder's head
 */
record Exhaust(BikeFigures figures, Part front, Part rear) {

    private static final int ROUND = 16;
    /** How far round a header is, the collector and the muffler. */
    private static final double HEADER = 2.2;
    private static final double COLLECTOR = 3;
    private static final double MUFFLER = 5;
    /**
     * How far out of its port the front header runs straight, and the back one; how far below its port the back one
     * crosses over the clutch cover; how far ahead the front one comes down, clear of the crankcase.
     */
    private static final double OUT = 5;
    private static final double OUT_BACK = 3;
    private static final double DROP = 5.5;
    private static final double AHEAD = 16.5;
    /** How high the headers' low run is over the ground, and how far to the left. */
    private static final double LOW = 14;
    private static final double LEFT = 23.5;
    /** How far the muffler stands out to the left, how high its front and back are, and how far back it reaches. */
    private static final double MUFFLER_LEFT = 26;
    private static final double MUFFLER_FRONT = 16;
    private static final double MUFFLER_BACK = 21;
    private static final double REACH = 8;

    /** The front header. */
    Part frontHeader() {
        final Vec3 port = port(front, "exhaust port 2.1");
        final Vec3 out = port.plus(outward(front).times(OUT));
        final Vec3 ahead = new Vec3(AHEAD, out.y() - 8, figures.centre());
        final Vec3 down = new Vec3(AHEAD, ground(LOW + 2), figures.centre());
        final Vec3 side = new Vec3(AHEAD - 1, ground(LOW), left(LEFT));
        final List<Vec3> path = List.of(port, out, ahead, down, side, meeting());
        return new Part("front header", Group.EXHAUST, false, List.of(new Point("port", port),
                new Point("collector", meeting())), new Shapes(ROUND).pipe(path, HEADER),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.HeaderPipe());
    }

    /** The back header. */
    Part rearHeader() {
        final Vec3 port = port(rear, "exhaust port 1.1");
        final Vec3 out = port.plus(outward(rear).times(OUT_BACK));
        final Vec3 drop = new Vec3(out.x() + 1, port.y() - DROP, port.z());
        final Vec3 over = new Vec3(drop.x(), drop.y(), left(LEFT));
        final Vec3 down = new Vec3(drop.x() - 2, ground(LOW + 4), left(LEFT));
        final List<Vec3> path = List.of(port, out, drop, over, down, meeting());
        return new Part("rear header", Group.EXHAUST, false, List.of(new Point("port", port),
                new Point("collector", meeting())), new Shapes(ROUND).pipe(path, HEADER),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.HeaderPipe());
    }

    /** The collector, where the headers meet. */
    Part collector() {
        final Vec3 at = meeting();
        return new Part("exhaust collector", Group.EXHAUST, false, List.of(new Point("headers", at),
                new Point("muffler", muffler(0))), new Shapes(ROUND).capsule(at, muffler(0), COLLECTOR),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.Collector());
    }

    /** The muffler. */
    Part muffler() {
        return new Part("muffler", Group.EXHAUST, false, List.of(new Point("collector", muffler(0))),
                new Shapes(ROUND * 2).drum(muffler(0), muffler(1), MUFFLER), new Made(Material.STEEL, false))
                .as(new MotorcycleParts.Muffler());
    }

    /** The heat shield over the muffler's front. */
    Part heatShield() {
        return new Part("heat shield", Group.EXHAUST, false, List.of(new Point("muffler", muffler(0))),
                new Shapes(ROUND * 2).drum(muffler(0.15), muffler(0.5), MUFFLER + 0.5),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.HeatShield());
    }

    /** Where the headers meet, behind the crankcase, low on the left. */
    Vec3 meeting() {
        return new Vec3(figures.pivot().x(), ground(LOW), left(LEFT));
    }

    /** The place {@code t} of the way along the muffler, from its front to its back. */
    Vec3 muffler(final double t) {
        final Vec3 from = new Vec3(meeting().x() - 6, ground(MUFFLER_FRONT), left(MUFFLER_LEFT));
        final Vec3 to = new Vec3(figures.rearAxle().x() - REACH, ground(MUFFLER_BACK), left(MUFFLER_LEFT));
        return from.plus(to.minus(from).times(t));
    }

    /** The place {@code out} to the left of the middle. */
    private double left(final double out) {
        return figures.centre() - out;
    }

    /** How high {@code over} the ground is, in the engine's frame. */
    private double ground(final double over) {
        return figures.ground() + over;
    }

    /** Where head {@code head}'s port called {@code called} comes out. */
    private Vec3 port(final Part head, final String called) {
        return head.point(called).at();
    }

    /** The way out of head {@code head}'s exhaust side, away from the V, square to its cylinder. */
    private Vec3 outward(final Part head) {
        final Vec3 top = head.point("top").at();
        final Vec3 up = new Vec3(top.x(), top.y(), 0).unit();
        final Vec3 n = new Vec3(up.y(), -up.x(), 0);
        return up.x() > 0 ? n : n.times(-1);
    }
}
