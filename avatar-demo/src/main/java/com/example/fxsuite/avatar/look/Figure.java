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
}
