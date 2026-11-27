package com.example.fxsuite.baked;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Things kept in folders laid out as their tree is: a robot at {@code things/thing/machine/robot/fine.baked}, a rock
 * at {@code things/thing/rock/fine.baked}; what is put taken back whole; kept again, in place of what was. Taken for
 * none: what was never kept, or kept at another resolution, or made by other code, or a file cut short; and no file
 * half-written left about.
 */
class TreeShelfTest {

    private final Things things = new Things();

    @TempDir
    private Path root;

    @Test
    void whatIsPutIsTakenBackWholeFromItsOwnFolderAsTheTreeIsLaidOut() {
        final TreeShelf shelf = new TreeShelf(root);
        final Asked robot = new Asked(things.tree, new Things.Robot(), Resolution.FINE, "code one");
        assertTrue(shelf.put(robot, things.pieces()));
        assertEquals(root.resolve("things/thing/machine/robot/fine.baked"), shelf.where(robot));
        assertEquals(root.resolve("things/thing/rock/coarse.baked"),
                shelf.where(new Asked(things.tree, new Things.Rock(), Resolution.COARSE, "code one")));
        same(things.pieces(), shelf.taken(robot).orElseThrow());
        assertTrue(shelf.put(robot, things.pieces().subList(1, 2)));
        same(things.pieces().subList(1, 2), shelf.taken(robot).orElseThrow());
    }

    @Test
    void whatWasNeverKeptOrKeptOtherwiseOrMadeByOtherCodeOrCutShortIsTakenForNone() throws IOException {
        final TreeShelf shelf = new TreeShelf(root);
        final Asked robot = new Asked(things.tree, new Things.Robot(), Resolution.FINE, "code one");
        assertTrue(shelf.taken(robot).isEmpty());
        shelf.put(robot, things.pieces());
        assertTrue(shelf.taken(new Asked(things.tree, new Things.Robot(), Resolution.COARSE, "code one")).isEmpty());
        assertTrue(shelf.taken(new Asked(things.tree, new Things.Robot(), Resolution.FINE, "code two")).isEmpty());
        final byte[] whole = Files.readAllBytes(shelf.where(robot));
        Files.write(shelf.where(robot), java.util.Arrays.copyOf(whole, whole.length - 5));
        assertTrue(shelf.taken(robot).isEmpty());
        try (Stream<Path> all = Files.walk(root)) {
            assertEquals(List.of(), all.filter(p -> p.toString().endsWith(".part")).toList(), "nothing half-written");
        }
    }

    /** {@code taken} as {@code put}: each piece said of alike, its points and its triangles the same. */
    private void same(final List<Piece> put, final List<Piece> taken) {
        assertEquals(put.size(), taken.size());
        for (int i = 0; i < put.size(); i++) {
            assertEquals(put.get(i).labels(), taken.get(i).labels());
            assertArrayEquals(put.get(i).points(), taken.get(i).points());
            assertArrayEquals(put.get(i).triangles(), taken.get(i).triangles());
        }
    }
}
