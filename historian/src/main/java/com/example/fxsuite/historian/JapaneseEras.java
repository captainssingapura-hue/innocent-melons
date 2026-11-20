package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras of the Japanese. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public final class JapaneseEras {

    private JapaneseEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new Asuka(), new Nara(), new Heian(), new Kamakura(),
            new Muromachi(), new AzuchiMomoyama(), new Edo(), new Meiji());

    /**
     * Asuka: Buddhism comes; the first temples, Horyu-ji among them.
     */
    public record Asuka() implements Era<Japanese> {
        public String name() { return "Asuka"; }
        public Year from() { return Years.ce(538); }
        public Year to() { return Years.ce(710); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Nara: the capital at Heijo-kyo, laid out as Chang'an was.
     */
    public record Nara() implements Era<Japanese> {
        public String name() { return "Nara"; }
        public Year from() { return Years.ce(710); }
        public Year to() { return Years.ce(794); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Heian: the court at Heian-kyo (Kyoto); its palaces and gardens.
     */
    public record Heian() implements Era<Japanese> {
        public String name() { return "Heian"; }
        public Year from() { return Years.ce(794); }
        public Year to() { return Years.ce(1185); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Kamakura: the shoguns' rule; Zen temples.
     */
    public record Kamakura() implements Era<Japanese> {
        public String name() { return "Kamakura"; }
        public Year from() { return Years.ce(1185); }
        public Year to() { return Years.ce(1333); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Muromachi: the Ashikaga shoguns; tea houses and dry gardens.
     */
    public record Muromachi() implements Era<Japanese> {
        public String name() { return "Muromachi"; }
        public Year from() { return Years.ce(1336); }
        public Year to() { return Years.ce(1573); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Azuchi-Momoyama: the land united; great castles.
     */
    public record AzuchiMomoyama() implements Era<Japanese> {
        public String name() { return "Azuchi-Momoyama"; }
        public Year from() { return Years.ce(1573); }
        public Year to() { return Years.ce(1603); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Edo: the Tokugawa peace; towns of timber and tile.
     */
    public record Edo() implements Era<Japanese> {
        public String name() { return "Edo"; }
        public Year from() { return Years.ce(1603); }
        public Year to() { return Years.ce(1868); }
        public Japanese civilization() { return Japanese.IT; }
    }

    /**
     * Meiji: Japan opened; brick and Western styles come.
     */
    public record Meiji() implements Era<Japanese> {
        public String name() { return "Meiji"; }
        public Year from() { return Years.ce(1868); }
        public Year to() { return Years.ce(1912); }
        public Japanese civilization() { return Japanese.IT; }
    }
}
