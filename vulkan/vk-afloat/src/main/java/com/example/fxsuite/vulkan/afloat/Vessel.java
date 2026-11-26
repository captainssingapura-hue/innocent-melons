package com.example.fxsuite.vulkan.afloat;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A boat as a cruise needs it: how fast it goes at full throttle, how deep it draws, how long it is, where its
 * helmsman's eye is; and its drawing, put on the card and drawn standing where it is, its wheel turned so far. In its
 * own frame: across to its right, up from the water's line, along to its bow; centimetres, seconds and degrees.
 */
public interface Vessel extends AutoCloseable {

    /** Its top speed ahead, at full throttle. */
    double top();

    /** How far below the water's line its keel reaches. */
    double draught();

    /** How long it is, overall. */
    double length();

    /** Where its helmsman's eye is, in its own frame. */
    Vec3 helm();

    /** Put on {@code card}, its layouts as {@code laid} says. */
    void carded(Card card, Laid laid);

    /** Drawn by {@code pass} as {@code camera} sees it, standing as {@code placed} says, its wheel over {@code wheel}. */
    void drawn(Pass pass, Camera camera, Mat4 placed, double wheel);

    /** Gone from the card. */
    @Override
    void close();
}
