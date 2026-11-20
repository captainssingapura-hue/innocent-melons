package com.example.fxsuite.taxonomy;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A taxonomy read from its leaves: its branches found through their parents, its nodes named from their classes, each
 * with its meaning; a question answered by lineage, the nearest answer winning; everything wrong with a tree found at
 * once.
 */
class TaxonomyTest {

    private static final Map<String, String> MEANINGS = Meanings.beside(Failures.class);
    private static final Taxonomy TREE = Taxonomy.of("failures", new Failures.Failure(), Failures.LEAVES, MEANINGS);

    @Test
    void readFromItsLeavesItsBranchesFoundItsNodesNamedEachWithItsMeaning() {
        assertEquals(List.of(), TREE.problems());
        assertEquals(List.of(new Failures.Failure(), new Failures.Transient(), new Failures.Permanent()),
                TREE.branches());
        assertEquals(7, TREE.nodes().size());
        assertEquals("rate-limited", new Failures.RateLimited().name());
        assertEquals(new Failures.RateLimited(), TREE.named("rate-limited").orElseThrow());
        assertEquals("No answer came in time.", TREE.meaning(new Failures.Timeout()));
        assertEquals(List.of(new Failures.NotFound(), new Failures.Forbidden()), TREE.under(new Failures.Permanent()));
        assertEquals(List.of(new Failures.Timeout(), new Failures.Transient(), new Failures.Failure()),
                Taxonomy.lineage(new Failures.Timeout()));
    }

    @Test
    void aQuestionAnsweredByLineageTheNearestAnswerWinning() {
        assertEquals(3, Taxonomy.answer(new Failures.Timeout(), Failures.Retry.class).orElseThrow().times(),
                "its branch's");
        assertEquals(1, Taxonomy.answer(new Failures.RateLimited(), Failures.Retry.class).orElseThrow().times(),
                "its own, nearer");
        assertEquals(0, Taxonomy.answer(new Failures.Forbidden(), Failures.Retry.class).orElseThrow().times());
        assertTrue(Taxonomy.answer(new Failures.Failure(), Failures.Retry.class).isEmpty(), "the root says nothing");
    }

    @Test
    void everythingWrongWithATreeFoundAtOnce() {
        final Map<String, String> meanings = new HashMap<>(MEANINGS);
        meanings.remove("NotFound");
        meanings.put("Gone", "No longer there.");
        final Taxonomy broken = Taxonomy.of("broken", new Failures.Failure(), List.of(new Failures.Timeout(),
                new Failures.Timeout(), new Failures.NotFound(), new Failures.Stray()), meanings);
        final List<String> problems = broken.problems();
        assertTrue(problems.contains("Timeout: listed twice"), problems.toString());
        assertTrue(problems.contains("NotFound: says nothing of what it means"), problems.toString());
        assertTrue(problems.contains("a meaning for Gone, which is no node of this tree"), problems.toString());
        assertTrue(problems.contains("Stray: its lineage ends at Elsewhere, another tree's root"), problems.toString());
        // and the meanings it was given for leaves it does not list
        assertTrue(problems.contains("a meaning for Forbidden, which is no node of this tree"), problems.toString());
    }

    @Test
    void meaningsReadBySection() {
        final Map<String, String> read = Meanings.read(
                "# Title\n\nIntro.\n\n## One\n\nFirst.\nStill.\n\n## Two\nSecond.\n");
        assertEquals(List.of("One", "Two"), List.copyOf(read.keySet()));
        assertEquals("First.\nStill.", read.get("One"));
        assertEquals("Second.", read.get("Two"));
        assertEquals(Map.of(), Meanings.beside(TaxonomyTest.class), "none beside a class with no such file");
    }
}
