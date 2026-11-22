package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Hit;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.view.Chase;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;

/**
 * The view that follows the avatar: the engine's own chase camera ({@code view.Chase}), moved each frame as the
 * engine's game moves it. It looks at the avatar's head from behind; the mouse tilts it; Q and E swing it round
 * the avatar, and C puts it back behind; the wheel brings it nearer; and it swings round behind the avatar again as
 * the avatar goes, unless it is being held round. Nothing of the camera's arithmetic is written here: only how the
 * engine's camera, whose heights are counted downward, is the renderer's, whose heights are counted up.
 *
 * <p>How far a turn of the wheel zooms, how fast Q and E swing, and how wide and from how near the game sees are
 * the engine's game's own numbers, which it keeps to itself, and are copied here.
 */
public final class Following {

    /** How fast Q and E swing the view round, in degrees a second. */
    private static final double CIRCLE = 100;
    /** How many times as far off one count of the wheel puts the view, and how many counts there are to a notch. */
    private static final double SCROLLED = 1.0015;
    private static final double NOTCH = 40;
    /** How many degrees the game sees from top to bottom, and from how near it begins to see, in centimetres. */
    private static final double FIELD = 48;
    private static final double NEAR = 10;

    private Chase chase;

    /** Behind the avatar, with the avatar where {@code body} is. */
    public Following(final Body body) {
        this.chase = Chase.behind(head(body), body.yaw());
    }

    /**
     * One frame of following the avatar, which is where {@code shown} is: put back behind it if {@code behind};
     * nearer or further by {@code scrolled} notches of the wheel; tilted up by {@code tilt} degrees; swung round by
     * Q and E as {@code circling} says (1 for E, -1 for Q); and then gone after the avatar, {@code seconds} on.
     */
    public void followed(final Body shown, final double tilt, final double circling, final double scrolled,
                         final boolean behind, final double seconds) {
        if (behind) {
            chase = Chase.behind(chase.pivot(), shown.yaw()).zoomed(chase.distance() / Chase.DEFAULT_DISTANCE);
        }
        if (scrolled != 0) {
            chase = chase.zoomed(Math.pow(SCROLLED, -NOTCH * scrolled));
        }
        if (tilt != 0 || circling != 0) {
            chase = chase.turned(circling * CIRCLE * seconds, tilt);
        }
        chase = chase.follow(head(shown), shown.yaw(), 1, seconds, circling != 0);
    }

    /** The engine's camera itself, for what of the engine's wants it: its sound, which is heard from where it is. */
    public Chase chase() {
        return chase;
    }

    /** The heading the view looks along: where the avatar's head turns to look. */
    public double yaw() {
        return chase.yaw();
    }

    /** How far above its usual tilt the view looks, in degrees: how far up the avatar looks, and aims. */
    public double lookUp() {
        return chase.pitch() - Chase.DEFAULT_PITCH;
    }

    /** The renderer's camera, where the engine's is: the engine tilts up and counts height down, the renderer not. */
    public Camera camera() {
        return camera(Obstacles.NONE);
    }

    /**
     * The same, kept this side of whatever of {@code inTheWay} stands between the camera and the avatar's head: as
     * the engine's camera is kept inside the room the avatar is in, it is pulled in to just short of what it would
     * be behind, so the avatar is never hidden by a wall or a melon it has gone round.
     */
    public Camera camera(final Obstacles inTheWay) {
        final com.example.fxsuite.avatar.sim.Vec3 back = chase.forward().multiply(-1);
        double off = chase.distance();
        for (final Block block : inTheWay.blocks()) {
            final Hit met = block.ray(chase.pivot(), back, off);
            if (met != null) {
                off = Math.min(off, Math.max(Chase.NEAREST, met.distance() - Chase.WALL_GAP));
            }
        }
        final Vec3 looksAt = new Vec3(chase.pivot().x(), -chase.pivot().y(), chase.pivot().z());
        return new Orbit(looksAt, chase.yaw(), -chase.pitch(), off).camera(FIELD, NEAR);
    }

    private com.example.fxsuite.avatar.sim.Vec3 head(final Body body) {
        return body.position().add(0, -Chase.EYE_HEIGHT, 0);
    }
}
