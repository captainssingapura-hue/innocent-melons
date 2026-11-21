package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/** What says, on the card, that one step is done and the next may begin: made here, by name. */
record Signalled(Reach reach) {

    /** A signal called {@code name}, not yet given. */
    Kept<Of.Signal> by(final String name) {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateSemaphore").of(vkCreateSemaphore(device,
                    VkSemaphoreCreateInfo.calloc(stack).sType$Default(), null, made));
            return reach.owner().kept(name, made.get(0), raw -> vkDestroySemaphore(device, raw, null));
        }
    }
}
