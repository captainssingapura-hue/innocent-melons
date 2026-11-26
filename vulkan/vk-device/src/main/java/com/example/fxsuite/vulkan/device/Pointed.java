package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The mouse over a window: where it is and what is held are asked of the desk each time, and the wheel, which the
 * desk only tells of as it turns, is added up between askings. Its going is counted from the first asking, and is a
 * drag from the asking after a button goes down, so the press itself drags nothing. What the wheel is heard with
 * belongs to the window, and goes when the window does.
 */
final class Pointed {

    private final Kept<Of.Glass> glass;
    private double scrolled;
    private double lastAcross;
    private double lastDown;
    private boolean wasHeld;
    private boolean asked;

    /** The clicks heard since they were last asked for, where each was, in the picture's pixels. */
    private final List<Clicked> clicks = new ArrayList<>();

    Pointed(final Kept<Of.Glass> glass) {
        this.glass = glass;
        glfwSetScrollCallback(glass.raw(), (window, across, away) -> scrolled += away);
        glfwSetMouseButtonCallback(glass.raw(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                clicks.add(at(window));
            }
        });
    }

    /** Where the pointer is over {@code window}, in its picture's pixels: the desk's units times its scale. */
    private Clicked at(final long window) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer across = stack.mallocDouble(1);
            final DoubleBuffer down = stack.mallocDouble(1);
            glfwGetCursorPos(window, across, down);
            final IntBuffer wide = stack.mallocInt(1);
            final IntBuffer high = stack.mallocInt(1);
            glfwGetWindowSize(window, wide, high);
            final IntBuffer pixelsWide = stack.mallocInt(1);
            final IntBuffer pixelsHigh = stack.mallocInt(1);
            glfwGetFramebufferSize(window, pixelsWide, pixelsHigh);
            return new Clicked(across.get(0) * pixelsWide.get(0) / Math.max(1, wide.get(0)),
                    down.get(0) * pixelsHigh.get(0) / Math.max(1, high.get(0)));
        }
    }

    /** The clicks heard since this was last asked, the first first. */
    List<Clicked> clicks() {
        final List<Clicked> heard = List.copyOf(clicks);
        clicks.clear();
        return heard;
    }

    /** What the mouse did since this was last asked. */
    Moved moved() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer across = stack.mallocDouble(1);
            final DoubleBuffer down = stack.mallocDouble(1);
            glfwGetCursorPos(glass.raw(), across, down);
            final FloatBuffer wider = stack.mallocFloat(1);
            final FloatBuffer taller = stack.mallocFloat(1);
            glfwGetWindowContentScale(glass.raw(), wider, taller);
            final double hereAcross = across.get(0) / wider.get(0);
            final double hereDown = down.get(0) / taller.get(0);
            final boolean first = glfwGetMouseButton(glass.raw(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
            final boolean second = glfwGetMouseButton(glass.raw(), GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;
            final boolean shift = glfwGetKey(glass.raw(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS
                    || glfwGetKey(glass.raw(), GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
            final boolean held = first || second;
            final Moved moved = new Moved(asked ? hereAcross - lastAcross : 0, asked ? hereDown - lastDown : 0,
                    held && wasHeld, second || first && shift, scrolled);
            asked = true;
            lastAcross = hereAcross;
            lastDown = hereDown;
            wasHeld = held;
            scrolled = 0;
            return moved;
        }
    }
}
