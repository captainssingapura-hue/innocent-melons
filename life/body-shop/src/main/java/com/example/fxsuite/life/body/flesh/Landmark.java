package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.Level;
import com.example.fxsuite.life.body.bone.thorax.Rib;

/**
 * Heights on the trunk the torso's shape is drawn to, each found on the bones, as the body is held to be bound, in the
 * drawn figure's centimetres (up from the hip joints): the crotch, below the pubic symphysis; the hips at their widest,
 * level with the greater trochanters; the iliac crests, level with the fifth lumbar; the navel, with the fourth; the
 * lowest ribs' ends; the xiphoid, under the seventh rib's cartilage; the bust, a little under the fourth rib's end; the
 * armpits, below the shoulder joints; the shoulders, a little over them; the neck's base, under the seventh cervical;
 * the neck, at the fourth; under the jaw, below the joint the head nods on.
 */
public enum Landmark {
    CROTCH, HIPS, CREST, NAVEL, RIBS, XIPHOID, BUST, ARMPIT, SHOULDER, NECK_BASE, NECK, JAW;

    /** How high it is on the body {@code bound} holds. */
    public double y(final Rig bound) {
        return switch (this) {
            case CROTCH -> bound.frame(Part.PELVIS).place(HipBone.Points.SYMPHYSIS.frame().at()).y() - 3.5;
            case HIPS -> bound.frame(Part.RIGHT_THIGH).place(new V3(5.6, -2.8, -1.0)).y();
            case CREST -> Level.L5.middle().y();
            case NAVEL -> Level.L4.middle().y();
            case RIBS -> Rib.TENTH.end().y();
            case XIPHOID -> Rib.SEVENTH.cartilage().y() - 1.5;
            case BUST -> Rib.FOURTH.end().y() - 1.0;
            case ARMPIT -> bound.frame(Part.RIGHT_UPPER_ARM).at().y() - 6.0;
            case SHOULDER -> bound.frame(Part.RIGHT_UPPER_ARM).at().y() + 1.0;
            case NECK_BASE -> Level.C7.middle().y() - 1.8;
            case NECK -> Level.C4.middle().y() - 0.5;
            case JAW -> bound.frame(Part.HEAD).at().y() - 1.0;
        };
    }
}
