package com.example.fxsuite.vulkan.render;

/**
 * The ways shadows are found. Either gives each point one answer, how much of the sun reaches it, and the lighting
 * does the same with it whichever way it was found; so a frame may be drawn either way, and the way changed from
 * one frame to the next with nothing built anew. More ways may be added here.
 */
public enum Shadowing {

    /**
     * From the sun's own picture of the frame, drawn first: a point is in shadow where the picture has something
     * nearer the sun. What any card draws. As fine as the picture's pixels, and only over the ground the picture
     * covers.
     */
    PICTURE("The sun's picture"),

    /**
     * By a ray from each point toward the sun: a point is in shadow where anything stands along it. Exact to the
     * triangle, and as far as there is anything to stand in the way; for a card that answers rays.
     */
    RAYS("Rays to the sun");

    private final String said;

    Shadowing(final String said) {
        this.said = said;
    }

    /** The way, in a few words, for whoever chooses between them. */
    public String said() {
        return said;
    }
}
