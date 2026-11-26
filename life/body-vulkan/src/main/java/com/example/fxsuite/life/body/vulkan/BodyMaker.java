package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.fat.FatPattern;
import com.example.fxsuite.life.body.fat.SheetBed;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.head.ClayHead;
import com.example.fxsuite.life.body.head.FaceOpening;
import com.example.fxsuite.life.body.head.Helmet;
import com.example.fxsuite.life.body.head.NeckJoin;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.life.body.wear.Armour;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Garment;
import com.example.fxsuite.life.body.wear.Tailor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * The body the bench draws, made away from the screen, a long while: a male model's body (Proportions.MODEL) as it is
 * bound; its bones; its muscles, the deep ones too if asked; its skin, open where the face is to go
 * ({@link FaceOpening}), or with the clay head joined on at its neck if a reference head is named.
 *
 * <p>{@code -Dbody.deep} its deep muscles too; {@code -Dbody.bones} how fine its bones are, cm (0.4);
 * {@code -Dbody.skin=coarse} its skin as the motion bench draws it moving, a quarter the points, quicker;
 * {@code -Dhead.reference} a reference head's path, the clay head made from it joined on.</p>
 */
public final class BodyMaker {

    /** The colour its tights and its briefs are made, till it is shown wearing them otherwise. */
    private static final String CLOTH = "#22262e";

    /**
     * The body made, as it is bound: its bones, each moving with its part; its skin; its muscles; the body as bound;
     * what it may wear, each hard piece moving with its part: the helmet ({@link Helmet}) and the armour
     * ({@link Armour}); and what it may wear of cloth, bending as it does ({@link Tailor}).
     *
     * @param bones   its bones
     * @param skin    its skin
     * @param muscles its muscles
     * @param bound   the body as it is bound
     * @param worn    what it may wear that is hard
     * @param clothes what it may wear of cloth
     */
    public record Made(List<BodyPieces.Bone> bones, Flesh skin, MuscleBody.Bound muscles, Rig bound,
                       List<BodyPieces.Worn> worn, List<Garment> clothes) {
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
        final Skins skins = new Skins(FatPattern.LEAN);
        final Flesh skin = "coarse".equals(System.getProperty("body.skin")) ? skins.moving(p) : skins.of(p);
        final Flesh worn = clayed(skin, told);
        told.accept("its helmet and its armour");
        final List<BodyPieces.Worn> wears = new ArrayList<>(new BodyPieces().worn(bound, Helmet.INSTANCE.made()));
        final Beneath beneath = skins.beneath(skin);
        wears.addAll(new BodyPieces().worn(Armour.INSTANCE.of(beneath, bound)));
        told.accept("its tights and its briefs");
        final List<Garment> clothes = List.of(Tailor.INSTANCE.tights(beneath, bound, CLOTH),
                Tailor.INSTANCE.briefs(beneath, bound, CLOTH));
        return new Made(bones, worn, muscles, bound, wears, clothes);
    }

    /**
     * {@code skin} with the clay head made from the reference head {@code -Dhead.reference} names joined on at its
     * neck, where the skull fitted under it sets it, made a man's size as it was fitted, else as big as the skin's
     * head; if it names none, the skin open where the face is to go; as it is, if the head cannot be read.
     */
    private Flesh clayed(final Flesh skin, final Consumer<String> told) {
        final String path = System.getProperty("head.reference");
        if (path == null) {
            return FaceOpening.INSTANCE.open(skin);
        }
        try {
            told.accept("the clay head");
            final Mesher.Mesh head = ClayHead.INSTANCE.of(Path.of(path), SkullForm.INSTANCE.headWarp());
            final V3 at = SkullForm.INSTANCE.headAt().orElse(V3.ZERO);
            final double k = SkullForm.INSTANCE.headAt().isPresent() ? 1 : ClayHead.INSTANCE.scale(head, skin);
            told.accept("the clay head joined on, " + SkullForm.INSTANCE.headWarp().map(w -> String.format(Locale.ROOT,
                    "its vault %.3f, its face %.3f as high, %.3f as long, %.3f as broad at the cheekbones",
                    w.scales().vault(), w.scales().face(), w.scales().ahead(), w.scales().cheek()))
                    .orElse(String.format(Locale.ROOT, "%.3f times as big", k)));
            return NeckJoin.INSTANCE.joined(skin, head, at, k);
        } catch (final IOException e) {
            told.accept("the clay head not made: " + e.getMessage());
            return skin;
        }
    }
}
