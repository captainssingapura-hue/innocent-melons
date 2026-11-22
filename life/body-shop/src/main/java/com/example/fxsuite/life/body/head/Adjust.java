package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;

/**
 * How a piece of a head is adjusted from the reference's, as a sculptor works a piece of clay already on: moved, cm;
 * tilted up, degrees, its tip rising; turned out, degrees, its back going out from the head; made bigger all round, so
 * many times; stretched across, up and ahead, so many times more; made fuller, cm all round. A left piece adjusted as
 * the right one is, seen in a mirror.
 *
 * @param shift    how far it is moved: out from the middle, up, ahead, cm
 * @param tilt     how far its tip is tilted up, degrees
 * @param turn     how far its back is turned out, degrees
 * @param size     how many times as big it is made
 * @param stretch  how many times further across, up and ahead it is stretched, after it is made bigger
 * @param fullness how much fuller it is made, cm all round
 */
public record Adjust(V3 shift, double tilt, double turn, double size, V3 stretch, double fullness) {

    /** Whether it leaves the piece as it was. */
    public boolean plain() {
        return shift.length() == 0 && tilt == 0 && turn == 0 && size == 1 && stretch.equals(new V3(1, 1, 1))
                && fullness == 0;
    }

    /** {@code s} adjusted about {@code anchor}, a point at its root; seen in a mirror, if {@code mirrored}. */
    public Field.Shape on(final Field.Shape s, final V3 anchor, final boolean mirrored) {
        if (plain()) {
            return s;
        }
        final Rot right = Rot.aboutY(-turn).then(Rot.aboutX(-tilt));
        final V3 scale = stretch.times(size);
        return new Reshaped(s, anchor, mirrored ? new V3(-shift.x(), shift.y(), shift.z()) : shift,
                mirrored ? right.mirrored() : right, scale, fullness);
    }
}
