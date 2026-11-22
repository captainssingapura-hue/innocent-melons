package com.example.fxsuite.vulkan.device;

import java.util.Optional;

/**
 * What the looks of a frame read besides what they are told: the picture of depths they look things up in, and,
 * on a card that answers rays, what stands in the frame for a ray to be asked of.
 */
record Read(Depths depths, Optional<Standing> standing) {

    /** The same, looking things up in {@code looked} instead. */
    Read looking(final Depths looked) {
        return new Read(looked, standing);
    }

    /** The same, asking rays of {@code stood} instead. */
    Read asking(final Standing stood) {
        return new Read(depths, Optional.of(stood));
    }
}
