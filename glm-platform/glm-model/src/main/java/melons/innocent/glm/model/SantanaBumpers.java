package melons.innocent.glm.model;

/**
 * The retro saloon's bumpers, each one deep moulding, the bar and the cover under it in one piece, as the maker's
 * square-on side photographs show them, scaled by their wheelbases and heights to the drawing's, in millimetres along
 * from the front axle and up from the ground, then made larger as the car's lines are: the front bumper's face 928 mm
 * ahead of the axle, some 12 cm ahead of the wings, from 300 mm up to 548 mm, its chrome strip at 527 mm, wrapped back
 * along the sides to clear the front arch where it is widest; the rear bumper's face 3,554 mm behind it, some 9 cm
 * behind the tail panel, from 350 mm up to 578 mm, its strip at 541 mm, wrapped forward along the sides to clear the
 * rear arch; each standing a little proud of the body's sides, round its corners.
 *
 * @param lines the car's lines
 */
record SantanaBumpers(SantanaLines lines) {

    /** How far proud of the body's sides they stand, and how round their corners are, in centimetres. */
    private static final double PROUD = 1.5;
    private static final double ROUND = 18;

    /** The front bumper. */
    Bumper front() {
        final double mid = mm((300 + 548) / 2.0);
        return new Bumper("front bumper", lines.half(mm(600), mid) + PROUD, mm(410), mm(928), ROUND, mm(300),
                mm(548), mm(527));
    }

    /** The rear bumper. */
    Bumper rear() {
        final double mid = mm((350 + 578) / 2.0);
        return new Bumper("rear bumper", lines.half(mm(-3_100), mid) + PROUD, mm(-2_950), mm(-3_554), ROUND,
                mm(350), mm(578), mm(541));
    }

    /** So many of the drawing's millimetres, in the platform's centimetres, made larger. */
    private double mm(final double millimetres) {
        return millimetres * lines.scale() / 10;
    }
}
