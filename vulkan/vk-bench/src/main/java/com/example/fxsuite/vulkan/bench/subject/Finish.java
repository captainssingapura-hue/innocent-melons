package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * How a figure is finished: plain, as the engine's own is; gilt, its body and head polished gold and its limbs
 * titanium, with darker metals where parts join; or rose, its body and head a light metallic pink paint and its limbs
 * titanium, with titanium where the body and head join and gunmetal where the limbs do. What glows (its eyes, its
 * beacon) glows whatever it is finished in.
 */
public enum Finish {

    PLAIN,
    GILT,
    ROSE;

    /**
     * The pink of rose: the colour of the paint's coat itself, which the paint is told brighter so its coat shows
     * this; the sun and the sky light it, and its flakes shine paler. A light pink, asked for lighter than the
     * first, which was told {@code #ff4fa8} and showed a dusky plum. Its author's.
     */
    private static final String PINK = "#f4a6c8";

    /** What a part of {@code limb} is made of in this finish, which in the engine's figure is {@code tone}. */
    public Tone of(final Limb limb, final Tone tone) {
        if (this == PLAIN || tone == Tone.VISOR || tone == Tone.BEACON) {
            return tone;
        }
        final boolean trunk = limb == Limb.BODY || limb == Limb.HEAD;
        if (tone == Tone.JOINT) {
            if (this == ROSE) {
                return trunk ? Tone.TITANIUM : Tone.GUNMETAL;
            }
            return trunk ? Tone.BRONZE : Tone.GUNMETAL;
        }
        if (!trunk) {
            return Tone.TITANIUM;
        }
        return this == ROSE ? Tone.PAINT : Tone.GOLD;
    }

    /**
     * The colour a part takes that takes its colour from the thing: what its paint is told, so its coat shows the
     * pink. White, where it has no paint.
     */
    public Vec3 tint() {
        if (this != ROSE) {
            return new Vec3(1, 1, 1);
        }
        final int rgb = Integer.parseInt(PINK.substring(1), 16);
        return new FlakedLacquer().toShow(
                new Vec3((rgb >> 16 & 0xff) / 255.0, (rgb >> 8 & 0xff) / 255.0, (rgb & 0xff) / 255.0));
    }
}
