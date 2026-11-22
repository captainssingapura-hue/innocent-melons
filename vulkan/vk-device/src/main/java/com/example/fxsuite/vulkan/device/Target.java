package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.function.Consumer;

/**
 * Somewhere a frame is drawn: a window, or a picture with no window. Whatever draws is handed one of these and
 * cannot tell which it has, so what is checked in a picture is what is shown in a window.
 */
public interface Target {

    /** How many pixels wide it is now. */
    int width();

    /** How many pixels tall it is now. */
    int height();

    /**
     * A frame drawn: the whole of it cleared to {@code sky} (as much red, green and blue as it has), every look
     * drawn with told what {@code frame} holds, and whatever {@code drawing} draws drawn over the sky. What draws
     * is handed to {@code drawing} and draws only until {@code drawing} is done.
     */
    void drawn(Vec3 sky, Filling frame, Consumer<Pass> drawing);

    /**
     * The same, with a picture of depths drawn beforehand for its looks to look things up in: {@code looked}. Drawn
     * with none, as above, a look that looks anything up finds the furthest there is everywhere.
     */
    void drawn(Vec3 sky, Filling frame, Depths looked, Consumer<Pass> drawing);
}
