package com.example.fxsuite.vulkan.bench.subject;

/**
 * How the courier is finished: as the engine's own figure is, in its shell, trim and joints; or in metal, its body
 * and head polished gold and its arms and legs titanium, with what joins each a darker metal of the same kind so
 * that its parts are still told apart. Either way its visor and its beacon are lit from within, as they were.
 */
public enum Finish {

    PLAIN,
    GILT;

    /** What a part of {@code limb} is made of in this finish, which in the engine's figure is {@code tone}. */
    public Tone of(final Limb limb, final Tone tone) {
        if (this == PLAIN || tone == Tone.VISOR || tone == Tone.BEACON) {
            return tone;
        }
        final boolean trunk = limb == Limb.BODY || limb == Limb.HEAD;
        if (tone == Tone.JOINT) {
            return trunk ? Tone.BRONZE : Tone.GUNMETAL;
        }
        return trunk ? Tone.GOLD : Tone.TITANIUM;
    }
}
