package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.AvatarDemo;
import com.example.fxsuite.avatar.buildings.TowerBench;
import com.example.fxsuite.bay.buildings.Refined;

/**
 * The bay's jar, run: its bench ({@link BayBench}), its towers one at a time where each stands, each played alone;
 * with {@code bay}, the bay itself played ({@link Bay}), its land, its roads and its refined towers standing in it, in
 * the engine's game, the avatar where the bay starts it; or, with {@code towers}, the tower bench with the bay's
 * towers ({@link Refined}) offered among its choices, each on its own ground, away from the bay. Started from a
 * class that is not JavaFX's, so the jar runs with JavaFX on its class path; the engine's worlds and the bay told of
 * the simulation first, by the bay itself.
 */
public final class BayMain {

    private BayMain() {
    }

    public static void main(final String[] args) {
        Bay.tell();
        Refined.TOWERS.forEach(t -> TowerBench.offer(t.name(), t.model()));
        final String asked = args.length > 0 ? args[0].toLowerCase(java.util.Locale.ROOT) : "";
        if (asked.equals("bay")) {
            // the game straight into the bay, unless another demo is asked for
            if (System.getProperty("avatar.demo") == null) {
                System.setProperty("avatar.demo", Bay.DEMO.name());
            }
            javafx.application.Application.launch(AvatarDemo.class, args);
        } else {
            javafx.application.Application.launch(asked.equals("towers") ? TowerBench.class : BayBench.class, args);
        }
    }
}
