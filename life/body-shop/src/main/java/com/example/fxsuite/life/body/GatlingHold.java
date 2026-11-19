package com.example.fxsuite.life.body;

/**
 * The Gatling gun held at the hip, as the game draws it ({@link com.example.fxsuite.avatar.look.Arsenal}): carried
 * with the chest, ahead and level but for its aim, its grip in the right hand, the left hand on the lever on top. The
 * gun goes where the body carries it; the arms reach to it ({@link Reach}), each its easy way.
 *
 * <p>The gun is drawn in a frame of its own, as the game's courier holds it in its forearm's: from where its forearm
 * would bend (its {@link #origin}), along it ahead, its grip {@link #HAND} on and the lever {@link #LEVER} at.
 */
public final class GatlingHold {

    /** How far along the gun's frame the grip is; where the lever is, in it: to the right, along, up. */
    static final double HAND = com.example.fxsuite.avatar.look.Arsenal.HAND;
    static final V3 LEVER = lever();
    /** Where the palm closes, in the hand's own frame. */
    static final V3 PALM = new V3(0, -8, 1);
    /** Where the gun's frame starts, from the right elbow standing at rest, cm: out, up, ahead. */
    static final V3 FROM_ELBOW = new V3(1, 3, -1);

    /** The gun's frame: its start, and its ways right, ahead (along the gun) and up, in the body's frame. */
    public record Frame(V3 origin, V3 right, V3 ahead, V3 up) {

        /** {@code local}, in the gun's frame (right, along, up), in the body's. */
        public V3 place(final V3 local) {
            return origin.plus(right.times(local.x())).plus(ahead.times(local.y())).plus(up.times(local.z()));
        }
    }

    /** Held: the pose, its arms on the gun, and where the gun is. */
    public record Held(Pose pose, Frame gun) {
    }

    private final Proportions proportions;
    /** Where the grip is, in the chest's own frame. */
    private final V3 grip;

    public GatlingHold(final Proportions proportions) {
        this.proportions = proportions;
        final Rig rest = new Rig(proportions, Pose.REST);
        final V3 origin = rest.frame(Part.RIGHT_FOREARM).at().plus(FROM_ELBOW);
        grip = rest.frame(Part.CHEST).local(origin.plus(V3.Z.times(HAND)));
    }

    /**
     * {@code pose} (its arms as they last held the gun, or at rest) with the gun held, aimed {@code aim} degrees up:
     * the grip where the chest carries it, the gun turned up about it, the hands reaching to it.
     */
    public Held hold(final Pose pose, final double aim) {
        final Rig rig = new Rig(proportions, pose);
        final V3 at = rig.frame(Part.CHEST).place(grip);
        final double a = Math.toRadians(aim);
        final V3 ahead = new V3(0, Math.sin(a), Math.cos(a));
        final V3 up = new V3(0, Math.cos(a), -Math.sin(a));
        final Frame gun = new Frame(at.minus(ahead.times(HAND)), V3.X, ahead, up);
        Pose p = Reach.toward(proportions, pose, Reach.arm(1, -5, 10, 95), Part.RIGHT_HAND, PALM, at);
        p = Reach.toward(proportions, p, Reach.arm(-1, 35, 5, 75), Part.LEFT_HAND, PALM, gun.place(LEVER));
        return new Held(p, gun);
    }

    /** Arms at the start, before they first reach: as they hold it, near enough. */
    public static Pose ready(final Pose pose) {
        return pose.with(Part.RIGHT_UPPER_ARM, Dof.RAISE_FORWARD, -5).with(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 10)
                .with(Part.RIGHT_FOREARM, Dof.ELBOW, 95).with(Part.LEFT_UPPER_ARM, Dof.RAISE_FORWARD, 35)
                .with(Part.LEFT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 5).with(Part.LEFT_FOREARM, Dof.ELBOW, 75);
    }

    /** The arms of {@code from} on {@code pose}: to start reaching where they last were. */
    public static Pose arms(final Pose pose, final Pose from) {
        Pose p = pose;
        for (final Part part : new Part[] {Part.RIGHT_GIRDLE, Part.LEFT_GIRDLE, Part.RIGHT_UPPER_ARM,
            Part.LEFT_UPPER_ARM, Part.RIGHT_FOREARM, Part.LEFT_FOREARM}) {
            for (final Dof d : part.dofs()) {
                p = p.with(part, d, from.get(part, d));
            }
        }
        return p;
    }

    private static V3 lever() {
        final double[] l = com.example.fxsuite.avatar.look.Arsenal.lever();
        return new V3(l[0], l[1], l[2]);
    }
}
