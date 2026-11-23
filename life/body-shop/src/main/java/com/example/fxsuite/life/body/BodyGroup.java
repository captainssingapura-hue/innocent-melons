package com.example.fxsuite.life.body;

import java.util.EnumSet;
import java.util.Set;

/** A group of the body's parts drawn alike ({@link Layers}): the head; the neck; the trunk; the arms; the legs. */
public enum BodyGroup {
    HEAD("Head", EnumSet.of(Part.HEAD)),
    NECK("Neck", EnumSet.of(Part.NECK)),
    TRUNK("Trunk", EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST)),
    ARMS("Arms", EnumSet.of(Part.RIGHT_GIRDLE, Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM, Part.RIGHT_HAND,
            Part.LEFT_GIRDLE, Part.LEFT_UPPER_ARM, Part.LEFT_FOREARM, Part.LEFT_HAND)),
    LEGS("Legs", EnumSet.of(Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, Part.RIGHT_TOES, Part.LEFT_THIGH,
            Part.LEFT_SHIN, Part.LEFT_FOOT, Part.LEFT_TOES));

    private final String label;
    private final Set<Part> parts;

    BodyGroup(final String label, final Set<Part> parts) {
        this.label = label;
        this.parts = parts;
    }

    public String label() {
        return label;
    }

    /** Whether {@code p} is one of its parts. */
    public boolean has(final Part p) {
        return parts.contains(p);
    }
}
