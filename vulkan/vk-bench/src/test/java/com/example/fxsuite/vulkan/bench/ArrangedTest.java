package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Window;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An arrangement kept: asked to be kept with the console standing somewhere, the next time the card hears it takes
 * where the picture stands and writes both, and nothing is written until it is asked. The window is never shown.
 */
class ArrangedTest {

    @Test
    void askedToBeKeptItWritesWhereBothWindowsStandWhenTheCardNextHears(@TempDir final Path folder) {
        final String file = folder.resolve("windows.spread").toString();
        final String before = System.getProperty("bench.spread", "");
        System.setProperty("bench.spread", file);
        try (Desk desk = new Desk(); Card card = new Card(desk);
             Window window = card.window("a window never shown", 320, 240)) {
            final Arranged arranged = new Arranged(true, 60, 120, 380);
            arranged.heard(window, desk);
            assertEquals(Optional.empty(), new Spreads(file).kept(), "nothing kept until it is asked");
            final Seat console = new Seat(Seat.Which.OTHER, false, 40, 40, 386, 916);
            arranged.asked(console);
            arranged.heard(window, desk);
            final Spread kept = new Spreads(file).kept().orElseThrow();
            assertEquals(console, kept.console());
            assertFalse(kept.picture().maximized());
            assertEquals(320, kept.picture().width(), 1, "the picture as big as it is");
            assertEquals(240, kept.picture().height(), 1);
            final int screens = desk.monitors().size();
            assertTrue(kept.picture().on() == Seat.Which.FIRST || screens > 1, "on a screen the desk has");
            // an arrangement made after that is by what was kept
            arranged.heard(window, desk);
            assertEquals(Optional.of(kept), new Spreads(file).kept(), "heard again with nothing asked, as it was");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        } finally {
            System.setProperty("bench.spread", before);
        }
    }
}
