package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.Game;

/**
 * The campus's jar, run: its bench ({@link CampusBench}). Started from a class that is not JavaFX's, so the jar runs
 * with JavaFX on its class path; the game's worlds told of the simulation first, as any game window needs; the
 * campus's offices told to the drawing.
 */
public final class CampusMain {

    private CampusMain() {
    }

    public static void main(final String[] args) {
        Game.tellWorlds();
        Campus.register();
        javafx.application.Application.launch(CampusBench.class, args);
    }
}
