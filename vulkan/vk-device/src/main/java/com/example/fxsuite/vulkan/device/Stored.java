package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * The memory a buffer or an image stands in: as much as the card says the thing needs, of a kind that has what is
 * {@code wanted} of it (that Java can reach it, say), and the thing set in it. A card with no memory of that kind
 * refuses, by name.
 */
record Stored(Reach reach) {

    /** Memory under {@code buffer}. */
    Kept<Of.Memory> under(final Kept<Of.Buffer> buffer, final int wanted) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkMemoryRequirements needs = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(reach.device(), buffer.raw(), needs);
            final Kept<Of.Memory> memory = made(buffer.name(), needs, wanted, stack);
            new Checked("vkBindBufferMemory")
                    .of(vkBindBufferMemory(reach.device(), buffer.raw(), memory.raw(), 0));
            return memory;
        }
    }

    /** Memory behind {@code image}. */
    Kept<Of.Memory> behind(final Kept<Of.Image> image, final int wanted) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkMemoryRequirements needs = VkMemoryRequirements.malloc(stack);
            vkGetImageMemoryRequirements(reach.device(), image.raw(), needs);
            final Kept<Of.Memory> memory = made(image.name(), needs, wanted, stack);
            new Checked("vkBindImageMemory").of(vkBindImageMemory(reach.device(), image.raw(), memory.raw(), 0));
            return memory;
        }
    }

    private Kept<Of.Memory> made(final String of, final VkMemoryRequirements needs, final int wanted,
                                 final MemoryStack stack) {
        final VkDevice device = reach.device();
        final VkMemoryAllocateInfo asked = VkMemoryAllocateInfo.calloc(stack).sType$Default()
                .allocationSize(needs.size()).memoryTypeIndex(kind(needs.memoryTypeBits(), wanted, stack));
        final LongBuffer made = stack.mallocLong(1);
        new Checked("vkAllocateMemory").of(vkAllocateMemory(device, asked, null, made));
        return reach.owner().kept("the memory of " + of, made.get(0), raw -> vkFreeMemory(device, raw, null));
    }

    /** Which of the card's kinds of memory is one of those {@code allowed} and has all that is {@code wanted}. */
    private int kind(final int allowed, final int wanted, final MemoryStack stack) {
        final VkPhysicalDeviceMemoryProperties has = VkPhysicalDeviceMemoryProperties.malloc(stack);
        vkGetPhysicalDeviceMemoryProperties(reach.card(), has);
        for (int kind = 0; kind < has.memoryTypeCount(); kind++) {
            if ((allowed & 1 << kind) != 0 && (has.memoryTypes(kind).propertyFlags() & wanted) == wanted) {
                return kind;
            }
        }
        throw new Refused("vkAllocateMemory", "the card has no memory of the kind wanted (" + wanted + ")");
    }
}
