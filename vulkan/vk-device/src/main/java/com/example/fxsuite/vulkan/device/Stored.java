package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkMemoryAllocateFlagsInfo;
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

    private static final double MEGABYTE = 1024.0 * 1024;

    /** Memory under {@code buffer}; where {@code addressed}, memory whose place on the card can be asked. */
    Kept<Of.Memory> under(final Kept<Of.Buffer> buffer, final int wanted, final boolean addressed) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkMemoryRequirements needs = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(reach.device(), buffer.raw(), needs);
            final Kept<Of.Memory> memory = made(buffer.name(), needs, wanted, addressed, stack);
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
            final Kept<Of.Memory> memory = made(image.name(), needs, wanted, false, stack);
            new Checked("vkBindImageMemory").of(vkBindImageMemory(reach.device(), image.raw(), memory.raw(), 0));
            return memory;
        }
    }

    private Kept<Of.Memory> made(final String of, final VkMemoryRequirements needs, final int wanted,
                                 final boolean addressed, final MemoryStack stack) {
        final VkDevice device = reach.device();
        final int kind = kind(needs.memoryTypeBits(), wanted, stack);
        final VkMemoryAllocateInfo asked = VkMemoryAllocateInfo.calloc(stack).sType$Default()
                .allocationSize(needs.size()).memoryTypeIndex(kind);
        if (addressed) {
            asked.pNext(VkMemoryAllocateFlagsInfo.calloc(stack).sType$Default()
                    .flags(VK_MEMORY_ALLOCATE_DEVICE_ADDRESS_BIT).address());
        }
        final LongBuffer made = stack.mallocLong(1);
        new Checked("vkAllocateMemory").of(vkAllocateMemory(device, asked, null, made));
        final long bytes = needs.size();
        final boolean own = own(kind, stack);
        reach.holding().more(bytes, own);
        return reach.owner().kept("the memory of " + of, made.get(0), raw -> {
            vkFreeMemory(device, raw, null);
            reach.holding().more(-bytes, own);
        });
    }

    /** Whether memory of kind number {@code kind} is the card's own, and not the computer's. */
    private boolean own(final int kind, final MemoryStack stack) {
        final VkPhysicalDeviceMemoryProperties has = VkPhysicalDeviceMemoryProperties.malloc(stack);
        vkGetPhysicalDeviceMemoryProperties(reach.card(), has);
        return (has.memoryHeaps(has.memoryTypes(kind).heapIndex()).flags() & VK_MEMORY_HEAP_DEVICE_LOCAL_BIT) != 0;
    }

    /** How many megabytes of its own memory the card has, all its own heaps together. */
    double whole() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkPhysicalDeviceMemoryProperties has = VkPhysicalDeviceMemoryProperties.malloc(stack);
            vkGetPhysicalDeviceMemoryProperties(reach.card(), has);
            double bytes = 0;
            for (int heap = 0; heap < has.memoryHeapCount(); heap++) {
                bytes += (has.memoryHeaps(heap).flags() & VK_MEMORY_HEAP_DEVICE_LOCAL_BIT) != 0
                        ? has.memoryHeaps(heap).size() : 0;
            }
            return bytes / MEGABYTE;
        }
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
