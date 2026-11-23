package melons.innocent.terrain.bench;

import java.util.Locale;

/**
 * A count, frame by frame, of how the land kept up once its first view was filled: how many frames there have been
 * since, how many had holes in the land and how many at worst, and how many had old tiles standing in for new ones and
 * how many at worst. On the card's thread only.
 */
final class Tally {

    private boolean filled;
    private int frames;
    private int holed;
    private int worstHoles;
    private int standing;
    private int worstStanding;

    /** Whether the first view has been filled: whether a frame has had no hole yet. */
    boolean filled() {
        return filled;
    }

    /** A frame with {@code gaps} holes and {@code stale} tiles stood in for, counted once the first view is filled. */
    void framed(final int gaps, final int stale) {
        if (!filled) {
            filled = gaps == 0;
            return;
        }
        frames++;
        if (gaps > 0) {
            holed++;
            worstHoles = Math.max(worstHoles, gaps);
        }
        if (stale > 0) {
            standing++;
            worstStanding = Math.max(worstStanding, stale);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%d frames once filled: %d with holes, %d at worst; %d with old tiles"
                + " standing in, %d at worst", frames, holed, worstHoles, standing, worstStanding);
    }
}
