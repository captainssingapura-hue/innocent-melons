package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engines' blocks from the one modular design: each displacing what its figures make it, its deck as high over
 * the crank as its half stroke, rod and crown; a V's banks leaning half its V each way, its second bank set back; a
 * cylinder's point where its bore is, every block cast aluminium, bare; the whole range put together on its stands;
 * and figures that cannot be cast refused.
 */
class EngineBlockTest {

    private final EngineRange range = new EngineRange();

    @Test
    void eachDisplacingWhatItsFiguresMakeIt() {
        assertEquals(1984, range.l4().displacement(), 1, "a 2.0 litre four");
        assertEquals(2480, range.l5().displacement(), 1, "a 2.5 litre five");
        assertEquals(2995, range.v6().displacement(), 1, "a 3.0 litre V6");
        assertEquals(3993, range.v8().displacement(), 1, "a 4.0 litre V8, the V6's cylinder");
        assertEquals(5990, range.v12().displacement(), 1, "a 6.0 litre V12, the same cylinder again");
    }

    @Test
    void itsDeckOverTheCrankItsBanksLeaningItsCylindersWhereTheirBoresAre() {
        final EngineFigures v8 = range.v8();
        final Part block = new CastBlock(v8).part();
        assertEquals(v8.stroke() / 2 + v8.rod() + v8.crown(), v8.deck(), 1e-9);
        final Vec3 first = block.point("cylinder 1.1").at();
        final Vec3 second = block.point("cylinder 2.1").at();
        assertEquals(first.x(), -second.x(), 1e-9, "the banks leaning half the V each way");
        assertEquals(v8.offset(), second.z() - first.z(), 1e-9, "the second bank set back");
        assertEquals(v8.deck(), Math.hypot(first.x(), first.y()), 1e-9, "the deck as high over the crank as said");
        assertEquals(Material.ALUMINIUM, block.made().material());
        assertTrue(block.made().bare());
        assertEquals(0, new CastBlock(range.l4()).part().point("cylinder 1.3").at().x(), 1e-9, "in line, upright");
    }

    @Test
    void theRangePutTogetherOnItsStands() {
        final Assembled shown = new Assembly(new EngineShow().platform()).assembled();
        assertEquals(List.of(), shown.problems(1e-6));
        assertEquals(1 + 5, shown.placed().size(), "the stands and five blocks");
    }

    @Test
    void figuresThatCannotBeCastRefused() {
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("V7", 2, 90, 7, 8.45, 8.9, 9.3, 1.8,
                15.4, 3));
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("fat", 1, 0, 4, 9, 9, 9.1, 0, 14, 3));
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("bent", 1, 30, 4, 8, 9, 8.8, 0, 14, 3));
    }
}
