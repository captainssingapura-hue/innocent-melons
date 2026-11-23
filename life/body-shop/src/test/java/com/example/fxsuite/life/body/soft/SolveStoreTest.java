package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolveStoreTest {

    private static final SoftRegion REGION = new SoftRegion(new V3(0, 0, 0), new V3(4, 4, 4));

    private Lattice lattice(final float shift) {
        final float[] nodes = {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 + shift};
        return new Lattice(0.5, nodes, new int[] {0, 1, 2, 3}, new boolean[4], new int[0], new float[0]);
    }

    @Test
    void aSolveKeptIsGivenBackAsItEnded() throws IOException {
        final SolveStore store = new SolveStore(dir);
        final Lattice moved = lattice(0.25f);
        store.keep("one", List.of(moved), "how it went", java.util.Optional.empty());
        final SolveStore.Kept kept = store.kept("one").orElseThrow();
        assertEquals("how it went", kept.said());
        assertArrayEquals(moved.nodes(), kept.on(List.of(lattice(0))).getFirst().nodes());
    }

    @Test
    void aSolveNeverKeptOrSpoiltIsNotGiven() throws IOException {
        final SolveStore store = new SolveStore(dir);
        assertTrue(store.kept("none").isEmpty());
        Files.write(dir.resolve("spoilt.solve"), new byte[] {1, 2, 3});
        assertTrue(store.kept("spoilt").isEmpty());
    }

    @Test
    void itsNameChangesWithAnythingThatGoesIntoIt() {
        final SolveStore store = new SolveStore(dir);
        final SoftSolve.Settings s = SoftSolve.Settings.first();
        final String name = store.name(REGION, 0.25, s, List.of(lattice(0)));
        assertEquals(name, store.name(REGION, 0.25, s, List.of(lattice(0))));
        assertNotEquals(name, store.name(REGION, 0.3, s, List.of(lattice(0))));
        assertNotEquals(name, store.name(new SoftRegion(new V3(0, 0, 0), new V3(4, 4, 5)), 0.25, s,
                List.of(lattice(0))));
        assertNotEquals(name, store.name(REGION, 0.25, s, List.of(lattice(0.001f))));
        assertNotEquals(name, store.name(REGION, 0.25, new SoftSolve.Settings(10, 0.45, 0.35, 40, 160, 60, 8, 300,
                0.9, 0.25, 1, 0.08, true), List.of(lattice(0))));
    }

    @TempDir
    Path dir;
}
