package com.example.fxsuite.vulkan.device;

/**
 * What a frame is drawn on: a picture of colours and its view, the depths drawn against, and how many pixels wide
 * and tall they all are. A picture with no window and a window's picture each hand one over, and are drawn on the
 * same way from there.
 */
record Ground(Kept<Of.Image> colours, Kept<Of.View> view, Sheet depths, int width, int height) {
}
