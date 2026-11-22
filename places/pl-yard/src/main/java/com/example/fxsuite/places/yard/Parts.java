package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.texture.materials.Materials;

import java.util.List;

/**
 * The parts of a small car, each by itself and none of them put together: a frame of steel tube; the two arms of a
 * double wishbone, lower and upper; the upright the arms carry between them; a damper; a wheel's rim; and its tyre.
 * Each is drawn as a graph in its own frame and is of steel, but for the tyre, which is rubber. The same arms,
 * upright, damper and wheel are meant for all four corners, front and rear; how they are joined to the frame, and
 * how they move, comes later.
 *
 * <p>The sizes are a first guess at a light buggy, to be put right in a vehicle lab: none of them is measured from
 * a car.
 */
public record Parts() {

    /** How far each rail of the frame is from its middle line, and where its tubes stand, in centimetres. */
    private static final double RAIL = 35;
    private static final double LOW = 26;
    private static final double HIGH = 50;
    private static final double TOWER = 66;
    /** How many straight pieces a hoop is drawn in, and how big a rim and a tyre are round. */
    private static final int PIECES = 24;
    private static final double RIM = 20;
    private static final double TYRE = 27;

    /** Every part, the frame first. */
    public List<Part> all() {
        return List.of(frame(), lowerWishbone(), upperWishbone(), upright(), damper(), rim(), tyre());
    }

    /**
     * The frame: two lower rails and two upper, joined by posts where the wishbones will pivot and by members across;
     * and over each axle, either side, a tower for a damper's top. 3.3 m long, with its axles 2.6 m apart.
     */
    public Part frame() {
        final Form.Builder frame = new Form.Builder("frame");
        final double[] low = {-165, -150, -110, 110, 150, 165};
        final double[] high = {-150, -110, 110, 150};
        for (final String side : List.of("left", "right")) {
            final double across = side.equals("left") ? -RAIL : RAIL;
            for (int at = 0; at < low.length; at++) {
                frame.vertex(side + "-low-" + at, across, low[at], LOW);
            }
            for (int at = 0; at < high.length; at++) {
                frame.vertex(side + "-high-" + at, across, high[at], HIGH);
                frame.edge(side + "-low-" + (at + 1), side + "-high-" + at);
            }
            for (int at = 0; at + 1 < low.length; at++) {
                frame.edge(side + "-low-" + at, side + "-low-" + (at + 1));
            }
            for (int at = 0; at + 1 < high.length; at++) {
                frame.edge(side + "-high-" + at, side + "-high-" + (at + 1));
            }
            // a tower over each axle, a little outside the rail, on two legs from the upper rail
            frame.vertex(side + "-tower-rear", across + Math.signum(across) * 6, -130, TOWER);
            frame.vertex(side + "-tower-fore", across + Math.signum(across) * 6, 130, TOWER);
            frame.edge(side + "-tower-rear", side + "-high-0").edge(side + "-tower-rear", side + "-high-1");
            frame.edge(side + "-tower-fore", side + "-high-2").edge(side + "-tower-fore", side + "-high-3");
        }
        for (int at = 0; at < low.length; at++) {
            frame.edge("left-low-" + at, "right-low-" + at);
        }
        for (int at = 0; at < high.length; at++) {
            frame.edge("left-high-" + at, "right-high-" + at);
        }
        return new Part("frame", frame.build(), 2.5, new Materials.Steel());
    }

