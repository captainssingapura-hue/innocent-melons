package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras of the Chinese, by their dynasties. Their years as historians commonly give them; each era a record, its
 * years fixed in its class.
 */
public final class ChineseEras {

    private ChineseEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new Shang(), new Zhou(), new Qin(), new Han(), new SixDynasties(),
            new Sui(), new Tang(), new FiveDynasties(), new Song(), new NorthernSong(), new SouthernSong(), new Yuan(),
            new Ming(), new Qing(), new Republic());

    /**
     * The Shang: bronze, oracle bones, the first walled capitals.
     */
    public record Shang() implements Era<Chinese> {
        public String name() { return "Shang"; }
        public Year from() { return Years.bce(1600); }
        public Year to() { return Years.bce(1046); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Zhou: the longest dynasty; its rites, its states at war.
     */
    public record Zhou() implements Era<Chinese> {
        public String name() { return "Zhou"; }
        public Year from() { return Years.bce(1046); }
        public Year to() { return Years.bce(256); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Qin: the first empire; the walls joined, weights and measures made one.
     */
    public record Qin() implements Era<Chinese> {
        public String name() { return "Qin"; }
        public Year from() { return Years.bce(221); }
        public Year to() { return Years.bce(206); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Han: the empire made lasting; timber halls on rammed-earth terraces.
     */
    public record Han() implements Era<Chinese> {
        public String name() { return "Han"; }
        public Year from() { return Years.bce(206); }
        public Year to() { return Years.ce(220); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Six Dynasties: the empire divided; Buddhism's temples and pagodas come.
     */
    public record SixDynasties() implements Era<Chinese> {
        public String name() { return "Six Dynasties"; }
        public Year from() { return Years.ce(220); }
        public Year to() { return Years.ce(589); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Sui: the empire reunited; the Grand Canal dug.
     */
    public record Sui() implements Era<Chinese> {
        public String name() { return "Sui"; }
        public Year from() { return Years.ce(581); }
        public Year to() { return Years.ce(618); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Tang: Chang'an, the greatest city of its day; halls of great brackets.
     */
    public record Tang() implements Era<Chinese> {
        public String name() { return "Tang"; }
        public Year from() { return Years.ce(618); }
        public Year to() { return Years.ce(907); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Five Dynasties and Ten Kingdoms: the north and south divided again.
     */
    public record FiveDynasties() implements Era<Chinese> {
        public String name() { return "Five Dynasties"; }
        public Year from() { return Years.ce(907); }
        public Year to() { return Years.ce(960); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Song: the empire reunited, its cities of trade; the Yingzao Fashi, 1103, setting how to build.
     */
    public record Song() implements Era<Chinese> {
        public String name() { return "Song"; }
        public Year from() { return Years.ce(960); }
        public Year to() { return Years.ce(1279); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Northern Song: its capital at Bianjing (Kaifeng), the city of the Qingming scroll, until the Jin took it.
     */
    public record NorthernSong() implements Era<Chinese> {
        public String name() { return "Northern Song"; }
        public Year from() { return Years.ce(960); }
        public Year to() { return Years.ce(1127); }
        public Chinese civilization() { return Chinese.IT; }
        public Era<?> within() { return new Song(); }
    }

    /**
     * The Southern Song: its capital moved south to Lin'an (Hangzhou), until the Mongols took it.
     */
    public record SouthernSong() implements Era<Chinese> {
        public String name() { return "Southern Song"; }
        public Year from() { return Years.ce(1127); }
        public Year to() { return Years.ce(1279); }
        public Chinese civilization() { return Chinese.IT; }
        public Era<?> within() { return new Song(); }
    }

    /**
     * The Yuan: the Mongols' empire; Dadu built where Beijing is.
     */
    public record Yuan() implements Era<Chinese> {
        public String name() { return "Yuan"; }
        public Year from() { return Years.ce(1271); }
        public Year to() { return Years.ce(1368); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Ming: the Forbidden City, the wall rebuilt in brick.
     */
    public record Ming() implements Era<Chinese> {
        public String name() { return "Ming"; }
        public Year from() { return Years.ce(1368); }
        public Year to() { return Years.ce(1644); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Qing: the last empire; its halls the ones most built after since.
     */
    public record Qing() implements Era<Chinese> {
        public String name() { return "Qing"; }
        public Year from() { return Years.ce(1644); }
        public Year to() { return Years.ce(1912); }
        public Chinese civilization() { return Chinese.IT; }
    }

    /**
     * The Republic: the empire ended; the treaty ports' foreign streets.
     */
    public record Republic() implements Era<Chinese> {
        public String name() { return "Republic"; }
        public Year from() { return Years.ce(1912); }
        public Year to() { return Years.ce(1949); }
        public Chinese civilization() { return Chinese.IT; }
    }
}
