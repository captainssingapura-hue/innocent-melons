package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;
import melons.innocent.plants.tree.Stage;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A forest without end, made of a set of samples, with no card. A sample is the same every time, its trees within
 * its square about its own middle, and no two samples alike. A square is the same sample, turned the same, whenever
 * it is asked, far from the world's middle as near it; over many squares every sample and every turn is used, about
 * as often as any other; and a square's trees stand within it. What is seen from a place is the squares within the
 * furthest reach, the one stood in at its finest and the levels coarser outward; from high above all are trunks.
 */
class EndlessTest {

    private final Samples samples = new Samples(32, 2_500, 6, 20261124L);
    private final Endless endless = new Endless(samples, 20261124L);

    @Test
    void aSampleIsTheSameEveryTimeWithinItsSquareAndNoTwoAreAlike() {
        final Set<String> first = new HashSet<>();
        for (int sample = 0; sample < samples.count(); sample++) {
            final List<WoodDrawn.Planted> once = samples.planted(sample);
            final List<WoodDrawn.Planted> again = samples.planted(sample);
            assertEquals(16, once.size(), "four rows of four in twenty-five metres");
            for (int tree = 0; tree < once.size(); tree++) {
                assertEquals(once.get(tree).placed().at(0, 3), again.get(tree).placed().at(0, 3));
                assertEquals(once.get(tree).grown(), again.get(tree).grown());
                assertTrue(Math.abs(once.get(tree).placed().at(0, 3)) < 1_250
                        && Math.abs(once.get(tree).placed().at(2, 3)) < 1_250, "within its square");
            }
            first.add(once.getFirst().placed().at(0, 3) + " " + once.getFirst().grown());
        }
        assertEquals(samples.count(), first.size(), "no two samples begin alike");
        assertThrows(IllegalArgumentException.class, () -> samples.planted(32));
    }

    @Test
    void aSquareIsTheSameSampleTurnedTheSameWheneverAskedAndEverySampleAndTurnIsUsed() {
        final int[] used = new int[samples.count()];
        final int[] turned = new int[4];
        for (long along = -50; along < 50; along++) {
            for (long across = -50; across < 50; across++) {
                assertEquals(endless.sample(across, along), endless.sample(across, along));
                used[endless.sample(across, along)]++;
                turned[endless.quarters(across, along)]++;
            }
        }
        for (final int times : used) {
            assertTrue(times > 200 && times < 430, "each of 32 samples about 312 times in 10,000: " + times);
        }
        for (final int times : turned) {
            assertTrue(times > 2_300 && times < 2_700, "each turn about a quarter of the time: " + times);
        }
        assertNotEquals(-1, endless.sample(4_000_000_000L, -4_000_000_000L), "far from the world's middle too");
        for (final WoodDrawn.Planted tree : endless.trees(7, -3)) {
            assertTrue(tree.placed().at(0, 3) > 7 * 2_500 && tree.placed().at(0, 3) < 8 * 2_500
                    && tree.placed().at(2, 3) > -3 * 2_500 && tree.placed().at(2, 3) < -2 * 2_500,
                    "a square's trees stand within it, however it is turned");
        }
        assertEquals(new Vec3(18_750, 0, -6_250), endless.middle(7, -3));
    }

    @Test
    void whatIsSeenIsTheSquaresInReachTheOneStoodInFinestAndFromHighAboveAllTrunks() {
        final Reaches reaches = new Reaches(20, 60, 200, 600, 1_000);
        final List<Endless.Seen> seen = endless.seen(new Vec3(1_000, 170, 1_000), reaches);
        assertTrue(seen.size() > 4_800 && seen.size() < 5_300, "about pi times forty squares squared: "
                + seen.size());
        final Endless.Seen stoodIn = seen.stream().filter(one -> one.across() == 0 && one.along() == 0).findFirst()
                .orElseThrow();
        assertEquals(Stage.FINE, stoodIn.level());
        assertEquals(170, stoodIn.away(), 1e-9, "the eye's own height from the square it is over");
        for (final Stage level : Stage.values()) {
            assertTrue(seen.stream().anyMatch(one -> one.level() == level), "some squares at " + level);
        }
        assertTrue(seen.stream().allMatch(one -> one.away() <= 100_000), "none beyond the furthest");
        assertTrue(endless.seen(new Vec3(0, 70_000, 0), reaches).stream().allMatch(one -> one.level()
                == Stage.WHOLE), "from seven hundred metres up, trunks alone");
        assertEquals(Optional.empty(), reaches.level(100_001));
        assertEquals(Optional.of(Stage.FINE), new Reaches(1_000, 1_000, 1_000, 1_000, 1_000).level(99_000),
                "the finest as far as the furthest is the finest everywhere");
        assertThrows(IllegalArgumentException.class, () -> new Reaches(50, 20, 200, 600, 1_000));
    }

