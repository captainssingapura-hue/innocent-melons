package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Field;
import com.example.fxsuite.vulkan.layout.Kind;
import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Shader;
import com.example.fxsuite.vulkan.shade.Stage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * A look built ready to draw with: its two shaders compiled, and the card told how a mesh's points are laid out,
 * from the same layout the shaders' declarations and the mesh's bytes are made from. Built once, before any frame;
 * the compiled shaders are let go as soon as the look is built.
 */
record Piped(Reach reach, Compiler compiler) {

    private static final String BEGINS = "main";

    /** The look of {@code vertex} and {@code fragment}, laid out as {@code laid} says and told as {@code telling}. */
    Kept<Of.Pipeline> pipeline(final Laid laid, final Shader vertex, final Shader fragment,
                               final Kept<Of.Telling> telling) {
        if (vertex.stage() != Stage.VERTEX || fragment.stage() != Stage.FRAGMENT) {
            throw new IllegalArgumentException("a look is a shader for each point and one for each pixel, not "
                    + vertex.stage() + " and " + fragment.stage());
        }
        final VkDevice device = reach.device();
        final Kept<Of.Module> points = module(vertex);
        final Kept<Of.Module> pixels = module(fragment);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final Fixed fixed = new Fixed(stack);
            final VkGraphicsPipelineCreateInfo.Buffer asked = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                    .sType$Default().pNext(fixed.drawnOn().address())
                    .pStages(stages(points, pixels, stack)).pVertexInputState(taken(laid.point(), stack))
                    .pInputAssemblyState(fixed.triangles()).pViewportState(fixed.sized())
                    .pRasterizationState(fixed.facing()).pMultisampleState(fixed.sampledOnce())
                    .pDepthStencilState(fixed.nearest()).pColorBlendState(fixed.written())
                    .pDynamicState(fixed.saidLater()).layout(telling.raw());
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateGraphicsPipelines")
                    .of(vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, asked, null, made));
            return reach.owner().kept("a look", made.get(0), raw -> vkDestroyPipeline(device, raw, null));
        } finally {
            pixels.close();
            points.close();
        }
    }

    private VkPipelineShaderStageCreateInfo.Buffer stages(final Kept<Of.Module> points, final Kept<Of.Module> pixels,
                                                          final MemoryStack stack) {
        final VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.calloc(2, stack);
        stages.get(0).sType$Default().stage(VK_SHADER_STAGE_VERTEX_BIT).module(points.raw())
                .pName(stack.UTF8(BEGINS));
        stages.get(1).sType$Default().stage(VK_SHADER_STAGE_FRAGMENT_BIT).module(pixels.raw())
                .pName(stack.UTF8(BEGINS));
        return stages;
    }

    /** How a mesh's points are taken in: packed as {@code point} says, each field where its shader looks for it. */
    private VkPipelineVertexInputStateCreateInfo taken(final Layout point, final MemoryStack stack) {
        final VkVertexInputBindingDescription.Buffer each = VkVertexInputBindingDescription.calloc(1, stack)
                .binding(0).stride(point.packedBytes()).inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
        final VkVertexInputAttributeDescription.Buffer fields = VkVertexInputAttributeDescription
                .calloc(point.fields().size(), stack);
        for (int place = 0; place < point.fields().size(); place++) {
            final Field field = point.fields().get(place);
            fields.get(place).location(place).binding(0).format(format(field.kind()))
                    .offset(point.packedAt(field.name()));
        }
        return VkPipelineVertexInputStateCreateInfo.calloc(stack).sType$Default()
                .pVertexBindingDescriptions(each).pVertexAttributeDescriptions(fields);
    }

    private int format(final Kind kind) {
        return switch (kind) {
            case REAL -> VK_FORMAT_R32_SFLOAT;
            case PAIR -> VK_FORMAT_R32G32_SFLOAT;
            case TRIPLE -> VK_FORMAT_R32G32B32_SFLOAT;
            case QUAD -> VK_FORMAT_R32G32B32A32_SFLOAT;
            case MATRIX -> throw new IllegalArgumentException("a point of a mesh brings no matrix");
        };
    }

    /** {@code shader}, compiled and handed to the card. */
    private Kept<Of.Module> module(final Shader shader) {
        final VkDevice device = reach.device();
        final ByteBuffer compiled = compiler.compiled(shader);
        final ByteBuffer words = MemoryUtil.memAlloc(compiled.remaining());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            words.put(compiled).flip();
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateShaderModule").of(vkCreateShaderModule(device,
                    VkShaderModuleCreateInfo.calloc(stack).sType$Default().pCode(words), null, made));
            return reach.owner().kept("a shader, compiled", made.get(0),
                    raw -> vkDestroyShaderModule(device, raw, null));
        } finally {
            MemoryUtil.memFree(words);
        }
    }
}
