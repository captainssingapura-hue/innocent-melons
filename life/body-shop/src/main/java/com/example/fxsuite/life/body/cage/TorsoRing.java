package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.flesh.Landmark;
import com.example.fxsuite.life.body.flesh.Torso;

import static com.example.fxsuite.life.body.flesh.Landmark.ARMPIT;
import static com.example.fxsuite.life.body.flesh.Landmark.BUST;
import static com.example.fxsuite.life.body.flesh.Landmark.CREST;
import static com.example.fxsuite.life.body.flesh.Landmark.CROTCH;
import static com.example.fxsuite.life.body.flesh.Landmark.HIPS;
import static com.example.fxsuite.life.body.flesh.Landmark.JAW;
import static com.example.fxsuite.life.body.flesh.Landmark.NAVEL;
import static com.example.fxsuite.life.body.flesh.Landmark.NECK;
import static com.example.fxsuite.life.body.flesh.Landmark.NECK_BASE;
import static com.example.fxsuite.life.body.flesh.Landmark.SHOULDER;
import static com.example.fxsuite.life.body.flesh.Landmark.XIPHOID;

/**
 * The rings of a body's cage round its torso ({@link BodyCage}), bottom to top, each at a height found on the bones:
 * the lowest, where the legs part, at the folds under the buttocks; the hips; the crests; the navel and the abs' three
 * tendinous intersections over it; under the bust, its fold; the bust; from the armpits to the shoulders, where the arms go out;
 * over the shoulders; the neck; under the jaw, where the cage ends.
 */
public enum TorsoRing {
    LOW_HIPS("the folds under the buttocks", CROTCH, HIPS, 0.2, 0),
    HIPS_RING("the hips", HIPS, HIPS, 0, 0),
    ABOVE_HIPS("above the hips", HIPS, CREST, 0.5, 0),
    CRESTS("the iliac crests", CREST, CREST, 0, 0),
    BELOW_NAVEL("below the navel", CREST, NAVEL, 0.5, 0),
    NAVEL_RING("the navel", NAVEL, NAVEL, 0, 0),
    MID_ABS("the middle of the abs", NAVEL, XIPHOID, 0.4, 0),
    HIGH_ABS("high on the abs", NAVEL, XIPHOID, 0.75, 0),
    XIPHOID_RING("the xiphoid", XIPHOID, XIPHOID, 0, 0),
    FOLD("under the bust", XIPHOID, BUST, 0.4, 0),
    UNDER_BUST("the bust's underside", XIPHOID, BUST, 0.75, 0),
    BUST_RING("the bust", BUST, BUST, 0, 0),
    ARMPITS("the armpits", ARMPIT, ARMPIT, 0, 0),
    UNDER_SHOULDERS("under the shoulders", ARMPIT, SHOULDER, 0.5, 0),
    SHOULDERS("the shoulders", SHOULDER, SHOULDER, 0, 0),
    OVER_SHOULDERS("over the shoulders", SHOULDER, SHOULDER, 0, 2),
    NECK_FOOT("the neck's base", NECK_BASE, NECK_BASE, 0, 0),
    NECK_RING("the neck", NECK, NECK, 0, 0),
    UNDER_JAW("under the jaw", JAW, JAW, 0, 0);

    private final String said;
    private final Torso.Height height;

    TorsoRing(final String said, final Landmark at, final Landmark toward, final double along, final double off) {
        this.said = said;
        this.height = new Torso.Height(at, toward, along, off);
    }

    public String said() {
        return said;
    }

    /** How high it is. */
    public Torso.Height height() {
        return height;
    }
}
