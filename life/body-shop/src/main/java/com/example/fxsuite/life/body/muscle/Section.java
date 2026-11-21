package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A muscle across, where it passes a point on a bone ({@link Anchor}): lying there on what is under it, the bone or
 * another muscle; so wide across its fibres and so deep, as it is drawn, its depth the way it faces, out from what it
 * lies on toward the skin (as the bone is drawn); an oval, its middle half its depth out. At a tendon, small; over the
 * belly, as thick as the muscle is drawn, more or less as a body has more or less of it ({@link Muscularity}).
 *
 * @param at     where it lies on what is under it
 * @param wide   how wide across, cm
 * @param deep   how deep, cm
 * @param facing which way its depth is, as the bone is drawn
 */
public record Section(Anchor at, double wide, double deep, V3 facing) implements Scalar {
}
