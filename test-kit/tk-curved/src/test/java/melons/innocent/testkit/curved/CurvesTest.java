package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Pavement;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.surface.Underfoot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trial of curved surfaces, with no card: the game engine stands a foot just where the core engine says each
 * sheet is; its avatar, steered up the spiral ramp's middle, climbs both turns without ever going up or down a
 * step; and what is trodden on is said as any mover would be told it.
 */
class CurvesTest {

    private final Curves curves = new Curves();
    private final Obstacles inTheWay = curves.inTheWay();
    private final Underfoot underfoot = new Underfoot(curves.world(), new Ground(), curves);

    @Test
    void theGameEngineStandsAFootJustWhereTheSheetIs() {
        // on the ramp's first turn and its second, on the vault's flank, and near the dome's top
        for (final Spot over : List.of(new Spot(300, 300, 2_420), new Spot(1_150, 700, 1_400),
                new Spot(2_250, 500, 900), new Spot(-1_900, 400, 1_080))) {
            final double sheet = underfoot.under(over, 500).orElseThrow().at().up();
            assertTrue(sheet > 20, "on a shell at " + over);
            assertEquals(sheet, inTheWay.footing(over.across(), over.along(), 0, over.up()).height(), 1e-6);
        }
        assertEquals(0, inTheWay.footing(0, 0, 0, 100).height(), 1e-9, "open ground between them");
        assertEquals(5 + 1 + 1, inTheWay.volumes().stream().filter(Pavement.class::isInstance).count(),
                "the ramp in five bands of height, the vault, the dome");
    }

