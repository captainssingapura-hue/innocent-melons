package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.sim.Curve;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;

/**
 * The body of a plain saloon car, as the graph a building's skeleton is: named vertices and the edges between them,
 * some of them bowed. It is here to show paint on sheet and glass in its windows, and for nothing else: three boxes
 * (bonnet, cabin, boot) with a little crown to each sheet, two windows a side, a windscreen and a rear window, and
 * an arch over each wheel. It has no lamps and no doors that open.
 *
 * <p>Its sizes are the owner's: 4,545 mm long, 1,695 wide and 1,400 high over all, on a wheelbase of 2,550. How
 * those are shared out along it and up it (how long the bonnet is, how high the windows begin, how far the wheels
 * stand in from its ends) is the builder's, measured from no car.
 *
 * <p>In its own frame, the engine's way round: across to its right, along ahead to its nose, up from the ground
 * its wheels stand on, in centimetres, its middle on the ground under the middle of its length.
 *
 * <p>Its vertices, each named with its side but those down the middle: the sill and the belt (where the windows
 * begin) at the nose, the scuttle (the windscreen's foot), the deck (the rear window's foot) and the tail; the two
 * ends of each wheel's arch, on the sill; the roof's four corners; each window's four corners, standing apart from
 * the rest as a building's windows do until a face holds them as holes ({@link CarFaces}); and the two ends of
 * each axle, which are the middles of its wheels ({@link CarWheels}).
 */
public record CarBody() {

    private static final double LONG = 454.5;
    private static final double WHEELBASE = 255;
    /** How far behind the nose the front wheels' middles are. */
    private static final double OVERHANG = 87;
    private static final double NOSE_BOW = 6;
    private static final double TAIL_BOW = 4;
    private static final double SCUTTLE = 98;
    private static final double SCREEN_TOP = 41;
    private static final double REAR_TOP = -83;
    private static final double DECK = -139.5;
    private static final double SILL = 20;
    private static final double BELT = 90;
    private static final double ROOF = 135.8;
    private static final double SILL_WIDE = 82;
    private static final double WIDE = 82;
    private static final double ROOF_WIDE = 66;
    /** How far the flanks are bowed out at their middles: with the belt's width, the car's whole width. */
    private static final double FLANK_BOW = 2.75;
    /** A wheel: how big round, how wide, and how far its middle is from the car's. Its arch is a little bigger. */
    private static final double WHEEL = 30;
    private static final double TYRE_WIDE = 18.5;
    private static final double TRACK = 70.25;
    private static final double ARCH = 34;
    /** How far a window's edges are from the edges of the face it is in, of the face's length and its height. */
    private static final double INSET = 0.1;
    private static final double TOP_INSET = 0.12;

    /** Its skeleton. */
    public Form skeleton() {
        final Form.Builder made = new Form.Builder("car body");
        for (final Side side : Side.values()) {
            side(made, side);
        }
        // across the car, from its left to its right: each bowed, so the sheets between them are crowned
        cross(made, "nose sill", new Vec3(0, NOSE_BOW, 0));
        cross(made, "nose belt", new Vec3(0, NOSE_BOW, 2));
        cross(made, "scuttle", sloping(SCUTTLE - SCREEN_TOP, 3));
        cross(made, "screen top", sloping(SCUTTLE - SCREEN_TOP, 3));
        cross(made, "rear top", sloping(DECK - REAR_TOP, 3));
        cross(made, "deck", sloping(DECK - REAR_TOP, 3));
        cross(made, "tail belt", new Vec3(0, -TAIL_BOW, 1.5));
        cross(made, "tail sill", new Vec3(0, -TAIL_BOW, 0));
        made.edge("front axle, left", "front axle, right").edge("rear axle, left", "rear axle, right");
        window(made, "windscreen", new String[] {"scuttle, left", "scuttle, right", "screen top, right",
                "screen top, left"}, 0.07, 0.93);
        window(made, "rear window", new String[] {"deck, right", "deck, left", "rear top, left",
                "rear top, right"}, 0.07, 0.93);
        return made.build();
    }

    /** How big round a wheel is, from its middle to its tread. */
    public double wheel() {
        return WHEEL;
    }

    /** How wide a wheel's tyre is. */
    public double tyreWide() {
        return TYRE_WIDE;
    }

