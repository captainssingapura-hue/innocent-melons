package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFormatProperties;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * How a picture of depths is looked up by a shader: not for the depth kept at a place, but for whether a depth the
 * shader has in hand is at least as near as the one kept there, which it answers with all or nothing. Where the
 * card can, the answers of the four pixels round the place are blended, so the edge between all and nothing is a
 * little soft. Outside the picture, the depth kept is taken to be the furthest there is: nothing is nearer than
 * anything there.
 */
record Compared(Reach reach) {

    /** The way of looking up, kept for whoever asks until they close it. */
    Kept<Of.Sampler> sampler() {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkFormatProperties has = VkFormatProperties.malloc(stack);
            vkGetPhysicalDeviceFormatProperties(reach.card(), Paper.DEPTH_LOOKED_UP.format(), has);
            final boolean blended = (has.optimalTilingFeatures()
                    & VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT) != 0;
            final int filter = blended ? VK_FILTER_LINEAR : VK_FILTER_NEAREST;
            final VkSamplerCreateInfo asked = VkSamplerCreateInfo.calloc(stack).sType$Default()
                    .magFilter(filter).minFilter(filter).mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
                    .addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER)
                    .addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER)
                    .addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER)
                    .borderColor(VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK)
                    .compareEnable(true).compareOp(VK_COMPARE_OP_GREATER_OR_EQUAL);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateSampler").of(vkCreateSampler(device, asked, null, made));
            return reach.owner().kept("the looking up of depths", made.get(0),
                    raw -> vkDestroySampler(device, raw, null));
        }
    }
}
