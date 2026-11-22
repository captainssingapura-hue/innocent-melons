package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;

/**
 * Each rib, on the right, in the body's frame as the papers' figure stands, as Holcombe, Wang and Grotberg measured
 * 1,042 adults' ribs by CT (J Anat 2017, 231:229, tables A1 and A2): the vertebra it is hung from; how long it is end
 * to end, how far out of that line it reaches and how its line is turned up from hanging down, each as table A2's row
 * has it for who it is ({@link RibMeasure}); and, the same for all, table A1's means: how far along that line it reaches
 * furthest out of it, in lengths; the angle it leaves its head at, from that line, degrees; how far its line is swung
 * out from the body's middle, and how far it is turned about its line, its outer side up, degrees. Its shape from these
 * ({@link RibLine}); its cartilage reaching the sternum for the first seven, the cartilage above for the next three, a
 * free tip for the last two.
 */
public enum Rib {
    FIRST(Level.T1, new int[] {31, 98, -24, 190, 40}, new int[] {62, -63, -9, -36, -7},
            new int[] {756, 96, -358, -236, 235}, 0.384, 81.7, 36.2, 11.6),
    SECOND(Level.T2, new int[] {56, 129, -34, 286, 13}, new int[] {67, -83, 0, 2, -7},
            new int[] {757, 103, -350, -238, 263}, 0.318, 111.8, 20.3, 5.4),
    THIRD(Level.T3, new int[] {67, 168, -39, 403, -9}, new int[] {63, -93, -1, -2, 13},
            new int[] {770, 94, -350, -243, 271}, 0.344, 116.8, 20.7, 5.4),
    FOURTH(Level.T4, new int[] {81, 195, -61, 469, -18}, new int[] {57, -96, 1, -8, 24},
            new int[] {752, 88, -354, -235, 272}, 0.322, 115.4, 22.9, 6.9),
    FIFTH(Level.T5, new int[] {88, 237, -90, 528, -15}, new int[] {53, -104, 7, -17, 27},
            new int[] {740, 86, -357, -235, 274}, 0.312, 113.9, 24.6, 8.2),
    SIXTH(Level.T6, new int[] {96, 265, -102, 562, -37}, new int[] {49, -111, 6, -20, 37},
            new int[] {702, 87, -318, -227, 280}, 0.297, 112.0, 28.0, 10.7),
    SEVENTH(Level.T7, new int[] {106, 283, -105, 566, -33}, new int[] {44, -109, 2, -16, 39},
            new int[] {706, 88, -266, -236, 271}, 0.284, 109.3, 33.9, 14.6),
    EIGHTH(Level.T8, new int[] {92, 246, -106, 651, -14}, new int[] {45, -92, -5, -40, 35},
            new int[] {664, 85, -175, -222, 261}, 0.297, 104.8, 39.9, 16.6),
    NINTH(Level.T9, new int[] {83, 192, -104, 648, 14}, new int[] {41, -79, -4, -24, 29},
            new int[] {670, 91, -128, -224, 243}, 0.346, 97.4, 46.5, 14.0),
    TENTH(Level.T10, new int[] {67, 187, -93, 628, 55}, new int[] {38, -70, -9, -14, 11},
            new int[] {601, 102, -78, -182, 210}, 0.372, 90.0, 56.1, 11.2),
    ELEVENTH(Level.T11, new int[] {38, 168, -85, 629, 59}, new int[] {32, -57, -15, -10, 5},
            new int[] {544, 77, -16, -132, 164}, 0.388, 82.7, 69.9, 10.4),
    TWELFTH(Level.T12, new int[] {8, 35, -92, 469, 129}, new int[] {10, -7, -5, 42, 15},
            new int[] {631, 49, -113, -117, 96}, 0.390, 60.9, 94.0, 14.6);

    private final Level vertebra;
    private final int[] chord;
    private final int[] peak;
    private final int[] pump;
    private final double along;
    private final double leaves;
    private final double swing;
    private final double turn;

    Rib(final Level vertebra, final int[] chord, final int[] peak, final int[] pump, final double along,
        final double leaves, final double swing, final double turn) {
        this.vertebra = vertebra;
        this.chord = chord;
        this.peak = peak;
        this.pump = pump;
        this.along = along;
        this.leaves = leaves;
        this.swing = swing;
        this.turn = turn;
    }

    public Level vertebra() {
        return vertebra;
    }

    /** How long it is end to end, cm, as female as said. */
    public double chord(final double female) {
        return RibMeasure.CHORD.of(chord, female) / 10;
    }

    /** How far out of its line from end to end it reaches at most, in lengths end to end. */
    public double peak(final double female) {
        return RibMeasure.PEAK.of(peak, female);
    }

    /** How far up from hanging straight down its line from end to end is turned, degrees. */
    public double pump(final double female) {
        return RibMeasure.PUMP.of(pump, female);
    }

    /** How far along its line from end to end it reaches furthest out of it, in lengths end to end. */
    public double along() {
        return along;
    }

    /** The angle it leaves its head at, from its line from end to end, degrees. */
    public double leaves() {
        return leaves;
    }

    /** How far its line from end to end is swung out from the body's middle, degrees. */
    public double swing() {
        return swing;
    }

    /** How far it is turned about its line from end to end, its outer side up, degrees. */
    public double turn() {
        return turn;
    }

    /** Its head, where it meets its vertebra's body. */
    public V3 head() {
        final Level l = vertebra;
        return l.at(l.wide() / 2 + 0.25, l.high() * 0.35, -l.deep() * 0.2);
    }

    /** Its head as the spine was drawn, before it was curved. */
    public V3 drawnHead() {
        final Level l = vertebra;
        return l.drawnAt(l.wide() / 2 + 0.25, l.high() * 0.35, -l.deep() * 0.2);
    }

    /** Where its bone ends in front, a man's. */
    public V3 end() {
        return RibLine.INSTANCE.end(this, 0);
    }

    /** Where its cartilage reaches, bent from where it was drawn to reach as the chest is ({@link ThoraxForm}). */
    public V3 cartilage() {
        return ThoraxForm.INSTANCE.apply(asDrawn().cartilage());
    }

    /** As it was first drawn, before it was measured. */
    public RibAsDrawn asDrawn() {
        return RibAsDrawn.values()[ordinal()];
    }

    /** Whether it floats, its cartilage reaching nothing. */
    public boolean floating() {
        return this == ELEVENTH || this == TWELFTH;
    }
}
