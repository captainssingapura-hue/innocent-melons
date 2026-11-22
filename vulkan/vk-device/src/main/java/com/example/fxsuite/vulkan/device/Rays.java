package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceAccelerationStructureFeaturesKHR;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceRayQueryFeaturesKHR;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan12Features;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.vulkan.KHRAccelerationStructure.VK_KHR_ACCELERATION_STRUCTURE_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRDeferredHostOperations.VK_KHR_DEFERRED_HOST_OPERATIONS_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRRayQuery.VK_KHR_RAY_QUERY_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK13.*;

/**
 * Whether a card answers rays: whether, asked from inside a shader if anything stands along a straight line, it can
 * say. That takes three of Vulkan's extensions (its ray queries, the structures the things of a frame are kept in
 * to be asked, and what those are built with) and what each brings switched on. A card with all of it is opened
 * with it; one without is opened as it always was, and draws no worse for it.
 */
record Rays(VkPhysicalDevice card) {

    /** The extensions a card is opened with to answer rays. */
    List<String> needed() {
        return List.of(VK_KHR_ACCELERATION_STRUCTURE_EXTENSION_NAME, VK_KHR_RAY_QUERY_EXTENSION_NAME,
                VK_KHR_DEFERRED_HOST_OPERATIONS_EXTENSION_NAME);
    }

    /** Whether the card has every one of them, and what each brings. */
    boolean answered() {
        return has().containsAll(needed()) && brings();
    }

    /** The names of every extension the card has. */
    private Set<String> has() {
        final Set<String> has = new HashSet<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer count = stack.mallocInt(1);
            new Checked("vkEnumerateDeviceExtensionProperties")
                    .of(vkEnumerateDeviceExtensionProperties(card, (ByteBuffer) null, count, null));
            // a card has some hundreds of them: more than the stack is for
            final VkExtensionProperties.Buffer all = VkExtensionProperties.malloc(count.get(0));
            try {
                new Checked("vkEnumerateDeviceExtensionProperties")
                        .of(vkEnumerateDeviceExtensionProperties(card, (ByteBuffer) null, count, all));
                all.forEach(one -> has.add(one.extensionNameString()));
            } finally {
                all.free();
            }
        }
        return has;
    }

    /** Whether the card says it does what the extensions are for. */
    private boolean brings() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkPhysicalDeviceRayQueryFeaturesKHR asks = VkPhysicalDeviceRayQueryFeaturesKHR.calloc(stack)
                    .sType$Default();
            final VkPhysicalDeviceAccelerationStructureFeaturesKHR keeps =
                    VkPhysicalDeviceAccelerationStructureFeaturesKHR.calloc(stack).sType$Default()
                            .pNext(asks.address());
            final VkPhysicalDeviceVulkan12Features addressed = VkPhysicalDeviceVulkan12Features.calloc(stack)
                    .sType$Default().pNext(keeps.address());
            vkGetPhysicalDeviceFeatures2(card,
                    VkPhysicalDeviceFeatures2.calloc(stack).sType$Default().pNext(addressed.address()));
            return asks.rayQuery() && keeps.accelerationStructure() && addressed.bufferDeviceAddress();
        }
    }
}
