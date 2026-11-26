package melons.innocent.glm.model;

import melons.innocent.vehiclelab.forms.Spot;

/**
 * A wheel's arch as the side view draws it, in the drawing's millimetres: its middle, along from the front axle and
 * up from the ground; how far round its lip is from its middle; and how far round the lip runs, in degrees from
 * ahead, ninety straight up, from {@code from} to {@code to}.
 *
 * @param along how far along its middle is
 * @param up    how high its middle is
 * @param reach how far round its lip is
 * @param from  where its lip begins, in degrees
 * @param to    where its lip ends, in degrees
 */
record Arch(double along, double up, double reach, double from, double to) {

    /** A spot {@code out} mm beyond its lip, {@code degrees} round, in centimetres made {@code scale} larger. */
    Spot at(final double degrees, final double out, final double scale) {
        final double a = Math.toRadians(degrees);
        return new Spot((along + (reach + out) * Math.cos(a)) * scale / 10,
                (up + (reach + out) * Math.sin(a)) * scale / 10);
    }
}
