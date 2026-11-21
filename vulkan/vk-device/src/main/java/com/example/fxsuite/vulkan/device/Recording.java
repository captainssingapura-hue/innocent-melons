package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * The commands of one frame, written down and then sent: begun, filled by whoever draws, and sent to the card to be
 * done, all of them, before anything more is asked. One frame at a time, and the next not begun until the last is
 * done.
 */
final class Recording {

    /** How long a frame is waited for before it is given up as lost, in nanoseconds: ten seconds. */
    private static final long PATIENCE = 10_000_000_000L;

    private final Reach reach;
    private final Kept<Of.Commands> pool;
    private final VkCommandBuffer commands;
    private final Kept<Of.Fence> done;

    /** One frame's commands on {@code reach}'s card, what they need kept by {@code own}. */
    Recording(final Reach reach, final Owner own) {
        this.reach = reach;
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer made = stack.mallocLong(1);
            final VkCommandPoolCreateInfo asked = VkCommandPoolCreateInfo.calloc(stack).sType$Default()
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT).queueFamilyIndex(reach.family());
            new Checked("vkCreateCommandPool").of(vkCreateCommandPool(device, asked, null, made));
            this.pool = own.kept("the commands' memory", made.get(0), raw -> vkDestroyCommandPool(device, raw, null));
            final VkCommandBufferAllocateInfo one = VkCommandBufferAllocateInfo.calloc(stack).sType$Default()
                    .commandPool(pool.raw()).level(VK_COMMAND_BUFFER_LEVEL_PRIMARY).commandBufferCount(1);
            final PointerBuffer given = stack.mallocPointer(1);
            new Checked("vkAllocateCommandBuffers").of(vkAllocateCommandBuffers(device, one, given));
            this.commands = new VkCommandBuffer(given.get(0), device);
            new Checked("vkCreateFence")
                    .of(vkCreateFence(device, VkFenceCreateInfo.calloc(stack).sType$Default(), null, made));
            this.done = own.kept("the frame's being done", made.get(0), raw -> vkDestroyFence(device, raw, null));
        }
    }

    /** The commands, emptied and begun, to be written to. */
    Strokes begun() {
        pool.raw();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            new Checked("vkResetCommandBuffer").of(vkResetCommandBuffer(commands, 0));
            new Checked("vkBeginCommandBuffer").of(vkBeginCommandBuffer(commands,
                    VkCommandBufferBeginInfo.calloc(stack).sType$Default()
                            .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT)));
        }
        return new Strokes(commands);
    }

    /** The commands ended, sent to the card, and waited for until every one is done. */
    void sent() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            sent(VkSubmitInfo.calloc(stack).sType$Default(), stack);
        }
    }

    /**
     * The same, for a window: nothing is drawn until {@code after} says the window's picture is ready to be drawn
     * on, and {@code then} is told when the drawing is done.
     */
    void sent(final Kept<Of.Signal> after, final Kept<Of.Signal> then) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            sent(VkSubmitInfo.calloc(stack).sType$Default()
                    .waitSemaphoreCount(1).pWaitSemaphores(stack.longs(after.raw()))
                    .pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                    .pSignalSemaphores(stack.longs(then.raw())), stack);
        }
    }

    private void sent(final VkSubmitInfo all, final MemoryStack stack) {
        final VkDevice device = reach.device();
        new Checked("vkEndCommandBuffer").of(vkEndCommandBuffer(commands));
        all.pCommandBuffers(stack.pointers(commands));
        new Checked("vkQueueSubmit").of(vkQueueSubmit(reach.queue(), all, done.raw()));
        new Checked("vkWaitForFences").of(vkWaitForFences(device, done.raw(), true, PATIENCE));
        new Checked("vkResetFences").of(vkResetFences(device, done.raw()));
    }
}
