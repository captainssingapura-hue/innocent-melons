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
 * The bot the walks show: a figure built of boxes, as the engine's courier is, but shaped as a man or a woman is
 * ({@code build}), at {@code tall} centimetres to the top of its head. Its shoulders and hips are as wide as its
 * build's; its legs are as long as a person's, thigh and shin, bent at the knee, and its arms, upper arm and forearm,
 * bent at the elbow; each limb narrows from its root to its end, and it has hands and feet. Its head is a toy
 * figure's: a round drum with softened rims and a stud on top, on a short round neck, with two glowing eyes; it has
 * no antenna. It holds the engine's own pose, its knees bent as {@link Knees} works out, its joints where
 * {@link Joints} has them. On its feet, the whole of it stands so that the lower of its feet is on the ground, so it
 * dips and rises through its stride as a walker does. Its jets are in its back, as the courier's are.
 *
 * <p>It is not the engine's figure, and is not a copy of it: the courier is that. Its sizes are general figures for
 * a person of its height, their author's, from general knowledge, not from a survey.
 */
public record Bot(Build build, double tall, Finish finish) {

    /** How tall it is drawn at its own size, to the top of its head's stud. */
    private static final double OWN = 188;
    /** Where a sole is, in the shin's own frame: how far down, and its heel and toe, behind and ahead of the ankle. */
    private static final double SOLE = Joints.SHIN + 9;
    private static final double HEEL = -7;
    private static final double TOE = 19;
    private static final double FLAME = 55;

    /** A man, 1.88 m, finished in metal. */
    public Bot() {
        this(Build.MAN);
    }

    /** Of {@code build}, as tall as it stands: a man finished in metal, a woman in metallic pink. */
    public Bot(final Build build) {
        this(build, build.tall(), build == Build.WOMAN ? Finish.ROSE : Finish.GILT);
    }

    /** Every part of it, each in its limb's own frame: down the way its second number grows. */
    public List<Courier.Piece> pieces() {
        final List<Courier.Piece> all = new ArrayList<>(trunk());
        all.addAll(head());
        final double legs = build.legs();
        for (final Limb leg : List.of(Limb.LEFT_LEG, Limb.RIGHT_LEG)) {
            all.add(new Courier.Piece(leg, Tone.JOINT, 14 * legs, 12, 15 * legs, 0, 2, 0));
            all.add(new Courier.Piece(leg, Tone.SHELL, 15 * legs, 22, 16 * legs, 0, 12, 0.5));
            all.add(new Courier.Piece(leg, Tone.SHELL, 12.5 * legs, 19, 13.5 * legs, 0, 32.5, 0));
        }
        for (final Limb shin : List.of(Limb.LEFT_SHIN, Limb.RIGHT_SHIN)) {
            all.add(new Courier.Piece(shin, Tone.JOINT, 11 * legs, 10, 12 * legs, 0, 0, 0));
            all.add(new Courier.Piece(shin, Tone.SHELL, 11.5 * legs, 20, 12.5 * legs, 0, 13, -1));
            all.add(new Courier.Piece(shin, Tone.SHELL, 8.5 * legs, 17, 9 * legs, 0, 31.5, -0.5));
            all.add(new Courier.Piece(shin, Tone.JOINT, 7.5 * legs, 6, 8 * legs, 0, 42, 0));
            all.add(new Courier.Piece(shin, Tone.SHELL, 10 * legs, 7, TOE - HEEL, 0, SOLE - 3.5, (TOE + HEEL) / 2));
        }
        final double arms = build.arms();
        for (final Limb arm : List.of(Limb.LEFT_ARM, Limb.RIGHT_ARM)) {
            all.add(new Courier.Piece(arm, Tone.JOINT, 12 * arms, 12 * arms, 12 * arms, 0, 0, 0));
            all.add(new Courier.Piece(arm, Tone.SHELL, 9.5 * arms, 26, 10 * arms, 0, 16, 0));
        }
        for (final Limb forearm : List.of(Limb.LEFT_FOREARM, Limb.RIGHT_FOREARM)) {
            all.add(new Courier.Piece(forearm, Tone.JOINT, 8 * arms, 7, 8 * arms, 0, 0, 0));
            all.add(new Courier.Piece(forearm, Tone.SHELL, 8.5 * arms, 13, 8.5 * arms, 0, 9.5, 0));
            all.add(new Courier.Piece(forearm, Tone.SHELL, 7 * arms, 9, 6.5 * arms, 0, 20.5, 0));
            all.add(new Courier.Piece(forearm, Tone.JOINT, 5.5 * arms, 3, 5 * arms, 0, 26.5, 0));
            all.add(new Courier.Piece(forearm, Tone.JOINT, 3 * arms, 18, 8.5 * arms, 0, 37, 0));
        }
        return all;
    }

