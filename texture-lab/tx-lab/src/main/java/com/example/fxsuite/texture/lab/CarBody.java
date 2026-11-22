package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.sim.Curve;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;

/**
 * The body of a plain saloon car, as the graph a building's skeleton is: named vertices and the edges between them,
 * some of them bowed. It is here to show paint on sheet and glass in its windows, and for nothing else: three boxes
 * (bonnet, cabin, boot) with a little crown to each sheet, two windows a side, a windscreen and a rear window. It
 * has no wheels or arches, no lamps, no doors that open, and is measured from no car; it stands on its sills.
 *
 * <p>In its own frame, the engine's way round: across to its right, along ahead to its nose, up from the ground,
 * in centimetres, its middle on the ground under the middle of its length. It is 4.4 m long and 1.64 m wide.
 *
 * <p>Its vertices, each named with its side but those down the middle: the sill and the belt (where the windows
 * begin) at the nose, the scuttle (the windscreen's foot), the deck (the rear window's foot) and the tail; the
 * roof's four corners; and each window's four corners, standing apart from the rest as a building's windows do
 * until a face holds them as holes ({@link CarFaces}).
 */
public record CarBody() {

    private static final double NOSE = 214;
    private static final double SCUTTLE = 95;
    private static final double SCREEN_TOP = 40;
    private static final double REAR_TOP = -80;
    private static final double DECK = -135;
    private static final double TAIL = -216;
    private static final double BELT = 72;
    private static final double ROOF = 122;
    private static final double WIDE = 82;
    private static final double ROOF_WIDE = 64;
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
        cross(made, "nose sill", new Vec3(0, 6, 0));
        cross(made, "nose belt", new Vec3(0, 6, 2));
        cross(made, "scuttle", sloping(SCUTTLE - SCREEN_TOP, 3));
        cross(made, "screen top", sloping(SCUTTLE - SCREEN_TOP, 3));
        cross(made, "rear top", sloping(DECK - REAR_TOP, 3));
        cross(made, "deck", sloping(DECK - REAR_TOP, 3));
        cross(made, "tail belt", new Vec3(0, -4, 1.5));
        cross(made, "tail sill", new Vec3(0, -4, 0));
        window(made, "windscreen", new String[] {"scuttle, left", "scuttle, right", "screen top, right",
                "screen top, left"}, 0.07, 0.93);
        window(made, "rear window", new String[] {"deck, right", "deck, left", "rear top, left",
                "rear top, right"}, 0.07, 0.93);
        return made.build();
    }

    /** One side's vertices and the edges that run along it and up it. */
    private void side(final Form.Builder made, final Side side) {
        final double out = side.out();
        final String of = ", " + side.called();
        made.vertex("nose sill" + of, out * 74, NOSE, 0);
        made.vertex("nose belt" + of, out * 74, NOSE, 58);
        made.vertex("scuttle sill" + of, out * 80, SCUTTLE, 0);
        made.vertex("scuttle" + of, out * WIDE, SCUTTLE, BELT);
        made.vertex("deck sill" + of, out * 80, DECK, 0);
        made.vertex("deck" + of, out * WIDE, DECK, BELT);
        made.vertex("tail sill" + of, out * 76, TAIL, 0);
        made.vertex("tail belt" + of, out * 76, TAIL, 68);
        made.vertex("screen top" + of, out * ROOF_WIDE, SCREEN_TOP, ROOF);
        made.vertex("rear top" + of, out * ROOF_WIDE, REAR_TOP, ROOF);
        // along it, low and high
        made.edge("nose sill" + of, "scuttle sill" + of).edge("scuttle sill" + of, "deck sill" + of)
                .edge("deck sill" + of, "tail sill" + of).edge("scuttle" + of, "deck" + of);
        made.edge(made.at("nose belt" + of), made.at("scuttle" + of), Curve.bow(new Vec3(0, 0, 1.5), 2));
        made.edge(made.at("deck" + of), made.at("tail belt" + of), Curve.bow(new Vec3(0, 0, 1), 2));
        // the roof's edge, bowed up the cabin's side, which leans in: so the side stays flat and the roof is crowned
        final Vec3 lean = new Vec3(out * (ROOF_WIDE - WIDE), 0, ROOF - BELT).normalize().multiply(2.5);
        made.edge(made.at("screen top" + of), made.at("rear top" + of), Curve.bow(lean, 2));
        // up it: the corners straight, the posts at the doors' ends bowed out, so the flanks are full
        made.edge("nose sill" + of, "nose belt" + of).edge("tail sill" + of, "tail belt" + of);
        made.edge(made.at("scuttle sill" + of), made.at("scuttle" + of), Curve.bow(new Vec3(out * 3, 0, 0), 2));
        made.edge(made.at("deck sill" + of), made.at("deck" + of), Curve.bow(new Vec3(out * 3, 0, 0), 2));
        made.edge("scuttle" + of, "screen top" + of).edge("deck" + of, "rear top" + of);
        final String[] face = {"scuttle" + of, "deck" + of, "rear top" + of, "screen top" + of};
        window(made, "door window" + of, face, INSET, 0.5);
        window(made, "quarter window" + of, face, 0.56, 1 - INSET);
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
