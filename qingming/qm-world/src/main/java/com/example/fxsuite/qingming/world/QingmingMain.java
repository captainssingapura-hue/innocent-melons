package com.example.fxsuite.qingming.world;

import com.example.fxsuite.avatar.AvatarDemo;

/**
 * The world's jar, run: the engine's game straight into the reach ({@link Qingming}), unless another demo is asked for
 * ({@code -Davatar.demo}). Started from a class that is not JavaFX's, so the jar runs with JavaFX on its class path;
 * the engine's worlds and the reach told of the simulation first, by the world itself.
 */
public final class QingmingMain {

    private QingmingMain() {
    }

    public static void main(final String[] args) {
        Qingming.tell();
        if (System.getProperty("avatar.demo") == null) {
            System.setProperty("avatar.demo", Qingming.DEMO.name());
        }
        javafx.application.Application.launch(AvatarDemo.class, args);
    }
}
