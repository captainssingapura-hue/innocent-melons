package com.example.fxsuite.vulkan.device;

/**
 * A click in a window: where the first button went down, in the window's picture's own pixels, across from its left
 * and down from its top, as what is drawn there is placed.
 *
 * @param across how far from the picture's left edge
 * @param down   how far from its top edge
 */
public record Clicked(double across, double down) {
}
