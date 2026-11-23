package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The avatar: the engine's small courier robot, built of boxes hung from joints, about two metres tall. Its pose is
 * the engine's own ({@code Pose}, worked out by the engine's {@code Gait}); so is where it stands (the simulation's
 * {@code Body}).
 *
 * <p>Its boxes are <em>copied</em> from the engine's figure ({@code BoxRig}), which describes them only as JavaFX
 * shapes: their sizes, places and tones are that class's, number for number. They are to be read from one place
 * once the engine has the figure described free of JavaFX. Its antenna and its beacon, a rod and a ball there, are
 * boxes here; it wears no pack and holds nothing.
 *
 * <p>{@code tall} is how high the top of its head stands, in centimetres: the whole figure is made bigger or smaller
 * to it, its feet where they were. {@code finish} is what it is made of. The engine's own is 201 centimetres to the
 * top of its head, in its shell and trim; the simulation takes it for two metres tall whatever size it is drawn.
 */
public record Courier(double tall, Finish finish) {

    /** How high the top of the engine's figure's head stands: its soles' clearance, its neck, and its head. */
    private static final double ENGINES = 201;

    /** As the engine's own figure is: as tall, and finished as it is. */
    public Courier() {
        this(ENGINES, Finish.PLAIN);
    }

    /**
     * One part of a figure: the limb it is part of, what it is made of, how big it is and where its middle is; and
     * whether it is round, a drum standing on its end, or, as every part of the courier is, a box.
     */
    public record Piece(Limb limb, Tone tone, double across, double up, double along, double x, double y, double z,
                        boolean round) {

        /** A box. */
        public Piece(final Limb limb, final Tone tone, final double across, final double up, final double along,
                     final double x, final double y, final double z) {
            this(limb, tone, across, up, along, x, y, z, false);
        }
    }

    /**
     * One part of a figure as it is drawn: what it is made of, and what takes a unit box, or a unit drum, to where
     * it is; whether it is round; and, for a part whose material takes its colour from the thing, its colour.
     */
    public record Placed(Tone tone, Mat4 stands, boolean round, Vec3 tint) {

        /** A box of a material that has its own colour. */
        public Placed(final Tone tone, final Mat4 stands) {
            this(tone, stands, false, new Vec3(1, 1, 1));
        }
    }

    /** How far down a forearm its hand is. */
    private static final double HAND = 32;
    /** Its jets: how far either side of its spine, how far behind its middle, and how long a flame at full thrust. */
    private static final double JET_OUT = 16;
    private static final double JET_BEHIND = 23;
    private static final double FLAME = 70;

    /** Every box of it, each in its limb's own frame as the engine has it: down the way its second number grows. */
    public List<Piece> pieces() {
        final List<Piece> all = new ArrayList<>(trunk());
        all.addAll(head());
        for (final Limb leg : List.of(Limb.LEFT_LEG, Limb.RIGHT_LEG)) {
            all.add(new Piece(leg, Tone.SHELL, 24, 72, 26, 0, 38, 0));
            all.add(new Piece(leg, Tone.JOINT, 26, 8, 28, 0, 40, 0));
            all.add(new Piece(leg, Tone.JOINT, 26, 12, 34, 0, 74, 5));
        }
        for (final Limb arm : List.of(Limb.LEFT_ARM, Limb.RIGHT_ARM)) {
            all.add(new Piece(arm, Tone.JOINT, 22, 18, 22, 0, 0, 0));
            all.add(new Piece(arm, Tone.SHELL, 18, 30, 18, 0, 15, 0));
        }
        for (final Limb forearm : List.of(Limb.LEFT_FOREARM, Limb.RIGHT_FOREARM)) {
            all.add(new Piece(forearm, Tone.JOINT, 17, 8, 17, 0, 0, 0));
            all.add(new Piece(forearm, Tone.SHELL, 18, 26, 18, 0, 15, 0));
            all.add(new Piece(forearm, Tone.JOINT, 16, 14, 16, 0, HAND, 0));
        }
        return all;
    }

