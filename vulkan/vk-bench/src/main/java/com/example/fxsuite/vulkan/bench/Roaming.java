package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Motion;

import java.util.List;
import java.util.Optional;

/**
 * What moves the figures a walk draws: stepped in fixed steps, each as its source tells it, and read between its
 * last two steps to be drawn. The walk's loop and its film maker speak to this and to nothing behind it, so the same
 * windows, light, shadows, kept runs and films serve whatever moves: the engine's own avatar on its feet and its
 * jets ({@link Company}), or anything else that is stepped so. In the renderer's frame, height counted up,
 * centimetres and degrees.
 */
public interface Roaming {

    /** What is in the way of everyone moving, from now on. */
    void inTheWay(Obstacles blocks);

    /** Taken on by the steps {@code seconds} earn; how far on the next step is, for drawing between the last two. */
    double stepped(double seconds);

    /** Whether it has nothing more to be told. */
    boolean over();

    /** Whether it ends of itself, as a script or a kept run does. */
    boolean ends();

    /** How long it is, in seconds of its own time; of someone playing, no end. */
    double seconds();

    /** Where the lead's eye is, {@code blend} of the way between its last two steps. */
    Vec3 head(double blend);

    /** Which way round the lead is turned, so far, in degrees. */
    double yaw(double blend);

    /** How far above its usual tilt the lead's view looks, in degrees: how far up the lead looks, and aims. */
    default double tilt(final double blend) {
        return 0;
    }

    /** The camera as the one moving it would have it: following the lead. */
    Camera camera(double blend);

    /** The spot on the ground under the lead, for the sun's picture to be drawn round. */
    Vec3 foot(double blend);

    /** Every figure's parts, as they are drawn. */
    List<Courier.Placed> figures(double blend);

    /** How the lead is moving, as the core engine detects it from the run's steps: not known, of most. */
    default Optional<Motion> motion() {
        return Optional.empty();
    }

    /** What has happened since this was last asked, heard as the lead would hear it: nothing, of most. */
    default void heard(final Hearing hearing, final double blend) {
    }

    /** What it draws of its own, beyond the figures, put on {@code card} once it is open: nothing, of most. */
    default void carded(final Card card, final Laid laid) {
    }

    /** What it draws of its own, solid, by {@code pass} as {@code camera} sees it, casting its shadow too. */
    default void drawn(final Pass pass, final Camera camera, final double blend) {
    }

    /** What it draws over the picture, by {@code pass}: an instrument's display, say. Nothing, of most. */
    default void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
    }

    /**
     * What a click at {@code click} on a picture {@code width} by {@code height} pixels means: the names of what it
     * presses, told to the next step as keys are, so a kept run plays it again. Nothing, of most.
     */
    default List<String> clicked(final Clicked click, final int width, final int height) {
        return List.of();
    }

    /** Whether the mouse is left free to click, not taken to turn with. */
    default boolean pointing() {
        return false;
    }

    /** What it put on the card, taken off it. */
    default void uncarded() {
    }
}
