package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.space.Vec3;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAccelerationStructureBuildGeometryInfoKHR;
import org.lwjgl.vulkan.VkAccelerationStructureBuildRangeInfoKHR;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;
import org.lwjgl.vulkan.VkViewport;

import java.util.Optional;
import java.util.function.Consumer;

import static org.lwjgl.vulkan.KHRAccelerationStructure.vkCmdBuildAccelerationStructuresKHR;
import static org.lwjgl.vulkan.VK13.*;

/**
 * A frame's commands as they are written down, outside of any drawing: a picture made ready for what comes next,
 * drawing begun on a picture, a picture read back into bytes. What draws is not here but in the {@link Pass} that
 * beginning hands over, so nothing can be drawn before drawing is begun or after it is ended.
 */
final class Strokes {

    private static final float SOLID = 1;
    /** The furthest there is: depth runs backwards, from 1 where the camera begins to see down to nothing. */
    private static final float FURTHEST = 0;

    private final VkCommandBuffer commands;

    Strokes(final VkCommandBuffer commands) {
        this.commands = commands;
    }

    /** {@code image} made ready as {@code move} says. */
    void moved(final Kept<Of.Image> image, final Move move) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkImageMemoryBarrier.Buffer change = VkImageMemoryBarrier.calloc(1, stack).sType$Default()
                    .srcAccessMask(move.finished()).dstAccessMask(move.begun())
                    .oldLayout(move.from()).newLayout(move.to())
                    .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED).dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .image(image.raw())
                    .subresourceRange(all -> all.aspectMask(move.aspect()).levelCount(1).layerCount(1));
            vkCmdPipelineBarrier(commands, move.after(), move.before(), 0, null, null, change);
        }
    }

    /**
     * Drawing begun on {@code colours}, against {@code depths}, both {@code width} pixels by {@code height}: the
     * whole picture cleared to {@code sky} and to the furthest there is.
     */
    private void begun(final Kept<Of.View> colours, final Kept<Of.View> depths, final int width, final int height,
               final Vec3 sky) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkRenderingAttachmentInfo.Buffer on = VkRenderingAttachmentInfo.calloc(1, stack).sType$Default()
                    .imageView(colours.raw()).imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR).storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .clearValue(clear -> clear.color().float32(0, (float) sky.x()).float32(1, (float) sky.y())
                            .float32(2, (float) sky.z()).float32(3, SOLID));
            final VkRenderingAttachmentInfo against = VkRenderingAttachmentInfo.calloc(stack).sType$Default()
                    .imageView(depths.raw()).imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR).storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .clearValue(clear -> clear.depthStencil().depth(FURTHEST));
            final VkRenderingInfo whole = VkRenderingInfo.calloc(stack).sType$Default()
                    .renderArea(area -> area.extent(size -> size.width(width).height(height)))
                    .layerCount(1).pColorAttachments(on).pDepthAttachment(against);
            vkCmdBeginRendering(commands, whole);
            sized(width, height, stack);
        }
    }

    /** The whole of a picture {@code width} pixels by {@code height} said to be what is drawn on. */
    private void sized(final int width, final int height, final MemoryStack stack) {
        vkCmdSetViewport(commands, 0, VkViewport.calloc(1, stack).width(width).height(height).maxDepth(1));
        final VkRect2D.Buffer within = VkRect2D.calloc(1, stack);
        within.extent().width(width).height(height);
        vkCmdSetScissor(commands, 0, within);
    }

    /**
     * A frame drawn on {@code ground}: its colours and its depths made ready, the whole cleared to {@code sky}, and
     * whatever {@code drawing} draws drawn, by what is handed to it and ended as soon as it is done. Each look
     * drawn with is told what {@code frame} holds, and reads what {@code read} has.
     */
    void drawn(final Ground ground, final Vec3 sky, final Filling frame, final Read read,
               final Consumer<Pass> drawing) {
        moved(ground.colours(), Move.TO_BE_DRAWN_ON);
        moved(ground.depths().image(), Move.TO_BE_DRAWN_AGAINST);
        begun(ground.view(), ground.depths().view(), ground.width(), ground.height(), sky);
        final Pass pass = new Pass(commands, frame, Optional.of(read), Optional.empty());
        try {
            drawing.accept(pass);
        } finally {
            pass.ended();
        }
    }

    /**
     * Drawing begun on {@code depths} alone, {@code size} pixels each way, whatever was on it forgotten: the whole
     * of it set to the furthest there is, and kept when the drawing ends.
     */
    private void against(final Sheet depths, final int size) {
        moved(depths.image(), Move.TO_BE_DRAWN_AGAINST);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkRenderingAttachmentInfo against = VkRenderingAttachmentInfo.calloc(stack).sType$Default()
                    .imageView(depths.view().raw()).imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR).storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .clearValue(clear -> clear.depthStencil().depth(FURTHEST));
            final VkRenderingInfo whole = VkRenderingInfo.calloc(stack).sType$Default()
                    .renderArea(area -> area.extent(extent -> extent.width(size).height(size)))
                    .layerCount(1).pDepthAttachment(against);
            vkCmdBeginRendering(commands, whole);
            sized(size, size, stack);
        }
    }

    /** {@code depths}, {@code size} pixels each way, set all over to the furthest there is, and ready to look up. */
    void emptied(final Sheet depths, final int size) {
        against(depths, size);
        vkCmdEndRendering(commands);
        moved(depths.image(), Move.TO_BE_LOOKED_UP);
    }

    /**
     * {@code depths}, {@code size} pixels each way, drawn afresh: whatever {@code drawing} draws drawn on it with
     * {@code with}, which is told what {@code frame} holds; then made ready to be looked up.
     */
    void traced(final Sheet depths, final int size, final Filling frame, final Drawing with,
                final Consumer<Pass> drawing) {
        against(depths, size);
        final Pass pass = new Pass(commands, frame, Optional.empty(), Optional.of(with));
        try {
            drawing.accept(pass);
        } finally {
            pass.ended();
        }
        moved(depths.image(), Move.TO_BE_LOOKED_UP);
    }

    /** What {@code asked} describes built: pieces a ray can be asked of, {@code count} of them. */
    void built(final VkAccelerationStructureBuildGeometryInfoKHR.Buffer asked, final int count) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkAccelerationStructureBuildRangeInfoKHR.Buffer all = VkAccelerationStructureBuildRangeInfoKHR
                    .calloc(1, stack).primitiveCount(count);
            vkCmdBuildAccelerationStructuresKHR(commands, asked, stack.pointers(all.address()));
        }
    }

    /** {@code image}, {@code width} pixels by {@code height} and ready to be read, copied whole into {@code into}. */
    void read(final Kept<Of.Image> image, final Bytes into, final int width, final int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkBufferImageCopy.Buffer whole = VkBufferImageCopy.calloc(1, stack)
                    .imageSubresource(part -> part.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT).layerCount(1))
                    .imageExtent(size -> size.width(width).height(height).depth(1));
            vkCmdCopyImageToBuffer(commands, image.raw(), VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                    into.buffer().raw(), whole);
        }
    }
}
