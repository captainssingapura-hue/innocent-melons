package com.example.fxsuite.vulkan.device;

/**
 * Where a window stands on the desk at the moment: the top left corner of its frame, in the desk's own measure
 * across all its screens; how big its inside is and how far its frame's corner is from a screen's, as the screen
 * measures its own windows, which on a screen that draws everything larger is fewer than its pixels; how many of
 * the desk's measure one of the screen's own is; and whether it is as large as its screen lets it be.
 *
 * @param left      how far across the desk its frame's corner is
 * @param top       how far down the desk its frame's corner is
 * @param width     how wide its inside is, as the screen measures its own windows
 * @param height    how high its inside is, as the screen measures its own windows
 * @param larger    how many of the desk's measure one of the screen's own is: 1.5, on a screen set to 150%
 * @param maximized whether it is as large as its screen lets it be
 */
public record Stands(int left, int top, int width, int height, double larger, boolean maximized) {
}
