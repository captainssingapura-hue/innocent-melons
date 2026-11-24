package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Where each of the bot's limbs is, holding the engine's pose and bending its knees as {@code knees} says, its
 * shoulders and hips as far apart as its {@code build} has them, and its legs changed about, the left swung as the
 * pose swings the right, where {@code otherLeg} says so: its
 * joints where a man's of 1.88 m are, its legs, arms and head swung, spread and turned by the pose as the engine's
 * own figure is, in the same order. All of it is in the figure's own frame as the engine has it: its feet at the
 * frame's middle, facing along the frame (the way the third number grows), and <em>down</em> the way the second
 * grows, so its head is at about -188.
 *
 * <p>The heights are general figures for a man of that height (the hip joint 95 cm up, the knee 52, the ankle 9;
 * the shoulder 152, and the neck 160), their author's from general knowledge, not from a survey.
 */
public record Joints(Pose pose, Knees knees, Build build, boolean otherLeg) {

    /** The height of the joint the whole body tips about, near the hips. */
    static final double MIDDLE = 100;
    static final double HIP = 95;

    /** From the hip down to the knee, and from the knee down to the ankle. */
    static final double THIGH = 43;
    static final double SHIN = 43;
    static final double SHOULDER = 152;

    static final double NECK = 160;
    /** From the shoulder down to the elbow. */
    static final double UPPER_ARM = 32;
    /**
     * How far its legs swing, of how far the engine's pose swings them. The engine's gait swings them as far as
     * suits its own short-legged figure; on legs as long as a man's that is a lunge, so the bot's swing three
     * quarters as far. Its arms swing as the pose says.
     */
    static final double STRIDE = 0.75;

    /** Its legs as the pose has them. */
    public Joints(final Pose pose, final Knees knees, final Build build) {
        this(pose, knees, build, false);
    }

    /** A man's build. */
    public Joints(final Pose pose, final Knees knees) {
        this(pose, knees, Build.MAN);
    }

    /**
     * How far the pose swings the left leg, or the right: each the other's, where the legs are changed about, as in
     * every second jump.
     */
    private double swung(final boolean left) {
        return left != otherLeg ? pose.leftLeg() : pose.rightLeg();
    }

    /** Where {@code limb} is: what takes a point of it, in the limb's own frame, to the figure's. */
    public Mat4 of(final Limb limb) {
        return switch (limb) {
            case BODY -> body();
            case LEFT_LEG -> hung(-build.hips(), -HIP, swung(true) * STRIDE, pose.legSpread());
            case RIGHT_LEG -> hung(build.hips(), -HIP, swung(false) * STRIDE, -pose.legSpread());
            case LEFT_SHIN -> of(Limb.LEFT_LEG).times(new Mat4(new Vec3(0, THIGH, 0)))
                    .times(about(1, 0, 0, -knees.left()));
            case RIGHT_SHIN -> of(Limb.RIGHT_LEG).times(new Mat4(new Vec3(0, THIGH, 0)))
                    .times(about(1, 0, 0, -knees.right()));
            case HEAD -> body().times(new Mat4(new Vec3(0, -NECK, 0))).times(about(0, 1, 0, pose.headYaw()))
                    .times(about(1, 0, 0, pose.headPitch()));
            case LEFT_ARM -> hung(-build.shoulders(), -SHOULDER, pose.leftArm(), pose.leftSpread())
                    .times(about(0, 1, 0, pose.leftTwist()));
            case RIGHT_ARM -> hung(build.shoulders(), -SHOULDER, pose.rightArm(), -pose.rightSpread())
                    .times(about(0, 1, 0, pose.rightTwist()));
            case LEFT_FOREARM -> of(Limb.LEFT_ARM).times(new Mat4(new Vec3(0, UPPER_ARM, 0)))
                    .times(about(1, 0, 0, pose.leftElbow()));
            case RIGHT_FOREARM -> of(Limb.RIGHT_ARM).times(new Mat4(new Vec3(0, UPPER_ARM, 0)))
                    .times(about(1, 0, 0, pose.rightElbow()));
        };
    }

    /** The body: raised by the pose's lift, then rolled and tipped about its middle. */
    private Mat4 body() {
        final Vec3 middle = new Vec3(0, -MIDDLE, 0);
        return new Mat4(new Vec3(0, -pose.lift(), 0))
                .times(new Turned(new Vec3(0, 0, 1), pose.bank()).matrix(middle))
                .times(new Turned(new Vec3(1, 0, 0), -pose.lean()).matrix(middle));
    }

    /** A limb hung from a joint of the body at ({@code x}, {@code y}): swung forward, then spread to the side. */
    private Mat4 hung(final double x, final double y, final double swung, final double spread) {
        return body().times(new Mat4(new Vec3(x, y, 0))).times(about(1, 0, 0, swung)).times(about(0, 0, 1, spread));
    }

    private Mat4 about(final double x, final double y, final double z, final double degrees) {
        return new Turned(new Vec3(x, y, z), degrees).matrix();
    }
}
