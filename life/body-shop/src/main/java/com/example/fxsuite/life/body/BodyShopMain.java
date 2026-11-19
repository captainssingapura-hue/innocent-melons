package com.example.fxsuite.life.body;

/**
 * The body shop's jar, run: its bench ({@link BodyShopBench}). Started from a class that is not JavaFX's, so the jar
 * runs with JavaFX on its class path.
 */
public final class BodyShopMain {

    private BodyShopMain() {
    }

    public static void main(final String[] args) {
        javafx.application.Application.launch(BodyShopBench.class, args);
    }
}
