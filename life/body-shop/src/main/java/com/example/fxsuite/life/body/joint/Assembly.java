package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A skeleton put together from its joints, as far as a scope of bones goes ({@link Scope}), from any one of them
 * outward: that bone where it is said to be, then every bone met at one of its joints placed so that its point there
 * lies on its neighbour's, turned as far as the joint has moved; then theirs, until every bone that can be reached is
 * placed. A joint walked from the bone it carries back to the one it is carried by is turned back. A bone and a joint
 * are drawn as on the right; on the left, each is its mirror, its points and its turns mirrored across the middle, so
 * that a leg swung out on the right is swung out on the left. Where bones meet again by a way not walked (a ring of
 * them), how far apart their points are is told, not mended.
 */
public record Assembly() implements StatelessFunctionalObject {

    public static final Assembly INSTANCE = new Assembly();

    /** The bones of {@code scope}, put together from {@code root} placed at {@code where}, posed {@code posture}. */
    public Assembled build(final Scope scope, final BoneAt root, final Place where, final Posture posture) {
        final List<Edge> edges = edges(scope, posture);
        final Map<BoneAt, Place> places = new HashMap<>();
        final Set<Edge> walked = new HashSet<>();
        places.put(root, where);
        final Deque<BoneAt> next = new ArrayDeque<>(List.of(root));
        while (!next.isEmpty()) {
            final BoneAt here = next.poll();
            for (final Edge e : edges) {
                final boolean outward = e.from().equals(here) && !places.containsKey(e.to());
                final boolean inward = e.to().equals(here) && !places.containsKey(e.from());
                if (outward) {
                    places.put(e.to(), places.get(here).then(e.fromPoint()).then(e.turn()).then(e.toPoint().inverse()));
                } else if (inward) {
                    places.put(e.from(), places.get(here).then(e.toPoint()).then(e.turn().inverse())
                            .then(e.fromPoint().inverse()));
                }
                if (outward || inward) {
                    walked.add(e);
                    next.add(outward ? e.to() : e.from());
                }
            }
        }
        final List<Gap> gaps = new ArrayList<>();
        for (final Edge e : edges) {
            if (!walked.contains(e) && places.containsKey(e.from()) && places.containsKey(e.to())) {
                gaps.add(gap(e, places));
            }
        }
        return new Assembled(places, gaps, edges.size());
    }

    /** Every articulation between two bones of {@code scope}, on every side it is, turned as {@code posture} has it. */
    private List<Edge> edges(final Scope scope, final Posture posture) {
        final List<Edge> edges = new ArrayList<>();
        for (final Complex c : scope.joints()) {
            for (final Joint<?, ?> j : c.articulations()) {
                if (scope.holds(j.from()) && scope.holds(j.to())) {
                    for (final Joint.Ends ends : j.sides()) {
                        edges.add(edge(c, j, ends, posture));
                    }
                }
            }
        }
        return edges;
    }

    /** Articulation {@code j} of joint {@code c}, its bones on the sides {@code ends} say, turned as posed. */
    private Edge edge(final Complex c, final Joint<?, ?> j, final Joint.Ends ends, final Posture posture) {
        final Side side = ends.from() != Side.MIDDLE ? ends.from() : ends.to();
        final Map<Movement, Double> moved = new EnumMap<>(Movement.class);
        for (final Movement m : c.movements()) {
            moved.put(m, posture.degrees(c, side, m));
        }
        final Rot turn = j.motion().turn(c.turns(j, moved));
        final boolean left = side == Side.LEFT;
        return new Edge(j, new BoneAt(j.from(), ends.from()),
                mirrored(j.fromPoint().frame(), ends.from() == Side.LEFT || ends.from() == Side.MIDDLE && left),
                new BoneAt(j.to(), ends.to()),
                mirrored(j.toPoint().frame(), ends.to() == Side.LEFT || ends.to() == Side.MIDDLE && left),
                new Place(V3.ZERO, left ? mirror(turn) : turn));
    }

    /** {@code p} mirrored across the middle, if {@code left}: its origin's x the other way, its turn mirrored. */
    private Place mirrored(final Place p, final boolean left) {
        return left ? new Place(new V3(-p.at().x(), p.at().y(), p.at().z()), mirror(p.turn())) : p;
    }

    /** {@code r} seen in a mirror across the middle: still a turn, the other way about what crosses the mirror. */
    private Rot mirror(final Rot r) {
        return new Rot(r.xx(), -r.xy(), -r.xz(), -r.yx(), r.yy(), r.yz(), -r.zx(), r.zy(), r.zz());
    }

    /** How far apart a joint's two points are, placed by ways that did not walk it: how far, and how far turned. */
    private Gap gap(final Edge e, final Map<BoneAt, Place> places) {
        final Place one = places.get(e.from()).then(e.fromPoint()).then(e.turn());
        final Place other = places.get(e.to()).then(e.toPoint());
        final Rot between = one.turn().back().then(other.turn());
        final double cos = Math.clamp((between.xx() + between.yy() + between.zz() - 1) / 2, -1, 1);
        return new Gap(e.from().said() + " to " + e.to().said() + " (" + e.joint().name() + ")",
                other.at().minus(one.at()).length(), Math.toDegrees(Math.acos(cos)));
    }

    /** An articulation on one side: the bones it joins, its points' frames on them, and how far it is turned. */
    private record Edge(Joint<?, ?> joint, BoneAt from, Place fromPoint, BoneAt to, Place toPoint, Place turn) {
    }

    /**
     * A skeleton put together: where each bone is, its frame in the body's; where it did not close, how far; how many
     * articulations it had.
     */
    public record Assembled(Map<BoneAt, Place> places, List<Gap> gaps, int articulations) {

        public Assembled {
            places = Map.copyOf(places);
            gaps = List.copyOf(gaps);
        }

        /** The widest gap, cm; none if it closed everywhere. */
        public double widest() {
            return gaps.stream().mapToDouble(Gap::apart).max().orElse(0);
        }
    }

    /** A joint whose points did not meet: how far apart, cm, and how far turned from each other, degrees. */
    public record Gap(String joint, double apart, double turned) {
    }
}
