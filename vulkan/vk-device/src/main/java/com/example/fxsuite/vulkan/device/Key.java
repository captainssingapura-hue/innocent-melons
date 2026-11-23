package com.example.fxsuite.vulkan.device;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A key of the keyboard that a window can be asked about: the few a figure is walked with, each named as the engine
 * names it. Which key each is to the desk is kept in here, and nothing of the desk's numbering leaves the kit.
 */
public enum Key {

    W(GLFW_KEY_W, GLFW_KEY_W),
    A(GLFW_KEY_A, GLFW_KEY_A),
    S(GLFW_KEY_S, GLFW_KEY_S),
    D(GLFW_KEY_D, GLFW_KEY_D),
    Q(GLFW_KEY_Q, GLFW_KEY_Q),
    E(GLFW_KEY_E, GLFW_KEY_E),
    C(GLFW_KEY_C, GLFW_KEY_C),
    F(GLFW_KEY_F, GLFW_KEY_F),
    /** Faster, flying only. */
    R(GLFW_KEY_R, GLFW_KEY_R),
    /** Slower, flying only. */
    V(GLFW_KEY_V, GLFW_KEY_V),
    SPACE(GLFW_KEY_SPACE, GLFW_KEY_SPACE),
    ESCAPE(GLFW_KEY_ESCAPE, GLFW_KEY_ESCAPE),
    /** Either Shift. */
    SHIFT(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT),
    /** Either Ctrl. */
    CONTROL(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT_CONTROL);

    private final int one;
    private final int other;

    Key(final int one, final int other) {
        this.one = one;
        this.other = other;
    }

    /** The key, as the desk numbers it. */
    int one() {
        return one;
    }

    /** The other key that counts as this one, as the desk numbers it; the same key, where there is no other. */
    int other() {
        return other;
    }
}
