package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What a robot's pieces are drawn in. As built: its frame steel, its housings gunmetal, its shell a gloss of its
 * colour, its visor lit glass. A paint's colour its own to choose, a bare metal's not. One piece's coat changed, the
 * others as they were; one figure painted, the others as they were. A shelled robot painted its shell as well as all
 * that a framed one is; every whole look a paintwork of things that may be offered.
 */
class PaintworkTest {

    private final Paintwork built = new Paintwork();

    @Test
    void asBuiltItsFrameSteelItsHousingsGunmetalItsShellAGlossItsVisorLit() {
        assertEquals(new Materials.Steel(), built.of(Paint.FRAME).material());
        assertEquals(new Materials.Gunmetal(), built.of(Paint.HOUSING).material());
        assertEquals(new Materials.GlossPaint(), built.of(Paint.SHELL).material());
        assertEquals(new Materials.LitGlass(), built.of(Paint.VISOR).material());
        assertTrue(built.of(Paint.SHELL).painted());
        assertFalse(built.of(Paint.FRAME).painted());
    }

    @Test
    void onePieceRepaintedTheOthersAsTheyWereAndOneFigureTheOthersAsTheyWere() {
        final Paintwork red = built.with(Paint.SHELL, new Paintwork.Coat(new Materials.MetallicPaint(),
                new Vec3(0.7, 0.1, 0.1)));
        assertEquals(new Materials.MetallicPaint(), red.of(Paint.SHELL).material());
        assertEquals(built.of(Paint.FRAME), red.of(Paint.FRAME));
        final LineupShown painted = new LineupShown(2).picking(1).painting(red);
        assertEquals(built, painted.paint().get(0));
        assertEquals(red, painted.paint().get(1));
    }

    @Test
    void aShelledRobotHasItsShellToPaintAndEveryLookIsOfWhatIsOffered() {
        final AvatarMaker maker = new AvatarMaker();
        final List<Paint> framed = maker.roles(new Figures.FramedRobot());
        final List<Paint> shelled = maker.roles(new Figures.ShelledRobot());
        assertFalse(framed.contains(Paint.SHELL));
        assertEquals(Paint.SHELL, shelled.getFirst());
        assertTrue(shelled.containsAll(framed));
        for (final Looks look : Looks.values()) {
            for (final Paint p : shelled) {
                assertTrue(built.offered().contains(look.work().of(p).material()), look + ": " + p.label());
            }
        }
        assertNotEquals(built, Looks.RACING_RED.work());
        assertEquals(built, Looks.AS_BUILT.work());
    }
}
