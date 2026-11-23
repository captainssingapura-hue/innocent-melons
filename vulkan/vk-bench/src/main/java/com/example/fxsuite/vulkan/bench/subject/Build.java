package com.example.fxsuite.vulkan.bench.subject;

/**
 * How a bot is built across, where a man's build and a woman's differ, in centimetres as the bot is drawn at its
 * own size (1.88 m) before it is made as tall as it is: how far apart its shoulder joints are and its hip joints,
 * how wide its chest is and how deep, how wide its waist and its pelvis, and how thick its arms and legs are, of a
 * man's. Its heights (where its knees, hips and shoulders are, of its height) are the same in both. The figures
 * are their author's, from general knowledge of how a man and a woman differ, not from a survey.
 */
public enum Build {

    /** A man's: broad at the shoulders, narrow at the hips. */
    MAN(18, 9.5, 32, 23, 28, 34, 1, 1, 188),
    /** A woman's: narrower at the shoulders, chest and waist, wider at the hips, slimmer in the arms and legs. */
    WOMAN(15.5, 10.5, 28, 21, 22, 36, 0.85, 0.92, 170);

    private final double shoulders;
    private final double hips;
    private final double chest;
    private final double chestDeep;
    private final double waist;
    private final double pelvis;
    private final double arms;
    private final double legs;
    private final double tall;

    Build(final double shoulders, final double hips, final double chest, final double chestDeep, final double waist,
          final double pelvis, final double arms, final double legs, final double tall) {
        this.shoulders = shoulders;
        this.hips = hips;
        this.chest = chest;
        this.chestDeep = chestDeep;
        this.waist = waist;
        this.pelvis = pelvis;
        this.arms = arms;
        this.legs = legs;
        this.tall = tall;
    }

    /** How far either shoulder joint is from its middle. */
    public double shoulders() {
        return shoulders;
    }

    /** How far either hip joint is from its middle. */
    public double hips() {
        return hips;
    }

    public double chest() {
        return chest;
    }

    public double chestDeep() {
        return chestDeep;
    }

    public double waist() {
        return waist;
    }

    public double pelvis() {
        return pelvis;
    }

    /** How thick its arms are, of a man's. */
    public double arms() {
        return arms;
    }

    /** How thick its legs are, of a man's. */
    public double legs() {
        return legs;
    }

    /** How tall a bot of this build stands unless told otherwise, in centimetres. */
    public double tall() {
        return tall;
    }
}
