package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras of the lands of Islam, by their caliphates and empires. Their years as historians commonly give them; each
 * era a record, its years fixed in its class.
 */
public final class IslamicEras {

    private IslamicEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new Umayyad(), new Abbasid(), new Ottoman(), new Safavid());

    /**
     * The Umayyad: the Dome of the Rock, the Great Mosque of Damascus.
     */
    public record Umayyad() implements Era<Islamic> {
        public String name() { return "Umayyad"; }
        public Year from() { return Years.ce(661); }
        public Year to() { return Years.ce(750); }
        public Islamic civilization() { return Islamic.IT; }
    }

    /**
     * The Abbasid: round Baghdad; its House of Wisdom.
     */
    public record Abbasid() implements Era<Islamic> {
        public String name() { return "Abbasid"; }
        public Year from() { return Years.ce(750); }
        public Year to() { return Years.ce(1258); }
        public Islamic civilization() { return Islamic.IT; }
    }

    /**
     * The Ottoman: Istanbul's great domed mosques.
     */
    public record Ottoman() implements Era<Islamic> {
        public String name() { return "Ottoman"; }
        public Year from() { return Years.ce(1299); }
        public Year to() { return Years.ce(1922); }
        public Islamic civilization() { return Islamic.IT; }
    }

    /**
     * The Safavid: Isfahan's squares and tiled domes.
     */
    public record Safavid() implements Era<Islamic> {
        public String name() { return "Safavid"; }
        public Year from() { return Years.ce(1501); }
        public Year to() { return Years.ce(1736); }
        public Islamic civilization() { return Islamic.IT; }
    }
}
