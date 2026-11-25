package com.example.fxsuite.vulkan.bench;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A spread of a bench's two windows: the usual one on a desk with one screen and with two, and one kept in a file,
 * read back the same, forgotten, and read as nothing where the file is not a spread.
 */
class SpreadsTest {

    private final Spread before = new Spread(new Seat(Seat.Which.FIRST, 440, 120), new Seat(Seat.Which.FIRST, 60, 120));

    @Test
    void theUsualSpreadFillsTheFirstScreenWithThePictureAndPutsTheConsoleOnASecond() {
        final Spread two = new Spread(true, before);
        assertEquals(new Seat(Seat.Which.FIRST, true, 0, 0, 0, 0), two.picture());
        assertEquals(Seat.Which.OTHER, two.console().on());
        assertFalse(two.console().maximized());
        assertEquals(before, new Spread(false, before), "on one screen, both where the bench has always put them");
    }

    @Test
    void aSpreadKeptIsReadBackTheSameAndForgottenIsNothingKept(@TempDir final Path folder) {
        final Spreads spreads = new Spreads(folder.resolve("deeper").resolve("windows.spread").toString());
        assertEquals(Optional.empty(), spreads.kept(), "nothing kept to begin with");
        final Spread spread = new Spread(new Seat(Seat.Which.OTHER, true, -8, -8, 1920, 1040),
                new Seat(Seat.Which.FIRST, false, 30, 50, 420, 900));
        assertTrue(spreads.keep(spread), "its folder made for it");
        assertEquals(Optional.of(spread), spreads.kept());
        assertEquals(Optional.of(spread), new Spreads(spreads.said()).kept(), "by whoever reads the same file");
        spreads.forget();
        assertEquals(Optional.empty(), spreads.kept());
        spreads.forget();
    }

    @Test
    void aFileThatIsNotASpreadIsAsNothingKeptAndNoneKeepsNothing(@TempDir final Path folder) throws IOException {
        final Path file = folder.resolve("windows.spread");
        for (final List<String> lines : List.of(List.of("not a spread"), List.of("spread 1", "picture on=first"),
                List.of("spread 2", "picture on=first maximized=true x=0 y=0 width=0 height=0",
                        "console on=other maximized=false x=40 y=40 width=0 height=0"),
                List.of("spread 1", "picture on=nowhere maximized=true x=0 y=0 width=0 height=0",
                        "console on=other maximized=false x=40 y=40 width=0 height=0"))) {
            Files.write(file, lines);
            assertEquals(Optional.empty(), new Spreads(file.toString()).kept(), lines.toString());
        }
        final Spreads none = new Spreads("none");
        assertFalse(none.keep(before));
        assertEquals(Optional.empty(), none.kept());
        assertEquals("nowhere", none.said());
        assertEquals(List.of("spread 1", "picture on=first maximized=false x=440 y=120 width=0 height=0",
                "console on=first maximized=false x=60 y=120 width=0 height=0"), before.written());
    }
}
