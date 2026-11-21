package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

/**
 * The desk the card's windows stand on: the computer's own windows, as far as the card is shown in them. Opened
 * before the card, by the thread that will use the card, and closed after it. That thread is the one that hears what
 * happens to the windows, and it has to ask, between frames.
 */
public final class Desk implements AutoCloseable {

    private final Thread thread = Thread.currentThread();
    private boolean closed;

    public Desk() {
        if (!glfwInit()) {
            throw new Refused("glfwInit", "the computer's windows could not be reached");
        }
        if (!glfwVulkanSupported()) {
            glfwTerminate();
            throw new Refused("glfwVulkanSupported", "nothing here shows Vulkan in a window");
        }
    }

    /** What the way in to Vulkan has to be opened with, for a window to be drawn to. */
    List<String> needed() {
        mine();
        final PointerBuffer names = glfwGetRequiredInstanceExtensions();
        if (names == null) {
            throw new Refused("glfwGetRequiredInstanceExtensions", "nothing here shows Vulkan in a window");
        }
        final List<String> needed = new ArrayList<>();
        for (int at = 0; at < names.remaining(); at++) {
            needed.add(names.getStringASCII(at));
        }
        return needed;
    }

    /** Whatever has happened to the windows since this was last asked, heard: a window closed, moved or resized. */
    public void heard() {
        mine();
        glfwPollEvents();
    }

    /** Refuses any thread but the one that opened the desk, and a desk that is closed. */
    void mine() {
        if (Thread.currentThread() != thread) {
            throw new IllegalStateException("the desk is used by the thread " + thread.getName()
                    + " alone, and was called from " + Thread.currentThread().getName());
        }
        if (closed) {
            throw new IllegalStateException("the desk is closed, and has no windows any more");
        }
    }

    @Override
    public void close() {
        if (!closed) {
            mine();
            closed = true;
            glfwTerminate();
        }
    }
}
