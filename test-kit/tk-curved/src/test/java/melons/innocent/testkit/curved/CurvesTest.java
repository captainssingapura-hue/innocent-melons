package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
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
                new Spot(2_050, 400, 900), new Spot(-1_900, 400, 1_080))) {
            final double sheet = underfoot.under(over, 500).orElseThrow().at().up();
            assertTrue(sheet > 20, "on a shell at " + over);
            assertEquals(sheet, inTheWay.footing(over.across(), over.along(), 0, over.up()).height(), 1e-6);
        }
        assertEquals(0, inTheWay.footing(0, 0, 0, 100).height(), 1e-9, "open ground between them");
        assertEquals(5 + 1 + 1, inTheWay.volumes().size(), "the ramp in five bands of height, the vault, the dome");
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
        treading.stepped(new Spot(2_400 + 350, 170, 1_200), 0);
        assertTrue(treading.across().orElseThrow().rise() < -5, treading.said());
        assertEquals(0, treading.ahead().orElseThrow().rise(), 1e-9);
        assertTrue(treading.said().contains("Underfoot: the vault; a slope of"), treading.said());
    }
}
