package melons.innocent.terrain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A stretch of the map: some size each way, holding what lies on or within its edges. */
class ExtentTest {

    @Test
    void someSizeEachWayHoldingWhatLiesOnOrWithinItsEdges() {
        final Extent extent = new Extent(-100, 0, 300, 50);
        assertEquals(400, extent.across());
        assertEquals(50, extent.along());
        assertTrue(extent.holds(-100, 50));
        assertTrue(extent.holds(0, 25));
        assertFalse(extent.holds(301, 25));
        assertThrows(IllegalArgumentException.class, () -> new Extent(0, 0, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Extent(0, 10, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> new Extent(0, 0, Double.POSITIVE_INFINITY, 10));
    }
}
