package com.example.fxsuite.baked;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A robot made fine at great cost and coarse cheaply: fine, made once and then taken from the shelf; coarse, made each
 * time and never kept; made again once the code that makes it has changed; not made at a resolution it is not offered
 * at. Baked ahead, its fine one only, and only if not kept already.
 */
class BakerTest {

    private final Things things = new Things();

    @TempDir
    private Path root;

    @Test
    void aDearOneIsMadeOnceThenTakenACheapOneMadeEachTimeAndNeverKept() {
        final Counted robot = new Counted("code one");
        final Baker baker = new Baker(new TreeShelf(root));
        baker.pieces(robot, Resolution.FINE, said -> { });
        assertEquals(things.pieces().size(), baker.pieces(robot, Resolution.FINE, said -> { }).size());
        assertEquals(1, robot.made, "made once, then taken");
        baker.pieces(robot, Resolution.COARSE, said -> { });
        baker.pieces(robot, Resolution.COARSE, said -> { });
        assertEquals(3, robot.made, "a cheap one made each time");
        assertFalse(Files.exists(root.resolve("things/thing/machine/robot/coarse.baked")), "and never kept");
        final Counted changed = new Counted("code two");
        baker.pieces(changed, Resolution.FINE, said -> { });
        assertEquals(1, changed.made, "made again by code changed");
        assertThrows(IllegalArgumentException.class, () -> baker.pieces(robot, Resolution.MIDDLE, said -> { }));
    }

    @Test
    void bakedAheadOnlyWhatIsDearAndNotKeptAlready() {
        final Counted robot = new Counted("code one");
        final Baker baker = new Baker(new TreeShelf(root));
        assertEquals(1, baker.baked(robot, said -> { }));
        assertEquals(0, baker.baked(robot, said -> { }));
        assertEquals(1, robot.made);
    }

    /** A robot's recipe, by code said, counting how many times it is made: fine dear, coarse cheap. */
    private final class Counted implements Recipe {

        private final String code;
        private int made;

        Counted(final String code) {
            this.code = code;
        }

        @Override
        public Taxonomy tree() {
            return things.tree;
        }

        @Override
        public Leaf<?> leaf() {
            return new Things.Robot();
        }

        @Override
        public List<Resolution> offered() {
            return List.of(Resolution.COARSE, Resolution.FINE);
        }

        @Override
        public boolean dear(final Resolution resolution) {
            return resolution == Resolution.FINE;
        }

        @Override
        public String madeBy() {
            return code;
        }

        @Override
        public List<Piece> made(final Resolution resolution, final Consumer<String> told) {
            made++;
            return things.pieces();
        }
    }
}
