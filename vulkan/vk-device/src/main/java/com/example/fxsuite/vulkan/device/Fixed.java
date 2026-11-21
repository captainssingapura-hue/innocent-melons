package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRenderingCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;

import static org.lwjgl.vulkan.VK13.*;

/**
 * How every look draws, whatever it looks like, said once: triangles, three corners each; only those wound
 * clockwise in the picture drawn, which are the ones facing the camera; the nearer of two things kept, depth running
 * backwards; colours written over what was there; one sample to a pixel; and the picture's size left to be said as
 * each frame begins. Each is set down in {@code stack}'s memory, for the one call that builds a look.
 */
record Fixed(MemoryStack stack) {

    VkPipelineInputAssemblyStateCreateInfo triangles() {
        return VkPipelineInputAssemblyStateCreateInfo.calloc(stack).sType$Default()
                .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
    }

    /** One picture's size, said later: as each frame begins. */
    VkPipelineViewportStateCreateInfo sized() {
        return VkPipelineViewportStateCreateInfo.calloc(stack).sType$Default().viewportCount(1).scissorCount(1);
    }

    VkPipelineDynamicStateCreateInfo saidLater() {
        return VkPipelineDynamicStateCreateInfo.calloc(stack).sType$Default()
                .pDynamicStates(stack.ints(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR));
    }

    /** Filled triangles, and only those that face the camera: wound clockwise, as the camera's own test has it. */
    VkPipelineRasterizationStateCreateInfo facing() {
        return VkPipelineRasterizationStateCreateInfo.calloc(stack).sType$Default()
                .polygonMode(VK_POLYGON_MODE_FILL).cullMode(VK_CULL_MODE_BACK_BIT)
                .frontFace(VK_FRONT_FACE_CLOCKWISE).lineWidth(1);
    }

    VkPipelineMultisampleStateCreateInfo sampledOnce() {
        return VkPipelineMultisampleStateCreateInfo.calloc(stack).sType$Default()
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
    }

    /** The nearer kept: depth runs backwards, so the nearer is the greater. */
    VkPipelineDepthStencilStateCreateInfo nearest() {
        return VkPipelineDepthStencilStateCreateInfo.calloc(stack).sType$Default()
                .depthTestEnable(true).depthWriteEnable(true).depthCompareOp(VK_COMPARE_OP_GREATER);
    }

    /** Colours written over what was there, nothing mixed. */
    VkPipelineColorBlendStateCreateInfo written() {
        final VkPipelineColorBlendAttachmentState.Buffer whole = VkPipelineColorBlendAttachmentState.calloc(1, stack)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT
                        | VK_COLOR_COMPONENT_A_BIT);
        return VkPipelineColorBlendStateCreateInfo.calloc(stack).sType$Default().pAttachments(whole);
    }

    /** What is drawn on: one picture of colours and one of depths, as the kit keeps them. */
    VkPipelineRenderingCreateInfo drawnOn() {
        return VkPipelineRenderingCreateInfo.calloc(stack).sType$Default()
                .pColorAttachmentFormats(stack.ints(Paper.COLOUR.format()))
                .depthAttachmentFormat(Paper.DEPTH.format());
    }
}
