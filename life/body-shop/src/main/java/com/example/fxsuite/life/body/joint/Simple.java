package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;

import java.util.List;

/**
 * A joint of one articulation, the joint the body has and the articulation both: two vertebrae on their disc, a hip. It
 * moves the ways its articulation turns, as far.
 *
 * @param <A> the bone it is carried by
 * @param <B> the bone it carries
 */
public interface Simple<A extends Bone, B extends Bone> extends Joint<A, B>, Complex {

    @Override
    default List<Joint<?, ?>> articulations() {
        return List.of(this);
    }

    @Override
    default List<Movement> movements() {
        return motion().movements();
    }

    @Override
    default Range range(final Movement m) {
        return motion().range(m);
    }

    @Override
    default Drive drive(final Movement m) {
        return new Drive(this, m);
    }
}
