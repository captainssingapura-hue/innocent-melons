package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Flakes;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;

import java.util.List;

/**
 * Metallic paint on sheet, as a car's body wears it: a coat of colour with fine flakes of metal lying in it, under
 * the same clear, hard lacquer as gloss paint. The colour is the thing's own, told with each thing drawn. The
 * coat itself is darker than the colour said, and the flakes give the rest back as a wide, soft shine round where
 * the sun is mirrored, paler than the colour, since a flake is silver seen through it. So a curved sheet is bright
 * where it turns toward the sun's mirror and deep away from it and toward its edges, which is how such a paint
 * shows a body's shape. Seen from within a pace or two the flakes are told apart and glitter; further off they
 * run together into the shine.
 *
 * <p>The lacquer over it is gloss paint's: a small hard glint, and the sky's light and the ground's mirrored
 * faintly seen square on and nearly wholly at a glancing angle. Nothing that stands beside the thing is seen in
 * it. A colour that shifts as the thing turns, as a pearl's does, is another paint and not this one.
 */
public record FlakedLacquer() {

    /** How much of the colour said the coat itself shows, and how far toward silver the flakes' shine is. */
    private static final double COAT = 0.62;
    private static final double SILVERED = 0.35;
    private static final double SHINING = 0.85;
    /** The flakes' shine: how wide (the lower, the wider), and how much duller the coat is at the very edge. */
    private static final double SPREAD = 12;
    private static final double FLOP = 0.55;
    /** How far it is from one flake that can be told apart to the next, in centimetres; and how many catch the sun. */
    private static final double APART = 0.08;
    private static final double FEW = 0.3;

    /** Its surface: the thing's colour, the flakes in it, and the lacquer's glint and mirroring over both. */
    public Surface surface() {
        final Colour said = new Tint().colour();
        return new Surface(said.scaled(new Real(COAT)), List.of(), new Colour("#ffffff"), 260)
                .mirroring(new Colour(0.05, 0.05, 0.05)).glancing(new Colour(0.72, 0.72, 0.72))
                .flaked(new Flakes(said.mixed(new Colour("#ffffff"), new Real(SILVERED)).scaled(new Real(SHINING)),
                        SPREAD, FLOP, APART, FEW));
    }
}
