package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.fat.SheetBed;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.head.ClayHead;
import com.example.fxsuite.life.body.head.NeckJoin;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.skin.Skins;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * The body the bench draws, made away from the screen, a long while: a male model's body (Proportions.MODEL) as it is
 * bound; its bones; its muscles, the deep ones too if asked; its skin, with the clay head joined on at its neck if a
 * reference head is named.
 *
 * <p>{@code -Dbody.deep} its deep muscles too; {@code -Dbody.bones} how fine its bones are, cm (0.4);
 * {@code -Dbody.skin=coarse} its skin as the motion bench draws it moving, a quarter the points, quicker;
 * {@code -Dhead.reference} a reference head's path, the clay head made from it joined on.</p>
 */
public final class BodyMaker {

    /**
     * The body made, as it is bound: its bones, each moving with its part; its skin; its muscles; the body as bound.
     *
     * @param bones   its bones
     * @param skin    its skin
     * @param muscles its muscles
     * @param bound   the body as it is bound
     */
    public record Made(List<BodyPieces.Bone> bones, Flesh skin, MuscleBody.Bound muscles, Rig bound) {
    }

    /** The body made, each step said to {@code told} as it is begun. */
    public Made made(final Consumer<String> told) {
        final Proportions p = Proportions.MODEL;
        final Rig bound = new Rig(p, BoundPose.INSTANCE.pose(), false);
        told.accept("its bones");
        final Detail fine = new Detail(Double.parseDouble(System.getProperty("body.bones", "0.4")));
        final List<BodyPieces.Bone> bones = new BodyPieces().bones(bound, fine);
        told.accept("its muscles");
        final MuscleBody.Bound muscles = SheetBed.INSTANCE.body(p, Boolean.getBoolean("body.deep"), 1);
        told.accept("its skin");
        final Skins skins = new Skins();
        final Flesh skin = "coarse".equals(System.getProperty("body.skin")) ? skins.moving(p) : skins.of(p);
        return new Made(bones, clayed(skin, told), muscles, bound);
    }

    /**
     * {@code skin} with the clay head made from the reference head {@code -Dhead.reference} names joined on at its
     * neck, where the skull fitted under it sets it, made a man's size along each way as it was fitted, else as big as
     * the skin's head; as it is, if it names none or it cannot be read.
     */
    private Flesh clayed(final Flesh skin, final Consumer<String> told) {
        final String path = System.getProperty("head.reference");
        if (path == null) {
            return skin;
        }
        try {
            told.accept("the clay head");
            final V3 size = SkullForm.INSTANCE.headSize().orElse(new V3(1, 1, 1));
            final Mesher.Mesh head = ClayHead.INSTANCE.of(Path.of(path), size);
            final V3 at = SkullForm.INSTANCE.headAt().orElse(V3.ZERO);
            final double k = SkullForm.INSTANCE.headAt().isPresent() ? 1 : ClayHead.INSTANCE.scale(head, skin);
            told.accept(String.format(Locale.ROOT, "the clay head joined on, %.3f, %.3f, %.3f times as big across, up "
                    + "and ahead", size.x() * k, size.y() * k, size.z() * k));
            return NeckJoin.INSTANCE.joined(skin, head, at, k);
        } catch (final IOException e) {
            told.accept("the clay head not made: " + e.getMessage());
            return skin;
        }
    }
}
