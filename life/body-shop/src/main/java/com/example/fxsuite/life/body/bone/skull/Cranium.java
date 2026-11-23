package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * The skull drawn whole, as it is grown, in the head's frame (x to its right, y up from the joint it nods on atop the
 * atlas, z ahead), its lower jaw apart: the vault, a shell of bone round the brain, hollow, broad low at the ears; its
 * base pierced by the foramen magnum, the condyles beside it; the face before it, its orbits, its cheekbones and their
 * arches back to the ears, its nose's bones over its opening and cavity, its upper teeth's arch and the hard palate.
 * The skull is this whole, its bones knit into one ({@link Skull}). A woman's lighter: her brow and her mastoids less.
 */
public record Cranium() implements StatelessFunctionalObject {

    public static final Cranium INSTANCE = new Cranium();

    /** The whole skull, a woman's as far as {@code female} says. */
    public void draw(final Sketch s, final double female) {
        final double brow = 1 - 0.35 * female;
        vault(s, brow);
        for (final int side : new int[] {1, -1}) {
            side(s, side, brow);
        }
        s.blend(0.6).egg(v(0, 7.0, 9.3), 1.4, 0.8, 0.6 * brow + 0.1);
        for (final int side : new int[] {1, -1}) {
            s.blend(0.5).carve().egg(v(side * 3.3, 4.75, 7.9), 1.95, 1.7, 2.6);
            s.blend(0.8).ring(v(side * 3.3, 4.75, 8.55), v(side * 0.18, 0, 1), 2.0, 0.5);
            s.blend(0.3).carve().ball(v(side * 2.6, 2.85, 8.9), 0.28);
        }
        nose(s);
        // the upper jaw's arch for its teeth, the hard palate within it
        s.blend(0.7).tube(0.8, 0.75, v(3.0, 0.15, 3.8), v(2.65, 0.15, 6.3), v(1.7, 0.15, 7.9), v(0, 0.15, 8.6));
        s.tube(0.75, 0.8, v(0, 0.15, 8.6), v(-1.7, 0.15, 7.9), v(-2.65, 0.15, 6.3), v(-3.0, 0.15, 3.8));
        s.blend(0.5).plate(v(2.5, 0.3, 3.8), v(-2.5, 0.3, 3.8), v(0, 0.3, 8.0), 0.6);
    }

    /** The vault, hollow; the foramen magnum through its base, the condyles beside it; the face's block. */
    private void vault(final Sketch s, final double brow) {
        s.blend(1.6).egg(v(0, 7.6, 0.3), 6.9, 7.4, 9.2);
        s.egg(v(0, 3.6, -0.6), 6.4, 3.6, 7.6);
        s.blend(0.25).carve().egg(v(0, 7.8, 0.3), 6.3, 6.8, 8.6);
        s.carve().egg(v(0, 4.2, -0.6), 5.8, 3.0, 7.0);
        s.blend(0.5).carve().egg(v(0, 0.6, -1.9), 1.55, 1.6, 1.85);
        s.blend(0.4);
        for (final int side : new int[] {1, -1}) {
            s.egg(v(side * 1.3, 0.05, -0.6), 0.55, 0.45, 1.05, 0, side * 20, 0);
        }
        s.ball(v(0, 3.6, -8.55), 0.55 * brow);
        s.blend(1.4).egg(v(0, 2.6, 6.6), 4.2, 3.0, 3.3);
    }

    /**
     * One side: its cheekbone and its arch back to the ear, the hollow the jaw's joint sits in; its mastoid, styloid,
     * ear canal and middle ear; its brow over the orbit; the pterygoid plate behind the upper jaw.
     */
    private void side(final Sketch s, final int side, final double brow) {
        s.blend(1.0).egg(v(side * 4.8, 3.2, 6.9), 1.0, 1.2, 1.3);
        s.blend(0.6).flat(v(side * 6.0, 2.6, 4.6), v(side * 6.3, 2.45, 0.9), 0.5, 0.5, V3.X, 0.55);
        s.egg(v(side * 5.45, 1.6, 0.6), 0.5, 0.45, 0.5);
        s.blend(0.3).carve().ball(v(side * 5.2, 1.15, -0.1), 0.58);
        s.blend(0.9).rod(v(side * 5.4, 1.4, -1.6), v(side * 5.25, -0.6, -1.3), 1.0 * brow, 0.55 * brow);
        s.blend(0.3).rod(v(side * 4.6, 0.3, -0.5), v(side * 4.2, -2.0, 0.6), 0.18, 0.08);
        s.carve().rod(v(side * 7.4, 1.5, -0.3), v(side * 5.3, 1.55, -0.25), 0.45, 0.42);
        s.carve().egg(v(side * 4.85, 1.7, -0.45), 0.45, 0.55, 0.5);
        s.blend(0.8).tube(0.75 * brow, 0.6 * brow, v(side * 4.6, 6.4, 7.7), v(side * 2.6, 6.9, 9.0),
                v(side * 0.4, 6.95, 9.55));
        s.blend(0.5).flat(v(side * 1.7, 1.2, 3.6), v(side * 1.95, -1.2, 3.25), 0.4, 0.3, V3.X, 0.5);
    }

    /** The nose: its bones over its opening, its cavity behind, open to the throat; the spine under the opening. */
    private void nose(final Sketch s) {
        s.blend(0.5);
        for (final int side : new int[] {1, -1}) {
            s.rod(v(side * 0.3, 6.1, 9.55), v(side * 0.5, 4.7, 10.15), 0.32, 0.26);
        }
        s.blend(0.6).carve().egg(v(0, 2.4, 6.6), 1.1, 2.0, 3.6);
        s.carve().egg(v(0, 2.9, 9.6), 1.05, 1.75, 1.6);
        s.blend(0.3).rod(v(0, 0.9, 9.55), v(0, 0.95, 10.3), 0.25, 0.12);
    }
}
