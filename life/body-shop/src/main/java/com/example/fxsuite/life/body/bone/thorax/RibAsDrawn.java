package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Each rib, on the right, as it was first drawn, by eye, before it was measured ({@link RibLine}): what the muscles
 * held on the ribs, and the cartilages' far ends, were drawn against, and are bent from ({@link ThoraxForm}). How far
 * out it reached at its widest and how far ahead that was; where its bone ended in front; where its cartilage reached
 * from there.
 */
public enum RibAsDrawn {
    FIRST(5.6, 0.4, new V3(3.3, 43.4, 5.0), new V3(2.0, 43.6, 6.6)),
    SECOND(8.0, -0.2, new V3(5.2, 40.6, 7.0), new V3(1.6, 40.9, 7.5)),
    THIRD(9.8, -0.6, new V3(6.6, 37.4, 8.2), new V3(1.5, 38.6, 7.9)),
    FOURTH(11.1, -0.8, new V3(7.8, 34.4, 9.0), new V3(1.55, 36.3, 8.3)),
    FIFTH(12.1, -0.9, new V3(8.8, 31.6, 9.4), new V3(1.65, 33.9, 8.7)),
    SIXTH(12.8, -1.0, new V3(9.6, 28.8, 9.4), new V3(1.8, 31.8, 9.1)),
    SEVENTH(13.3, -1.0, new V3(10.2, 26.2, 9.0), new V3(1.6, 30.2, 9.3)),
    EIGHTH(13.5, -1.0, new V3(10.6, 23.8, 8.2), new V3(5.4, 27.4, 9.9)),
    NINTH(13.4, -1.2, new V3(10.9, 21.8, 7.0), new V3(7.8, 24.7, 9.4)),
    TENTH(13.1, -1.6, new V3(11.1, 20.2, 5.4), new V3(9.6, 22.6, 8.4)),
    ELEVENTH(12.2, -3.0, new V3(11.4, 20.6, 0.2), new V3(11.3, 20.0, 1.6)),
    TWELFTH(10.4, -5.0, new V3(9.2, 22.4, -3.6), new V3(9.0, 22.0, -2.6));

    private final double reach;
    private final double widestAhead;
    private final V3 end;
    private final V3 cartilage;

    RibAsDrawn(final double reach, final double widestAhead, final V3 end, final V3 cartilage) {
        this.reach = reach;
        this.widestAhead = widestAhead;
        this.end = end;
        this.cartilage = cartilage;
    }

    /** Where its cartilage reached. */
    public V3 cartilage() {
        return cartilage;
    }

    /**
     * Its line as drawn, from its head past its tubercle on the transverse process, round its angle, out to its widest
     * and on to its end.
     */
    public List<V3> line() {
        final Rib rib = Rib.values()[ordinal()];
        final Level l = rib.vertebra();
        final V3 tip = l.drawnTransverseTip();
        final List<V3> keys = new ArrayList<>();
        keys.add(rib.drawnHead());
        keys.add(rib.floating() ? new V3(tip.x() * 0.9, tip.y() - 0.4, tip.z() + 0.3)
                : tip.plus(new V3(0.15, -0.2, 0.25)));
        keys.add(new V3(tip.x() + 1.6 + 0.16 * ordinal(), tip.y() - 0.8, tip.z() - 0.25));
        keys.add(new V3(reach, (tip.y() + end.y()) / 2 - 0.5, widestAhead));
        keys.add(end);
        return Sketch.smooth(keys, 28);
    }
}
