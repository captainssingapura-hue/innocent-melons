package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        assertEquals(1988, range.b4().displacement(), 1, "a 2.0 litre flat four");
        assertEquals(2981, range.b6().displacement(), 1, "a 3.0 litre flat six, the same cylinder");
        assertEquals(1252, range.v2().displacement(), 1, "a 1,252 cc twin");
    }

    @Test
    void aTwinsCrankcaseItsBarrelsBoltedOnItsPinsSplit() {
        final EngineFigures v2 = range.v2();
        final Part crankcase = new CastBlock(v2).part();
        assertTrue(v2.twin() && !range.v6().twin() && !range.b4().twin());
        assertTrue(crankcase.name().endsWith("crankcase") && !crankcase.has("deck, bank 1"));
        final BlockForm form = new BlockForm(v2);
        for (int b = 0; b < 2; b++) {
            final Part barrel = new Barrel(v2, b).part();
            final String bank = ", bank " + (b + 1);
            assertEquals(0, barrel.point("foot").at().minus(crankcase.point("barrel" + bank).at()).length(), 1e-9);
            assertEquals(v2.deck(), barrel.point("cylinder " + (b + 1) + ".1").at().minus(new Vec3(0, 0,
                    v2.along(b, 0))).length(), 1e-9, "its deck as high over the crank as said");
            assertTrue(form.top() < v2.deck() - 5, "its foot well down its bank");
        }
        final Vec3 inner = form.up(1).times(form.top()).minus(form.out(1).times(form.half()));
        assertTrue(inner.x() > 0 && inner.x() < 0.5, "the barrels' feet clear of each other over the middle");
        final CrankFigures crank = new CrankFigures(v2);
        assertEquals(30, crank.angle(0, 0) - crank.angle(1, 0), 1e-9, "its pins split 30 degrees");
        assertEquals(90, new Firing(v2).top(1, 0) - new Firing(v2).top(0, 0) + 360, 1e-9,
                "its pistons at their tops a quarter turn apart, as a 90 degree twin's");
        assertEquals(2, form.bulkheads().size(), "a main bearing at each end, its two pins between");
    }

    @Test
    void aTwinsBanksClearOfEachOtherAcrossItsV() {
        final EngineInternals inside = new EngineInternals(range.v2());
        final Assembled covered = new Assembly(inside.platform(true)).assembled();
        final List<Part> outside = covered.placed().keySet().stream().map(p -> p.part())
                .filter(p -> inside.cast(p) || inside.cover(p)).toList();
        final TurnedTouching touching = new TurnedTouching();
        final List<String> touched = new ArrayList<>();
        for (final Part a : outside) {
            for (final Part b : outside) {
                if (a.name().endsWith("bank 1") && b.name().endsWith("bank 2")
                        && !touching.wheres(a.form(), new Mat4(), b.form(), new Mat4()).isEmpty()) {
                    touched.add(a.name() + " on " + b.name());
                }
            }
        }
        assertEquals(9, outside.size(), "two barrels, two heads, two cam covers, their coils and the timing cover");
        assertEquals(List.of(), touched, "each bank's barrel, head, cover and coils clear of the other's");
    }

    @Test
    void aBoxersBanksFlatEachRodOnItsOwnPinOpposedPistonsInStep() {
        for (final EngineFigures e : List.of(range.b4(), range.b6())) {
            final Part block = new CastBlock(e).part();
            final CrankFigures crank = new CrankFigures(e);
            final Firing firing = new Firing(e);
            for (int k = 0; k < e.perBank(); k++) {
                final Vec3 right = block.point("cylinder 1." + (k + 1)).at();
                final Vec3 left = block.point("cylinder 2." + (k + 1)).at();
                assertEquals(0, right.y(), 1e-9, e.name() + " its banks lying flat");
                assertEquals(-e.deck(), right.x(), 1e-9);
                assertEquals(e.deck(), left.x(), 1e-9);
                assertEquals(e.spacing() / 2, left.z() - right.z(), 1e-9, "the second bank half a spacing back");
                assertEquals(180, crank.angle(1, k) - crank.angle(0, k), 1e-9, "each on its own pin, opposite");
                assertEquals(firing.top(0, k), firing.top(1, k), 1e-9, "opposed pistons at their tops together");
            }
            assertEquals(e.cylinders() + 1, new BlockForm(e).bulkheads().size(),
                    "a main bearing between each two pins and at each end");
        }
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
        assertEquals(1 + 8 + 2 + 8 + 6 * (2 + 1 + 2 + 1 + 2 + 2 + 2 + 2) + 11 * (2 + 4 + 4 + 5 + 6 + 6 + 8 + 12),
                shown.placed().size(), "the stands, eight blocks, the twin's two barrels, eight cranks; a head, two"
                        + " camshafts, a timing chain, its guide and its tensioner on every bank; and a rod, a piston,"
                        + " a pin, four valves and four springs in every cylinder");
    }

    @Test
    void everyRodItsLengthEveryPistonInItsBoreAndTheV6sPinsSplit() {
        for (final EngineFigures e : range.all()) {
            for (int b = 0; b < e.banks(); b++) {
                for (int k = 0; k < e.perBank(); k++) {
                    final Cylinder c = new Cylinder(e, b, k);
                    assertEquals(e.rod(), c.smallEnd().minus(c.bigEnd()).length(), 1e-9, c.called());
                    final double up = c.smallEnd().minus(c.deck()).dot(c.up()) + e.crown();
                    assertTrue(up <= 1e-9 && up >= -e.stroke() - 1e-9, c.called() + " in its bore: " + up);
                    assertEquals(0, c.smallEnd().minus(c.deck()).minus(c.up().times(c.smallEnd().minus(c.deck())
                            .dot(c.up()))).length(), 1e-9, "on its bore's axis");
                }
            }
        }
        final CrankFigures v6 = new CrankFigures(range.v6());
        assertEquals(30, v6.angle(0, 0) - v6.angle(1, 0), 1e-9, "the V6's pins split, the first bank's leading");
        final CrankFigures v8 = new CrankFigures(range.v8());
        assertEquals(v8.angle(0, 1), v8.angle(1, 1), 1e-9, "the V8's banks sharing a pin");
        final Cylinder top = new Cylinder(range.l4(), 0, 0);
        assertEquals(top.deck().y(), top.smallEnd().y() + range.l4().crown(), 1e-9, "the first at the top, flush");
    }

    @Test
    void posedAtAnyAngleTheCrankTurnedTheRodsSwungThePistonsSlid() {
        for (final EngineFigures e : List.of(range.l5(), range.v6(), range.v12(), range.b4(), range.b6())) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled built = new Assembly(inside.platform()).assembled();
            assertEquals(List.of(), built.problems(1e-6));
            for (final double turned : List.of(37.0, 180.0, 291.0)) {
                for (final var placed : built.placed().keySet()) {
                    final Part part = placed.part();
                    for (final var point : part.points()) {
                        final Vec3 now = moved(inside.moved(part, turned), point.at());
                        if (part.name().contains("rod")) {
                            final Cylinder c = cylinder(e, part.name());
                            assertEquals(0, now.minus(point.name().equals("big end") ? c.bigEnd(turned)
                                    : c.smallEnd(turned)).length(), 1e-9, part.name() + " at " + turned);
                        }
                        if (part.name().contains("piston") && point.name().equals("pin")) {
                            assertEquals(0, now.minus(cylinder(e, part.name()).smallEnd(turned)).length(), 1e-9);
                        }
                    }
                }
            }
        }
    }

    private Cylinder cylinder(final EngineFigures e, final String name) {
        final String[] at = name.substring(name.lastIndexOf(' ') + 1).split("[.]");
        return new Cylinder(e, Integer.parseInt(at[0]) - 1, Integer.parseInt(at[1]) - 1);
    }

    private Vec3 moved(final com.example.fxsuite.vulkan.space.Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }

    @Test
    void figuresThatCannotBeCastRefused() {
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("V7", 2, 90, 7, 8.45, 8.9, 9.3, 1.8,
                15.4, 3));
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("fat", 1, 0, 4, 9, 9, 9.1, 0, 14, 3));
        assertThrows(IllegalArgumentException.class, () -> new EngineFigures("bent", 1, 30, 4, 8, 9, 8.8, 0, 14, 3));
    }
}
