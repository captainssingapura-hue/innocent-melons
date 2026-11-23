package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.HouseSpec;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The generic house: nothing wrong with it, every storey reached, by its door and its stairs; its windows as many as
 * asked, front and back, one each side, but for its door; its roof a flat concrete slab overhanging its walls, a
 * parapet round it; as wide, deep and high as asked; its walls and slabs concrete; and a house too small refused.
 */
class GenericHouseTest {

    private final HouseSpec<Civilization, CommonEras.Contemporary> spec = new HouseSpec<>(900, 700, 2, 300, 3);
    private final Structure house = new GenericHouse().build(spec);

    @Test
    void nothingWrongEveryStoreyReachedItsWindowsAndItsDoor() {
        assertEquals(List.of(), house.problems());
        assertEquals(2, house.rooms().size());
        assertEquals(house.rooms(), house.reached(), "every storey reached");
        assertEquals(1, house.solids(new Elements.Door()).size());
        final long panes = house.solids(new Elements.Window()).stream().filter(s -> s.material() == Material.GLASS)
                .count();
        assertEquals(2 + 3 + 3 + 3 + 2 * 2, panes, "two and three in front, three and three behind, one each side");
        assertEquals(1, house.stairs().size());
    }

    @Test
    void itsRoofFlatConcreteOverhangingAParapetRoundIt() {
        final List<Solid> roof = house.solids(new Elements.Roof());
        assertEquals(1, roof.size());
        assertEquals(Material.CONCRETE, roof.getFirst().material());
        assertEquals(4, house.solids(new Elements.Parapet()).size());
        final double[] size = size(house.solids());
        assertEquals(900 + 2 * GenericHouse.OVERHANG, size[0], 1e-6, "across, the roof overhanging");
        assertEquals(700 + 2 * GenericHouse.OVERHANG, size[1], 1e-6, "along");
        assertEquals(2 * 300 + GenericHouse.PARAPET + GenericHouse.SLAB, size[2], 1e-6, "from under the ground slab"
                + " to the parapet's top");
        assertTrue(house.solids(new Elements.BearingWall()).stream().allMatch(s -> s.material() == Material.CONCRETE));
        assertThrows(IllegalArgumentException.class, () -> new HouseSpec<>(300, 700, 2, 300, 1));
        assertThrows(IllegalArgumentException.class, () -> new HouseSpec<>(900, 700, 2, 300, 7));
    }

    /** How far {@code solids} reach across, along and up, all told. */
    private double[] size(final List<Solid> solids) {
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (final Solid solid : solids) {
            for (final Point p : solid.corners()) {
                final double[] at = {p.across(), p.along(), p.up()};
                for (int k = 0; k < 3; k++) {
                    low[k] = Math.min(low[k], at[k]);
                    high[k] = Math.max(high[k], at[k]);
                }
            }
        }
        return new double[] {high[0] - low[0], high[1] - low[1], high[2] - low[2]};
    }
}
