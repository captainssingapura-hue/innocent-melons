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
 * A skeleton put together from its joints, from any one bone outward: that bone where it is said to be, then every bone
 * met at one of its joints placed so that its point there lies on its neighbour's, turned as far as the joint has moved
 * each way; then theirs, and so on, until every bone that can be reached is placed. A joint walked from the bone it
 * carries back to the one it is carried by is turned back. Where bones meet again by a way not walked (a ring of them),
 * how far apart their points are is told, not mended.
 */
public record Assembly() implements StatelessFunctionalObject {

    public static final Assembly INSTANCE = new Assembly();

    /** What {@code joints} join, put together from {@code root}, placed at {@code where}, posed {@code posture}. */
    public Assembled build(final List<Complex> joints, final BoneAt root, final Place where, final Posture posture) {
        final List<Edge> edges = edges(joints, posture);
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
        return new Assembled(places, gaps);
    }

    /** Every articulation of {@code joints} on every side it is, each turned as {@code posture} has its joint moved. */
    private List<Edge> edges(final List<Complex> joints, final Posture posture) {
        final List<Edge> edges = new ArrayList<>();
        for (final Complex c : joints) {
            for (final Joint<?, ?> j : c.articulations()) {
                for (final Joint.Ends ends : j.sides()) {
                    final Side side = ends.from() != Side.MIDDLE ? ends.from() : ends.to();
                    final Map<Movement, Double> degrees = new EnumMap<>(Movement.class);
                    for (final Movement m : c.movements()) {
                        final Complex.Drive d = c.drive(m);
                        if (d != null && d.joint() == j) {
                            degrees.put(d.movement(), posture.degrees(c, side, m));
                        }
                    }
                    edges.add(new Edge(j, new BoneAt(j.from(), ends.from()), j.fromPoint().frame(),
                            new BoneAt(j.to(), ends.to()), j.toPoint().frame(),
                            new Place(V3.ZERO, j.motion().turn(degrees))));
                }
            }
        }
        return edges;
    }

    /** How far apart a joint's two points are, placed by ways that did not walk it: how far, and how far turned. */
    private Gap gap(final Edge e, final Map<BoneAt, Place> places) {
        final Place one = places.get(e.from()).then(e.fromPoint()).then(e.turn());
        final Place other = places.get(e.to()).then(e.toPoint());
        final Rot between = one.turn().back().then(other.turn());
        final double cos = Math.clamp((between.xx() + between.yy() + between.zz() - 1) / 2, -1, 1);
        return new Gap(e.joint().name(), other.at().minus(one.at()).length(), Math.toDegrees(Math.acos(cos)));
    }

    /** An articulation on one side: the bones it joins, its points' frames on them, and how far it is turned. */
    private record Edge(Joint<?, ?> joint, BoneAt from, Place fromPoint, BoneAt to, Place toPoint, Place turn) {
    }

    /** A skeleton put together: where each bone is, its frame in the body's; and where it did not close, how far. */
    public record Assembled(Map<BoneAt, Place> places, List<Gap> gaps) {

        public Assembled {
            places = Map.copyOf(places);
            gaps = List.copyOf(gaps);
        }
    }

    /** A joint whose points did not meet: how far apart, cm, and how far turned from each other, degrees. */
    public record Gap(String joint, double apart, double turned) {
    }
}
