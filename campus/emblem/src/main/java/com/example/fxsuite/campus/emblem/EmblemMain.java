package com.example.fxsuite.campus.emblem;

/**
 * The emblem's jar, run: its bench ({@link EmblemBench}). Started from a class that is not JavaFX's, so the jar runs
 * with JavaFX on its class path.
 */
public final class EmblemMain {

    private EmblemMain() {
    }

    public static void main(final String[] args) {
        javafx.application.Application.launch(EmblemBench.class, args);
    }
}
