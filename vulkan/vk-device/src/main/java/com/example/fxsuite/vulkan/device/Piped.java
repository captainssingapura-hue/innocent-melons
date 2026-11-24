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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.vulkan.VK13.*;

/**
 * A look built ready to draw with: its two shaders compiled, and the card told how a mesh's points are laid out,
 * from the same layout the shaders' declarations and the mesh's bytes are made from. Built once, before any frame;
 * the compiled shaders are let go as soon as the look is built.
 */
record Piped(Reach reach, Compiler compiler) {

    private static final String BEGINS = "main";

    /**
     * The look of {@code vertex} and {@code fragment}, laid out as {@code laid} says and told as {@code telling}.
     * With no {@code fragment} it colours nothing, and is built to draw on a picture of depths alone. Where
     * {@code mixed}, what it draws is mixed with what is already drawn, as far as its own colour lets that show.
     */
    Kept<Of.Pipeline> pipeline(final Laid laid, final Shader vertex, final Optional<Shader> fragment,
                               final boolean mixed, final Kept<Of.Telling> telling) {
        if (vertex.stage() != Stage.VERTEX || fragment.filter(shader -> shader.stage() != Stage.FRAGMENT).isPresent()) {
            throw new IllegalArgumentException("a look is a shader for each point and one for each pixel, not "
                    + vertex.stage() + " and " + fragment.map(Shader::stage));
        }
        final VkDevice device = reach.device();
        final List<Kept<Of.Module>> modules = new ArrayList<>(List.of(module(vertex)));
        try (MemoryStack stack = MemoryStack.stackPush()) {
            fragment.ifPresent(shader -> modules.add(module(shader)));
            final Fixed fixed = new Fixed(stack);
            final boolean colours = fragment.isPresent();
            final VkGraphicsPipelineCreateInfo.Buffer asked = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                    .sType$Default().pNext(fixed.drawnOn(colours).address())
                    .pStages(stages(modules, stack))
                    .pVertexInputState(taken(laid, fields(laid, colours), stack))
                    .pInputAssemblyState(fixed.triangles()).pViewportState(fixed.sized())
                    .pRasterizationState(fixed.facing()).pMultisampleState(fixed.sampledOnce())
                    .pDepthStencilState(fixed.nearest(mixed)).pColorBlendState(fixed.written(colours, mixed))
                    .pDynamicState(fixed.saidLater()).layout(telling.raw());
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateGraphicsPipelines")
                    .of(vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, asked, null, made));
            return reach.owner().kept("a look", made.get(0), raw -> vkDestroyPipeline(device, raw, null));
        } finally {
            modules.reversed().forEach(Kept::close);
        }
    }

    /** Which of a point's fields a look takes: all of them, or, colouring nothing, only where the point is. */
    private List<String> fields(final Laid laid, final boolean colours) {
        return colours ? laid.point().fields().stream().map(Field::name).toList() : laid.placing();
    }

    /** The shaders a look runs: the one for each point first, and the one for each pixel where there is one. */
    private VkPipelineShaderStageCreateInfo.Buffer stages(final List<Kept<Of.Module>> modules,
                                                          final MemoryStack stack) {
        final VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo
                .calloc(modules.size(), stack);
        for (int which = 0; which < modules.size(); which++) {
            stages.get(which).sType$Default()
                    .stage(which == 0 ? VK_SHADER_STAGE_VERTEX_BIT : VK_SHADER_STAGE_FRAGMENT_BIT)
                    .module(modules.get(which).raw()).pName(stack.UTF8(BEGINS));
        }
        return stages;
    }

    /**
     * How a mesh's points are taken in, packed as {@code laid}'s point says, each field where its shader looks for
     * it; and, from a second run of bytes, each copy drawn, packed as its copy says, from where the shader looks for
     * it on, a matrix a column at a time.
     */
    private VkPipelineVertexInputStateCreateInfo taken(final Laid laid, final List<String> only,
                                                       final MemoryStack stack) {
        final Layout point = laid.point();
        final Layout copy = laid.copy();
        final VkVertexInputBindingDescription.Buffer each = VkVertexInputBindingDescription.calloc(2, stack);
        each.get(0).binding(0).stride(point.packedBytes()).inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
        each.get(1).binding(1).stride(copy.packedBytes()).inputRate(VK_VERTEX_INPUT_RATE_INSTANCE);
        final int copied = copy.fields().stream().mapToInt(field -> columns(field.kind())).sum();
        final VkVertexInputAttributeDescription.Buffer fields = VkVertexInputAttributeDescription
                .calloc(only.size() + copied, stack);
        int told = 0;
        for (int place = 0; place < point.fields().size(); place++) {
            final Field field = point.fields().get(place);
            if (only.contains(field.name())) {
                fields.get(told++).location(place).binding(0).format(format(field.kind()))
                        .offset(point.packedAt(field.name()));
            }
        }
        int place = laid.copyPlace();
        for (final Field field : copy.fields()) {
            for (int column = 0; column < columns(field.kind()); column++) {
                final boolean matrix = field.kind() == Kind.MATRIX;
                fields.get(told++).location(place++).binding(1)
                        .format(format(matrix ? Kind.QUAD : field.kind()))
                        .offset(copy.packedAt(field.name()) + column * Kind.QUAD.bytes());
            }
        }
        return VkPipelineVertexInputStateCreateInfo.calloc(stack).sType$Default()
                .pVertexBindingDescriptions(each).pVertexAttributeDescriptions(fields);
    }

    /** How many places a field of {@code kind} takes in: a matrix four, a column in each; anything else one. */
    private int columns(final Kind kind) {
        return kind == Kind.MATRIX ? Kind.MATRIX.numbers() / Kind.QUAD.numbers() : 1;
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