    @Test
    void allInReachIsDrawnAsItWillBeNeededShortlyAndAtSpeedNothingAtItsFinestAndNeverTwoBelow() {
        final Reaches reaches = new Reaches(25, 60, 300, 500, 1_200);
        final Vec3 eye = new Vec3(0, 170, 0);
        final Motion still = new Motion(new Spot(0, 170, 0));
        final Motion slow = new Motion(new Spot(0, 170, 0), new Spot(0, 0, 1_000), 0);
        final Motion fast = new Motion(new Spot(0, 170, 0), new Spot(0, 0, 4_166.67), 0);
        assertEquals(Stage.FINE, new Going(still, 3, 50).finest(), "standing still, every level there is");
        assertEquals(Stage.FINE, new Going(slow, 3, 50).finest(), "at 36 km an hour, still");
        assertEquals(Stage.TWIGS_GONE, new Going(fast, 3, 50).finest(), "at 150 km an hour, nothing at its finest");
        assertEquals(Stage.FINE, new Going(fast, 3, 200).finest(), "unless the walk sets that speed higher");
        assertEquals(eye, new Going(still, 3, 50).soon(eye, new Vec3(0, 0, 9_000)), "going nowhere, it is where it is");
        assertEquals(new Vec3(0, 170, 3_000), new Going(slow, 3, 50).soon(eye, new Vec3(0, 0, 9_000)),
                "three seconds on, for a square further ahead than that");
        assertEquals(new Vec3(0, 170, 2_000), new Going(slow, 3, 50).soon(eye, new Vec3(4_000, 0, 2_000)),
                "abreast of a square it passes sooner");
        assertEquals(eye, new Going(slow, 3, 50).soon(eye, new Vec3(0, 0, -5_000)), "and no earlier than now");
        final List<Endless.Seen> plain = endless.seen(eye, reaches);
        final List<Endless.Seen> atRest = endless.seen(eye, reaches, new Going(still, 3, 50));
        final List<Endless.Seen> going = endless.seen(eye, reaches, new Going(slow, 3, 50));
        final List<Endless.Seen> atSpeed = endless.seen(eye, reaches, new Going(fast, 3, 50));
        assertEquals(plain, atRest, "standing still, as its distance alone has it");
        assertTrue(plain.size() > 7_000, "everything within twelve hundred metres, all round: " + plain.size());
        for (int square = 0; square < plain.size(); square++) {
            final int needed = plain.get(square).level().ordinal();
            assertEquals(plain.get(square).away(), atSpeed.get(square).away(), "the same squares, as far off");
            assertTrue(going.get(square).level().ordinal() <= needed, "going slowly, never coarser than needed");
            assertTrue(atSpeed.get(square).level().ordinal() <= needed + 1, "at speed, never two levels below");
            assertTrue(needed == 0 || atSpeed.get(square).level().ordinal() <= needed,
                    "and only what would have been finest is any coarser");
        }
        final Endless.Seen ahead = going.stream().filter(one -> one.across() == 0 && one.along() == 2).findFirst()
                .orElseThrow();
        assertEquals(Stage.FINE, ahead.level(), "a square fifty metres ahead, reached in the window: at its finest");
        assertEquals(Stage.TWIGS_GONE, plain.stream().filter(one -> one.across() == 0 && one.along() == 2)
                .findFirst().orElseThrow().level(), "which its distance alone would have with its twigs gone");
        assertTrue(atSpeed.stream().filter(one -> one.level() == Stage.FINE).count() == 0, "at speed, none finest");
    }

    @Test
    void underACapNothingIsFinerThanItAndWhatIsCoarserAlreadyIsAsItWas() {
        final Reaches reaches = new Reaches(25, 60, 300, 500, 1_200);
        final Vec3 eye = new Vec3(0, 170, 0);
        final Motion fast = new Motion(new Spot(0, 170, 0), new Spot(0, 0, 4_166.67), 0);
        final List<Endless.Seen> plain = endless.seen(eye, reaches);
        assertEquals(plain, endless.seen(eye, reaches, new Capped(new Plainly(), Stage.FINE)),
                "a cap at the finest caps nothing");
        for (final Stage cap : Stage.values()) {
            final List<Endless.Seen> capped = endless.seen(eye, reaches, new Capped(new Plainly(), cap));
            assertEquals(plain.size(), capped.size(), "the same squares under a cap at " + cap);
            for (int square = 0; square < plain.size(); square++) {
                assertEquals(Math.max(cap.ordinal(), plain.get(square).level().ordinal()),
                        capped.get(square).level().ordinal(), "no finer than " + cap + ", and no coarser for it");
            }
        }
        assertEquals(Stage.TWIGS_GONE, new Capped(new Going(fast, 3, 50), Stage.FINE).finest(),
                "what the looking itself puts off stays off under a finer cap");
        assertEquals(Stage.LIMBS, new Capped(new Going(fast, 3, 50), Stage.LIMBS).finest(),
                "and a coarser cap goes over it");
    }
}
