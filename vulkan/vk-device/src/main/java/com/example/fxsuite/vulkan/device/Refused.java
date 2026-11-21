package com.example.fxsuite.vulkan.device;

/**
 * Something the card, or what stands before it, refused: which call it was, and what was answered. Nothing the card
 * answers is dropped, so whatever goes wrong is said where it went wrong and by name.
 */
public final class Refused extends RuntimeException {

    public Refused(final String call, final String answered) {
        super(call + " was refused: " + answered);
    }
}
