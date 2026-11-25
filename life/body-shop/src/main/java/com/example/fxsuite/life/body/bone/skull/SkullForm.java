package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bend;
import com.example.fxsuite.life.body.head.SkullUnder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * The skull and its jaw as the body has them ({@link Bend}): as men's are measured ({@link SkullLandmarks}); or,
 * a clay head put on the body from the reference head {@code -Dhead.reference} names, fitted under it, its bone as far
 * under its skin as men's is ({@link SkullUnder}), the measures shaping the rest. What is drawn on the head, and what
 * is held on it, bent alike, so that a muscle held on the skull stays on it. Found once.
 */
public enum SkullForm {
    INSTANCE;

    private final Bend bend;
    private final Optional<SkullUnder.Fit> under;

    SkullForm() {
        under = fit();
        bend = SkullBending.INSTANCE.through(SkullLandmarks.INSTANCE.targets(under.map(SkullUnder.Fit::asked)
                .orElse(Map.of())));
    }

    /** The skull fitted under the clay head put on the body, if one is; none, if not. */
    private Optional<SkullUnder.Fit> fit() {
        final String reference = System.getProperty("head.reference");
        if (reference == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(SkullUnder.INSTANCE.fit(Path.of(reference)));
        } catch (final IOException e) {
            throw new UncheckedIOException("the head the skull is to fit not read", e);
        }
    }

    /** How many times as big along each way the clay head the skull is fitted under is made; none, if none. */
    public Optional<V3> headSize() {
        return under.map(SkullUnder.Fit::size);
    }

    /** Where the clay head the skull is fitted under is set on it, moved from where its frame has it; none, if none. */
    public Optional<V3> headAt() {
        return under.map(SkullUnder.Fit::headAt);
    }

    /** Where {@code p}, as drawn in the head's frame, is on the skull as the body has it. */
    public V3 apply(final V3 p) {
        return bend.apply(p);
    }

    /** {@code drawn}, a shape drawn in the head's frame, as the skull is bent. */
    public Field.Compound shaped(final Field.Compound drawn) {
        return bend.shaped(drawn);
    }
}
