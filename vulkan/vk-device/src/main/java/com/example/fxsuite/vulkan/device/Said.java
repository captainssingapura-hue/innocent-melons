package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT;
import static org.lwjgl.vulkan.VK13.VK_FALSE;

/**
 * What the validation layer has said: every warning and every error about how the card is used, kept until it is
 * heard. The layer says them in the middle of the call it is about, where nothing can be thrown, so they are kept
 * here and thrown from where it is safe.
 */
final class Said {

    private final List<String> kept = new ArrayList<>();
    private final VkDebugUtilsMessengerCallbackEXT hearing = VkDebugUtilsMessengerCallbackEXT.create(this::told);

    private int told(final int severity, final int kinds, final long said, final long unused) {
        final String message = VkDebugUtilsMessengerCallbackDataEXT.create(said).pMessageString();
        synchronized (kept) {
            kept.add(message);
        }
        return VK_FALSE;
    }

    /** What to ask the card for, so that this hears the layer: its warnings and errors, of use and of slowness. */
    VkDebugUtilsMessengerCreateInfoEXT asked(final MemoryStack stack) {
        return VkDebugUtilsMessengerCreateInfoEXT.calloc(stack).sType$Default()
                .messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT
                        | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT)
                .messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT
                        | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT)
                .pfnUserCallback(hearing);
    }

    /** Everything said since this was last asked, and nothing of it kept any longer. */
    List<String> heard() {
        synchronized (kept) {
            final List<String> heard = List.copyOf(kept);
            kept.clear();
            return heard;
        }
    }

    /** No longer hearing: to be done once nothing can say anything more. */
    void closed() {
        hearing.free();
    }
}
