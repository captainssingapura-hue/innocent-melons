package melons.innocent.doors.swing;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * What a door hangs in and on: for a door with a frame, a jamb each side and a head over it, of the door's own
 * wood, their face flush with the leaf's on the side it opens toward and reaching back through the wall from
 * there; and three hinges down the hinge edge, their knuckles standing just proud on that side. A sheet of glass on
 * patches has neither. None of it moves. The sizes are a joiner's usual ones, and Claude's.
 *
 * @param hanging how the leaf hangs
 */
record Framing(Hanging hanging) {

    /** How wide a jamb's face is, how deep the frame is through the wall, and the gap it leaves round the leaf. */
    private static final double JAMB = 6;
    private static final double DEEP = 12;
    private static final double GAP = 0.3;
    /** How high a hinge's knuckle is, how thick, and how far in from the leaf's head and foot the outer two are. */
    private static final double KNUCKLE = 10;
    private static final double PIN = 1.8;
    private static final double INSET = 22;

    /** How wide a jamb's face is. */
    double jamb() {
        return JAMB;
    }

    /** How deep the frame is, through the wall. */
    double deep() {
        return DEEP;
    }

    /** How far to either side of the doorway's middle a jamb's own middle is. */
    double aside() {
        return hanging.size().wide() / 2 + GAP + JAMB / 2;
    }

    /** How high over the floor the underside of the head is. */
    double head() {
        return hanging.clears() + hanging.size().high() + GAP;
    }

    /** How far through the wall the frame's middle is from where the leaf's is. */
    double back() {
        return hanging.swing().side() * (hanging.size().thick() - DEEP) / 2;
    }

    /** The frame of {@code wood} and the hinges of {@code fittings}, added to {@code boxes}. */
    void framed(final Boxes boxes, final Wood wood, final Fittings fittings) {
        for (final int side : new int[] {-1, 1}) {
            boxes.box(wood.leaf(), wood.tint(), Moves.FIXED, new Vec3(side * aside(), head() / 2, back()), JAMB,
                    head(), DEEP);
        }
        boxes.box(wood.leaf(), wood.tint(), Moves.FIXED, new Vec3(0, head() + JAMB / 2, back()),
                2 * aside() + JAMB, JAMB, DEEP);
        final Vec3 hinge = hanging.hinge();
        final double high = hanging.size().high();
        for (final double up : new double[] {INSET, high / 2, high - INSET}) {
            boxes.box(fittings.leaf(), new Vec3(1, 1, 1), Moves.FIXED, new Vec3(hinge.x(),
                    hanging.clears() + up, hinge.z() + hanging.swing().side() * PIN / 2), PIN, KNUCKLE, PIN);
        }
    }
}
