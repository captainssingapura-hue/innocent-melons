package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK13.*;

/**
 * The pictures a window shows in turn, at the size the window is when they are made, and the depths drawn against.
 * The pictures are the window's own, lent to be drawn on and gone when these go. They are kept as the kit keeps
 * every picture of colours, so a look built once draws to them as to any other; a window that cannot keep them so
 * is refused. Shown in step with the screen, one frame to each time the screen is drawn.
 */
final class Shown {

    private final Reach reach;
    private final Kept<Of.Shown> chain;
    private final List<Kept<Of.Image>> pictures = new ArrayList<>();
    private final List<Kept<Of.View>> views = new ArrayList<>();
    private final List<Kept<Of.Signal>> drawn = new ArrayList<>();
    private final Sheet depths;
    private final int width;
    private final int height;
    private boolean closed;

    /** The pictures {@code surface}'s window shows, at the size it is now. */
    Shown(final Reach reach, final Kept<Of.Surface> surface) {
        this.reach = reach;
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            kept(reach, surface, stack);
            final VkSurfaceCapabilitiesKHR can = VkSurfaceCapabilitiesKHR.malloc(stack);
            new Checked("vkGetPhysicalDeviceSurfaceCapabilitiesKHR")
                    .of(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(reach.card(), surface.raw(), can));
            this.width = can.currentExtent().width();
            this.height = can.currentExtent().height();
            final int spare = can.minImageCount() + 1;
            final VkSwapchainCreateInfoKHR asked = VkSwapchainCreateInfoKHR.calloc(stack).sType$Default()
                    .surface(surface.raw())
                    .minImageCount(can.maxImageCount() > 0 ? Math.min(spare, can.maxImageCount()) : spare)
                    .imageFormat(Paper.COLOUR.format()).imageColorSpace(VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
                    .imageExtent(size -> size.width(width).height(height)).imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT).imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .preTransform(can.currentTransform()).compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .presentMode(VK_PRESENT_MODE_FIFO_KHR).clipped(true);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateSwapchainKHR").of(vkCreateSwapchainKHR(device, asked, null, made));
            this.chain = reach.owner().kept("a window's pictures", made.get(0),
                    raw -> vkDestroySwapchainKHR(device, raw, null));
            lent(reach, stack);
        }
        this.depths = new Sheet(reach, "a window's depths", Paper.DEPTH, width, height);
    }

    /** Refuses a window that cannot keep its pictures as the kit keeps colours. */
    private void kept(final Reach reach, final Kept<Of.Surface> surface, final MemoryStack stack) {
        final IntBuffer count = stack.mallocInt(1);
        new Checked("vkGetPhysicalDeviceSurfaceFormatsKHR")
                .of(vkGetPhysicalDeviceSurfaceFormatsKHR(reach.card(), surface.raw(), count, null));
        final VkSurfaceFormatKHR.Buffer ways = VkSurfaceFormatKHR.malloc(count.get(0), stack);
        new Checked("vkGetPhysicalDeviceSurfaceFormatsKHR")
                .of(vkGetPhysicalDeviceSurfaceFormatsKHR(reach.card(), surface.raw(), count, ways));
        if (ways.stream().noneMatch(way -> way.format() == Paper.COLOUR.format()
                && way.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)) {
            throw new Refused("vkCreateSwapchainKHR", "the window cannot keep its pictures as the kit keeps colours");
        }
    }

    /**
     * The window's own pictures, each held while these are; a view of each; and for each, what says it has been
     * drawn on and may be shown. Each picture has its own, since the window may still be waiting on one picture's
     * when the next is drawn.
     */
    private void lent(final Reach reach, final MemoryStack stack) {
        final IntBuffer count = stack.mallocInt(1);
        new Checked("vkGetSwapchainImagesKHR")
                .of(vkGetSwapchainImagesKHR(reach.device(), chain.raw(), count, null));
        final LongBuffer lent = stack.mallocLong(count.get(0));
        new Checked("vkGetSwapchainImagesKHR")
                .of(vkGetSwapchainImagesKHR(reach.device(), chain.raw(), count, lent));
        for (int which = 0; which < count.get(0); which++) {
            final Kept<Of.Image> picture = reach.owner().kept("a window's picture", lent.get(which), raw -> { });
            pictures.add(picture);
            views.add(new Viewed(reach).of(picture, Paper.COLOUR));
            drawn.add(new Signalled(reach).by("a window's picture being drawn"));
        }
    }

    Kept<Of.Shown> chain() {
        return chain;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    /** What says picture {@code which} of them has been drawn on. */
    Kept<Of.Signal> drawn(final int which) {
        return drawn.get(which);
    }

    /** Picture {@code which} of them, to be drawn on. */
    Ground ground(final int which) {
        return new Ground(pictures.get(which), views.get(which), depths, width, height);
    }

    /** Closed, once the card has finished with everything it was asked: the screen may still be showing one. */
    void close() {
        if (closed) {
            return;
        }
        closed = true;
        new Checked("vkDeviceWaitIdle").of(vkDeviceWaitIdle(reach.device()));
        depths.close();
        drawn.reversed().forEach(Kept::close);
        views.reversed().forEach(Kept::close);
        pictures.reversed().forEach(Kept::close);
        chain.close();
    }
}
