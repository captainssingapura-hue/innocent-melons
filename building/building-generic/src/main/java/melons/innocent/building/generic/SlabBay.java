package melons.innocent.building.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * The pillars of a bay of a corridor-access slab block's structure, from one line of pillars across it to the next:
 * after the housing board's plan of one of its flats, 10.2 m across between its party lines and 10.6 m deep to its
 * access balcony. Its structure only the pillars black on that plan, measured off the drawing at 17.9 mm a pixel across
 * and 18.1 mm down, its skew taken out, to within some 5 cm: blades 30 cm thick on four lines across it, its two edges,
 * each pillar on them shared with the bay next door, and two between, 3.6 m and 6.6 m across, where the flat's rooms
 * part; on each line a blade some 1.3 m long 1.8 m in from its back and another at its face on the balcony, and small
 * ones in its back wall. Its back taken at its back wall, the bathroom's 35 cm jut left out: 10.25 m deep. How it is
 * parted into flats, and their inner walls, are not its to say. In centimetres, its own way: across from its left edge,
 * along from its back to its face on the balcony.
 *
 * <p>Asked to be wider or narrower, or deeper or shallower, than the board's, its pillars keep their sizes: across,
 * where they stand is stretched to its width; along, those in its back half keep how far they are from its back, those
 * in its front half how far from its face. Mirrored, what is on its left is on its right: next door's mirror of it, so
 * the two lie bathroom to bathroom and the pillars on the line between them are the same.
 */
final class SlabBay {

    /** How wide and deep the board's is. */
    static final double WIDE = 1_020;
    static final double DEEP = 1_025;
    /** How thick its pillars are. */
    static final double PILLAR = 30;

    /** Its pillars between its edges. */
    private static final List<Box> INSIDE = List.of(
            new Box(165, 0, 195, 48), new Box(165, 183, 195, 220),
            new Box(345, 0, 375, 32), new Box(345, 182, 375, 310), new Box(345, 895, 375, 1_025),
            new Box(645, 0, 675, 30), new Box(645, 182, 675, 308), new Box(645, 895, 675, 1_025),
            new Box(855, 0, 885, 30));
    /** Along its left edge, where its pillars are, from and to. */
    private static final List<double[]> LEFT = List.of(new double[] {182, 310}, new double[] {895, 1_025});
    /** Along its right edge. */
    private static final List<double[]> RIGHT = List.of(new double[] {0, 60}, new double[] {180, 348},
            new double[] {870, 1_025});

    /**
     * Its pillars between its edges, it lying from {@code left} across, {@code wide}, its back at {@code back}
     * along, {@code deep}; mirrored or not.
     */
    List<Box> inside(final double left, final double wide, final double back, final double deep,
                     final boolean mirrored) {
        final List<Box> pillars = new ArrayList<>();
        for (final Box p : INSIDE) {
            final double half = (p.x1() - p.x0()) / 2;
            final double middle = (p.x0() + p.x1()) / 2 * wide / WIDE;
            final double at = left + (mirrored ? wide - middle : middle);
            pillars.add(new Box(at - half, back + along(p.z0(), p.z1(), deep)[0], at + half,
                    back + along(p.z0(), p.z1(), deep)[1]));
        }
        return pillars;
    }

    /**
     * The pillars on a line {@code across}, between bays whose back is at {@code back}, {@code deep}: as on a
     * bay's left edge if {@code leftHand}, as on its right if not.
     */
    List<Box> line(final double across, final double back, final double deep, final boolean leftHand) {
        final List<Box> pillars = new ArrayList<>();
        for (final double[] p : leftHand ? LEFT : RIGHT) {
            final double[] at = along(p[0], p[1], deep);
            pillars.add(new Box(across - PILLAR / 2, back + at[0], across + PILLAR / 2, back + at[1]));
        }
        return pillars;
    }

    /** Where a pillar from {@code from} to {@code to} along the board's is, along one {@code deep}. */
    private double[] along(final double from, final double to, final double deep) {
        final double shift = (from + to) / 2 < DEEP / 2 ? 0 : deep - DEEP;
        return new double[] {from + shift, to + shift};
    }
}
