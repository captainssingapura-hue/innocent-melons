package com.example.fxsuite.vulkan.bench.subject;

/**
 * The parts of the courier that move as one: each hangs from a joint, and everything in it turns with that joint.
 * The body is tipped about its middle; the legs swing at the hips, the arms at the shoulders, each forearm at its
 * elbow, and the head on the neck. The bot ({@link Bot}) has shins as well, each bent at its knee, and feet, each
 * turned at its ankle; the courier, copied from the engine's figure, has neither, and its shins and feet are where
 * its legs are.
 */
public enum Limb {
    BODY,
    LEFT_LEG,
    LEFT_SHIN,
    LEFT_FOOT,
    RIGHT_LEG,
    RIGHT_SHIN,
    RIGHT_FOOT,
    HEAD,
    LEFT_ARM,
    LEFT_FOREARM,
    RIGHT_ARM,
    RIGHT_FOREARM
}
