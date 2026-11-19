package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.buildings.TowerBench;
import com.example.fxsuite.bay.buildings.AsiaSquare;
import com.example.fxsuite.bay.buildings.Mbfc;
import com.example.fxsuite.bay.buildings.Orq;

/**
 * The bay's jar, run: its bench ({@link BayBench}), the bay with its towers standing in it; or, with {@code towers},
 * the tower bench with the bay's towers offered among its choices, each to be looked into on its own. Started from a
 * class that is not JavaFX's, so the jar runs with JavaFX on its class path; the engine's worlds and the bay told of
 * the simulation first, by the bay itself.
 */
public final class BayMain {

    private BayMain() {
    }

    public static void main(final String[] args) {
        Bay.tell();
        TowerBench.offer("MBFC Tower 1", Mbfc::tower1);
        TowerBench.offer("MBFC Tower 2", Mbfc::tower2);
        TowerBench.offer("MBFC Tower 3", Mbfc::tower3);
        TowerBench.offer("ORQ North Tower", Orq::north);
        TowerBench.offer("ORQ South Tower", Orq::south);
        TowerBench.offer("Asia Square Tower 1", AsiaSquare::tower1);
        TowerBench.offer("Asia Square Tower 2", AsiaSquare::tower2);
        final boolean towers = args.length > 0 && args[0].equalsIgnoreCase("towers");
        javafx.application.Application.launch(towers ? TowerBench.class : BayBench.class, args);
    }
}
