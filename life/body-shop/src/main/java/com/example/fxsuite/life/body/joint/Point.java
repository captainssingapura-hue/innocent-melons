package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;

/**
 * Where a bone meets another, on bone {@code B} alone: a frame in the frame the bone is drawn in, at the joint's middle
 * (the middle of a disc between two vertebrae, the middle of the head of a femur), its axes those the joint turns
 * about: x to the right, y along the bone carried, z ahead. Two bones that meet each have their point there, and at
 * rest the two are one frame.
 *
 * @param <B> the bone it is on
 */
public interface Point<B extends Bone> {

    /** What it is, in words. */
    String said();

    /** The frame, in the frame its bone is drawn in. */
    Place frame();
}
