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
 * The bot the walks show: a metal figure built of boxes, as the engine's courier is, but shaped as a man is, at
 * {@code tall} centimetres to the top of its head (1.88 m unless said). Its head is a man's size in a helmet; its
 * shoulders and hips are as wide as a man's; its legs are as long, thigh and shin, bent at the knee, and its arms,
 * upper arm and forearm, bent at the elbow; each limb narrows from its root to its end, and it has hands and feet.
 * It holds the engine's own pose, its knees bent as {@link Knees} works out, its joints where {@link Joints} has
 * them. On its feet, the whole of it stands so that the lower of its feet is on the ground, so it dips and rises
 * through its stride as a walker does. Its jets are in its back, as the courier's are.
 *
 * <p>It is not the engine's figure, and is not a copy of it: the courier is that. Its sizes are general figures for
 * a man of its height, their author's, from general knowledge, not from a survey.
 */
public record Bot(double tall, Finish finish) {

    /** How tall it is drawn at its own size, to the top of its helmet. */
    private static final double OWN = 188;
    /** Where a sole is, in the shin's own frame: how far down, and its heel and toe, behind and ahead of the ankle. */
    private static final double SOLE = Joints.SHIN + 9;
    private static final double HEEL = -7;
    private static final double TOE = 19;
    private static final double FLAME = 55;

    /** At a man's height, 1.88 m, finished in metal. */
    public Bot() {
        this(OWN, Finish.GILT);
    }

    /** Every box of it, each in its limb's own frame: down the way its second number grows. */
    public List<Courier.Piece> pieces() {
        final List<Courier.Piece> all = new ArrayList<>(trunk());
        all.addAll(head());
        for (final Limb leg : List.of(Limb.LEFT_LEG, Limb.RIGHT_LEG)) {
            all.add(new Courier.Piece(leg, Tone.JOINT, 14, 12, 15, 0, 2, 0));
            all.add(new Courier.Piece(leg, Tone.SHELL, 15, 22, 16, 0, 12, 0.5));
            all.add(new Courier.Piece(leg, Tone.SHELL, 12.5, 19, 13.5, 0, 32.5, 0));
        }
        for (final Limb shin : List.of(Limb.LEFT_SHIN, Limb.RIGHT_SHIN)) {
            all.add(new Courier.Piece(shin, Tone.JOINT, 11, 10, 12, 0, 0, 0));
            all.add(new Courier.Piece(shin, Tone.SHELL, 11.5, 20, 12.5, 0, 13, -1));
            all.add(new Courier.Piece(shin, Tone.SHELL, 8.5, 17, 9, 0, 31.5, -0.5));
            all.add(new Courier.Piece(shin, Tone.JOINT, 7.5, 6, 8, 0, 42, 0));
            all.add(new Courier.Piece(shin, Tone.SHELL, 10, 7, TOE - HEEL, 0, SOLE - 3.5, (TOE + HEEL) / 2));
        }
        for (final Limb arm : List.of(Limb.LEFT_ARM, Limb.RIGHT_ARM)) {
            all.add(new Courier.Piece(arm, Tone.JOINT, 12, 12, 12, 0, 0, 0));
            all.add(new Courier.Piece(arm, Tone.SHELL, 9.5, 26, 10, 0, 16, 0));
        }
        for (final Limb forearm : List.of(Limb.LEFT_FOREARM, Limb.RIGHT_FOREARM)) {
            all.add(new Courier.Piece(forearm, Tone.JOINT, 8, 7, 8, 0, 0, 0));
            all.add(new Courier.Piece(forearm, Tone.SHELL, 8.5, 13, 8.5, 0, 9.5, 0));
            all.add(new Courier.Piece(forearm, Tone.SHELL, 7, 9, 6.5, 0, 20.5, 0));
            all.add(new Courier.Piece(forearm, Tone.JOINT, 5.5, 3, 5, 0, 26.5, 0));
            all.add(new Courier.Piece(forearm, Tone.JOINT, 3, 18, 8.5, 0, 37, 0));
        }
        return all;
    }

    /** Its pelvis, waist, chest and chest plate, and its neck. */
    private List<Courier.Piece> trunk() {
        return List.of(
                new Courier.Piece(Limb.BODY, Tone.TRIM, 34, 16, 22, 0, -97, 0),
                new Courier.Piece(Limb.BODY, Tone.JOINT, 28, 14, 18, 0, -112, 0),
                new Courier.Piece(Limb.BODY, Tone.TRIM, 32, 34, 23, 0, -136, 0),
                new Courier.Piece(Limb.BODY, Tone.SHELL, 22, 16, 1.5, 0, -140, 12.25),
                new Courier.Piece(Limb.BODY, Tone.JOINT, 11, 8, 11, 0, -156, 0));
    }

