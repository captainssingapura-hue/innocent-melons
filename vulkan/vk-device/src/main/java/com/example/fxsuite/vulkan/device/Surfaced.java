package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;

/**
 * A window on the desk as the card draws to it. A window on a screen the card chosen cannot show on is refused here,
 * before anything is drawn.
 */
record Surfaced(Reach reach) {

    /** {@code glass} as the card draws to it. */
    Kept<Of.Surface> of(final Kept<Of.Glass> glass) {
        final VkInstance way = reach.way();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer made = stack.mallocLong(1);
            new Checked("glfwCreateWindowSurface").of(glfwCreateWindowSurface(way, glass.raw(), null, made));
            final Kept<Of.Surface> surface = reach.owner().kept(glass.name() + ", as the card draws to it",
                    made.get(0), raw -> vkDestroySurfaceKHR(way, raw, null));
            final IntBuffer shows = stack.mallocInt(1);
            new Checked("vkGetPhysicalDeviceSurfaceSupportKHR")
                    .of(vkGetPhysicalDeviceSurfaceSupportKHR(reach.card(), reach.family(), surface.raw(), shows));
            if (shows.get(0) == 0) {
                surface.close();
                throw new Refused("vkGetPhysicalDeviceSurfaceSupportKHR",
                        "the card " + reach.name() + " cannot show on this window's screen");
            }
            return surface;
        }
    }
}
