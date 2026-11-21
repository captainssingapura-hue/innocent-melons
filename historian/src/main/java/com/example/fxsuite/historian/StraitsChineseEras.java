package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the Straits Chinese. Their years as historians commonly give them; each era a record, its years fixed in
 * its class.
 */
public record StraitsChineseEras() implements StatelessFunctionalObject {

    public static final StraitsChineseEras INSTANCE = new StraitsChineseEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new StraitsSettlements());
    }

    /**
     * The Straits Settlements: Penang, Malacca and Singapore under the British; the shophouse streets of Chinatown and
     * Telok Ayer.
     */
    public record StraitsSettlements() implements Era<StraitsChinese> {
        public String name() { return "Straits Settlements"; }
        public Year from() { return Years.INSTANCE.ce(1826); }
        public Year to() { return Years.INSTANCE.ce(1946); }
        public StraitsChinese civilization() { return StraitsChinese.IT; }
    }
}
