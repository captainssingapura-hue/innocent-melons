package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bot, shaped as a man of 1.88 m is, worked out with no card. Standing, the top of its helmet is 1.87 to 1.89 m
 * up, its shoulders at about four fifths of its height, its hips over half, its fingertips as far down as a man's
 * hang, and it is as wide at the shoulders and as slim in the limbs as a man is. Walking and running, the lower of
 * its feet is always on the ground and neither goes through it; its knees bend back, never forward, and most as the
 * leg swings through; its elbows bend the forearm forward. Drawn at another height, all of it is so much bigger.
 */
class BotTest {

    private final Bot bot = new Bot();
    private final Pose still = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    private final Body standing = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);

    @Test
    void standingItIsAsTallAndAsWideAsAManOfItsHeightAndItsLimbsAsSlim() {
        final List<Courier.Placed> boxes = bot.placed(standing, 0, still);
        final double top = boxes.stream()
                .mapToDouble(box -> highest(box, 1)).filter(y -> y < 190).max().orElseThrow();
        assertTrue(top > 186 && top < 190, "the top of its helmet: " + top);
        assertEquals(0, lowest(boxes), 0.5, "its soles on the ground");
        final Joints joints = new Joints(still, new Knees(standing));
        assertEquals(152 / 188.0, -height(joints, Limb.LEFT_ARM, 0) / 188, 0.02, "its shoulders, at four fifths");
        assertEquals(95 / 188.0, -height(joints, Limb.LEFT_LEG, 0) / 188, 0.02, "its hips, over half");
        final double fingertips = -height(joints, Limb.LEFT_FOREARM, 46);
        assertTrue(fingertips > 65 && fingertips < 80, "its fingertips, hanging by its thighs: " + fingertips);
        final double across = boxes.stream().mapToDouble(box -> highest(box, 0)).max().orElseThrow();
        assertTrue(across > 21 && across < 26, "half as wide as its shoulders: " + across);
        for (final Courier.Piece piece : bot.pieces()) {
            if (piece.limb() == Limb.LEFT_ARM || piece.limb() == Limb.LEFT_FOREARM) {
                assertTrue(piece.across() <= 12, "an arm no thicker than a man's: " + piece);
            }
        }
    }

    @Test
    void theWomanIsOneSeventyNarrowerAtTheShouldersWiderAtTheHipsAndPaintedPink() {
        final Bot woman = new Bot(Build.WOMAN);
        final List<Courier.Placed> boxes = woman.placed(standing, 0, still);
        final double top = boxes.stream().mapToDouble(box -> highest(box, 1)).max().orElseThrow();
        assertTrue(top > 168 && top < 172, "the top of her head: " + top);
        assertEquals(0, lowest(boxes), 0.5, "her soles on the ground");
        final double scale = 170 / 188.0;
        assertTrue(Build.WOMAN.shoulders() * scale < Build.MAN.shoulders() * 0.85, "narrower at the shoulders");
        assertTrue(Build.WOMAN.hips() > Build.MAN.hips() && Build.WOMAN.pelvis() > Build.MAN.pelvis(),
                "wider at the hips, for her height and more");
        assertTrue(boxes.stream().anyMatch(box -> box.tone() == Tone.PAINT), "painted");
        final Courier.Placed painted = boxes.stream().filter(box -> box.tone() == Tone.PAINT).findFirst()
                .orElseThrow();
        assertTrue(painted.tint().x() > painted.tint().z() && painted.tint().x() > painted.tint().y() + 0.2,
                "pink: " + painted.tint());
        assertTrue(painted.tint().y() > 0.5, "a light pink: " + painted.tint());
        assertEquals(Tone.PAINT, Finish.ROSE.of(Limb.BODY, Tone.SHELL), "her body painted");
        assertEquals(Tone.TITANIUM, Finish.ROSE.of(Limb.LEFT_SHIN, Tone.SHELL), "her limbs titanium");
        assertEquals(Tone.TITANIUM, Finish.ROSE.of(Limb.RIGHT_ARM, Tone.TRIM), "her limbs titanium");
    }

    @Test
    void neitherHasAnAntennaAndEachHeadIsRoundWithAStudOnTop() {
        for (final Bot bot : List.of(new Bot(), new Bot(Build.WOMAN))) {
            final List<Courier.Placed> boxes = bot.placed(standing, 0, still);
            assertTrue(boxes.stream().noneMatch(box -> box.tone() == Tone.BEACON), "no beacon");
            final long round = bot.pieces().stream().filter(piece -> piece.limb() == Limb.HEAD && piece.round())
                    .count();
            assertEquals(3, round, "a round neck, head and stud");
            final double top = boxes.stream().mapToDouble(box -> highest(box, 1)).max().orElseThrow();
            final double head = boxes.stream().filter(Courier.Placed::round).mapToDouble(box -> highest(box, 1))
                    .max().orElseThrow();
            assertEquals(top, head, 1e-9, "the stud is the top of it");
        }
    }

    @Test
    void walkingAndRunningTheLowerFootIsOnTheGroundAndTheKneesBendBackMostInTheSwing() {
        for (final boolean running : new boolean[] {false, true}) {
            Avatar avatar = new Avatar(standing, false);
            double mostBent = 0;
            for (int step = 0; step < 3 * 120; step++) {
                avatar = avatar.step(new Controls(1, 0, 0, 0, running), Clock.STEP, Obstacles.NONE).avatar();
                final Body body = avatar.body();
                final Pose pose = Gait.of(avatar, body.yaw(), 0);
                final List<Courier.Placed> boxes = bot.placed(body, 0, pose);
                assertEquals(body.position().y() * -1, lowest(boxes), 0.5, "the lower foot on the ground");
                final Knees knees = new Knees(body);
                assertTrue(knees.left() >= 0 && knees.right() >= 0, "knees bend one way only");
                mostBent = Math.max(mostBent, Math.max(knees.left(), knees.right()));
            }
            assertTrue(mostBent > (running ? 70 : 30), "the swing bends the knee well: " + mostBent);
        }
        // a knee bent, the thigh upright: the foot behind the knee, never ahead of it
        final Joints bent = new Joints(still, new Knees(60, 60));
        final Mat4 shin = bent.of(Limb.LEFT_SHIN);
        assertTrue(shin.times(new Mat4(new Vec3(0, Joints.SHIN, 0))).at(2, 3) < shin.at(2, 3) - 30,
                "the shin swung back from the knee");
    }

    @Test
    void itsElbowsBendTheForearmForwardAndAtAnotherHeightItIsSoMuchBigger() {
        final Pose bent = new Pose(0, 0, 0, 0, 0, 0, 0, 90, 90, 0, 0, 0, 0, 0, 0);
        final Joints joints = new Joints(bent, new Knees(standing));
        final double hand = joints.of(Limb.LEFT_FOREARM).times(new Mat4(new Vec3(0, 30, 0))).at(2, 3);
        assertTrue(hand > 25, "the hand forward of the elbow: " + hand);
        final double tall = new Bot(Build.MAN, 200, Finish.GILT).placed(standing, 0, still).stream()
                .mapToDouble(box -> highest(box, 1)).max().orElseThrow();
        final double own = bot.placed(standing, 0, still).stream().mapToDouble(box -> highest(box, 1)).max()
                .orElseThrow();
        assertEquals(200 / 188.0, tall / own, 1e-9);
    }

    /** Where the point so far down {@code limb} is, how high, as the figure's frame has it: down, the way it grows. */
    private double height(final Joints joints, final Limb limb, final double down) {
        return joints.of(limb).times(new Mat4(new Vec3(0, down, 0))).at(1, 3);
    }

    /** The lowest any box comes, as drawn: up the way the second number grows. */
    private double lowest(final List<Courier.Placed> boxes) {
        return boxes.stream().mapToDouble(box -> -highest(box, -1)).min().orElseThrow();
    }

    /**
     * How far the box reaches the way {@code axis} says: its highest second number, for 1; its widest first
     * number, for 0; the negative of its lowest second number, for -1.
     */
    private double highest(final Courier.Placed box, final int axis) {
        double most = Double.NEGATIVE_INFINITY;
        for (int corner = 0; corner < 8; corner++) {
            final Mat4 at = box.stands().times(new Mat4(new Vec3((corner & 1) - 0.5, (corner >> 1 & 1) - 0.5,
                    (corner >> 2 & 1) - 0.5)));
            most = Math.max(most, axis == 0 ? Math.abs(at.at(0, 3)) : axis * at.at(1, 3));
        }
        return most;
    }
}
