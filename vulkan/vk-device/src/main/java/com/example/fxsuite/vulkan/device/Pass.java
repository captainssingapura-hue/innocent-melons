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
    private final Optional<Read> read;
    private final Optional<Drawing> instead;
    /** Where the things drawn are gathered, when they are drawn on no picture but only to be kept for rays. */
    private final Optional<Gathered> gathered;
    private final Set<Drawing> told = new HashSet<>();
    /** Whether a look that is mixed with what is drawn has been drawn with: after one, nothing is written over. */
    private boolean mixing;
    private boolean ended;

    /**
     * What draws, each look told {@code frame}: on colours, each look reading what {@code read} has; or, on
     * depths alone, everything drawn with {@code instead}.
     */
    Pass(final VkCommandBuffer commands, final Filling frame, final Optional<Read> read,
         final Optional<Drawing> instead) {
        this.commands = commands;
        this.frame = frame;
        this.read = read;
        this.instead = instead;
        this.gathered = Optional.empty();
    }

    /** What draws on no picture: each thing drawn is added to {@code gathered}, where it stands, and no more. */
    Pass(final Gathered gathered) {
        this.commands = null;
        this.frame = null;
        this.read = Optional.empty();
        this.instead = Optional.empty();
        this.gathered = Optional.of(gathered);
    }

    /**
     * {@code mesh} drawn with {@code drawing}, told {@code placed}: where it stands, as the camera sees it. A mesh
     * laid out otherwise than the look takes its points, and a telling laid out otherwise than the look reads it,
     * are refused.
     */
    public void draw(final Drawing drawing, final Sent mesh, final Filling placed) {
        draw(drawing, mesh, placed, Optional.empty());
    }

    /**
     * {@code mesh} drawn with {@code drawing} once for each of {@code copies}, in one draw: each copy where it takes
     * the mesh, and then where {@code placed} says the whole stands, as the camera sees it. None, and nothing is
     * drawn. Gathered for rays, each copy stands of itself.
     */
    public void draw(final Drawing drawing, final Sent mesh, final Filling placed, final Copies copies) {
        draw(drawing, mesh, placed, Optional.of(copies));
    }

    private void draw(final Drawing drawing, final Sent mesh, final Filling placed, final Optional<Copies> asked) {
        if (ended) {
            throw new IllegalStateException("drawing on this picture has ended, and nothing more is drawn on it");
        }
        if (drawing.mixes() && (instead.isPresent() || gathered.isPresent())) {
            // what is seen through is nowhere in a picture of depths, and stands in the way of no ray
            return;
        }
        final Copies copies = asked.orElse(drawing.once());
        if (copies.count() == 0) {
            return;
        }
        if (gathered.isPresent()) {
            gathered.get().stood(drawing.laid(), mesh, placed, copies.each());
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
            with.told(frame, read);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer telling = placed.filled();
            final ByteBuffer pushed = stack.malloc(telling.remaining()).put(telling).flip();
            vkCmdBindPipeline(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, with.pipeline().raw());
            vkCmdBindDescriptorSets(commands, VK_PIPELINE_BIND_POINT_GRAPHICS, with.telling().raw(), 0,
                    stack.longs(with.bound().raw()), null);
            vkCmdBindVertexBuffers(commands, 0,
                    stack.longs(mesh.points().buffer().raw(), copies.bytes().buffer().raw()), stack.longs(0, 0));
            vkCmdBindIndexBuffer(commands, mesh.corners().buffer().raw(), 0, VK_INDEX_TYPE_UINT32);
            vkCmdPushConstants(commands, with.telling().raw(), Described.PUSHED, 0, pushed);
            vkCmdDrawIndexed(commands, mesh.count(), copies.count(), 0, 0, 0);
        }
    }

    /** Drawing ended: this draws no more. */
    void ended() {
        if (!ended && gathered.isPresent()) {
            ended = true;
        }
        if (!ended) {
            ended = true;
            vkCmdEndRendering(commands);
        }
    }
}
