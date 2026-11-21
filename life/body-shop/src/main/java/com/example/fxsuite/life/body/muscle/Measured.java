package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.muscle.arm.ArmVolumes;
import com.example.fxsuite.life.body.muscle.leg.LegVolumes;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.Optional;

/**
 * A body's muscles as big as they were measured in bodies its height and mass ({@link Muscularity}): the leg's by
 * Handsfield's lines ({@link LegVolumes}), the arm's by Holzbaur's shares ({@link ArmVolumes}); any other, not yet
 * measured, as drawn.
 *
 * @param height how tall, m
 * @param mass   how heavy, kg
 */
public record Measured(double height, double mass) implements Muscularity, Scalar {

    @Override
    public double volume(final Muscle m, final double asDrawn) {
        return measured(m).orElse(asDrawn);
    }

    /** How big {@code m} was measured in such a body, cm3, if it was. */
    public Optional<Double> measured(final Muscle m) {
        return LegVolumes.INSTANCE.volume(m.name(), LegVolumes.INSTANCE.whole(height, mass))
                .or(() -> ArmVolumes.INSTANCE.volume(m.name(), ArmVolumes.INSTANCE.whole(height, mass)));
    }
}
