package com.example.fxsuite.avatar.look;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Vec3;

import javafx.scene.Node;

/**
 * The avatar as it is drawn: whatever figure it is (the courier, {@link BoxRig}, or another offered,
 * {@link Figures}), put where the avatar is each frame, holding the pose its gait gives it ({@link Pose}), carrying
 * its jet-pack and what its hands hold; seen from its own eyes, its arms only. The game asks nothing more of it.
 */
public interface Figure {

    /** What to put in the scene: the figure. */
    Node node();

    /** What its weapons throw off into the world, to put in the scene beside it. */
    Node effects();

    /** Put where the avatar is, holding {@code pose}. */
    void show(Avatar avatar, Pose pose);

    /** Seen from its own eyes: its arms held as {@code pose} has them, its eyes at {@code eye}, facing {@code yaw}. */
    void showFromEyes(Avatar avatar, Pose pose, Vec3 eye, double yaw);

    /** Seen from its own eyes ({@code true}): its arms only drawn; or whole. */
    void fromItsEyes(boolean eyes);

    /** Where its eyes are in the world, standing at {@code where} holding {@code pose}. */
    Vec3 eyes(Body where, Pose pose);

    /** Whether it can hold {@code weapon}: drawn, one it cannot is not taken up. Any, unless it says. */
    default boolean holds(final com.example.fxsuite.avatar.sim.Weapon weapon) {
        return true;
    }

    /** A point on the figure to look from, in the world: where, turned {@code yaw} round, {@code pitch} up, rolled {@code roll} to the right. */
    record Viewpoint(Vec3 at, double yaw, double pitch, double roll) {
    }

    /**
     * Whether, seen from its own eyes, it is drawn whole but for its head, the view from where its eyes are as it is
     * drawn ({@link #eyesNow}), rather than its arms alone held up into view ({@link #showFromEyes}). Not, unless it
     * says.
     */
    default boolean seesFromItsHead() {
        return false;
    }

    /** Its head drawn ({@code false}) or out of sight ({@code true}), as when it is seen from its own eyes. */
    default void headless(final boolean off) {
    }

    /** Where its eyes are in the world, as it was last shown ({@link #show}); for one that sees from its head. */
    default Vec3 eyesNow() {
        return null;
    }

    /** The cameras on its body, by name, to be seen from in turn: none, unless it says. */
    default java.util.List<String> bodyCams() {
        return java.util.List.of();
    }

    /** Where the camera on its body called {@code name} is, and which way it looks, as it was last shown. */
    default Viewpoint bodyCam(final String name) {
        return null;
    }
}
