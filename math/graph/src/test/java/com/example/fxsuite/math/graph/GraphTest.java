package com.example.fxsuite.math.graph;

import hue.captains.singapura.tao.ontology.Scalar;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A graph of any values: its vertices each once, its edges each once and between its own vertices, never from one to
 * itself; gone along one way or either; what is reached; the order the edges put the vertices in, what bears before
 * what it bears on, none if they go round; the parts that hang together; equal by what it holds.
 */
class GraphTest {

    /** A part of a frame, for vertices. */
    record Part(String name) implements Scalar {
    }

    /** What an edge carries, for labels. */
    record Course(String name) implements Scalar {
    }

    private static final Unlabelled BARE = new Unlabelled();

    private final Part roof = new Part("roof");
    private final Part beam = new Part("beam");
    private final Part left = new Part("left post");
    private final Part right = new Part("right post");
    private final Part ground = new Part("ground");

    @Test
    void itsVerticesEachOnceItsEdgesEachOnceBetweenItsOwnVertices() {
        assertThrows(IllegalArgumentException.class, () -> new Graph<Part, Unlabelled>(List.of(roof, roof), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Directed<>(roof, roof, BARE));
        assertThrows(IllegalArgumentException.class, () -> new Undirected<>(beam, beam, BARE));
        assertThrows(IllegalArgumentException.class, () -> new Graph<>(List.of(roof), List.of(
                new Directed<>(roof, beam, BARE))));
        assertThrows(IllegalArgumentException.class, () -> new Graph<>(List.of(roof, beam), List.of(
                new Directed<>(roof, beam, BARE), new Directed<>(roof, beam, BARE))));
        // an arch's curve and its straight chord join the same two vertices, each an edge of its own
        final Graph<Part, Course> arch = new Graph<>(List.of(left, right), List.of(
                new Undirected<>(left, right, new Course("arch")), new Undirected<>(left, right, new Course("chord"))));
        assertEquals(2, arch.edges().touching(left).size());
        assertEquals(List.of(right), arch.next(left));
    }

    @Test
    void goneAlongOneWayOrEitherAndWhatIsReached() {
        final Graph<Part, Unlabelled> graph = new Graph<>(List.of(roof, beam, left), List.of(
                new Directed<>(roof, beam, BARE), new Undirected<>(beam, left, BARE)));
        assertEquals(List.of(beam), graph.next(roof));
        assertEquals(List.of(left), graph.next(beam));
        assertEquals(List.of(beam), graph.next(left));
        assertEquals(List.of(roof, beam, left), graph.reached(roof));
        assertEquals(List.of(left, beam), graph.reached(left));
        assertEquals(beam, new Undirected<>(beam, left, BARE).beyond(left));
        assertThrows(IllegalArgumentException.class, () -> new Directed<>(roof, beam, BARE).beyond(left));
        assertThrows(IllegalArgumentException.class, () -> graph.reached(ground));
    }

    @Test
    void theOrderTheEdgesPutThemInWhatBearsBeforeWhatItBearsOn() {
        // the vertices listed out of order: the order comes from the edges, ties from the listing
        final List<Part> parts = List.of(ground, right, beam, left, roof);
        final List<Directed<Part, Unlabelled>> bearing = List.of(new Directed<>(roof, beam, BARE),
                new Directed<>(beam, left, BARE), new Directed<>(beam, right, BARE),
                new Directed<>(left, ground, BARE), new Directed<>(right, ground, BARE));
        assertEquals(Optional.of(List.of(roof, beam, right, left, ground)), new Graph<>(parts, bearing).order());
        final List<Directed<Part, Unlabelled>> round = List.of(new Directed<>(roof, beam, BARE),
                new Directed<>(beam, roof, BARE));
        assertEquals(Optional.empty(), new Graph<>(List.of(roof, beam), round).order());
        // gone along either way, an undirected edge goes round
        assertEquals(Optional.empty(), new Graph<>(List.of(roof, beam), List.of(
                new Undirected<>(roof, beam, BARE))).order());
        assertEquals(Optional.of(List.of(roof, beam)), new Graph<Part, Unlabelled>(List.of(roof, beam),
                List.of()).order());
    }

    @Test
    void thePartsThatHangTogetherWhicheverWayTheirEdgesGo() {
        final Graph<Part, Unlabelled> graph = new Graph<>(List.of(ground, roof, left, beam, right), List.of(
                new Directed<>(roof, beam, BARE), new Directed<>(right, ground, BARE)));
        assertEquals(List.of(List.of(ground, right), List.of(roof, beam), List.of(left)), graph.parts());
    }

    @Test
    void equalByWhatItHolds() {
        final List<Directed<Part, Course>> one = List.of(new Directed<>(roof, beam, new Course("rafter")));
        assertEquals(new Graph<>(List.of(roof, beam), one), new Graph<>(List.of(roof, beam), one));
        assertNotEquals(new Graph<>(List.of(roof, beam), one), new Graph<>(List.of(beam, roof), one));
        assertNotEquals(new Graph<>(List.of(roof, beam), one), new Graph<>(List.of(roof, beam), List.of(
                new Directed<>(roof, beam, new Course("purlin")))));
        assertNotEquals(new Directed<>(roof, beam, BARE), new Undirected<>(roof, beam, BARE));
    }
}