    /** Its pelvis, waist, chest and chest plate, as wide as its build has them. */
    private List<Courier.Piece> trunk() {
        return List.of(
                new Courier.Piece(Limb.BODY, Tone.TRIM, build.pelvis(), 16, 22, 0, -97, 0),
                new Courier.Piece(Limb.BODY, Tone.JOINT, build.waist(), 14, 18, 0, -112, 0),
                new Courier.Piece(Limb.BODY, Tone.TRIM, build.chest(), 34, build.chestDeep(), 0, -136, 0),
                new Courier.Piece(Limb.BODY, Tone.SHELL, build.chest() * 0.7, 16, 1.5, 0, -140,
                        build.chestDeep() / 2 + 0.75));
    }

    /**
     * Its head, a toy figure's: a short round neck, a round head with softened rims, a round stud on top, and two
     * eyes that glow, set into the front of the head.
     */
    private List<Courier.Piece> head() {
        return List.of(
                new Courier.Piece(Limb.HEAD, Tone.JOINT, 10, 8, 10, 0, 1, 0, true),
                new Courier.Piece(Limb.HEAD, Tone.SHELL, 20, 19, 20, 0, -12.5, 0, true),
                new Courier.Piece(Limb.HEAD, Tone.SHELL, 11, 4.5, 11, 0, -24.25, 0, true),
                new Courier.Piece(Limb.HEAD, Tone.VISOR, 2.4, 3.2, 1.2, -3.6, -14, 9.6),
                new Courier.Piece(Limb.HEAD, Tone.VISOR, 2.4, 3.2, 1.2, 3.6, -14, 9.6));
    }

    /**
     * Every part of it as it is drawn, standing where {@code body} is, holding {@code pose}, its jets firing as hard
     * as {@code thrust} says, in the world as the renderer has it: up the way the second number grows. Each part is
     * placed in the engine's frame, and the whole is then turned over, as the courier is.
     */
    public List<Courier.Placed> placed(final Body body, final double thrust, final Pose pose) {
        final Joints joints = new Joints(pose, new Knees(body), build);
        final double planted = body.grounded() && !body.flying() ? lowest(joints) : 0;
        final Mat4 stood = new Mat4(new Vec3(body.position().x(), body.position().y(), body.position().z()))
                .times(new Turned(new Vec3(0, 1, 0), body.yaw()).matrix())
                .times(new Sized(tall / OWN).matrix())
                .times(new Mat4(new Vec3(0, -planted, 0)));
        final List<Courier.Placed> all = new ArrayList<>();
        for (final Courier.Piece piece : pieces()) {
            all.add(new Courier.Placed(finish.of(piece.limb(), piece.tone()), there(stood, joints, piece),
                    piece.round(), finish.tint()));
        }
        if (finish != Finish.PLAIN) {
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
        final double back = -(build.chestDeep() / 2 + 4);
        for (final double side : new double[] {-7.5, 7.5}) {
            jets.add(new Courier.Piece(Limb.BODY, Tone.GUNMETAL, 9, 24, 8, side, -136, back));
            jets.add(new Courier.Piece(Limb.BODY, Tone.BRONZE, 7, 5, 7, side, -121.5, back));
            if (thrust > 0) {
                final double flame = FLAME * thrust;
                jets.add(new Courier.Piece(Limb.BODY, Tone.FLAME, 4.5, flame, 4.5, side, -119 + flame / 2, back));
            }
        }
        return jets;
    }

    /** What takes a unit box or drum to where {@code piece} is, standing as {@code stood} says, turned over. */
    private Mat4 there(final Mat4 stood, final Joints joints, final Courier.Piece piece) {
        final Mat4 inEngine = stood.times(joints.of(piece.limb()))
                .times(new Mat4(new Vec3(piece.x(), piece.y(), piece.z())))
                .times(new Sized(piece.across(), piece.up(), piece.along()).matrix());
        // the engine counts height down and the renderer up: turned over, as the courier is
        final Mat4 over = new Sized(1, -1, 1).matrix();
        return over.times(inEngine).times(over);
    }
}