    /** Its helmet, visor, jaw line, antenna and beacon. */
    private List<Courier.Piece> head() {
        return List.of(
                new Courier.Piece(Limb.HEAD, Tone.SHELL, 17, 26, 21, 0, -14, 0),
                new Courier.Piece(Limb.HEAD, Tone.VISOR, 13, 5, 1.5, 0, -16, 11.25),
                new Courier.Piece(Limb.HEAD, Tone.JOINT, 15, 4, 19, 0, -2.5, 0),
                new Courier.Piece(Limb.HEAD, Tone.JOINT, 1.5, 9, 1.5, 6, -31.5, 0),
                new Courier.Piece(Limb.HEAD, Tone.BEACON, 3.5, 3.5, 3.5, 6, -37.5, 0));
    }

    /**
     * Every box of it as it is drawn, standing where {@code body} is, holding {@code pose}, its jets firing as hard
     * as {@code thrust} says, in the world as the renderer has it: up the way the second number grows. Each box is
     * placed in the engine's frame, and the whole is then turned over, as the courier is.
     */
    public List<Courier.Placed> placed(final Body body, final double thrust, final Pose pose) {
        final Joints joints = new Joints(pose, new Knees(body));
        final double planted = body.grounded() && !body.flying() ? lowest(joints) : 0;
        final Mat4 stood = new Mat4(new Vec3(body.position().x(), body.position().y(), body.position().z()))
                .times(new Turned(new Vec3(0, 1, 0), body.yaw()).matrix())
                .times(new Sized(tall / OWN).matrix())
                .times(new Mat4(new Vec3(0, -planted, 0)));
        final List<Courier.Placed> all = new ArrayList<>();
        for (final Courier.Piece piece : pieces()) {
            all.add(new Courier.Placed(finish.of(piece.limb(), piece.tone()), there(stood, joints, piece)));
        }
        if (finish == Finish.GILT) {
            for (final Courier.Piece jet : jets(thrust)) {
                all.add(new Courier.Placed(jet.tone(), there(stood, joints, jet)));
            }
        }
        return all;
    }

    /**
     * How far below the ground the lower of its soles stands, holding the pose {@code joints} gives it: less than
     * nothing where both are above it, as a stride lifts them. The figure is lowered by as much, which plants it.
     */
    private double lowest(final Joints joints) {
        double lowest = Double.NEGATIVE_INFINITY;
        for (final Limb shin : List.of(Limb.LEFT_SHIN, Limb.RIGHT_SHIN)) {
            for (final double along : new double[] {HEEL, TOE}) {
                lowest = Math.max(lowest, joints.of(shin).times(new Mat4(new Vec3(0, SOLE, along))).at(1, 3));
            }
        }
        return lowest;
    }

    /** Its jets: two, set into its back either side of its spine, each a housing, a nozzle and, firing, a flame. */
    private List<Courier.Piece> jets(final double thrust) {
        final List<Courier.Piece> jets = new ArrayList<>();
        for (final double side : new double[] {-7.5, 7.5}) {
            jets.add(new Courier.Piece(Limb.BODY, Tone.GUNMETAL, 9, 24, 8, side, -136, -15.5));
            jets.add(new Courier.Piece(Limb.BODY, Tone.BRONZE, 7, 5, 7, side, -121.5, -15.5));
            if (thrust > 0) {
                final double flame = FLAME * thrust;
                jets.add(new Courier.Piece(Limb.BODY, Tone.FLAME, 4.5, flame, 4.5, side, -119 + flame / 2, -15.5));
            }
        }
        return jets;
    }

    /** What takes a box a unit each way to where {@code piece} is, standing as {@code stood} says, turned over. */
    private Mat4 there(final Mat4 stood, final Joints joints, final Courier.Piece piece) {
        final Mat4 inEngine = stood.times(joints.of(piece.limb()))
                .times(new Mat4(new Vec3(piece.x(), piece.y(), piece.z())))
                .times(new Sized(piece.across(), piece.up(), piece.along()).matrix());
        // the engine counts height down and the renderer up: turned over, as the courier is
        final Mat4 over = new Sized(1, -1, 1).matrix();
        return over.times(inEngine).times(over);
    }
}
