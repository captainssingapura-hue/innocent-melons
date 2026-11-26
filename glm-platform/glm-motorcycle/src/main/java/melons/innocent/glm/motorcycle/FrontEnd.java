package melons.innocent.glm.motorcycle;

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
 * A motorcycle's front end, as its figures place it ({@link BikeFigures}): the steering head, a tube on its steering
 * axis between its yokes, which the frame is welded to; the bottom and top yokes, cast in aluminium, each clamping
 * both the fork's legs ahead of the steering axis by its offset and turning on its stem in the head; and the fork's
 * two legs, telescopic, 41 mm, each its slider at the foot round the front axle and its tube up through both yokes.
 *
 * @param figures the bike's figures
 */
record FrontEnd(BikeFigures figures) {

    private static final int ROUND = 24;
    /** How far round the steering head is; how thick a yoke is; how far a yoke reaches past each leg. */
    private static final double HEAD = 2.6;
    private static final double YOKE = 3.5;
    private static final double PAST = 3.2;
    /** How far round a fork's tube is, and its slider; how far up the slider reaches from the axle. */
    private static final double TUBE = 2.05;
    private static final double SLIDER = 2.75;
    private static final double SLIDES = 30;

    /** The steering head: a tube on the steering axis, from just over the bottom yoke to just under the top. */
    Part head() {
        final Vec3 low = figures.stem(BikeFigures.LOW_YOKE + YOKE / 2 + 0.3);
        final Vec3 high = figures.stem(BikeFigures.HIGH_YOKE - YOKE / 2 - 0.3);
        return new Part("steering head", Group.FRAME, false, List.of(new Point("bottom", low),
                new Point("top", high), new Point("middle", low.plus(high).times(0.5))),
                new Shapes(ROUND).drum(low, high, HEAD), new Made(Material.STEEL, false))
                .as(new MotorcycleParts.SteeringHead());
    }

    /** The bottom yoke, or the top: clamping both legs, on its stem in the steering head. */
    Part yoke(final boolean top) {
        final double along = top ? BikeFigures.HIGH_YOKE : BikeFigures.LOW_YOKE;
        final Vec3 stem = figures.stem(along);
        final double half = BikeFigures.LEGS / 2;
        final double reach = figures.offset() + PAST;
        final Outlines outlines = new Outlines(ROUND);
        final List<Spot> outline = new ArrayList<>(outlines.circle(new Spot(reach - PAST, -half), PAST));
        outline.addAll(outlines.circle(new Spot(reach - PAST, half), PAST));
        outline.addAll(outlines.circle(new Spot(0, 0), PAST));
        final List<List<Spot>> holes = List.of(outlines.circle(new Spot(reach - PAST, -half), TUBE + 0.05),
                outlines.circle(new Spot(reach - PAST, half), TUBE + 0.05));
        final Vec3 end = figures.stem(along + (top ? -YOKE / 2 - 0.3 : YOKE / 2 + 0.3));
        final Shapes shapes = new Shapes(ROUND);
        return new Part((top ? "top" : "bottom") + " yoke", Group.STEERING, false, List.of(new Point("head", end),
                new Point("leg, left", leg(along, -1)), new Point("leg, right", leg(along, 1))),
                shapes.joined(List.of(new Plate(new Hull(outline).outline(), holes,
                        stem, figures.ahead(), new Vec3(0, 0, 1), YOKE).mesh(), shapes.drum(stem, end, 1.6))),
                new Made(Material.ALUMINIUM, false))
                .as(top ? new MotorcycleParts.TopYoke() : new MotorcycleParts.BottomYoke());
    }

    /** The fork's leg on side {@code side}, -1 the left: its slider round the axle, its tube up through the yokes. */
    Part leg(final int side) {
        final Shapes shapes = new Shapes(ROUND);
        final Vec3 foot = leg(-3, side);
        return new Part("fork leg, " + (side < 0 ? "left" : "right"), Group.DAMPERS, false, List.of(
                new Point("yoke", leg(BikeFigures.LOW_YOKE, side)), new Point("axle", leg(0, side))),
                shapes.joined(List.of(shapes.drum(foot, leg(SLIDES, side), SLIDER),
                        shapes.drum(leg(SLIDES - 2, side), leg(BikeFigures.HIGH_YOKE + YOKE, side), TUBE))),
                new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.ForkLeg());
    }

    /** The place {@code along} up the fork's leg on side {@code side}, -1 the left. */
    Vec3 leg(final double along, final int side) {
        return figures.leg(along).plus(new Vec3(0, 0, side * BikeFigures.LEGS / 2));
    }
}
