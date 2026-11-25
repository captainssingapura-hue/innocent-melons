package com.example.fxsuite.vulkan.device;

/**
 * One of the screens the desk has: what the computer calls it, the part of it windows may fill (all of it but what
 * the desk keeps for itself, a task bar), and whether it is the desk's first screen. Its place and size are in the
 * desk's own measure across all its screens, which a window's place is told in too.
 *
 * @param name    what the computer calls it: not always different from another's
 * @param left    how far across the desk the part windows may fill begins
 * @param top     how far down the desk it begins
 * @param wide    how wide that part is
 * @param high    how high that part is
 * @param larger  how many of the desk's measure one of this screen's own is: 1.5, on a screen set to 150%
 * @param primary whether it is the desk's first screen
 */
public record Monitor(String name, int left, int top, int wide, int high, double larger, boolean primary) {

    /** Whether the spot so far across the desk and so far down is on it. */
    public boolean holds(final int across, final int down) {
        return across >= left && across < left + wide && down >= top && down < top + high;
    }
}
