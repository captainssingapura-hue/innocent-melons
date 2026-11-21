package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * A picture on the card, so many pixels wide and tall, of colours or of depths: the picture, the memory it stands
 * in, and the view of it that is drawn on.
 */
final class Sheet {

    private final Kept<Of.Image> image;
    private final Kept<Of.Memory> memory;
    private final Kept<Of.View> view;

    /** A picture of {@code paper}'s kind called {@code name}, {@code width} pixels by {@code height}. */
    Sheet(final Reach reach, final String name, final Paper paper, final int width, final int height) {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkImageCreateInfo asked = VkImageCreateInfo.calloc(stack).sType$Default()
                    .imageType(VK_IMAGE_TYPE_2D).format(paper.format())
                    .extent(size -> size.width(width).height(height).depth(1))
                    .mipLevels(1).arrayLayers(1).samples(VK_SAMPLE_COUNT_1_BIT).tiling(VK_IMAGE_TILING_OPTIMAL)
                    .usage(paper.use()).sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateImage").of(vkCreateImage(device, asked, null, made));
            this.image = reach.owner().kept(name, made.get(0), raw -> vkDestroyImage(device, raw, null));
            this.memory = new Stored(reach).behind(image, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
            this.view = new Viewed(reach).of(image, paper);
        }
    }

    Kept<Of.Image> image() {
        return image;
    }

    Kept<Of.View> view() {
        return view;
    }

    void close() {
        view.close();
        image.close();
        memory.close();
    }
}
