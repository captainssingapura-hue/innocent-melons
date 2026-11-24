package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A tiltrotor as a flight needs it: how fast it goes, how far its nacelles turn, how low its rotors reach, where its
 * pilot's eye is; and its drawing, put on the card and drawn standing where it is, its nacelles turned and its
 * rotors round so far. In its own frame: across to its right, up, along to its nose, the ground under its wheels at
 * nothing high; centimetres, seconds and degrees.
 */
public interface Airframe extends AutoCloseable {

    /** Its top speed, flying ahead, at full power. */
    double top();

    /** Its cruise, at which its wing alone holds it up. */
    double cruise();

    /** How far its nacelles turn up from flying ahead, at most. */
    double tilted();

    /** How far below its wheels its rotors' tips reach, its nacelles {@code tilt} up: nothing, standing up enough. */
    double belowWheels(double tilt);

    /** Where its pilot's eye is, in its own frame. */
    Vec3 cockpit();

    /** Put on {@code card}, its layouts as {@code laid} says. */
    void carded(Card card, Laid laid);

    /**
     * Drawn by {@code pass} as {@code camera} sees it, standing as {@code placed} says, its nacelles {@code tilt} up,
     * its rotors turned {@code turned} degrees round.
     */
    void drawn(Pass pass, Camera camera, Mat4 placed, double tilt, double turned);

    /** Gone from the card. */
    @Override
    void close();
}
