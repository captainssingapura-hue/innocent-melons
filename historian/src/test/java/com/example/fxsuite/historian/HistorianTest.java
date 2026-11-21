package com.example.fxsuite.historian;

import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The historian: nothing wrong with what it lists; years before the common era counted as Java counts them; a
 * civilization's lineage and the eras open to it; what ran in a year and alongside an era; a provenance refused where
 * its civilization was not there.
 */
class HistorianTest {

    @Test
    void nothingWrongWithWhatItLists() {
        assertEquals(List.of(), Historian.INSTANCE.problems());
        assertEquals(12, Historian.INSTANCE.civilizations().size());
        assertEquals(59, Historian.INSTANCE.eras().size());
    }

    @Test
    void yearsBeforeTheCommonEraAsJavaCountsThem() {
        assertEquals(Year.of(0), Years.INSTANCE.bce(1), "1 BCE, Java's year 0");
        assertEquals(Year.of(-220), Years.INSTANCE.bce(221));
        assertEquals("221 BCE", Years.INSTANCE.said(Years.INSTANCE.bce(221)));
        assertEquals("960", Years.INSTANCE.said(Years.INSTANCE.ce(960)));
        assertEquals("now", Years.INSTANCE.said(Years.INSTANCE.ongoing()));
        assertThrows(IllegalArgumentException.class, () -> Years.INSTANCE.bce(0));
    }

    @Test
    void aCivilizationsLineageAndTheErasOpenToIt() {
        assertEquals(List.of(StraitsChinese.IT, Chinese.IT, Civilization.ROOT),
                Historian.INSTANCE.lineage(StraitsChinese.IT));
        final List<Era<?>> open = Historian.INSTANCE.erasOf(StraitsChinese.IT);
        assertEquals(new StraitsChineseEras.StraitsSettlements(), open.getFirst(), "its own first");
        assertTrue(open.contains(new ChineseEras.NorthernSong()), "its ancestors'");
        assertEquals(new CommonEras.Contemporary(), open.getLast(), "everyone's last");
        assertFalse(Historian.INSTANCE.erasOf(Chinese.IT).contains(new StraitsChineseEras.StraitsSettlements()),
                "not its descendants'");
        assertTrue(Chinese.IT.includes(StraitsChinese.IT));
        assertFalse(StraitsChinese.IT.includes(Chinese.IT));
    }

    @Test
    void whatRanInAYearAndAlongsideAnEra() {
        final List<Era<?>> in1100 = Historian.INSTANCE.at(Years.INSTANCE.ce(1100));
        assertTrue(in1100.contains(new ChineseEras.Song()) && in1100.contains(new ChineseEras.NorthernSong()));
        assertTrue(in1100.contains(new JapaneseEras.Heian()) && in1100.contains(new EuropeanEras.Romanesque()));
        final List<Era<?>> alongside = Historian.INSTANCE.contemporaries(new ChineseEras.NorthernSong());
        assertTrue(alongside.contains(new KoreanEras.Goryeo()) && alongside.contains(new IslamicEras.Abbasid()));
        assertTrue(alongside.stream().noneMatch(e -> e.civilization().equals(Chinese.IT)), "other civilizations'");
        assertEquals(new ChineseEras.Song(), new ChineseEras.NorthernSong().within());
    }

    @Test
    void aProvenanceRefusedWhereItsCivilizationWasNotThere() {
        final var qingming = new Provenance<>(Chinese.IT, new ChineseEras.NorthernSong());
        assertEquals("Northern Song", qingming.era().name());
        // a Straits Chinese thing may be of a Chinese era by lineage, but there were no Straits Chinese in 1100
        assertThrows(IllegalArgumentException.class,
                () -> new Provenance<>(StraitsChinese.IT, new ChineseEras.NorthernSong()));
        new Provenance<>(StraitsChinese.IT, new StraitsChineseEras.StraitsSettlements());
        new Provenance<>(British.IT, new BritishEras.Victorian());
        new Provenance<>(Chinese.IT, new CommonEras.Contemporary());
        // what does not compile, as it should not:
        // new Provenance<>(Chinese.IT, new StraitsChineseEras.StraitsSettlements())
        // and new Provenance<>(European.IT, new ChineseEras.NorthernSong())
    }
}
