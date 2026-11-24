package melons.innocent.plants.walk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A forest kept within a budget, with no card. Under the budget its rings stay as set. Over it, its quality comes
 * down a little each time it is asked, never below its least; well under it again, it comes back up more slowly, and
 * no further than as set; and between, it stays where it is. A little past the budget nothing more is sent. Rings
 * drawn in draw in by the same share, the trees never nearer than they may be, and the rings never out of order.
 */
class BudgetedTest {

    @Test
    void overItsBudgetItsQualityComesDownAndWellUnderItComesBackMoreSlowly() {
        final Budgeted budget = new Budgeted(1_000);
        assertEquals(1, budget.eased(500), 1e-9, "under, as set");
        final double down = budget.eased(1_200);
        assertTrue(down < 1, "over, down");
        assertEquals(down, budget.eased(900), 1e-9, "between, where it is");
        final double up = budget.eased(500);
        assertTrue(up > down && 1 - up > 0 && up - down < 1 - down, "well under, back up, more slowly than down");
        for (int asked = 0; asked < 200; asked++) {
            budget.eased(5_000);
        }
        assertEquals(0.3, budget.quality(), 1e-9, "never below its least");
        for (int asked = 0; asked < 200; asked++) {
            budget.eased(0);
        }
        assertEquals(1, budget.quality(), 1e-9, "no further than as set");
        assertTrue(!budget.full(1_050) && budget.full(1_200), "nothing more sent a little past it");
    }

    @Test
    void ringsDrawnInDrawInByTheSameShareAndStayInOrder() {
        final Rings set = new Rings(150, 350, 800);
        final Rings half = set.scaled(0.5);
        assertEquals(90, half.near(), 1e-9, "the trees no nearer than they may be");
        assertEquals(175, half.boughs(), 1e-9);
        assertEquals(400, half.limbs(), 1e-9);
        final Rings least = set.scaled(0.1);
        assertTrue(least.near() <= least.boughs() && least.boughs() <= least.limbs(), "never out of order");
        assertThrows(IllegalArgumentException.class, () -> new Rings(50, 350, 800));
    }
}
