package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Point;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * A ring round a limb or the trunk, level across it: its middle {@code along} of the way between two points on its
 * bones (where a bone meets the next), and {@code off} from there; so a longer bone, a longer spine, takes its rings
 * with it. A ring on the trunk hung from one point, the nearest disc of the spine: {@code from} and {@code to} the
 * same. Its spokes, each a way out and how far the flesh reaches that way, round from straight ahead. On the forearm,
 * turned so much of the forearm's twist as {@code twist} says, none at the elbow, all of it at the wrist.
 *
 * @param said   which it is, in words
 * @param from   the point it is measured from
 * @param to     the point it is measured toward
 * @param along  how far of the way, 0 to 1
 * @param off    how far off that its middle is, cm
 * @param twist  how much of the forearm's twist it takes, 0 to 1
 * @param spokes its spokes, round from straight ahead
 */
public record Ring(String said, Point<?> from, Point<?> to, double along, V3 off, double twist, List<Spoke> spokes)
        implements Scalar {

    public Ring {
        spokes = List.copyOf(spokes);
    }

    /** Its middle, in its part's frame, untwisted. */
    public V3 middle() {
        final V3 a = from.frame().at();
        final V3 b = to.frame().at();
        return a.plus(b.minus(a).times(along)).plus(off);
    }
}
