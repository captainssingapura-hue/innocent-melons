package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The costal cartilages: each lower one, from the fifth to the tenth, dipping below both its ends before it turns up,
 * and standing no further forward than either, so that nothing of it stands out of the chest's front.
 */
class RibsTest {

    @Test
    void theLowerCartilagesDipButStandNoFurtherForwardThanTheirEnds() {
        for (final Rib rib : Rib.values()) {
            if (rib.ordinal() < Rib.FIFTH.ordinal() || rib.floating()) {
                continue;
            }
            final V3 a = RibLine.INSTANCE.end(rib, 0);
            final V3 b = rib.cartilage();
            final V3 through = Ribs.INSTANCE.through(rib, 0);
            assertTrue(through.y() < Math.min(a.y(), b.y()), rib + " dips below its ends");
            assertTrue(through.z() <= Math.max(a.z(), b.z()), rib + " no further forward than its ends");
        }
    }
}
