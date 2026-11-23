package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A drum: a short round column standing on its end, one unit across and one unit high, its middle at the middle, its
 * top and bottom edges softened by a narrow band that slopes in, so it catches the light along its rims as a moulded
 * thing does. Its side is round in {@code sides} flat faces, lit as if smooth; its ends are flat. Sized and placed
 * as a box one unit each way is, it is drawn wherever a box could be, round where the box was square.
 *
 * @param sides  how many flat faces its side is round in
 * @param softer how wide the softened band at each rim is, of its height, and how far in it slopes, of its width
 */
public record Drum(int sides, double softer) {

    private static final double HALF = 0.5;

    public Drum {
        if (sides < 3 || !(softer >= 0 && softer < HALF)) {
            throw new IllegalArgumentException("a drum is round in three faces or more, its rims softened by less"
                    + " than half of it: " + sides + " faces, " + softer);
        }
    }

    /** A drum round in 24 faces, its rims softened by a tenth. */
    public Drum() {
        this(24, 0.1);
    }

    /** Its faces as triangles: its two ends, its two softened rims and its side. */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final double inner = HALF - softer;
        final double low = -HALF + softer;
        final double high = HALF - softer;
        // the side, then each rim, sloping in from the side to the end; each band its own points, lit its own way
        banded(forming, HALF, low, HALF, high, 0);
        banded(forming, HALF, high, inner, HALF, 1);
        banded(forming, inner, -HALF, HALF, low, -1);
        ended(forming, inner, HALF, 1);
        ended(forming, inner, -HALF, -1);
        return forming.formed();
    }

    /**
     * A band round it from a ring {@code fromRound} across at {@code fromUp} to one {@code toRound} across at
     * {@code toUp}, lit as facing out and as much up as {@code tilt} says (1 up, -1 down, 0 neither).
     */
    private void banded(final Forming forming, final double fromRound, final double fromUp, final double toRound,
                        final double toUp, final int tilt) {
        for (int side = 0; side <= sides; side++) {
            final double round = 2 * Math.PI * side / sides;
            final Vec3 out = new Vec3(Math.cos(round), 0, Math.sin(round));
            final Vec3 facing = out.plus(new Vec3(0, tilt, 0)).unit();
            final int at = forming.point(out.times(fromRound).plus(new Vec3(0, fromUp, 0)), facing);
            forming.point(out.times(toRound).plus(new Vec3(0, toUp, 0)), facing);
            if (side > 0) {
                forming.faced(at - 2, at, at + 1);
                forming.faced(at - 2, at + 1, at - 1);
            }
        }
    }

    /** One flat end, {@code round} across, at {@code up}, facing {@code way} (1 up, -1 down). */
    private void ended(final Forming forming, final double round, final double up, final int way) {
        final Vec3 facing = new Vec3(0, way, 0);
        final int middle = forming.point(new Vec3(0, up, 0), facing);
        for (int side = 0; side <= sides; side++) {
            final double turned = 2 * Math.PI * side / sides;
            forming.point(new Vec3(Math.cos(turned) * round, up, Math.sin(turned) * round), facing);
            if (side > 0) {
                forming.faced(middle, middle + side, middle + side + 1);
            }
        }
    }
}
