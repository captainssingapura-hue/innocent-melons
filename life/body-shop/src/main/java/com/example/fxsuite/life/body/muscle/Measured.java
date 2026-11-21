package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.muscle.leg.LegVolumes;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A body's muscles as big as they were measured in bodies its height and mass ({@link Muscularity}): the leg's by
 * Handsfield's lines ({@link LegVolumes}); any other, not yet measured, as drawn.
 *
 * @param height how tall, m
 * @param mass   how heavy, kg
 */
public record Measured(double height, double mass) implements Muscularity, Scalar {

    @Override
    public double volume(final Muscle m, final double asDrawn) {
        return LegVolumes.INSTANCE.volume(m.name(), LegVolumes.INSTANCE.whole(height, mass)).orElse(asDrawn);
    }
}
