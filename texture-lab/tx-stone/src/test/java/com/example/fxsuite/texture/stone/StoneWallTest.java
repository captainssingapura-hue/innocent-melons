package com.example.fxsuite.texture.stone;

import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stone walls: rubble, its stones no two alike with mortar between, the mortar's share worked out by Java; stone in
 * courses, laid as bricks are; and a wall standing on a footing, stone below a line and brick above it, each coming
 * down to its own colour far off.
 */
class StoneWallTest {

    private final Seen seen = new Seen();
    private final RubbleWall rubble = new RubbleWall();
    private final Averaged averaged = new Averaged(new Panel(300, 225, 5.3).formed());

    @Test
    void rubbleIsStonesEachItsOwnShadeWithMortarBetween() {
        int stone = 0;
        int mortar = 0;
        final Set<Double> shades = new HashSet<>();
        for (int across = 0; across < 60; across++) {
            for (int up = 0; up < 60; up++) {
                final Vec3 at = new Vec3(across * 2.1, up * 2.1, 0);
                final double mortared = share("mortar", at);
                assertTrue(mortared >= 0 && mortared <= 1, "a share: " + mortared);
                mortar += mortared > 0.5 ? 1 : 0;
                stone += mortared < 0.5 ? 1 : 0;
                shades.add(share("stones", at));
            }
        }
        assertTrue(stone > 3 * mortar && mortar > 100, "mostly stone, joints through it: " + stone + ", " + mortar);
        assertTrue(shades.size() > 15, "many stones, each its own shade: " + shades.size());
    }

    @Test
    void theMortarsShareOfTheWallIsWorkedOutAndIsWhatItSettlesToFarOff() {
        assertTrue(rubble.mortared() > 0.08 && rubble.mortared() < 0.3, "a joint of 1.6 cm round stones of 24: "
                + rubble.mortared());
        assertEquals(rubble.mortared(), averaged.of(term(rubble.surface(), "mortar").share()), 0.02,
                "the same over another stretch of wall");
        assertEquals(rubble.mortared(), share("mortar", new Vec3(31, 77, 0), 500), 1e-9, "and far off, everywhere");
    }

    @Test
    void fromFarOffRubbleIsOneGreyishColour() {
        final List<Double> far = assertInstanceOf(Constant.class,
                rubble.surface().coarser(200, averaged).colour().node()).numbers();
        assertTrue(far.get(0) > 0.5 && far.get(0) < 0.7 && Math.abs(far.get(0) - far.get(2)) < 0.08,
                "stone grey, a little warm: " + far);
    }

    @Test
    void stoneInCoursesIsLaidInLongBlocksHalfABlockAlongFromTheCourseBelow() {
        final Surface coursed = new CoursedStone().wall().surface();
        assertEquals(1, shown(coursed, "mortar", new Vec3(90.4, 10, 0)), 1e-6, "the joint after the first block");
        assertEquals(0, shown(coursed, "mortar", new Vec3(90.4, 45, 0)), 1e-6, "and a block over it");
        assertEquals(1, shown(coursed, "mortar", new Vec3(45, 30.4, 0)), 1e-6, "the bed between the courses");
    }

    @Test
    void aWallOnAFootingIsStoneBelowTheLineAndBrickAboveIt() {
        final Surface footed = new Footed(Brickwork.CHINESE_GREY.wall(), rubble.surface(), 60).surface();
        assertEquals(List.of("stones", "grain", "mortar", "firing", "pits", "bloom", "mortar"),
                footed.terms().stream().map(Term::name).toList(), "the footing's terms, then the wall's");
        final Vec3 below = new Vec3(14, 30, 0);
        final Vec3 above = new Vec3(14, 63, 0);
        assertEquals(0, footed.terms().get(3).shown().node().worked(seen.sample(below, 0))[0], "no brick below");
        assertEquals(0, footed.terms().get(0).shown().node().worked(seen.sample(above, 0))[0], "no stone above");
        // the first course of brick is laid on the footing's top: 3 cm up it is brick, 6.6 cm up its bed joint
        assertEquals(0, footed.terms().get(6).shown().node().worked(seen.sample(above, 0))[0], 1e-6);
        assertEquals(1, footed.terms().get(6).shown().node().worked(seen.sample(new Vec3(14, 66.6, 0), 0))[0], 1e-6);
    }

    @Test
    void farOffAFootedWallIsItsStonesColourBelowAndItsBricksAbove() {
        final Averaged over = new Averaged(new Panel(300, 240, 7.3).formed());
        final Surface far = new Footed(Brickwork.ADOBE.wall(), rubble.surface(), 60).surface().coarser(200, over);
        final double[] below = far.colour().node().worked(seen.sample(new Vec3(40, 20, 0), 0));
        final double[] above = far.colour().node().worked(seen.sample(new Vec3(40, 200, 0), 0));
        assertTrue(Math.abs(below[0] - below[2]) < 0.08, "grey stone below: " + below[0] + ", " + below[2]);
        assertTrue(above[0] - above[2] > 0.15, "earth above: " + above[0] + ", " + above[2]);
        assertTrue(far.terms().stream().allMatch(term -> term.share().node() instanceof Constant),
                "every term worked out once, each over its own part");
    }

    private double share(final String name, final Vec3 at) {
        return share(name, at, 0);
    }

    private double share(final String name, final Vec3 at, final double covers) {
        return term(rubble.surface(), name).share().node().worked(seen.sample(at, covers))[0];
    }

    private double shown(final Surface surface, final String name, final Vec3 at) {
        return term(surface, name).shown().node().worked(seen.sample(at, 0))[0];
    }

    private Term term(final Surface surface, final String name) {
        return surface.terms().stream().filter(term -> term.name().equals(name)).findFirst().orElseThrow();
    }
}
