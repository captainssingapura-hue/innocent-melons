package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Where each of the courier's limbs is, holding a pose: the engine's own pose, turned into the place of each limb
 * exactly as the engine's figure turns its joints, in the same order and about the same places. All of it is in the
 * figure's own frame as the engine has it: its feet at the frame's middle, facing along the frame, its right to the
 * frame's right, and <em>down</em> the way the frame's second number grows, so its head is at about -200.
 */
public record Stance(Pose pose) {

    /** The height of the joint the whole body tips about, and how far the soles stand clear of the ground. */
    private static final double MIDDLE = 105;
    private static final double SOLE = 3;
    private static final double HIP = 80;
    private static final double HIPS_APART = 15;
    private static final double SHOULDER = 144;
    private static final double SHOULDERS_APART = 41;
    private static final double NECK = 156;
    /** From the shoulder down to the elbow. */
    private static final double UPPER_ARM = 30;

    /** Where {@code limb} is: what takes a point of it, in the limb's own frame, to the figure's. */
    public Mat4 of(final Limb limb) {
        return switch (limb) {
            case BODY -> body();
            case LEFT_LEG -> hung(-HIPS_APART, -HIP, pose.leftLeg(), pose.legSpread());
            case RIGHT_LEG -> hung(HIPS_APART, -HIP, pose.rightLeg(), -pose.legSpread());
            case HEAD -> body().times(new Mat4(new Vec3(0, -NECK, 0))).times(about(0, 1, 0, pose.headYaw()))
                    .times(about(1, 0, 0, pose.headPitch()));
            case LEFT_ARM -> leftArm();
            case RIGHT_ARM -> rightArm();
            case LEFT_FOREARM -> leftArm().times(new Mat4(new Vec3(0, UPPER_ARM, 0)))
                    .times(about(1, 0, 0, pose.leftElbow()));
            case RIGHT_FOREARM -> rightArm().times(new Mat4(new Vec3(0, UPPER_ARM, 0)))
                    .times(about(1, 0, 0, pose.rightElbow()));
        };
    }

    /** The body: raised off its feet, then rolled and tipped about its middle. */
    private Mat4 body() {
        final Vec3 middle = new Vec3(0, -MIDDLE, 0);
        return new Mat4(new Vec3(0, -(pose.lift() + SOLE), 0))
                .times(new Turned(new Vec3(0, 0, 1), pose.bank()).matrix(middle))
                .times(new Turned(new Vec3(1, 0, 0), -pose.lean()).matrix(middle));
    }

    private Mat4 leftArm() {
        return hung(-SHOULDERS_APART, -SHOULDER, pose.leftArm(), pose.leftSpread())
                .times(about(0, 1, 0, pose.leftTwist()));
    }

    private Mat4 rightArm() {
        return hung(SHOULDERS_APART, -SHOULDER, pose.rightArm(), -pose.rightSpread())
                .times(about(0, 1, 0, pose.rightTwist()));
    }

    /** A limb hung from a joint of the body at ({@code x}, {@code y}): swung forward, then spread to the side. */
    private Mat4 hung(final double x, final double y, final double swung, final double spread) {
        return body().times(new Mat4(new Vec3(x, y, 0))).times(about(1, 0, 0, swung)).times(about(0, 0, 1, spread));
    }

    private Mat4 about(final double x, final double y, final double z, final double degrees) {
        return new Turned(new Vec3(x, y, z), degrees).matrix();
    }
}