    /** Its pelvis, torso, chest plate and neck. */
    private List<Piece> trunk() {
        return List.of(
                new Piece(Limb.BODY, Tone.JOINT, 56, 16, 30, 0, -84, 0),
                new Piece(Limb.BODY, Tone.TRIM, 64, 60, 34, 0, -120, 0),
                new Piece(Limb.BODY, Tone.SHELL, 40, 22, 2, 0, -128, 17.5),
                new Piece(Limb.BODY, Tone.JOINT, 18, 10, 18, 0, -152, 0));
    }

    /** Its head, visor, jaw line, antenna and beacon. */
    private List<Piece> head() {
        return List.of(
                new Piece(Limb.HEAD, Tone.SHELL, 46, 40, 42, 0, -22, 0),
                new Piece(Limb.HEAD, Tone.VISOR, 38, 12, 3, 0, -24, 21.5),
                new Piece(Limb.HEAD, Tone.JOINT, 48, 6, 44, 0, -3, 0),
                new Piece(Limb.HEAD, Tone.JOINT, 4, 26, 4, 14, -53, 0),
                new Piece(Limb.HEAD, Tone.BEACON, 9, 9, 9, 14, -68, 0));
    }

    /**
     * Every box of it as it is drawn, standing where {@code body} is and holding {@code pose}, in the world as the
     * renderer has it: up the way the second number grows, where the engine's grows down. Each box is placed in the
     * engine's frame, exactly as the engine places it, and the whole is then turned over.
     */
    public List<Placed> placed(final Body body, final Pose pose) {
        return placed(body, 0, pose);
    }

    /**
     * The same, its jets firing as hard as {@code thrust} says, from nothing to one: each flame as long as that, and
     * none at all at nothing. Only a courier finished in metal has jets.
     */
    public List<Placed> placed(final Body body, final double thrust, final Pose pose) {
        final Stance stance = new Stance(pose);
        final Mat4 stood = new Mat4(new Vec3(body.position().x(), body.position().y(), body.position().z()))
                .times(new Turned(new Vec3(0, 1, 0), body.yaw()).matrix())
                .times(new Sized(tall / ENGINES).matrix());
        final List<Placed> all = new ArrayList<>();
        for (final Piece piece : pieces()) {
            all.add(new Placed(finish.of(piece.limb(), piece.tone()), there(stood, stance, piece)));
        }
        if (finish == Finish.GILT) {
            for (final Piece jet : jets(thrust)) {
                all.add(new Placed(jet.tone(), there(stood, stance, jet)));
            }
        }
        return all;
    }

    /**
     * Its jets: two, set into its back either side of its spine, each a housing with a nozzle under it and, firing,
     * a flame under that. They are part of its body and turn with it; it has no pack, no straps and no wings. Not
     * the engine's, whose figure carries a pack with swing wings.
     */
    private List<Piece> jets(final double thrust) {
        final List<Piece> jets = new ArrayList<>();
        for (final double side : new double[] {-JET_OUT, JET_OUT}) {
            jets.add(new Piece(Limb.BODY, Tone.GUNMETAL, 16, 40, 14, side, -124, -JET_BEHIND));
            jets.add(new Piece(Limb.BODY, Tone.BRONZE, 12, 8, 12, side, -100, -JET_BEHIND));
            if (thrust > 0) {
                final double flame = FLAME * thrust;
                jets.add(new Piece(Limb.BODY, Tone.FLAME, 8, flame, 8, side, -96 + flame / 2, -JET_BEHIND));
            }
        }
        return jets;
    }

    /** Where {@code piece} is, in the engine's frame turned over: what takes a box a unit each way to it. */
    private Mat4 there(final Mat4 stood, final Stance stance, final Piece piece) {
        return overturned(stood.times(stance.of(piece.limb()))
                .times(new Mat4(new Vec3(piece.x(), piece.y(), piece.z())))
                .times(new Sized(piece.across(), piece.up(), piece.along()).matrix()));
    }

    /** {@code engines}, a placing in the engine's frame, as the same placing where up and down are the other way. */
    private Mat4 overturned(final Mat4 engines) {
        final Mat4 over = new Sized(1, -1, 1).matrix();
        return over.times(engines).times(over);
    }
}
