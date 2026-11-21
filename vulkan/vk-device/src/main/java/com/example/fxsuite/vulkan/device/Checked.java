package com.example.fxsuite.vulkan.device;

import java.util.stream.IntStream;

import static org.lwjgl.vulkan.KHRSurface.VK_ERROR_NATIVE_WINDOW_IN_USE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_ERROR_SURFACE_LOST_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_SUBOPTIMAL_KHR;
import static org.lwjgl.vulkan.VK13.*;

/**
 * The one place a call's answer is checked. Every call that answers is made inside {@link #of} or {@link #allowing},
 * with the call's own name, so an answer that is not success is an exception naming the call; a test that reads the
 * source keeps it so.
 */
record Checked(String call) {

    /** {@code answered} is success, or the call is refused. */
    void of(final int answered) {
        allowing(answered);
    }

    /** {@code answered}, where it is success or one of {@code also}; anything else, and the call is refused. */
    int allowing(final int answered, final int... also) {
        if (answered != VK_SUCCESS && IntStream.of(also).noneMatch(allowed -> allowed == answered)) {
            throw new Refused(call, named(answered));
        }
        return answered;
    }

    private String named(final int answered) {
        return switch (answered) {
            case VK_NOT_READY -> "not ready";
            case VK_TIMEOUT -> "timed out";
            case VK_INCOMPLETE -> "incomplete";
            case VK_ERROR_OUT_OF_HOST_MEMORY -> "out of the computer's memory";
            case VK_ERROR_OUT_OF_DEVICE_MEMORY -> "out of the card's memory";
            case VK_ERROR_INITIALIZATION_FAILED -> "could not be started";
            case VK_ERROR_DEVICE_LOST -> "the card was lost";
            case VK_ERROR_MEMORY_MAP_FAILED -> "the memory could not be reached";
            case VK_ERROR_LAYER_NOT_PRESENT -> "a layer asked for is not there";
            case VK_ERROR_EXTENSION_NOT_PRESENT -> "an extension asked for is not there";
            case VK_ERROR_FEATURE_NOT_PRESENT -> "a feature asked for is not there";
            case VK_ERROR_INCOMPATIBLE_DRIVER -> "the driver is too old";
            case VK_ERROR_TOO_MANY_OBJECTS -> "too many of them";
            case VK_ERROR_FORMAT_NOT_SUPPORTED -> "the format is not one the card has";
            case VK_ERROR_SURFACE_LOST_KHR -> "the window's surface was lost";
            case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR -> "the window is already in use";
            case VK_ERROR_OUT_OF_DATE_KHR -> "the window has changed since";
            case VK_SUBOPTIMAL_KHR -> "the window no longer fits";
            default -> "answer " + answered;
        };
    }
}
