package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * How a bank's cylinder head is laid out, after the engines it is after, four valves to a cylinder worked by two
 * camshafts over them, all as shares of the bore, estimates: over each bore a pent roof, its two planes falling either
 * side of a ridge along the bank at the valves' angle, 21 degrees, two inlet valves in one plane and two exhaust in
 * the other, each square to its plane, its seat a little in from the bore; an inlet valve 0.39 of the bore across, an
 * exhaust 0.33; each valve 1.2 bores long, its camshaft over its tip; the spark plug in the ridge's middle. The inlets
 * come in from the bank's outer side, the exhausts go out the other: in a V, into the V, where the turbochargers
 * sit; in a boxer, whose outer side is up, the inlets from above and the exhausts out underneath.
 *
 * <p>Laid out in a cylinder's own frame: across, toward the inlet side; up the bore from its deck; and along the bank,
 * from its middle. {@link #at} carries that to the block's frame.
 *
 * @param figures the engine's figures
 */
record HeadFigures(EngineFigures figures) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** The valves' angle from the bore, in degrees; how far the ridge stands over the roof where it meets the bore. */
    private static final double ANGLE = 21;
    private static final double RIDGE = 0.3;
    /** How far a camshaft's axis is past its valve's tip: the bucket and the cam's base circle. */
    private static final double OVER = 2.6;
    /** How thick its fire deck and its walls are cast. */
    static final double DECK = 0.8;
    static final double WALL = 0.5;

    /** How far round a bore is. */
    double radius() {
        return figures.bore() / 2;
    }

    /** How high the roof's ridge stands over the deck. */
    double ridge() {
        return radius() * slope() + RIDGE;
    }

    /** How high the roof stands over the deck {@code u} across from the bore's axis. */
    double roof(final double u) {
        return ridge() - Math.abs(u) * slope();
    }

    /** How far across a valve's head is: an inlet's, or an exhaust's. */
    double valve(final boolean inlet) {
        return figures.bore() * (inlet ? 0.39 : 0.33);
    }

    /** How far apart along the bank a cylinder's two valves of a kind are, each side of its middle. */
    double apart() {
        return figures.bore() * 0.21;
    }

    /** Where a valve's seat is in its cylinder's frame: an inlet's or an exhaust's, ahead ({@code side} -1) or not. */
    Vec3 seat(final boolean inlet, final int side) {
        final double u = (inlet ? 1 : -1) * figures.bore() * 0.19;
        return new Vec3(u, roof(u), side * apart());
    }

    /** The way a valve opens down its stem, up from its seat to its tip, in its cylinder's frame. */
    Vec3 axis(final boolean inlet) {
        final double a = Math.toRadians(ANGLE);
        return new Vec3((inlet ? 1 : -1) * Math.sin(a), Math.cos(a), 0);
    }

    /** How long a valve is, from its head to its tip. */
    double stem() {
        return figures.bore() * 1.2;
    }

    /** Where a camshaft's axis is in a cylinder's frame, the inlets' or the exhausts', at the cylinder's middle. */
    Vec3 cam(final boolean inlet) {
        final Vec3 over = seat(inlet, 0).plus(axis(inlet).times(stem() + OVER));
        return new Vec3(over.x(), over.y(), 0);
    }

    /** How far round a camshaft's journal is. */
    double journal() {
        return figures.bore() * 0.17;
    }

    /** How far across from the bores' axis its side is, the inlets' or the exhausts': past its camshaft's journal. */
    double side(final boolean inlet) {
        return Math.abs(cam(inlet).x()) + journal() + 0.9;
    }

    /** How high its walls stand over the deck: to its camshafts' axes. */
    double high() {
        return Math.max(cam(true).y(), cam(false).y());
    }

    /** The middle of bank {@code bank}'s head's top, where its walls stop, in the block's frame. */
    Vec3 top(final int bank) {
        return at(bank, 0, new Vec3(0, high(), (front(bank) + rear(bank)) / 2 - figures.along(bank, 0)));
    }

    /** Where along the crank its front and its rear are, bank {@code bank}'s. */
    double front(final int bank) {
        return figures.along(bank, 0) - figures.margin();
    }

    double rear(final int bank) {
        return figures.along(bank, figures.perBank() - 1) + figures.margin();
    }

    /** What carries bank {@code bank}'s cylinder {@code k}'s frame to the block's, its nought at its bore's top. */
    Mat4 frame(final int bank, final int k) {
        final BlockForm block = new BlockForm(figures);
        final Vec3 u = block.out(bank);
        final Vec3 v = block.up(bank);
        final Vec3 o = v.times(figures.deck()).plus(ALONG.times(figures.along(bank, k)));
        return new Mat4(new double[] {u.x(), v.x(), 0, o.x(), u.y(), v.y(), 0, o.y(), u.z(), v.z(), 1, o.z(),
            0, 0, 0, 1});
    }

    /** Where {@code local}, in bank {@code bank}'s cylinder {@code k}'s frame, is in the block's. */
    Vec3 at(final int bank, final int k, final Vec3 local) {
        final Mat4 m = frame(bank, k);
        return new Vec3(m.at(0, 0) * local.x() + m.at(0, 1) * local.y() + m.at(0, 2) * local.z() + m.at(0, 3),
                m.at(1, 0) * local.x() + m.at(1, 1) * local.y() + m.at(1, 2) * local.z() + m.at(1, 3),
                m.at(2, 0) * local.x() + m.at(2, 1) * local.y() + m.at(2, 2) * local.z() + m.at(2, 3));
    }

    /** The way {@code local} points, in bank {@code bank}'s frame, in the block's. */
    Vec3 way(final int bank, final Vec3 local) {
        return at(bank, 0, local).minus(at(bank, 0, new Vec3(0, 0, 0)));
    }

    private double slope() {
        return Math.tan(Math.toRadians(ANGLE));
    }
}
