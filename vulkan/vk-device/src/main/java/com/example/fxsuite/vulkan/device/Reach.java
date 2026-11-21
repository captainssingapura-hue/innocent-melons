package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan13Features;
import org.lwjgl.vulkan.VkQueue;

import java.util.List;

import static org.lwjgl.vulkan.VK13.*;

/**
 * The card as the kit's own classes reach it: the card chosen, opened; the queue its commands are sent down; and the
 * owner of whatever is made on it for others. Opened with what the kit draws by: drawing straight to a picture's
 * views, with no pass described ahead. Nothing made on it outlives it: its own owner closes it last of all.
 */
final class Reach {

    private final Choosing.Chosen chosen;
    private final VkDevice device;
    private final Kept<Of> opened;
    private final VkQueue queue;
    private final Owner theirs;

    /** {@code chosen} opened, kept by {@code own}; what is made on it for others kept by {@code theirs}. */
    Reach(final Choosing.Chosen chosen, final List<String> windowed, final Owner own, final Owner theirs) {
        this.chosen = chosen;
        this.theirs = theirs;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDeviceQueueCreateInfo.Buffer queues = VkDeviceQueueCreateInfo.calloc(1, stack).sType$Default()
                    .queueFamilyIndex(chosen.family()).pQueuePriorities(stack.floats(1));
            final VkPhysicalDeviceVulkan13Features drawnBy = VkPhysicalDeviceVulkan13Features.calloc(stack)
                    .sType$Default().dynamicRendering(true);
            final PointerBuffer extensions = stack.mallocPointer(windowed.size());
            windowed.forEach(extension -> extensions.put(stack.UTF8(extension)));
            final VkDeviceCreateInfo asked = VkDeviceCreateInfo.calloc(stack).sType$Default()
                    .pNext(drawnBy.address()).pQueueCreateInfos(queues).ppEnabledExtensionNames(extensions.flip());
            final PointerBuffer made = stack.mallocPointer(1);
            new Checked("vkCreateDevice").of(vkCreateDevice(chosen.card(), asked, null, made));
            final VkDevice card = new VkDevice(made.get(0), chosen.card(), asked);
            this.device = card;
            this.opened = own.kept("the card " + chosen.name(), card.address(), raw -> vkDestroyDevice(card, null));
            final PointerBuffer down = stack.mallocPointer(1);
            vkGetDeviceQueue(card, chosen.family(), 0, down);
            this.queue = new VkQueue(down.get(0), card);
        }
    }

    /** The card, to make a call on: for its own thread, and while it is open. */
    VkDevice device() {
        opened.raw();
        return device;
    }

    /** The card before it is opened, to ask what it has. */
    VkPhysicalDevice card() {
        opened.raw();
        return chosen.card();
    }

    /** The way in to Vulkan the card was chosen through: what a window's surface is made on. */
    VkInstance way() {
        opened.raw();
        return chosen.card().getInstance();
    }

    /** The queue commands are sent down. */
    VkQueue queue() {
        opened.raw();
        return queue;
    }

    /** Which of the card's families of queues that queue is of. */
    int family() {
        return chosen.family();
    }

    String name() {
        return chosen.name();
    }

    /** The owner of whatever is made on the card for others. */
    Owner owner() {
        return theirs;
    }
}
