package com.example.fxsuite.vulkan.bench.film;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Where a film's shots put the camera, worked out with no card. With none, the run as played. A fixed shot stands
 * where it is put and looks at the avatar; an orbit keeps its distance and height and goes round at its speed; a
 * shot beside the avatar stands to its right when told to, whichever way it faces; a path goes from its first place
 * to its last over the shot, the last shot of a film over to the film's end. A cut changes the camera at once; a blend begins where the shot before has it and ends
 * where the new one does. A list that is out of order, or a line that cannot be read, is refused, saying which line.
 */
class ShotsTest {

    private static final double NEAR = 1e-6;

    private final Vec3 head = new Vec3(500, 160, 800);
    private final Camera played = new Orbit(head, 0, 10, 600).camera(48, 10);

    @Test
    void withNoShotsTheFilmIsTheRunAsPlayed() {
        assertSame(played, new Shots().camera(3, head, 30, played));
        assertSame(played, new Shots("shots 1\n0 as played\n").camera(0, head, 30, played));
    }

    @Test
    void aFixedShotStandsWhereItIsPutAndLooksAtTheAvatar() {
        final Camera fixed = new Shots("shots 1\n0 fixed at=10,3,2 fov=35\n").camera(1, head, 0, played);
        near(new Vec3(1000, 300, 200), fixed.eye());
        near(head.minus(fixed.eye()).unit(), fixed.ahead());
        assertEquals(35, fixed.fieldOfView(), NEAR);
    }

    @Test
    void anOrbitKeepsItsDistanceAndHeightAndGoesRoundAtItsSpeed() {
        final Shots orbit = new Shots("shots 1\n0 orbit distance=8 height=2 speed=30 from=0\n");
        final Camera begun = orbit.camera(0, head, 0, played);
        final Camera later = orbit.camera(3, head, 0, played);
        for (final Camera camera : new Camera[] {begun, later}) {
            final Vec3 away = camera.eye().minus(head);
            assertEquals(800, Math.hypot(away.x(), away.z()), NEAR, "its distance across the ground");
            assertEquals(200, away.y(), NEAR, "its height above the head");
            near(head.minus(camera.eye()).unit(), camera.ahead());
        }
        near(head.plus(new Vec3(0, 200, 800)), begun.eye());
        near(head.plus(new Vec3(Math.sin(Math.toRadians(90)) * 800, 200, Math.cos(Math.toRadians(90)) * 800)),
                later.eye());
    }

    @Test
    void aShotBesideTheAvatarStandsToItsRightWhicheverWayItFaces() {
        final Shots beside = new Shots("shots 1\n0 beside offset=4,0,0\n");
        near(head.plus(new Vec3(400, 0, 0)), beside.camera(0, head, 0, played).eye());
        near(head.plus(new Vec3(0, 0, -400)), beside.camera(0, head, 90, played).eye());
    }

    @Test
    void aPathGoesFromItsFirstPlaceToItsLastOverTheShot() {
        final Shots path = new Shots("shots 1\n0 path 0,10,0 > 20,10,0 > 20,10,40\n10 as played\n");
        near(new Vec3(0, 1000, 0), path.camera(0, head, 0, played).eye());
        near(new Vec3(2000, 1000, 0), path.camera(5, head, 0, played).eye());
        near(new Vec3(2000, 1000, 4000), path.camera(9.999999, head, 0, played).eye());
        assertSame(played, path.camera(10, head, 0, played));
        final Shots last = new Shots("shots 1\n0 as played\n4 path 0,10,0 > 40,10,0\n").endingAt(12);
        near(new Vec3(0, 1000, 0), last.camera(4, head, 0, played).eye());
        near(new Vec3(2000, 1000, 0), last.camera(8, head, 0, played).eye(), "a last path, over to the film's end");
        near(new Vec3(4000, 1000, 0), last.camera(12, head, 0, played).eye());
    }

    private void near(final Vec3 wanted, final Vec3 got, final String what) {
        assertTrue(wanted.minus(got).length() < 1e-6 * Math.max(1, wanted.length()), what + ": wanted " + wanted
                + ", got " + got);
    }

    @Test
    void aCutChangesTheCameraAtOnceAndABlendGoesFromTheOneShotToTheOther() {
        final Shots cut = new Shots("shots 1\n0 fixed at=0,2,0\n4 fixed at=40,2,0 cut\n");
        near(new Vec3(4000, 200, 0), cut.camera(4, head, 0, played).eye());
        final Shots blend = new Shots("shots 1\n0 fixed at=0,2,0\n4 fixed at=40,2,0 blend 2\n");
        near(new Vec3(0, 200, 0), blend.camera(4, head, 0, played).eye());
        near(new Vec3(2000, 200, 0), blend.camera(5, head, 0, played).eye());
        near(new Vec3(4000, 200, 0), blend.camera(6, head, 0, played).eye());
    }

    @Test
    void aListOutOfOrderOrALineThatCannotBeReadIsRefusedSayingWhichLine() {
        assertThrows(IllegalArgumentException.class, () -> new Shots("shots 1\n5 as played\n2 as played\n"));
        final IllegalArgumentException unread = assertThrows(IllegalArgumentException.class,
                () -> new Shots("shots 1\n0 as played\n\n# a note\n4 swoop\n"));
        assertTrue(unread.getMessage().contains("line 5"), unread.getMessage());
        assertThrows(IllegalArgumentException.class, () -> new Shots("0 as played\n"));
        assertThrows(IllegalArgumentException.class, () -> new Shots("shots 1\n0 path 1,2,3\n"));
    }

    private void near(final Vec3 wanted, final Vec3 got) {
        assertTrue(wanted.minus(got).length() < 1e-6 * Math.max(1, wanted.length()), "wanted " + wanted + ", got "
                + got);
    }
}
