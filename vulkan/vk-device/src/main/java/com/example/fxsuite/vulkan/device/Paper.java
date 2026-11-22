package com.example.fxsuite.vulkan.device;

import static org.lwjgl.vulkan.VK13.*;

/**
 * What a picture on the card is of, and so how it is kept and used: the colours that are shown, a byte each of blue,
 * green, red and how solid, as a screen takes them; or how deep each pixel is, a single number each, kept only
 * while a frame is drawn or kept on to be looked up by what is drawn afterwards. Every picture the kit draws on is
 * one of these, so a look built once draws to a window and to a picture alike.
 */
enum Paper {

    /** Colours: drawn on, and read back or shown. */
    COLOUR(VK_FORMAT_B8G8R8A8_UNORM, VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
            VK_IMAGE_ASPECT_COLOR_BIT),
    /** Depths: drawn on, and kept only while a frame is drawn. */
    DEPTH(VK_FORMAT_D32_SFLOAT, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, VK_IMAGE_ASPECT_DEPTH_BIT),
    /** Depths: drawn on, kept, and looked up by the looks drawn afterwards. */
    DEPTH_LOOKED_UP(VK_FORMAT_D32_SFLOAT, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
            VK_IMAGE_ASPECT_DEPTH_BIT);

    private final int format;
    private final int use;
    private final int aspect;

    Paper(final int format, final int use, final int aspect) {
        this.format = format;
        this.use = use;
        this.aspect = aspect;
    }

    /** How a pixel of it is kept, as the card names it. */
    int format() {
        return format;
    }

    /** What it is used for, as the card names it. */
    int use() {
        return use;
    }

    /** Which part of a picture it is, as the card names it. */
    int aspect() {
        return aspect;
    }
}
