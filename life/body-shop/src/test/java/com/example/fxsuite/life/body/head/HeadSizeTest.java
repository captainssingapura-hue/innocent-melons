package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A head sized to a man's skull: a round head, its eye where the skull's orbit sets it; made as big as it is asked, it
 * is asked nothing more; made a fifth taller, it is made shorter by as much, its breadth and length left as they were.
 */
class HeadSizeTest {

    private final Field.Shape round = Field.Ellipsoid.of(new V3(0, 6.5, 0.5), new V3(10, 0, 0), new V3(0, 10, 0),
            new V3(0, 0, 10));
    private final V3 eye = SkullUnder.INSTANCE.at(V3.ZERO);
    private final V3 size = HeadSize.INSTANCE.of(round, eye);

    @Test
    void aHeadMadeAsBigAsItIsAskedIsAskedNothingMore() {
        final V3 again = HeadSize.INSTANCE.of(stretched(size), eye.times(size));
        assertEquals(1, again.x(), 0.01);
        assertEquals(1, again.y(), 0.01);
        assertEquals(1, again.z(), 0.01);
    }

    @Test
    void aHeadTallerIsMadeShorterByAsMuchAndNoNarrower() {
        final V3 taller = new V3(1, 1.2, 1);
        final V3 k = HeadSize.INSTANCE.of(stretched(taller), eye.times(taller));
        assertEquals(size.y() / 1.2, k.y(), 0.02 * size.y());
        assertEquals(size.x(), k.x(), 0.02 * size.x());
        assertEquals(size.z(), k.z(), 0.02 * size.z());
    }

    private Field.Shape stretched(final V3 k) {
        return new Reshaped(round, V3.ZERO, V3.ZERO, Rot.NONE, k, 0);
    }
}
