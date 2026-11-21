package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkLayerProperties;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK13.*;

/**
 * The way in to Vulkan, opened with the Khronos validation layer on and heard from the first call: the layer is
 * asked for by name, and where it is not installed nothing is opened at all. {@code windowed} is whatever more a
 * window needs of the way in; nothing, for a card that only draws pictures.
 */
record Opening(Said said, List<String> windowed) {

    private static final String LAYER = "VK_LAYER_KHRONOS_validation";
    private static final String CALLED = "fxsuite";

    /** The way in, kept by {@code owner}, with the layer heard from its very making. */
    VkInstance opened(final Owner owner) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            layered(stack);
            final PointerBuffer extensions = stack.mallocPointer(windowed.size() + 1);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            windowed.forEach(extension -> extensions.put(stack.UTF8(extension)));
            final VkApplicationInfo told = VkApplicationInfo.calloc(stack).sType$Default()
                    .pApplicationName(stack.UTF8(CALLED)).apiVersion(VK_API_VERSION_1_3);
            final VkInstanceCreateInfo asked = VkInstanceCreateInfo.calloc(stack).sType$Default()
                    .pNext(said.asked(stack).address())
                    .pApplicationInfo(told)
                    .ppEnabledLayerNames(stack.pointers(stack.UTF8(LAYER)))
                    .ppEnabledExtensionNames(extensions.flip());
            final PointerBuffer made = stack.mallocPointer(1);
            new Checked("vkCreateInstance").of(vkCreateInstance(asked, null, made));
            final VkInstance instance = new VkInstance(made.get(0), asked);
            owner.kept("the way in to Vulkan", instance.address(), raw -> vkDestroyInstance(instance, null));
            return instance;
        }
    }

    /** What hears the layer from here on, kept by {@code owner}. */
    Kept<Of.Messenger> hearing(final VkInstance instance, final Owner owner) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateDebugUtilsMessengerEXT")
                    .of(vkCreateDebugUtilsMessengerEXT(instance, said.asked(stack), null, made));
            return owner.kept("the hearing of the validation layer", made.get(0),
                    raw -> vkDestroyDebugUtilsMessengerEXT(instance, raw, null));
        }
    }

    /** Refuses to go on where the validation layer is not installed. */
    private void layered(final MemoryStack stack) {
        final IntBuffer count = stack.mallocInt(1);
        new Checked("vkEnumerateInstanceLayerProperties").of(vkEnumerateInstanceLayerProperties(count, null));
        final VkLayerProperties.Buffer layers = VkLayerProperties.malloc(count.get(0), stack);
        new Checked("vkEnumerateInstanceLayerProperties").of(vkEnumerateInstanceLayerProperties(count, layers));
        if (layers.stream().noneMatch(layer -> layer.layerNameString().equals(LAYER))) {
            throw new Refused("vkCreateInstance", LAYER + " is not installed, and nothing is drawn without it:"
                    + " it comes with the Vulkan SDK");
        }
    }
}
