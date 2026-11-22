package com.example.fxsuite.math.existences;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists in mathematics: nothing wrong with its tree, every node with its meaning. The kinds of curve by how
 * their value is worked out; continuous, smooth and through their points as their branches say, a fold continuous
 * but not smooth, steps neither.
 */
class ExistencesTest {

    @Test
    void nothingWrongWithItsTreeEveryNodeWithItsMeaning() {
        assertEquals(List.of(), Existences.INSTANCE.problems());
        assertEquals(List.of("curves"), Existences.INSTANCE.trees().stream().map(Taxonomy::name).toList());
        assertEquals(14, Curves.TREE.leaves().size());
        assertEquals(21, Curves.TREE.nodes().size());
        assertEquals("Straight from each point to the next, turning at each: a roof's slope folded at its purlins.",
                Curves.TREE.meaning(new Curves.Folded()));
    }

    @Test
    void theKindsOfCurveByHowTheirValueIsWorkedOut() {
        assertEquals(List.of(new Curves.MonotoneCubic(), new Curves.Spline(), new Curves.Piecewise(),
                new Curves.Curve()), new Curves.MonotoneCubic().lineage());
        assertEquals(List.of(new Curves.Constant(), new Curves.Linear(), new Curves.Parabolic(), new Curves.Cubic()),
                Curves.TREE.under(new Curves.Polynomial()));
        assertEquals(List.of(new Curves.Stepped(), new Curves.Folded(), new Curves.MonotoneCubic(),
                new Curves.NaturalCubic()), Curves.TREE.under(new Curves.Piecewise()));
        assertEquals("monotone-cubic", new Curves.MonotoneCubic().name());
    }

    @Test
    void continuousSmoothAndThroughTheirPointsAsTheirBranchesSay() {
        for (final Node smooth : List.<Node>of(new Curves.Parabolic(), new Curves.CircularArc(),
                new Curves.Catenary(), new Curves.MonotoneCubic())) {
            assertTrue(smooth.answer(Smooth.class).isPresent(), smooth.title() + " smooth");
            assertTrue(smooth.answer(Continuous.class).isPresent(), smooth.title() + " so continuous");
        }
        // a fold turns sharply at its points without jumping; steps jump at theirs
        assertTrue(new Curves.Folded().answer(Continuous.class).isPresent());
        assertFalse(new Curves.Folded().answer(Smooth.class).isPresent());
        assertFalse(new Curves.Stepped().answer(Continuous.class).isPresent());
        // what is chained is as its pieces and their joins are, nothing by its kind
        assertFalse(new Curves.Chained().answer(Continuous.class).isPresent());
        assertEquals(List.of(new Curves.Stepped(), new Curves.Folded(), new Curves.MonotoneCubic(),
                new Curves.NaturalCubic()), Curves.TREE.leaves().stream()
                .filter(leaf -> leaf.answer(Interpolating.class).isPresent()).toList());
    }
}
