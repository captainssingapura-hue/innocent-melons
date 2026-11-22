package com.example.fxsuite.vulkan.device;

import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK13.*;

/**
 * A picture on the card made ready for what is done with it next. The card keeps a picture differently for being
 * drawn on, read from or shown, and has to be told each change: from what to what, and what must be finished with
 * it before and may begin after. Each change the kit makes is one of these, said once.
 */
enum Move {

    /** A picture of colours, whatever was on it forgotten, made ready to be drawn on. */
    TO_BE_DRAWN_ON(VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
            0, VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
            VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
    /** A picture of depths, whatever was on it forgotten, made ready to be drawn against. */
    TO_BE_DRAWN_AGAINST(VK_IMAGE_ASPECT_DEPTH_BIT, VK_IMAGE_LAYOUT_UNDEFINED,
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, 0, VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
            VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT,
            VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT | VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT),
    /** A picture of depths just drawn on, made ready to be looked up by what is drawn next. */
    TO_BE_LOOKED_UP(VK_IMAGE_ASPECT_DEPTH_BIT, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
            VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL, VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
            VK_ACCESS_SHADER_READ_BIT,
            VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT | VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT,
            VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT),
    /** A picture of colours just drawn on, made ready to be read back. */
    TO_BE_READ(VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
            VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, VK_ACCESS_TRANSFER_READ_BIT,
            VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT),
    /** A picture of colours just drawn on, made ready to be shown in a window. */
    TO_BE_SHOWN(VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
            VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, 0,
            VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT);

    private final int aspect;
    private final int from;
    private final int to;
    private final int finished;
    private final int begun;
    private final int after;
    private final int before;

    Move(final int aspect, final int from, final int to, final int finished, final int begun, final int after,
         final int before) {
        this.aspect = aspect;
        this.from = from;
        this.to = to;
        this.finished = finished;
        this.begun = begun;
        this.after = after;
        this.before = before;
    }

    /** Which part of the picture is changed. */
    int aspect() {
        return aspect;
    }

    /** How the picture was kept. */
    int from() {
        return from;
    }

    /** How it is kept from now on. */
    int to() {
        return to;
    }

    /** What use of it has to be finished first. */
    int finished() {
        return finished;
    }

    /** What use of it begins after. */
    int begun() {
        return begun;
    }

    /** The step of drawing the change comes after. */
    int after() {
        return after;
    }

    /** The step of drawing the change comes before. */
    int before() {
        return before;
    }
}
