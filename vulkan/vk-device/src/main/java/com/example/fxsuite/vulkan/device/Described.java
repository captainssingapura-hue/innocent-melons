package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Layout;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * What a look is told, as the card has it described, made from the layouts and from nothing else: a block told once
 * a frame, bound where the shaders' declarations say it is; and what is pushed with each thing drawn. Both of its
 * shaders read both.
 */
record Described(Reach reach) {

    /** Where the frame's block is bound: what {@code Declared.block} is asked for by a look. */
    private static final int BLOCK = 0;
    /** Which shaders read what is pushed with each thing drawn: both. */
    static final int PUSHED = VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT;

    /** The frame's block, described: one block, read by both shaders. */
    Kept<Of.Told> told() {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDescriptorSetLayoutBinding.Buffer block = VkDescriptorSetLayoutBinding.calloc(1, stack)
                    .binding(BLOCK).descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(1)
                    .stageFlags(VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateDescriptorSetLayout").of(vkCreateDescriptorSetLayout(device,
                    VkDescriptorSetLayoutCreateInfo.calloc(stack).sType$Default().pBindings(block), null, made));
            return reach.owner().kept("a look's frame, described", made.get(0),
                    raw -> vkDestroyDescriptorSetLayout(device, raw, null));
        }
    }

    /** All a look is told, described: the frame's block as {@code told} has it, and {@code draw} pushed. */
    Kept<Of.Telling> telling(final Kept<Of.Told> told, final Layout draw) {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkPushConstantRange.Buffer pushed = VkPushConstantRange.calloc(1, stack)
                    .stageFlags(PUSHED).offset(0).size(draw.blockBytes());
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreatePipelineLayout").of(vkCreatePipelineLayout(device,
                    VkPipelineLayoutCreateInfo.calloc(stack).sType$Default().pSetLayouts(stack.longs(told.raw()))
                            .pPushConstantRanges(pushed), null, made));
            return reach.owner().kept("a look's telling, described", made.get(0),
                    raw -> vkDestroyPipelineLayout(device, raw, null));
        }
    }

    /** Where one frame's telling is kept. */
    Kept<Of.Tellings> tellings() {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDescriptorPoolSize.Buffer one = VkDescriptorPoolSize.calloc(1, stack)
                    .type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(1);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateDescriptorPool").of(vkCreateDescriptorPool(device,
                    VkDescriptorPoolCreateInfo.calloc(stack).sType$Default().maxSets(1).pPoolSizes(one), null, made));
            return reach.owner().kept("a look's frame, kept", made.get(0),
                    raw -> vkDestroyDescriptorPool(device, raw, null));
        }
    }

    /**
     * One frame's telling out of {@code from}, described as {@code told} has it and reading {@code frame}'s bytes:
     * ready to be bound. It goes when {@code from} goes.
     */
    Kept<Of.Bound> bound(final Kept<Of.Tellings> from, final Kept<Of.Told> told, final Bytes frame) {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkAllocateDescriptorSets").of(vkAllocateDescriptorSets(device,
                    VkDescriptorSetAllocateInfo.calloc(stack).sType$Default().descriptorPool(from.raw())
                            .pSetLayouts(stack.longs(told.raw())), made));
            final VkDescriptorBufferInfo.Buffer read = VkDescriptorBufferInfo.calloc(1, stack)
                    .buffer(frame.buffer().raw()).offset(0).range(frame.size());
            final VkWriteDescriptorSet.Buffer written = VkWriteDescriptorSet.calloc(1, stack).sType$Default()
                    .dstSet(made.get(0)).dstBinding(BLOCK).descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1).pBufferInfo(read);
            vkUpdateDescriptorSets(device, written, null);
            return reach.owner().kept("a look's frame, bound", made.get(0), raw -> { });
        }
    }
}
