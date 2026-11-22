package com.example.fxsuite.vulkan.device;

/**
 * The kinds of thing the card hands out a handle to. The card gives every one of them as the same bare number; here
 * each is a type, never made, that a {@link Kept} is marked with, so the compiler refuses a buffer where an image is
 * wanted.
 */
interface Of {

    /** What hears the validation layer. */
    interface Messenger extends Of {
    }

    /** Where commands' memory comes from. */
    interface Commands extends Of {
    }

    /** What says a run of commands is done. */
    interface Fence extends Of {
    }

    /** Bytes on the card. */
    interface Buffer extends Of {
    }

    /** A picture on the card. */
    interface Image extends Of {
    }

    /** A picture as it is drawn on or read. */
    interface View extends Of {
    }

    /** How a picture is looked up by a shader. */
    interface Sampler extends Of {
    }

    /** The things of a frame, or the triangles of one of them, kept so that a ray can be asked of them. */
    interface Structure extends Of {
    }

    /** The memory a buffer or an image stands in. */
    interface Memory extends Of {
    }

    /** A shader, compiled. */
    interface Module extends Of {
    }

    /** What a look is told each frame, as the card has it described. */
    interface Told extends Of {
    }

    /** Everything a look is told, a frame's and a thing's, as the card has it described. */
    interface Telling extends Of {
    }

    /** Where what is told is kept. */
    interface Tellings extends Of {
    }

    /** One frame's telling, ready to be bound. */
    interface Bound extends Of {
    }

    /** A look ready to draw with. */
    interface Pipeline extends Of {
    }

    /** A window on the screen, as the desk has it. */
    interface Glass extends Of {
    }

    /** A window as the card draws to it. */
    interface Surface extends Of {
    }

    /** The pictures a window shows in turn. */
    interface Shown extends Of {
    }

    /** What says one step is done, so the next may begin. */
    interface Signal extends Of {
    }
}
