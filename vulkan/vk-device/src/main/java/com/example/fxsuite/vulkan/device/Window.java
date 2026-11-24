package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.space.Vec3;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPresentInfoKHR;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK13.*;

/**
 * A window the card draws to, on the desk. It is made hidden and shown when asked, so nothing is seen of it before
 * its first frame. Its size is asked for as the screen's own windows are measured; on a screen that draws everything
 * larger it has more pixels than that, and its width and height here are its pixels. Drawn on exactly as a picture
 * with no window is. When its size changes, its pictures are made again at the new size before the next frame;
 * while it has no size at all, as when it is put away, nothing is drawn.
 *
 * <p>Its keys come as they are pressed: the system's input method, which would gather key presses to compose the
 * characters of some scripts, is parted from it when it is made ({@link PlainKeys}).
 */
public final class Window implements Target, AutoCloseable {

    /** How long a picture of the window's is waited for, in nanoseconds: ten seconds. */
    private static final long PATIENCE = 10_000_000_000L;

    private final Reach reach;
    private final Recording recording;
    private final Read nothing;
    private final Kept<Of.Glass> glass;
    private final Pointed pointed;
    private final Kept<Of.Surface> surface;
    private final Kept<Of.Signal> ready;
    private Shown shown;
    private boolean stale;

    /** A window on {@code desk}; a frame drawn to it with nothing to look up looks things up in {@code nothing}. */
    Window(final Reach reach, final Recording recording, final Read nothing, final Desk desk, final String title,
           final int width, final int height) {
        desk.mine();
        this.reach = reach;
        this.recording = recording;
        this.nothing = nothing;
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        final long made = glfwCreateWindow(width, height, title, 0, 0);
        if (made == 0) {
            throw new Refused("glfwCreateWindow", "no window " + width + " by " + height + " could be made");
        }
        this.glass = reach.owner().kept("the window " + title, made, raw -> {
            glfwFreeCallbacks(raw);
            glfwDestroyWindow(raw);
        });
        new PlainKeys(glass).kept();
        this.pointed = new Pointed(glass);
        this.surface = new Surfaced(reach).of(glass);
        this.ready = new Signalled(reach).by("a window's picture being ready");
        this.shown = new Shown(reach, surface);
    }

    /**
     * Shown on the screen, the top left corner of its frame {@code x} across the screen and {@code y} down, measured
     * as the screen's own windows are: on a screen that draws everything larger, that is more pixels than it says.
     */
    public void shown(final int x, final int y) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer across = stack.mallocFloat(1);
            final FloatBuffer down = stack.mallocFloat(1);
            glfwGetWindowContentScale(glass.raw(), across, down);
            // the window is placed by its inside, so its frame's left and top are added to stand the frame there
            final IntBuffer left = stack.mallocInt(1);
            final IntBuffer top = stack.mallocInt(1);
            glfwGetWindowFrameSize(glass.raw(), left, top, null, null);
            glfwSetWindowPos(glass.raw(), Math.round(x * across.get(0)) + left.get(0),
                    Math.round(y * down.get(0)) + top.get(0));
            glfwShowWindow(glass.raw());
        }
    }

    /** What the mouse did in it since this was last asked: to be asked once a frame, after the desk has heard. */
    public Moved mouse() {
        return pointed.moved();
    }

    /** The clicks of its first button since this was last asked, each where it was in the picture, the first first. */
    public List<Clicked> clicks() {
        return pointed.clicks();
    }

    /**
     * The mouse taken ({@code true}) or let go: taken, its pointer is hidden and held in this window, and it goes on
     * saying how far it moves however far that is, as a game wants of it; let go, it is the desk's again. It is the
     * window's only while the window is the one being used.
     */
    public void taken(final boolean taken) {
        glfwSetInputMode(glass.raw(), GLFW_CURSOR, taken ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    /** Whether {@code key} is held down now, with this window the one being typed in. */
    public boolean held(final Key key) {
        return glfwGetKey(glass.raw(), key.one()) == GLFW_PRESS || glfwGetKey(glass.raw(), key.other()) == GLFW_PRESS;
    }

    /** Whether its keys come as they are pressed, no input method of the system's gathering them first. */
    boolean plainKeys() {
        return new PlainKeys(glass).plain();
    }

    /** Whether it is still wanted: true until whoever sits at the desk closes it. */
    public boolean open() {
        return !glfwWindowShouldClose(glass.raw());
    }

    @Override
    public int width() {
        return shown.width();
    }

    @Override
    public int height() {
        return shown.height();
    }

    /** A frame drawn and handed to the screen, which shows it when it next draws itself. */
    @Override
    public void drawn(final Vec3 sky, final Filling frame, final Consumer<Pass> drawing) {
        drawn(sky, frame, nothing, drawing);
    }

    /** The same, its looks looking things up in {@code looked}. */
    @Override
    public void drawn(final Vec3 sky, final Filling frame, final Depths looked, final Consumer<Pass> drawing) {
        drawn(sky, frame, nothing.looking(looked), drawing);
    }

    /** The same, its looks asking rays of {@code stood}. */
    @Override
    public void drawn(final Vec3 sky, final Filling frame, final Standing stood, final Consumer<Pass> drawing) {
        drawn(sky, frame, nothing.asking(stood), drawing);
    }

    private void drawn(final Vec3 sky, final Filling frame, final Read read, final Consumer<Pass> drawing) {
        if (!fitted()) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer which = stack.mallocInt(1);
            final int lent = new Checked("vkAcquireNextImageKHR").allowing(vkAcquireNextImageKHR(reach.device(),
                    shown.chain().raw(), PATIENCE, ready.raw(), VK_NULL_HANDLE, which),
                    VK_SUBOPTIMAL_KHR, VK_ERROR_OUT_OF_DATE_KHR);
            if (lent == VK_ERROR_OUT_OF_DATE_KHR) {
                stale = true;
                return;
            }
            final Ground ground = shown.ground(which.get(0));
            final Strokes strokes = recording.begun();
            strokes.drawn(ground, sky, frame, read, drawing);
            strokes.moved(ground.colours(), Move.TO_BE_SHOWN);
            final Kept<Of.Signal> done = shown.drawn(which.get(0));
            recording.sent(ready, done);
            final int handed = new Checked("vkQueuePresentKHR").allowing(vkQueuePresentKHR(reach.queue(),
                    VkPresentInfoKHR.calloc(stack).sType$Default().pWaitSemaphores(stack.longs(done.raw()))
                            .swapchainCount(1).pSwapchains(stack.longs(shown.chain().raw())).pImageIndices(which)),
                    VK_SUBOPTIMAL_KHR, VK_ERROR_OUT_OF_DATE_KHR);
            stale = lent != VK_SUCCESS || handed != VK_SUCCESS;
        }
    }

    /** Its pictures made again where they no longer fit it; false while it has no size, and nothing can be drawn. */
    private boolean fitted() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer wide = stack.mallocInt(1);
            final IntBuffer tall = stack.mallocInt(1);
            glfwGetFramebufferSize(glass.raw(), wide, tall);
            if (wide.get(0) == 0 || tall.get(0) == 0) {
                return false;
            }
            if (stale || wide.get(0) != shown.width() || tall.get(0) != shown.height()) {
                shown.close();
                shown = new Shown(reach, surface);
                stale = false;
            }
            return true;
        }
    }

    @Override
    public void close() {
        shown.close();
        ready.close();
        surface.close();
        glass.close();
    }
}
