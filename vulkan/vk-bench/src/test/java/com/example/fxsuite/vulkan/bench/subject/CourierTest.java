package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The courier, placed as the engine places its own figure: standing, its soles just clear of the ground, its head
 * two metres up and its visor ahead of it; standing where its body is and facing the way its body faces; a leg
 * swung forward going forward; and, running by the engine's own simulation, its arms and legs swinging opposite
 * ways.
 */
class CourierTest {

    private final Courier courier = new Courier();
    private final Pose still = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    @Test
    void standingItsSolesAreJustClearOfTheGroundAndItsHeadTwoMetresUp() {
        final List<Courier.Placed> placed = courier.placed(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(
                0, 0, 0), 0), still);
        assertEquals(25, placed.size());
        assertEquals(3, lowest(placed), 1e-9, "its soles, clear of the ground by a hair");
        final Vec3 headTop = corner(placed.get(4), 0, 0.5, 0);
        assertEquals(3 + 156 + 42, headTop.y(), 1e-9, "the top of its head");
        final Vec3 visor = corner(placed.get(5), 0, 0, 0);
        assertTrue(visor.z() > 20 && Math.abs(visor.x()) < 1e-9, "its visor ahead of it: " + visor);
    }

    @Test
    void itStandsWhereItsBodyIsAndFacesTheWayItsBodyFaces() {
        // half a metre up, which the engine counts downward
        final Body body = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(100, -50, 200), 90);
        final List<Courier.Placed> placed = courier.placed(body, still);
        assertEquals(53, lowest(placed), 1e-9);
        final Vec3 visor = corner(placed.get(5), 0, 0, 0);
        assertEquals(121.5, visor.x(), 1e-9, "its visor ahead of it, which is now to the right");
        assertEquals(200, visor.z(), 1e-9);
    }

    @Test
    void aLegSwungForwardGoesForwardAndItsFootComesUp() {
        final Pose stepping = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, 0);
        final List<Courier.Placed> placed = courier.placed(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(
                0, 0, 0), 0), stepping);
        final Vec3 leftFoot = corner(placed.get(11), 0, 0, 0);
        final Vec3 rightFoot = corner(placed.get(14), 0, 0, 0);
        assertTrue(leftFoot.x() < 0 && rightFoot.x() > 0, "the left foot to the left");
        assertTrue(leftFoot.z() > rightFoot.z() + 30, "forward of the other: " + leftFoot + ", " + rightFoot);
        assertTrue(leftFoot.y() > rightFoot.y() + 5, "and higher");
    }

    @Test
    void finishedInMetalItStandsOneMetreNinetyItsBodyGoldAndItsArmsAndLegsTitanium() {
        final List<Courier.Placed> placed = new Courier(190, Finish.GILT).placed(
                Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), still);
        assertEquals(190, corner(placed.get(4), 0, 0.5, 0).y(), 1e-9, "the top of its head");
        assertEquals(3 * 190.0 / 201, lowest(placed), 1e-9, "its soles, as much nearer the ground as it is smaller");
        assertEquals(Tone.BRONZE, placed.get(0).tone(), "its pelvis");
        assertEquals(Tone.GOLD, placed.get(1).tone(), "its torso");
        assertEquals(Tone.GOLD, placed.get(4).tone(), "its head");
        assertEquals(Tone.VISOR, placed.get(5).tone(), "its visor, lit from within as it was");
        assertEquals(Tone.TITANIUM, placed.get(9).tone(), "a leg");
        assertEquals(Tone.GUNMETAL, placed.get(11).tone(), "a foot");
        assertEquals(Tone.TITANIUM, placed.get(16).tone(), "an arm");
    }

    @Test
    void itsJetsAreSetIntoItsBackAndFlameOnlyWhenTheyFireAndTheEnginesBodyFliesWithNoWings() {
        final Courier gilt = new Courier(190, Finish.GILT);
        final Body standing = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);
        final List<Courier.Placed> idle = gilt.placed(standing, still);
        final List<Courier.Placed> firing = gilt.placed(standing, 1, still);
        assertEquals(25 + 4, idle.size(), "two housings and two nozzles");
        assertEquals(25 + 6, firing.size(), "and two flames, firing");
        assertTrue(corner(idle.get(25), 0, 0, 0).z() < -15, "behind it: " + corner(idle.get(25), 0, 0, 0));
        assertEquals(Tone.FLAME, firing.get(27).tone());
        assertTrue(corner(firing.get(27), 0, -0.5, 0).y() < corner(firing.get(26), 0, -0.5, 0).y(),
                "a flame reaches down below its nozzle");
        assertEquals(25, new Courier().placed(standing, 1, still).size(), "the engine's own figure has none");
        // taken off as the walk takes it off, and told to climb for a second
        Body flying = standing.toggledFlight();
        for (int step = 0; step < 120; step++) {
            flying = flying.step(new Controls(0, 0, 0, 1, true), 1.0 / 120);
        }
        assertTrue(flying.flying() && flying.height() > 300, "well off the ground: " + flying.height());
        assertTrue(flying.thrust() > 0.3, "its jets firing: " + flying.thrust());
    }

    @Test
    void runningByTheEnginesOwnSimulationItGoesForwardWithArmAndLegOppositeWays() {
        Body body = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);
        final Controls running = new Controls(1, 0, 0, 0, true);
        double swung = 0;
        for (int step = 0; step < 240; step++) {
            body = body.step(running, 1.0 / 120);
            final Pose pose = Gait.of(body, 0, 0);
            swung = Math.max(swung, Math.abs(pose.leftLeg()));
            assertTrue(pose.leftLeg() * pose.leftArm() <= 0, "the left arm against the left leg");
            assertEquals(pose.leftLeg(), -pose.rightLeg(), 1e-9, "and the legs against each other");
        }
        assertTrue(body.position().z() > 1000, "two seconds' running, well over ten metres: " + body.position());
        assertTrue(swung > 40, "its legs swinging wide: " + swung);
        assertTrue(lowest(courier.placed(body, Gait.of(body, 0, 0))) >= 3 - 1e-9, "its feet never in the ground");
    }

    /** How high the lowest corner of any of its boxes is. */
    private double lowest(final List<Courier.Placed> placed) {
        double lowest = Double.MAX_VALUE;
        for (final Courier.Placed box : placed) {
            for (int corner = 0; corner < 8; corner++) {
                lowest = Math.min(lowest, corner(box, (corner & 1) - 0.5, (corner >> 1 & 1) - 0.5,
                        (corner >> 2 & 1) - 0.5).y());
            }
        }
        return lowest;
    }

    /** Where a point of {@code box} is in the world, said as a place in a box a unit each way. */
    private Vec3 corner(final Courier.Placed box, final double x, final double y, final double z) {
        return box.stands().clip(new Vec3(x, y, z)).shown();
    }
}
