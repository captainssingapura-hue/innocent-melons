package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.worked.Picture;

import java.util.Locale;

/**
 * How dark a picture comes out, the left half and the right half apart: one less the mean brightness of their
 * pixels, each its red, green and blue weighted as the eye weighs them. Nothing, for white all over; one, for black.
 * With the same as grown on the left and the stage on show on the right, the two say whether the stage darkens what
 * is behind it as much as the trees it stands for do.
 *
 * @param picture what was drawn
 */
public record Darkness(Picture picture) {

    private static final double RED = 0.2126;
    private static final double GREEN = 0.7152;
    private static final double BLUE = 0.0722;
    private static final double FULL = 255;

    /** How dark the left half is. */
    public double left() {
        return of(0, picture.width() / 2);
    }

    /** How dark the right half is. */
    public double right() {
        return of(picture.width() / 2, picture.width());
    }

    /** Both, in a line, and how much darker or lighter the right is than the left. */
    public String said() {
        final double left = left();
        final double right = right();
        return String.format(Locale.ENGLISH, "How dark: left %.3f, right %.3f (%+.1f%%)", left, right,
                left > 0 ? (right - left) / left * 100 : 0);
    }

    /** How dark the columns from {@code from} up to {@code to} are. */
    private double of(final int from, final int to) {
        double sum = 0;
        for (int down = 0; down < picture.height(); down++) {
            for (int across = from; across < to; across++) {
                sum += RED * picture.part(across, down, 0) + GREEN * picture.part(across, down, 1)
                        + BLUE * picture.part(across, down, 2);
            }
        }
        return 1 - sum / FULL / Math.max(1, (long) (to - from) * picture.height());
    }
}
