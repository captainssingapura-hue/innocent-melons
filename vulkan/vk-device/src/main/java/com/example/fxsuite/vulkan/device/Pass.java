package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK13.*;

/**
 * What draws. It is made when drawing on a picture is begun and ended when that drawing ends, and is handed only to
 * whoever draws in between: nothing else can draw, and once ended it refuses. Each look it draws with is told what
 * the frame holds, once, the first time the look is drawn with.
 */
public final class Pass {

    private final VkCommandBuffer commands;
    private final Filling frame;
    private final Set<Drawing> told = new HashSet<>();
    private boolean ended;

    Pass(final VkCommandBuffer commands, final Filling frame) {
        this.commands = commands;
        this.frame = frame;
    }

    /**
     * {@code mesh} drawn with {@code drawing}, told {@code placed}: where it stands, as the camera sees it. A mesh
     * laid out otherwise than the look takes its points, and a telling laid out otherwise than the look reads it,
     * are refused.
     */
    public void draw(final Drawing drawing, final Sent mesh, final Filling placed) {
        if (ended) {
            throw new IllegalStateException("drawing on this picture has ended, and nothing more is drawn on it");
        }
        if (!mesh.layout().equals(drawing.laid().point())) {
            throw new IllegalArgumentException("the look takes points laid out as " + drawing.laid().point()
                    + ", and the mesh's are " + mesh.layout());
        }
        if (!placed.layout().equals(drawing.laid().draw())) {
            throw new IllegalArgumentException("the look is told what is laid out as " + drawing.laid().draw()
                    + ", not as " + placed.layout());
        }
        if (told.add(drawing)) {
            drawing.told(frame);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer telling = placed.filled();
            final ByteBuffer pushed = stack.malloc(telling.remaining()).put(telling).flip();
            vkCmdBindPipeline(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, drawing.pipeline().raw());
            vkCmdBindDescriptorSets(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, drawing.telling().raw(), 0,
                    stack.longs(drawing.bound().raw()), null);
            vkCmdBindVertexBuffers(commands, 0, stack.longs(mesh.points().buffer().raw()), stack.longs(0));
            vkCmdBindIndexBuffer(commands, mesh.corners().buffer().raw(), 0, VK_INDEX_TYPE_UINT32);
            vkCmdPushConstants(commands, drawing.telling().raw(), Described.PUSHED, 0, pushed);
            vkCmdDrawIndexed(commands, mesh.count(), 1, 0, 0, 0);
        }
    }

    /** Drawing ended: this draws no more. */
    void ended() {
        if (!ended) {
            ended = true;
            vkCmdEndRendering(commands);
        }
    }
}