    /** One side's vertices and the edges that run along it and up it. */
    private void side(final Form.Builder made, final Side side) {
        final double out = side.out();
        final String of = ", " + side.called();
        final double nose = LONG / 2 - NOSE_BOW;
        final double tail = -LONG / 2 + TAIL_BOW;
        made.vertex("nose sill" + of, out * 75, nose, SILL);
        made.vertex("nose belt" + of, out * 75, nose, 70);
        made.vertex("scuttle sill" + of, out * SILL_WIDE, SCUTTLE, SILL);
        made.vertex("scuttle" + of, out * WIDE, SCUTTLE, BELT);
        made.vertex("deck" + of, out * WIDE, DECK, BELT);
        made.vertex("tail sill" + of, out * 77, tail, SILL);
        made.vertex("tail belt" + of, out * 77, tail, 86);
        made.vertex("screen top" + of, out * ROOF_WIDE, SCREEN_TOP, ROOF);
        made.vertex("rear top" + of, out * ROOF_WIDE, REAR_TOP, ROOF);
        final double front = LONG / 2 - OVERHANG;
        arch(made, "front", front, "nose sill" + of, "scuttle sill" + of, side);
        arch(made, "rear", front - WHEELBASE, "scuttle sill" + of, "tail sill" + of, side);
        made.edge("scuttle" + of, "deck" + of);
        made.edge(made.at("nose belt" + of), made.at("scuttle" + of), Curve.bow(new Vec3(0, 0, 1.5), 2));
        made.edge(made.at("deck" + of), made.at("tail belt" + of), Curve.bow(new Vec3(0, 0, 1), 2));
        // the roof's edge, bowed up the cabin's side, which leans in: so the side stays flat and the roof is crowned
        final Vec3 lean = new Vec3(out * (ROOF_WIDE - WIDE), 0, ROOF - BELT).normalize().multiply(2.5);
        made.edge(made.at("screen top" + of), made.at("rear top" + of), Curve.bow(lean, 2));
        // up it: the corners straight, the post at the windscreen's foot bowed out, as the whole flank is between
        // the windscreen's foot and the rear window's: so the flanks are full
        made.edge("nose sill" + of, "nose belt" + of).edge("tail sill" + of, "tail belt" + of);
        made.edge(made.at("scuttle sill" + of), made.at("scuttle" + of),
                Curve.bow(new Vec3(out * FLANK_BOW, 0, 0), 2));
        made.edge("scuttle" + of, "screen top" + of).edge("deck" + of, "rear top" + of);
        final String[] face = {"scuttle" + of, "deck" + of, "rear top" + of, "screen top" + of};
        window(made, "door window" + of, face, INSET, 0.5);
        window(made, "quarter window" + of, face, 0.56, 1 - INSET);
    }

    /**
     * A wheel and the arch over it, on one side: the wheel's middle, {@code along} the car, as one end of its axle;
     * and the sill from the vertex called {@code ahead} to the arch, round over the wheel, and on to the one called
     * {@code behind}. The arch is half a circle about the wheel's middle, a little bigger than the wheel, standing
     * on two short upright legs from the sill, so that it is nowhere wider than at its foot.
     */
    private void arch(final Form.Builder made, final String which, final double along, final String ahead,
                      final String behind, final Side side) {
        final String of = ", " + side.called();
        final Vec3 first = made.position(made.at(ahead));
        final Vec3 last = made.position(made.at(behind));
        final double out = first.x() + (last.x() - first.x()) * (first.y() - along) / (first.y() - last.y());
        made.vertex(which + " axle" + of, side.out() * TRACK, along, WHEEL);
        made.vertex(which + " arch fore" + of, out, along + ARCH, SILL);
        made.vertex(which + " arch fore spring" + of, out, along + ARCH, WHEEL);
        made.vertex(which + " arch aft spring" + of, out, along - ARCH, WHEEL);
        made.vertex(which + " arch aft" + of, out, along - ARCH, SILL);
        made.edge(ahead, which + " arch fore" + of).edge(which + " arch fore" + of, which + " arch fore spring" + of)
                .edge(which + " arch aft spring" + of, which + " arch aft" + of)
                .edge(which + " arch aft" + of, behind);
        made.edge(made.at(which + " arch fore spring" + of), made.at(which + " arch aft spring" + of),
                Curve.arc(new Vec3(0, 0, ARCH)));
    }

    /** An edge across the car between the two vertices called {@code called}, its middle {@code bow} off the line. */
    private void cross(final Form.Builder made, final String called, final Vec3 bow) {
        made.edge(made.at(called + ", left"), made.at(called + ", right"), Curve.bow(bow, 2));
    }

    /** {@code deep} centimetres up a face that rises from a foot {@code back} centimetres ahead of its head. */
    private Vec3 sloping(final double back, final double deep) {
        return new Vec3(0, -back, ROOF - BELT).normalize().multiply(deep);
    }

    /**
     * A window's four corners and the edges round them, in the flat face whose corners are {@code face} (its foot's
     * two ends, then its head's, going round): from {@code from} to {@code to} of the way along the face, and
     * inset from its foot and its head.
     */
    private void window(final Form.Builder made, final String called, final String[] face, final double from,
                        final double to) {
        final int[] corners = {
                within(made, called + ", low first", face, from, INSET),
                within(made, called + ", low second", face, to, INSET),
                within(made, called + ", high second", face, to, 1 - TOP_INSET),
                within(made, called + ", high first", face, from, 1 - TOP_INSET)};
        made.loop(corners);
    }

    /** A vertex {@code along} of the way along a face and {@code up} of the way up it, between its four corners. */
    private int within(final Form.Builder made, final String called, final String[] face, final double along,
                       final double up) {
        final Vec3 foot = between(made.position(made.at(face[0])), made.position(made.at(face[1])), along);
        final Vec3 head = between(made.position(made.at(face[3])), made.position(made.at(face[2])), along);
        final Vec3 at = between(foot, head, up);
        return made.vertex(called, at.x(), at.y(), at.z());
    }

    private Vec3 between(final Vec3 one, final Vec3 other, final double share) {
        return one.add(other.subtract(one).multiply(share));
    }

    /** A side of the car: which way is out from its middle, and what the side is called. */
    public enum Side {
        LEFT(-1), RIGHT(1);

        private final double out;

        Side(final double out) {
            this.out = out;
        }

        /** Out from the car's middle, toward this side: -1 or 1 across. */
        public double out() {
            return out;
        }

        /** What the side's vertices are named with. */
        public String called() {
            return name().toLowerCase();
        }
    }
}
