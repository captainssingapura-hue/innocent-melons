package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * A motorcycle's bodywork over its frames, as the reference bike's: the airbox cover, steel, where a tank would be,
 * astride the trellis's top rails from behind the steering head back to the seat, its top falling gently back, its
 * underside along the rails; the seat, resting on the subframe's rails, low over the back head and the tank where
 * they run level, up their step at its back, rising a little at its front; the fuel tank under it, between the
 * subframe's rails and over the back wheel; and a side cover either side, closing the frame under the seat.
 * Each made through sections along the bike, rounded, as the vehicles lab's saddle is.
 *
 * @param figures the bike's figures
 * @param frames  its frames
 */
record Bodywork(BikeFigures figures, Frames frames) {

    private static final int SECTIONS = 16;
    /** How far over the trellis's rails the cover's underside is. */
    private static final double OVER = 2.2;
    /** The cover's front and back along the bike, and how high its top is at each over the ground. */
    private static final double COVER_FRONT = 20;
    private static final double COVER_BACK = -7;
    private static final double COVER_HIGH = 90;
    private static final double COVER_LOW = 79;
    /** The seat's front and back along the bike; how far over its rider's place its front rises. */
    private static final double SEAT_FRONT = -8;
    private static final double SEAT_BACK = -62;
    private static final double RISE = 3;
    /** How thick the seat is over its pan. */
    private static final double CUSHION = 6;
    /** How high the tank's top and bottom are, over the ground. */
    private static final double TANK_TOP = 63;
    private static final double TANK_FOOT = 55.5;

    /** The airbox cover. */
    Part airboxCover() {
        final DoubleUnaryOperator top = x -> ground(COVER_LOW + (COVER_HIGH - COVER_LOW) * (x - COVER_BACK)
                / (COVER_FRONT - COVER_BACK));
        final List<List<Vec3>> rings = new ArrayList<>();
        for (int k = 0; k <= SECTIONS; k++) {
            final double t = (double) k / SECTIONS;
            final double x = COVER_FRONT + (COVER_BACK - COVER_FRONT) * t;
            final double wide = 11 + 6 * Math.sin(Math.PI * Math.min(1, t / 0.6) / 2) - 2 * Math.max(0, t - 0.7);
            rings.add(section(x, top.applyAsDouble(x), frames.topRail(x) + OVER, wide, wide - 1.5));
        }
        return new Part("airbox cover", Group.BODYWORK, false, List.of(new Point("trellis", frames.topRail())),
                new Shapes(16).lofted(rings, frames.topRail()), new Made(Material.STEEL, false))
                .as(new MotorcycleParts.AirboxCover());
    }

    /** The seat. */
    Part seat() {
        final List<List<Vec3>> rings = new ArrayList<>();
        for (int k = 0; k <= SECTIONS; k++) {
            final double t = (double) k / SECTIONS;
            final double x = SEAT_FRONT + (SEAT_BACK - SEAT_FRONT) * t;
            final double rise = RISE * Math.pow(Math.max(0, 0.25 - t) / 0.25, 2);
            final double wide = 9 + 7 * Math.sin(Math.PI / 2 * Math.min(1, t / 0.45));
            rings.add(section(x, pan(x) + CUSHION + rise, pan(x), wide, wide - 1.5));
        }
        return new Part("seat", Group.TRIM, false, List.of(new Point("subframe", frames.subframe().front())),
                new Shapes(16).lofted(rings, frames.subframe().front()), new Made(Material.PLASTIC, false))
                .as(new MotorcycleParts.Seat());
    }

    /** The fuel tank under the seat. */
    Part tank() {
        final List<List<Vec3>> rings = new ArrayList<>();
        final double front = frames.subframe().front().x() - 2;
        final double back = SEAT_BACK + 8;
        for (int k = 0; k <= SECTIONS / 2; k++) {
            final double x = front + (back - front) * k * 2 / SECTIONS;
            rings.add(section(x, ground(TANK_TOP), ground(TANK_FOOT), 10, 9));
        }
        return new Part("fuel tank", Group.BODYWORK, false, List.of(new Point("subframe", frames.subframe().front())),
                new Shapes(16).lofted(rings, frames.subframe().front()), new Made(Material.STEEL, true))
                .as(new MotorcycleParts.UnderseatTank());
    }

    /**
     * The side cover on side {@code side}, -1 the left: closing the frame under the seat, over the tank, behind the
     * mid-structure's top.
     */
    Part sideCover(final int side) {
        final double front = frames.subframe().front().x() - 2;
        final double back = SEAT_BACK + 14;
        final List<Spot> outline = List.of(new Spot(front - 3, ground(TANK_FOOT) + 1), new Spot(back,
                ground(TANK_FOOT) + 1), new Spot(back + 3, pan(back) - 0.5), new Spot(front, pan(front) - 0.5));
        final Mesh panel = new Plate(outline, List.of(), new Vec3(0, 0, figures.centre() + side * 16),
                new Vec3(1, 0, 0), new Vec3(0, 1, 0), 0.5).mesh();
        return new Part("side cover, " + (side < 0 ? "left" : "right"), Group.BODYWORK, false,
                List.of(new Point("subframe", frames.subframe().front())), panel, new Made(Material.PLASTIC, false))
                .as(new MotorcycleParts.SideCover());
    }

    /** How high the seat's pan is {@code x} along the bike: on the subframe's rails. */
    private double pan(final double x) {
        return frames.subframe().top(x);
    }

    /** How far along the bike the seat runs, its front and its back. */
    double[] seatRun() {
        return new double[] {SEAT_FRONT, SEAT_BACK};
    }

    /** A rounded section {@code x} along the bike, from {@code foot} up to {@code top}, its top and foot so wide. */
    private List<Vec3> section(final double x, final double top, final double foot, final double wide,
                               final double low) {
        final double c = figures.centre();
        final double round = Math.min(3, (top - foot) / 3);
        return List.of(new Vec3(x, top, c - wide + round), new Vec3(x, top, c + wide - round),
                new Vec3(x, top - round, c + wide), new Vec3(x, foot + round, c + low),
                new Vec3(x, foot, c + low - round), new Vec3(x, foot, c - low + round),
                new Vec3(x, foot + round, c - low), new Vec3(x, top - round, c - wide));
    }

    /** How high {@code over} the ground is, in the engine's frame. */
    private double ground(final double over) {
        return figures.ground() + over;
    }
}
