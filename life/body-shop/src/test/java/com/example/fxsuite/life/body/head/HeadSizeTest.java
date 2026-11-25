package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A head sized to a man's skull region by region: an egg of a head, longer down than up, its eye where the skull's
 * orbit sets it; made as it is asked, it is asked nothing more; its vault made a quarter higher, only its vault is
 * made lower again, by as much.
 */
class HeadSizeTest {

    private final Field.Shape egg = Field.Ellipsoid.of(new V3(0, 4, 0.5), new V3(9, 0, 0), new V3(0, 13, 0),
            new V3(0, 0, 10));
    private final V3 eye = SkullUnder.INSTANCE.eyeOnSkull();
    private final HeadWarp sized = HeadSize.INSTANCE.of(egg, eye);

    @Test
    void aHeadMadeAsItIsAskedIsAskedNothingMore() {
        final HeadWarp.Scales again = HeadSize.INSTANCE.of(HeadSize.INSTANCE.made(egg, sized), sized.apply(eye))
                .scales();
        assertEquals(1, again.vault(), 0.015);
        assertEquals(1, again.face(), 0.015);
        assertEquals(1, again.ahead(), 0.015);
        assertEquals(1, again.cheek(), 0.015);
    }

    @Test
    void aHigherVaultIsMadeLowerByAsMuchAndNothingElse() {
        final HeadWarp higher = new HeadWarp(eye, sized.levels(), new HeadWarp.Scales(1.25, 1, 1, 1));
        final HeadWarp.Scales k = HeadSize.INSTANCE.of(HeadSize.INSTANCE.made(egg, higher), higher.apply(eye))
                .scales();
        final HeadWarp.Scales s = sized.scales();
        assertEquals(s.vault() / 1.25, k.vault(), 0.02 * s.vault());
        assertEquals(s.face(), k.face(), 0.02 * s.face());
        assertEquals(s.ahead(), k.ahead(), 0.02 * s.ahead());
        assertEquals(s.cheek(), k.cheek(), 0.02 * s.cheek());
    }
}
