package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.lwjgl.vulkan.VK13.*;

/**
 * What draws. It is made when drawing on a picture is begun and ended when that drawing ends, and is handed only to
 * whoever draws in between: nothing else can draw, and once ended it refuses. Each look it draws with is told what
 * the frame holds, and which picture of depths to look things up in, once, the first time the look is drawn with.
 *
 * <p>Drawing on a picture of depths alone, it draws everything with the one look that colours nothing, whatever
 * look a thing is asked to be drawn with: only where a thing is goes into such a picture. Whoever draws cannot tell
 * which kind of picture it draws on, so what is drawn for the one can be drawn for the other as it stands.
 *
 * <p>A look that is mixed with what is already drawn ({@link Drawing#mixes}) is for what is seen through. Drawn on
 * colours, it has to come after every look that writes over what is there, since it leaves no depth to hide what
 * comes after it: once one has been drawn with, a look that writes over is refused. Drawn on depths alone, it
 * leaves nothing: what is seen through stands in the way of nothing.
 */
public final class Pass {

    private final VkCommandBuffer commands;
    private final Filling frame;
    private final Optional<Depths> looked;
    private final Optional<Drawing> instead;
    private final Set<Drawing> told = new HashSet<>();
    /** Whether a look that is mixed with what is drawn has been drawn with: after one, nothing is written over. */
    private boolean mixing;
    private boolean ended;

    /**
     * What draws, each look told {@code frame}: on colours, each look looking things up in {@code looked}; or, on
     * depths alone, everything drawn with {@code instead}.
     */
    Pass(final VkCommandBuffer commands, final Filling frame, final Optional<Depths> looked,
         final Optional<Drawing> instead) {
        this.commands = commands;
        this.frame = frame;
        this.looked = looked;
        this.instead = instead;
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
        if (drawing.mixes() && instead.isPresent()) {
            // what is seen through is nowhere in a picture of depths: it stands in the way of nothing
            return;
        }
        if (mixing && !drawing.mixes()) {
            throw new IllegalStateException("a look that writes over what is drawn comes before every look that is"
                    + " mixed with it: once one is mixed in, nothing more is written over");
        }
        mixing = drawing.mixes();
        final Drawing with = instead.orElse(drawing);
        if (!mesh.layout().equals(with.laid().point())) {
            throw new IllegalArgumentException("the look takes points laid out as " + with.laid().point()
                    + ", and the mesh's are " + mesh.layout());
        }
        if (!placed.layout().equals(with.laid().draw())) {
            throw new IllegalArgumentException("the look is told what is laid out as " + with.laid().draw()
                    + ", not as " + placed.layout());
        }
        if (told.add(with)) {
            with.told(frame, looked);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer telling = placed.filled();
            final ByteBuffer pushed = stack.malloc(telling.remaining()).put(telling).flip();
            vkCmdBindPipeline(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, with.pipeline().raw());
            vkCmdBindDescriptorSets(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, with.telling().raw(), 0,
                    stack.longs(with.bound().raw()), null);
            vkCmdBindVertexBuffers(commands, 0, stack.longs(mesh.points().buffer().raw()), stack.longs(0));
            vkCmdBindIndexBuffer(commands, mesh.corners().buffer().raw(), 0, VK_INDEX_TYPE_UINT32);
            vkCmdPushConstants(commands, with.telling().raw(), Described.PUSHED, 0, pushed);
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
