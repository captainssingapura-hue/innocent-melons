package com.example.fxsuite.life.body.flesh;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How light or dark a skin is, from 0, the lightest, to 1, the darkest: its colour found between swatches of skin as
 * it is seen, evenly spaced from light to dark, a little warmer in the middle. Representative colours, set by eye.
 *
 * @param tone how dark, 0 to 1
 */
public record SkinTone(double tone) implements Scalar {

    public SkinTone {
        if (!(tone >= 0 && tone <= 1)) {
            throw new IllegalArgumentException("a skin tone of " + tone + ", not 0 to 1");
        }
    }

    /** Its colour, as {@code #rrggbb}. */
    public String colour() {
        final int[][] swatches = {{0xf6, 0xe0, 0xcf}, {0xec, 0xc3, 0xa5}, {0xd9, 0xa4, 0x7f}, {0xb7, 0x80, 0x5c},
            {0x8d, 0x5a, 0x3d}, {0x5e, 0x3a, 0x27}, {0x3b, 0x24, 0x19}};
        final double at = tone * (swatches.length - 1);
        final int k = Math.min(swatches.length - 2, (int) Math.floor(at));
        final double f = at - k;
        final StringBuilder hex = new StringBuilder("#");
        for (int c = 0; c < 3; c++) {
            final double v = swatches[k][c] + (swatches[k + 1][c] - swatches[k][c]) * f;
            hex.append(String.format("%02x", (int) Math.round(v)));
        }
        return hex.toString();
    }
}
