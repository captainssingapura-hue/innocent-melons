package com.example.fxsuite.life.body.bone.form;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.math.form.Form;

import java.util.List;

/**
 * A bone drawn as a form: its landmarks, its borders and crests as edges between them, its fossae and shelves as
 * faces; and what it says about them by name, in turn ({@link BonePiece}): ridges, sheets, knobs, and pits carved
 * last. Its landmarks are where its measures are taken from, so placing them is fitting it to them.
 *
 * @param form   its landmarks, edges and faces, in its own frame
 * @param pieces what it says about them, drawn in turn
 */
public record BoneForm(Form form, List<BonePiece> pieces) {

    public BoneForm {
        pieces = List.copyOf(pieces);
        if (!form.problems().isEmpty()) {
            throw new IllegalArgumentException(form.name() + ": " + form.problems());
        }
    }

    /** It drawn on {@code s}, placed in the body as {@code placed} says. */
    public void drawn(final Sketch s, final Placed placed) {
        pieces.forEach(p -> p.drawn(form, placed, s));
    }
}
