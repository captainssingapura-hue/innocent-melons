package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The kinds of brickwork: the old Chinese grey brick, laid close in white lime three lengthwise and an end; and
 * adobe, big sun-dried bricks bedded thick in mud, straw in their faces. Each bonded as it usually is, and each, from
 * far off, the colour it should be.
 */
class BrickworkTest {

    private final Seen seen = new Seen();
    private final Averaged averaged = new Averaged(new Panel(300, 225, 3.7).formed());

    @Test
    void chineseGreyBrickIsLaidThreeLengthwiseAndAnEndAlongEveryCourse() {
        final Laying laying = new Laying(new Brick(28.8, 6.4, 14.4), Bond.THREE_AND_ONE, 0.4);
        // along the first course: three bricks of 28.8 and an end of 14.4, a joint of 0.4 after each
        assertEquals(0, mortar(laying, 14, 3), 1e-6, "the first lengthwise");
        assertEquals(1, mortar(laying, 29, 3), 1e-6, "its joint");
        assertEquals(0, mortar(laying, 44, 3), 1e-6, "the second");
        assertEquals(0, mortar(laying, 73, 3), 1e-6, "the third");
        assertEquals(1, mortar(laying, 87.4, 3), 1e-6, "its joint");
        assertEquals(0, mortar(laying, 95, 3), 1e-6, "the end");
        assertEquals(1, mortar(laying, 102.2, 3), 1e-6, "its joint, and round again");
        assertEquals(List.of(0.0, 1.0, 2.0, 3.0, 4.0),
                List.of(brick(laying, 14, 3), brick(laying, 44, 3), brick(laying, 73, 3), brick(laying, 95, 3),
                        brick(laying, 117, 3)), "each its own brick, counted along");
    }

    @Test
    void inTheNextCourseTheEndLiesOverTheMiddleOfTheMiddleOneOfTheThree() {
        final Laying laying = new Laying(new Brick(28.8, 6.4, 14.4), Bond.THREE_AND_ONE, 0.4);
        // the middle of the second brick below is 29.2 + 14.4 = 43.6 along; the end over it is 14.4 long
        assertEquals(0, mortar(laying, 43.6, 10), 1e-6, "the end's middle");
        assertEquals(1, mortar(laying, 43.6 - 7.4, 10), 1e-6, "its joint one side");
        assertEquals(1, mortar(laying, 43.6 + 7.4, 10), 1e-6, "and the other");
        assertEquals(0, mortar(laying, 43.6, 3), 1e-6, "and under it, brick");
    }

    @Test
    void chineseGreyBrickIsGreyWithThinWhiteJointsAndFromFarOffAPalerGrey() {
        final BrickWall wall = Brickwork.CHINESE_GREY.wall();
        assertEquals(Bond.THREE_AND_ONE, wall.bond());
        assertTrue(averaged.of(term(wall, "mortar").share()) < 0.09, "joints of 4 mm: little of the wall is lime");
        final List<Double> far = far(wall);
        assertTrue(Math.abs(far.get(0) - far.get(2)) < 0.04 && far.get(2) >= far.get(0),
                "grey, and if anything a little blue: " + far);
        assertTrue(far.get(1) > 0.45 && far.get(1) < 0.6, "neither dark nor pale: " + far);
    }

    @Test
    void adobeIsEarthColouredWithThickMudJointsAndStrawInItsFaces() {
        final BrickWall wall = Brickwork.ADOBE.wall();
        assertEquals(Bond.ENGLISH, wall.bond());
        assertTrue(averaged.of(term(wall, "mortar").share()) > 0.15, "bedded thick: much of the wall is mud");
        assertEquals(0.09 * 0.75, averaged.of(term(wall, "straw").share()), 0.01, "straw in its faces");
        final List<Double> far = far(wall);
        assertTrue(far.get(0) > far.get(1) && far.get(1) > far.get(2), "the colour of earth: " + far);
    }

    @Test
    void strawLiesLongAndThinAcrossAFace() {
        final Term straw = term(Brickwork.ADOBE.wall(Bond.STRETCHER), "straw");
        // a bit of straw is 2.2 cm long and 0.22 cm thick: one number all along it, another just above
        final double here = straw.share().node().worked(seen.sample(new Vec3(4.5, 3.05, 0), 0))[0];
        assertEquals(here, straw.share().node().worked(seen.sample(new Vec3(6.5, 3.05, 0), 0))[0], "along it");
        long differing = 0;
        for (int above = 1; above <= 200; above++) {
            final double up = 3.05 + 0.22 * above;
            differing += straw.share().node().worked(seen.sample(new Vec3(4.5, up, 0), 0))[0] == here ? 0 : 1;
        }
        assertTrue(differing > 0, "not the same all the way up");
    }

    /** {@code wall}'s colour from far enough off that it is one colour. */
    private List<Double> far(final BrickWall wall) {
        return assertInstanceOf(Constant.class, wall.surface().coarser(100, averaged).colour().node()).numbers();
    }

    private Term term(final BrickWall wall, final String name) {
        return wall.surface().terms().stream().filter(term -> term.name().equals(name)).findFirst().orElseThrow();
    }

    private double mortar(final Laying laying, final double across, final double up) {
        return laying.mortar(seen.at(), seen.covers()).node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }

    private double brick(final Laying laying, final double across, final double up) {
        return laying.brick(seen.at()).node().worked(seen.sample(new Vec3(across, up, 0), 0))[0];
    }
}
