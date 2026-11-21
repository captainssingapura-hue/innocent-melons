package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK13.*;

/**
 * The choosing of a card, of those the computer has: one that draws, with Vulkan 1.3 or later. A card of its own is
 * taken before one built into the processor. {@code wanted} is part of a card's name, to have that card and no
 * other; empty, to leave the choice here.
 */
record Choosing(VkInstance instance, String wanted) {

    /** A card that may be chosen: the card, which of its queues draws, its name, and whether it stands by itself. */
    record Chosen(VkPhysicalDevice card, int family, String name, boolean separate) {
    }

    Chosen chosen() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer count = stack.mallocInt(1);
            new Checked("vkEnumeratePhysicalDevices").of(vkEnumeratePhysicalDevices(instance, count, null));
            final PointerBuffer found = stack.mallocPointer(count.get(0));
            new Checked("vkEnumeratePhysicalDevices").of(vkEnumeratePhysicalDevices(instance, count, found));
            final List<Chosen> fit = new ArrayList<>();
            for (int at = 0; at < count.get(0); at++) {
                weighed(new VkPhysicalDevice(found.get(at), instance), fit, stack);
            }
            return fit.stream().filter(Chosen::separate).findFirst().or(() -> fit.stream().findFirst())
                    .orElseThrow(() -> new Refused("vkEnumeratePhysicalDevices", "no card here draws with"
                            + " Vulkan 1.3" + (wanted.isEmpty() ? "" : " and is called " + wanted)));
        }
    }

    /** {@code card} added to {@code fit}, where it is one that may be chosen. */
    private void weighed(final VkPhysicalDevice card, final List<Chosen> fit, final MemoryStack stack) {
        final VkPhysicalDeviceProperties said = VkPhysicalDeviceProperties.malloc(stack);
        vkGetPhysicalDeviceProperties(card, said);
        final int family = drawing(card, stack);
        final String name = said.deviceNameString();
        if (family >= 0 && said.apiVersion() >= VK_API_VERSION_1_3 && name.contains(wanted)) {
            fit.add(new Chosen(card, family, name, said.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU));
        }
    }

    /** Which of {@code card}'s families of queues draws; less than nothing, where none does. */
    private int drawing(final VkPhysicalDevice card, final MemoryStack stack) {
        final IntBuffer count = stack.mallocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(card, count, null);
        final VkQueueFamilyProperties.Buffer families = VkQueueFamilyProperties.malloc(count.get(0), stack);
        vkGetPhysicalDeviceQueueFamilyProperties(card, count, families);
        for (int family = 0; family < count.get(0); family++) {
            if ((families.get(family).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                return family;
            }
        }
        return -1;
    }
}
