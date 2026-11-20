package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.look.Figures;
import com.example.fxsuite.life.body.bone.BoneBench;
import com.example.fxsuite.life.body.joint.AssemblyBench;

import java.util.Arrays;

/**
 * The body shop's jar, run: its bench ({@link BodyShopBench}); with {@code parts}, the robot parts bench
 * ({@link RobotPartsBench}); with {@code motion}, the motion bench ({@link MotionBench}); with {@code wardrobe}, the
 * wardrobe ({@link WardrobeBench}); with {@code bones}, the bone bench, each bone on its own; with {@code assembly},
 * the skeleton put together from its joints, as far as the groups asked; with
 * {@code play}, the game, its humanoids offered as the avatar ({@link HumanoidFigure}) beside the courier, the exposed
 * robot chosen unless {@code -Davatar.figure} says otherwise; anything after {@code play} given to the game. Started
 * from a class that is not JavaFX's, so the jar runs with JavaFX on its class path.
 */
public final class BodyShopMain {

    private BodyShopMain() {
    }

    public static void main(final String[] args) throws Exception {
        final String first = args.length > 0 ? args[0].toLowerCase(java.util.Locale.ROOT) : "";
        switch (first) {
            case "play" -> {
                HumanoidFigure.offer();
                if (System.getProperty("avatar.figure") == null) {
                    Figures.choose(Puppet.Kind.EXPOSED.label());
                }
                com.example.fxsuite.avatar.Main.main(Arrays.copyOfRange(args, 1, args.length));
            }
            case "parts" -> javafx.application.Application.launch(RobotPartsBench.class, args);
            case "motion" -> javafx.application.Application.launch(MotionBench.class, args);
            case "wardrobe" -> javafx.application.Application.launch(WardrobeBench.class, args);
            case "bones" -> javafx.application.Application.launch(BoneBench.class, args);
            case "assembly" -> javafx.application.Application.launch(AssemblyBench.class, args);
            default -> javafx.application.Application.launch(BodyShopBench.class, args);
        }
    }
}
