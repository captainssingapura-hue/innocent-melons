package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;

/**
 * Each rib, on the right, in the body's frame as the papers' figure stands at rest: the vertebra it is hung from; how
 * far out it reaches at its widest and how far ahead that is; where its bone ends in front; and where its cartilage
 * reaches from there, the sternum for the first seven, the cartilage above for the next three, a free tip for the last
 * two.
 */
public enum Rib {
    FIRST(Level.T1, 5.6, 0.4, new V3(3.3, 43.4, 5.0), new V3(2.0, 43.6, 6.6)),
    SECOND(Level.T2, 8.0, -0.2, new V3(5.2, 40.6, 7.0), new V3(1.6, 40.9, 7.5)),
    THIRD(Level.T3, 9.8, -0.6, new V3(6.6, 37.4, 8.2), new V3(1.5, 38.6, 7.9)),
    FOURTH(Level.T4, 11.1, -0.8, new V3(7.8, 34.4, 9.0), new V3(1.55, 36.3, 8.3)),
    FIFTH(Level.T5, 12.1, -0.9, new V3(8.8, 31.6, 9.4), new V3(1.65, 33.9, 8.7)),
    SIXTH(Level.T6, 12.8, -1.0, new V3(9.6, 28.8, 9.4), new V3(1.8, 31.8, 9.1)),
    SEVENTH(Level.T7, 13.3, -1.0, new V3(10.2, 26.2, 9.0), new V3(1.6, 30.2, 9.3)),
    EIGHTH(Level.T8, 13.5, -1.0, new V3(10.6, 23.8, 8.2), new V3(5.4, 27.4, 9.9)),
    NINTH(Level.T9, 13.4, -1.2, new V3(10.9, 21.8, 7.0), new V3(7.8, 24.7, 9.4)),
    TENTH(Level.T10, 13.1, -1.6, new V3(11.1, 20.2, 5.4), new V3(9.6, 22.6, 8.4)),
    ELEVENTH(Level.T11, 12.2, -3.0, new V3(11.4, 20.6, 0.2), new V3(11.3, 20.0, 1.6)),
    TWELFTH(Level.T12, 10.4, -5.0, new V3(9.2, 22.4, -3.6), new V3(9.0, 22.0, -2.6));

    private final Level vertebra;
    private final double reach;
    private final double widestAhead;
    private final V3 end;
    private final V3 cartilage;

    Rib(final Level vertebra, final double reach, final double widestAhead, final V3 end, final V3 cartilage) {
        this.vertebra = vertebra;
        this.reach = reach;
        this.widestAhead = widestAhead;
        this.end = end;
        this.cartilage = cartilage;
    }

    public Level vertebra() {
        return vertebra;
    }

    public double reach() {
        return reach;
    }

    public double widestAhead() {
        return widestAhead;
    }

    /** Where its bone ends in front. */
    public V3 end() {
        return end;
    }

    /** Where its cartilage reaches. */
    public V3 cartilage() {
        return cartilage;
    }

    /** Whether it floats, its cartilage reaching nothing. */
    public boolean floating() {
        return this == ELEVENTH || this == TWELFTH;
    }
}
