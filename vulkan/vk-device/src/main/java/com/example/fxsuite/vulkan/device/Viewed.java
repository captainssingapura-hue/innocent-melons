package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * A picture on the card as it is drawn on or read: the whole of it, flat, as the kind of picture it is. A picture
 * the kit made and one a window lends are viewed the same way.
 */
record Viewed(Reach reach) {

    /** The view of {@code image}, a picture of {@code paper}'s kind. */
    Kept<Of.View> of(final Kept<Of.Image> image, final Paper paper) {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkImageViewCreateInfo asked = VkImageViewCreateInfo.calloc(stack).sType$Default()
                    .image(image.raw()).viewType(VK_IMAGE_VIEW_TYPE_2D).format(paper.format())
                    .subresourceRange(all -> all.aspectMask(paper.aspect()).levelCount(1).layerCount(1));
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateImageView").of(vkCreateImageView(device, asked, null, made));
            return reach.owner().kept("the view of " + image.name(), made.get(0),
                    raw -> vkDestroyImageView(device, raw, null));
        }
    }
}