    @Test
    void theAvatarSteeredUpTheRampClimbsBothTurnsWithoutAStep() {
        final Spot middle = curves.placed(Curves.RAMP).at();
        Avatar now = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(middle.across() - 900, 0,
                middle.along() - 60), 0), false);
        double steepest = 0;
        double furthest = 0;
        int steps = 0;
        while (now.body().height() < 700 && steps < 3_000) {
            final double x = now.body().position().x() - middle.across();
            final double z = now.body().position().z() - middle.along();
            // along the line round the middle, to the right, and back toward it when it is strayed from
            final double wanted = Math.toDegrees(Math.atan2(x, z)) + 90
                    + Math.max(-30, Math.min(30, 0.2 * (Math.hypot(x, z) - 900)));
            final double turn = ((wanted - now.body().yaw()) % 360 + 540) % 360 - 180;
            final double before = now.body().height();
            now = now.step(new Controls(1, 0, turn, 0, true, false, 0), Clock.STEP, inTheWay).avatar();
            // once it has stepped up onto the ramp's foot, which is a kerb's height
            steepest = before < 30 ? steepest : Math.max(steepest, Math.abs(now.body().height() - before));
            furthest = Math.max(furthest, Math.abs(Math.hypot(x, z) - 900));
            steps++;
            final Spot feet = new Spot(now.body().position().x(), now.body().height(), now.body().position().z());
            if (now.body().height() > 30) {
                assertEquals(underfoot.under(feet.plus(new Spot(0, 30, 0)), 200).orElseThrow().at().up(),
                        feet.up(), 1.6, "its feet on the sheet at step " + steps);
            }
        }
        assertTrue(now.body().height() >= 700, "it has climbed both turns: " + now.body().height());
        assertTrue(now.body().grounded(), "on its feet all the way");
        assertTrue(furthest < 150, "it kept to the ramp: " + furthest);
        // at a run it goes some six centimetres a step, up a grade of about one in sixteen
        assertTrue(steepest < 1.2, "never more than the grade's rise in a step, so never a stair: " + steepest);
    }

    /** The avatar set down at ({@code x}, {@code z}) on the ground, facing {@code yaw} degrees round. */
    private Avatar setDown(final double x, final double z, final double yaw) {
        return new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(x, 0, z), yaw), false);
    }

    /** {@code from} after running straight ahead for so many steps. */
    private Avatar ran(final Avatar from, final int steps) {
        Avatar now = from;
        for (int step = 0; step < steps; step++) {
            now = now.step(new Controls(1, 0, 0, 0, true, false, 0), Clock.STEP, inTheWay).avatar();
        }
        return now;
    }

    @Test
    void fromTheGroundTheRampsSideIsInTheWayWhereItIsHigherThanAStepAndLowerThanTheHead() {
        final Spot middle = curves.placed(Curves.RAMP).at();
        // from the ring's middle straight at its inner edge, a quarter of a turn up, where the deck is a metre high
        final Avatar stopped = ran(setDown(middle.across(), middle.along(), 0), 480);
        assertEquals(0, stopped.body().height(), 1e-9, "still on the ground");
        assertEquals(700 - Body.RADIUS, stopped.body().position().z() - middle.along(), 6, "stopped at its side");
        // and from outside it, at the same place
        final Avatar outside = ran(setDown(middle.across(), middle.along() + 1_600, 180), 480);
        assertEquals(1_100 + Body.RADIUS, outside.body().position().z() - middle.along(), 6);
        // where the deck is well over its head, seven tenths of a turn up, it walks under and out the other side
        final double under = Math.toRadians(270 + 0.7 * 360);
        final Avatar through = ran(setDown(middle.across(), middle.along(), Math.toDegrees(under)), 480);
        assertEquals(0, through.body().height(), 1e-9);
        assertTrue(Math.hypot(through.body().position().x() - middle.across(),
                through.body().position().z() - middle.along()) > 1_400, "out beyond the ramp");
    }

    @Test
    void underTheRampGoingRoundTowardItsFootTheAvatarIsStoppedWhereTheDeckComesDownToItsHead() {
        final Spot middle = curves.placed(Curves.RAMP).at();
        final double from = Math.toRadians(270 + 0.8 * 360);
        Avatar now = setDown(middle.across() + 900 * Math.sin(from), middle.along() + 900 * Math.cos(from),
                Math.toDegrees(from) - 90);
        for (int step = 0; step < 1_500; step++) {
            final double x = now.body().position().x() - middle.across();
            final double z = now.body().position().z() - middle.along();
            // back round the middle, to the left, the way the ramp comes down
            final double wanted = Math.toDegrees(Math.atan2(x, z)) - 90
                    - Math.max(-30, Math.min(30, 0.2 * (Math.hypot(x, z) - 900)));
            final double turn = ((wanted - now.body().yaw()) % 360 + 540) % 360 - 180;
            now = now.step(new Controls(1, 0, turn, 0, true, false, 0), Clock.STEP, inTheWay).avatar();
        }
        final double x = now.body().position().x() - middle.across();
        final double z = now.body().position().z() - middle.along();
        final double turns = ((Math.toDegrees(Math.atan2(x, z)) - 270) % 360 + 360) % 360 / 360;
        assertEquals(0, now.body().height(), 1e-9, "on the ground all the way");
        // the deck's underside is two metres up, the avatar's height, a little over six tenths of a turn from its foot
        assertEquals(0.64, turns, 0.05, "stopped where the deck is down to its head");
    }

    @Test
    void theVaultIsWalkedOverFromItsSideAndThroughFromItsEnd() {
        final Spot stands = curves.placed(Curves.VAULT).at();
        Avatar over = setDown(stands.across() + 1_000, stands.along(), 270);
        double highest = 0;
        for (int step = 0; step < 420; step++) {
            over = ran(over, 1);
            highest = Math.max(highest, over.body().height());
        }
        assertEquals(300, highest, 3, "over its crown");
        assertTrue(over.body().position().x() < stands.across() - 900 && over.body().height() < 1,
                "and down on the ground beyond it");
        final Avatar through = ran(setDown(stands.across(), stands.along() - 1_000, 0), 420);
        assertEquals(0, through.body().height(), 1e-9, "under it, on the ground");
        assertTrue(through.body().position().z() > stands.along() + 900, "and out of its far end");
        // inside it, toward its side, it is stopped where the timber comes down to its head
        final Avatar inside = ran(setDown(stands.across(), stands.along(), 90), 420);
        assertEquals(0, inside.body().height(), 1e-9);
        assertTrue(inside.body().position().x() - stands.across() < 520, "stopped inside it");
        assertTrue(inside.body().position().x() - stands.across() > 300, "well toward its side");
    }

    @Test
    void whatIsTroddenOnIsSaidAsAMoverIsToldIt() {
        final Treading treading = new Treading(curves);
        assertTrue(treading.said().startsWith("Underfoot: nothing within reach"));
        // a quarter of the way round the ramp's first turn, facing on round it
        treading.stepped(new Spot(300, 96, 2_400), 90);
        final String said = treading.said();
        assertTrue(said.contains("Underfoot: the spiral ramp; a slope of 4.0 degrees, concrete, dry; 200 cm to its"
                + " edge; a walker goes on it:"), said);
        assertTrue(said.contains("Between the feet, 19 cm apart: the right 0.57 cm lower than the left"), said);
        assertTrue(said.contains("A metre ahead: 6.51 cm higher, a grade of 6.5 in a hundred"), said);
        // astride the vault's flank, facing along it: one foot well above the other
        treading.stepped(new Spot(2_700 + 550, 180, 1_200), 0);
        assertTrue(treading.across().orElseThrow().rise() < -5, treading.said());
        assertEquals(0, treading.ahead().orElseThrow().rise(), 1e-9);
        assertTrue(treading.said().contains("Underfoot: the vault; a slope of"), treading.said());
    }
}
