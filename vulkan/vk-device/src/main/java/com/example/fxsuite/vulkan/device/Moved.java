package com.example.fxsuite.vulkan.device;

/**
 * What the mouse did in a window since the window was last asked. {@code across} and {@code down} are how far it
 * went, to the right and down the screen, in the screen's own units (not pixels, on a screen that draws everything
 * larger), whether or not anything was held. {@code dragged} is whether a button was held all the while, so that
 * its going was a drag, and {@code other} whether that was the second button, or the first with Shift.
 * {@code scrolled} is how many notches the wheel was turned, away from whoever turns it when more than nothing. The
 * window says only what was done; what it means is for whoever asks.
 */
public record Moved(double across, double down, boolean dragged, boolean other, double scrolled) {
}
