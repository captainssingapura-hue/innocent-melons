package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * Where two bones meet, one articulation: the bone it is carried by, nearer the root of the body, and its point there;
 * the bone it carries and its point; and how the one may move on the other. Each a stateless object of its own, its
 * points typed to its bones, so that a point of any other bone is refused.
 *
 * @param <A> the bone it is carried by
 * @param <B> the bone it carries
 */
public interface Joint<A extends Bone, B extends Bone> extends StatelessFunctionalObject {

    /** What it is called. */
    String name();

    /** The bone it is carried by. */
    A from();

    /** Its point on that bone. */
    Point<A> fromPoint();

    /** The bone it carries. */
    B to();

    /** Its point on that bone. */
    Point<B> toPoint();

    /** How the bone it carries may move on the other. */
    Motion motion();

    /**
     * On which sides the two bones meet, once each way they do: two bones down the middle once; a bone down the middle
     * and a paired one, or two paired ones, on each side.
     */
    default List<Ends> sides() {
        final boolean one = from().paired();
        final boolean other = to().paired();
        if (!one && !other) {
            return List.of(new Ends(Side.MIDDLE, Side.MIDDLE));
        }
        return List.of(new Ends(one ? Side.RIGHT : Side.MIDDLE, other ? Side.RIGHT : Side.MIDDLE),
                new Ends(one ? Side.LEFT : Side.MIDDLE, other ? Side.LEFT : Side.MIDDLE));
    }

    /** The sides of the two bones of one such meeting. */
    record Ends(Side from, Side to) {
    }
}