    /**
     * The lower arm: two legs from its pivots on the frame to the ball the upright stands on, braced across past the
     * middle, with the damper's foot in the middle of the brace. 40 cm from pivots to ball, its pivots 40 cm apart.
     */
    public Part lowerWishbone() {
        final Form.Builder arm = new Form.Builder("lower-wishbone");
        arm.vertex("pivot-fore", 0, 20, 0);
        arm.vertex("pivot-rear", 0, -20, 0);
        arm.vertex("leg-fore", 24, 8, 0);
        arm.vertex("leg-rear", 24, -8, 0);
        arm.vertex("ball", 40, 0, 0);
        arm.vertex("foot", 24, 0, 0);
        arm.edge("pivot-fore", "leg-fore").edge("leg-fore", "ball");
        arm.edge("pivot-rear", "leg-rear").edge("leg-rear", "ball");
        arm.edge("leg-fore", "foot").edge("foot", "leg-rear");
        return new Part("lower-wishbone", arm.build(), 1.6, new Materials.Steel());
    }

    /** The upper arm: two legs from its pivots to the ball the upright hangs from; shorter than the lower, 33 cm. */
    public Part upperWishbone() {
        final Form.Builder arm = new Form.Builder("upper-wishbone");
        arm.vertex("pivot-fore", 0, 20, 0);
        arm.vertex("pivot-rear", 0, -20, 0);
        arm.vertex("ball", 33, 0, 0);
        arm.edge("pivot-fore", "ball").edge("pivot-rear", "ball");
        return new Part("upper-wishbone", arm.build(), 1.6, new Materials.Steel());
    }

    /**
     * The upright: from the lower arm's ball up to the upper's, 26 cm above and a little inboard, bent out at the
     * hub between them, with the stub the wheel turns on standing out from the hub.
     */
    public Part upright() {
        final Form.Builder upright = new Form.Builder("upright");
        upright.vertex("low", 0, 0, 0);
        upright.vertex("hub", 3, 0, 12);
        upright.vertex("high", -7, 0, 26);
        upright.vertex("axle", 10, 0, 12);
        upright.edge("low", "hub").edge("hub", "high").edge("hub", "axle");
        return new Part("upright", upright.build(), 2.2, new Materials.Steel());
    }

    /** The damper: a strut half a metre long, from its foot on the lower arm to its top on the frame's tower. */
    public Part damper() {
        final Form.Builder damper = new Form.Builder("damper");
        damper.vertex("foot", 0, 0, 0);
        damper.vertex("top", 0, 0, 50);
        damper.edge("foot", "top");
        return new Part("damper", damper.build(), 2.6, new Materials.Steel());
    }

    /**
     * The rim: two hoops 40 cm across and 12 cm apart, joined by bars across the barrel, with six spokes from its
     * middle to the outer hoop. It turns about the line across it.
     */
    public Part rim() {
        final Form.Builder rim = new Form.Builder("rim");
        final int middle = rim.vertex("middle", 0, 0, 0);
        final int[] inner = hoop(rim, "inner", -6, RIM);
        final int[] outer = hoop(rim, "outer", 6, RIM);
        for (int piece = 0; piece < PIECES; piece += 3) {
            rim.edge(inner[piece], outer[piece]);
        }
        for (int piece = 0; piece < PIECES; piece += 4) {
            rim.edge(middle, outer[piece]);
        }
        return new Part("rim", rim.build(), 1, new Materials.Steel());
    }

    /** The tyre: one hoop of rubber 12 cm thick, 66 cm across over all, its inside as big as the rim's outside. */
    public Part tyre() {
        final Form.Builder tyre = new Form.Builder("tyre");
        hoop(tyre, "tread", 0, TYRE);
        return new Part("tyre", tyre.build(), 6, new Materials.Rubber());
    }

    /**
     * A hoop added to {@code shape}: so many points called {@code named} and a number, {@code across} along the line
     * the hoop turns about and {@code out} from it, joined round. The points' numbers, in order.
     */
    private int[] hoop(final Form.Builder shape, final String named, final double across, final double out) {
        final int[] points = new int[PIECES];
        for (int piece = 0; piece < PIECES; piece++) {
            final double turn = 2 * Math.PI * piece / PIECES;
            points[piece] = shape.vertex(named + "-" + piece, across, out * Math.cos(turn), out * Math.sin(turn));
        }
        shape.loop(points);
        return points;
    }
}
