package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A brick wall worked out brick by brick: each brick fired one colour all over, and its neighbour another; its
 * averages worked out by Java over three metres of it; further and further off, its specks, its joints and then its
 * bricks gone from its shader, till it is the one colour the engine draws brick.
 */
class BrickWallTest {

    private final Seen seen = new Seen();
    private final BrickWall wall = Brickwork.COMMON_RED.wall(Bond.STRETCHER);
    private final Averaged averaged = new Averaged(new Panel(300, 225, 3.7).formed());

    @Test
    void eachBrickIsFiredOneColourAllOverAndItsNeighbourAnother() {
        final double here = firing(2, 3);
        assertEquals(here, firing(20, 1), "two points of one brick");
        assertNotEquals(here, firing(30, 3), "the next brick along");
        assertNotEquals(here, firing(2, 10), "the brick over it");
        assertTrue(here >= 0 && here < 1, "somewhere between the paler colour and the darker: " + here);
    }

    @Test
    void javaWorksOutWhatEachTermComesToOverTheWall() {
        assertEquals(0.5, averaged.of(term("firing").share()), 0.05, "bricks fired evenly from pale to dark");
        assertEquals(0.12 * 0.6, averaged.of(term("specks").share()), 0.01, "an eighth of its grains, darkened");
        assertEquals(1 - 21.5 * 6.5 / (22.5 * 7.5), averaged.of(term("mortar").share()), 0.01, "its joints");
    }

    @Test
    void furtherAndFurtherOffItsSpecksItsJointsAndThenItsBricksGoFromItsShader() {
        assertEquals(List.of(), evened(wall.surface()), "close to, every term worked out at every pixel");
        assertEquals(2, scatters(wall.surface()), "the firing and the specks both scattered");
        final Surface withoutSpecks = wall.surface().coarser(3, averaged);
        assertEquals(List.of("specks"), evened(withoutSpecks), "a pixel covering 3 cm: the specks gone");
        assertEquals(1, scatters(withoutSpecks), "the firing alone scattered");
        final Surface withoutJoints = wall.surface().coarser(10, averaged);
        assertEquals(List.of("specks", "mortar"), evened(withoutJoints), "covering 10 cm: the joints gone too");
        assertEquals(1, scatters(withoutJoints), "and brick still told from brick");
        final Surface flat = wall.surface().coarser(60, averaged);
        assertEquals(List.of("firing", "specks", "mortar"), evened(flat), "covering 60 cm: nothing left");
        assertEquals(0, scatters(flat), "and nothing scattered");
    }

    /** The terms of {@code surface} that show the same share everywhere: those worked out once and left out. */
    private List<String> evened(final Surface surface) {
        return surface.terms().stream().filter(term -> term.share().node() instanceof Constant).map(Term::name)
                .toList();
    }

    @Test
    void fromFarEnoughItIsTheOneColourTheEngineDrawsBrick() {
        final Constant colour = assertInstanceOf(Constant.class, wall.surface().coarser(60, averaged).colour().node());
        // as the engine draws brick today, flat: #9a5844 (its BuildingModel)
        assertEquals(0x9a / 255.0, colour.numbers().get(0), 0.02, "red");
        assertEquals(0x58 / 255.0, colour.numbers().get(1), 0.02, "green");
        assertEquals(0x44 / 255.0, colour.numbers().get(2), 0.02, "blue");
    }

    @Test
    void theShaderDeclaresItsScatteringOnceHoweverOftenItScatters() {
        final String code = code(wall.surface());
        assertEquals(1, code.split("float scattered\\(float across, float up, float use\\) \\{", -1).length - 1);
        assertTrue(code.indexOf("float scattered(") < code.indexOf("void main()"), "before it is used");
    }

    private double firing(final double across, final double up) {
        return term("firing").share().node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }

    private Term term(final String name) {
        return wall.surface().terms().stream().filter(term -> term.name().equals(name)).findFirst().orElseThrow();
    }

    private String code(final Surface surface) {
        return new Look(new Laid(), surface).fragment().code();
    }

    /** How many times the fragment shader for {@code surface} scatters. */
    private int scatters(final Surface surface) {
        return code(surface).split(" = scattered\\(", -1).length - 1;
    }
}
