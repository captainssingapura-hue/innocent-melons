package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.shade.Laid;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkWriteDescriptorSet;
import org.lwjgl.vulkan.VkWriteDescriptorSetAccelerationStructureKHR;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.KHRAccelerationStructure.VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR;
import static org.lwjgl.vulkan.VK13.*;

/**
 * What a look is told, as the card has it described, made from the layouts and from nothing else: a block told once
 * a frame, and a picture of depths to look things up in, each bound where {@code laid} says the shaders'
 * declarations have it; and what is pushed with each thing drawn. Both of its shaders read the block and what is
 * pushed; the picture is looked up by the shader run for each pixel.
 */
record Described(Reach reach, Laid laid) {

    /** Which shaders read what is pushed with each thing drawn: both. */
    static final int PUSHED = VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT;

    /** The frame's block and the picture of depths, described. */
    Kept<Of.Told> told() {
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDescriptorSetLayoutBinding.Buffer both = VkDescriptorSetLayoutBinding
                    .calloc(laid.rays() ? 3 : 2, stack);
            if (laid.rays()) {
                both.get(2).binding(laid.standingBound())
                        .descriptorType(VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR)
                        .descriptorCount(1).stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);
            }
            both.get(0).binding(laid.frameBound()).descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1).stageFlags(VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT);
            both.get(1).binding(laid.depthsBound()).descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .descriptorCount(1).stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateDescriptorSetLayout").of(vkCreateDescriptorSetLayout(device,
                    VkDescriptorSetLayoutCreateInfo.calloc(stack).sType$Default().pBindings(both), null, made));
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
            final VkDescriptorPoolSize.Buffer each = VkDescriptorPoolSize.calloc(laid.rays() ? 3 : 2, stack);
            if (laid.rays()) {
                each.get(2).type(VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR).descriptorCount(1);
            }
            each.get(0).type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(1);
            each.get(1).type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER).descriptorCount(1);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateDescriptorPool").of(vkCreateDescriptorPool(device,
                    VkDescriptorPoolCreateInfo.calloc(stack).sType$Default().maxSets(1).pPoolSizes(each), null, made));
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
                    .dstSet(made.get(0)).dstBinding(laid.frameBound())
                    .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(1).pBufferInfo(read);
            vkUpdateDescriptorSets(device, written, null);
            return reach.owner().kept("a look's frame, bound", made.get(0), raw -> { });
        }
    }

    /**
     * {@code bound} set to look things up in the picture of depths seen as {@code view}, the way {@code sampler}
     * looks them up: for the frames drawn from now on, until it is set to another.
     */
    void looked(final Kept<Of.Bound> bound, final Kept<Of.View> view, final Kept<Of.Sampler> sampler) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDescriptorImageInfo.Buffer read = VkDescriptorImageInfo.calloc(1, stack)
                    .sampler(sampler.raw()).imageView(view.raw())
                    .imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL);
            final VkWriteDescriptorSet.Buffer written = VkWriteDescriptorSet.calloc(1, stack).sType$Default()
                    .dstSet(bound.raw()).dstBinding(laid.depthsBound())
                    .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER).descriptorCount(1).pImageInfo(read);
            vkUpdateDescriptorSets(reach.device(), written, null);
        }
    }

    /**
     * {@code bound} set to ask rays of {@code stood}: for the frames drawn from now on, until it is set to
     * another. Only for a look laid out to ask rays.
     */
    void asked(final Kept<Of.Bound> bound, final Kept<Of.Structure> stood) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkWriteDescriptorSetAccelerationStructureKHR of = VkWriteDescriptorSetAccelerationStructureKHR
                    .calloc(stack).sType$Default().pAccelerationStructures(stack.longs(stood.raw()));
            final VkWriteDescriptorSet.Buffer written = VkWriteDescriptorSet.calloc(1, stack).sType$Default()
                    .pNext(of.address()).dstSet(bound.raw()).dstBinding(laid.standingBound())
                    .descriptorType(VK_DESCRIPTOR_TYPE_ACCELERATION_STRUCTURE_KHR).descriptorCount(1);
            vkUpdateDescriptorSets(reach.device(), written, null);
        }
    }
}
