package melons.innocent.glm.model;

/**
 * Where the retro saloon's steel shell runs, in the platform's centimetres: its floor, level with the platform's,
 * from the bulkhead at the windscreen pillars' foot back to the rear seat's heel; the rear seat's pan over it, the
 * rear floor stepped up over the rear axle and down again behind it to the boot's floor; its inner sills along each
 * side; its wheel arches round each tyre, standing off it as far as the platform's do; the rear wheelhouses' inner
 * walls just inside the rear tyres; the scuttle under the windscreen; and the tail panel at the tail.
 *
 * @param lines       the car's lines
 * @param proportions the platform's figures
 */
record ShellLines(SantanaLines lines, Proportions proportions) {

    /** How far a wheel arch stands off its tyre, and its wall inside it. */
    private static final double ARCH_OFF = 4;
    private static final double WALL_OFF = 0.6;

    /** How high its floor is. */
    double floor() {
        return 24;
    }

    /** How far out its inner sills' middles are. */
    double sill() {
        return 72;
    }

    /** How far out the tunnel's walls are; how high its top is. */
    double tunnelX() {
        return 13;
    }

    double tunnelY() {
        return 42;
    }

    /** Where the bulkhead stands, just behind the windscreen pillars' foot. */
    double bulkhead() {
        return lines.side().getFirst().u() - 1.6;
    }

    /** How high the scuttle under the windscreen is. */
    double scuttle() {
        return 100;
    }

    /** Where the rear seat's heel is; how high its pan; where its back's wall stands. */
    double heel() {
        return -195;
    }

    double seat() {
        return 38;
    }

    double seatWall() {
        return arch(proportions.back()) + 1.5;
    }

    /** How high the floor over the rear axle is, and where it steps down behind it. */
    double overAxle() {
        return 67;
    }

    double step() {
        return proportions.back() - 23.5;
    }

    /** How high the boot's floor is; where the tail panel stands, just inside the body sides' tail. */
    double boot() {
        return lines.boot();
    }

    double tail() {
        return lines.tail() + 0.3;
    }

    /** How far round a wheel arch is from its axle. */
    double reach() {
        return proportions.tyre() + ARCH_OFF;
    }

    /** Where an arch round the axle at {@code axle} begins, ahead of it. */
    double arch(final double axle) {
        return axle + reach();
    }

    /** How far out a rear wheelhouse's inner wall is, just inside the tyre. */
    double wall() {
        return proportions.halfTrack() - proportions.width() * 0.1 / 2 - WALL_OFF;
    }

    /** How far out the body sides' outer faces are, {@code along} and {@code up}. */
    double half(final double along, final double up) {
        return lines.half(along, up);
    }
}
