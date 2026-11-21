package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.sim.Fruit;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Node;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engine's watermelon as the bench's subject: as big as the engine has it; its rind dark green, lighter on its
 * nine stripes, each wandering a little from end to end; its stripes' average worked out by Java, a third; and far
 * enough off, its rind the one colour that average makes.
 */
class WatermelonTest {

    private static final double[] DARK = {0x1e / 255.0, 0x4a / 255.0, 0x1c / 255.0};
    private static final double[] LIGHT = {0x5b / 255.0, 0x9a / 255.0, 0x3c / 255.0};
    /** As the engine's picture has it: two and a half of its 128 pixels round, nine stripes round. */
    private static final double WANDERED = 2.5 / 128 * 9;

    private final Watermelon watermelon = new Watermelon();
    private final Mesh formed = watermelon.formed(48);
    private final Seen seen = new Seen();

    @Test
    void itIsAsLongWideAndTallAsTheEnginesLyingOnItsSide() {
        assertEquals(Fruit.WATERMELON.length(), 2 * formed.points().stream().mapToDouble(Vec3::x).max().orElseThrow(),
                1e-9);
        assertEquals(Fruit.WATERMELON.height(), 2 * formed.points().stream().mapToDouble(Vec3::y).max().orElseThrow(),
                1e-9);
        assertEquals(Fruit.WATERMELON.width(), 2 * formed.points().stream().mapToDouble(Vec3::z).max().orElseThrow(),
                1e-9);
        assertEquals(Fruit.WATERMELON.height() / 2, watermelon.rests());
    }

    @Test
    void roundItsMiddleItIsLightForTheFirstThirdOfEachOfNineStripesAndDarkForTheRest() {
        for (int stripe = 0; stripe < 9; stripe++) {
            assertArrayEquals3(LIGHT, colourRound(stripe + 0.15), "in stripe " + stripe + ", its light third");
            assertArrayEquals3(DARK, colourRound(stripe + 0.7), "in stripe " + stripe + ", past it");
        }
    }

    @Test
    void itsStripesWanderOneWayAtItsMiddleAndTheOtherASixthOfTheWayFromAnEnd() {
        // the same turn round the fruit, four tenths of a stripe on: at the middle the stripes have wandered their
        // furthest one way and the point is on a light stripe; a sixth of the way from an end they have wandered
        // their furthest the other way, and it is off it
        final double turn = 2 * Math.PI * 0.40 / 9;
        final Vec3 middle = new Vec3(0, 10 * Math.cos(turn), 10 * Math.sin(turn));
        final Vec3 nearEnd = new Vec3(18 * Math.cos(Math.PI / 6), 5 * Math.cos(turn), 5 * Math.sin(turn));
        assertTrue(stripeAt(middle) > 0.5 && stripeAt(nearEnd) < 0.5,
                "on the stripe at the middle, off it near the end: " + stripeAt(middle) + ", " + stripeAt(nearEnd));
    }

    @Test
    void javaWorksOutThatItsStripesAverageToAThirdOfItsRind() {
        final double average = new Averaged(formed).of(watermelon.rind().terms().getFirst().share());
        assertEquals(0.33, average, 0.01);
    }

    @Test
    void farEnoughOffItsRindIsTheOneColourItsStripesAverageTo() {
        final Surface far = watermelon.rind().coarser(100, new Averaged(formed));
        final Constant colour = assertInstanceOf(Constant.class, far.colour().node());
        for (int part = 0; part < 3; part++) {
            assertEquals(DARK[part] + (LIGHT[part] - DARK[part]) * 0.33, colour.numbers().get(part), 0.005);
        }
        final Node near = watermelon.rind().coarser(5, new Averaged(formed)).colour().node();
        assertTrue(!(near instanceof Constant), "near, its stripes still show");
    }

    /**
     * Its rind's colour round its middle, {@code stripes} stripes on from where the first begins there, looked at
     * closely. At its middle the stripes have wandered their furthest, {@link #WANDERED} of a stripe, from where
     * they would run straight.
     */
    private double[] colourRound(final double stripes) {
        final double turn = 2 * Math.PI * (stripes + WANDERED) / 9;
        final Vec3 at = new Vec3(0, 10 * Math.cos(turn), 10 * Math.sin(turn));
        return watermelon.rind().colour().node().worked(seen.sample(at, 0));
    }

    private double stripeAt(final Vec3 at) {
        return watermelon.rind().terms().getFirst().share().node().worked(seen.sample(at, 0))[0];
    }

    private void assertArrayEquals3(final double[] wanted, final double[] got, final String where) {
        for (int part = 0; part < 3; part++) {
            assertEquals(wanted[part], got[part], 1e-6, where);
        }
    }
}
