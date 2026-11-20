package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How finely the game's skeleton is drawn ({@link PartBones}): the grid its big bones' surfaces are found on, cm; the
 * hands', the toes', the feet's, the chest's, the neck's, the head's and the shoulders' finer, so that a finger, a rib
 * or a collarbone holds together; each part's bones swollen by a third of their grid, so that they hold at any. The
 * finer, the more triangles: about four times as many for a grid half as coarse.
 *
 * @param cell the big bones' grid, cm
 */
public record Detail(double cell) implements Scalar {

    public Detail {
        if (!(cell > 0)) {
            throw new IllegalArgumentException("a grid of " + cell + " cm");
        }
    }

    /** {@code p}'s grid, cm. */
    public double cell(final Part p) {
        return cell * switch (p) {
            case RIGHT_HAND, LEFT_HAND, RIGHT_TOES, LEFT_TOES -> 0.4;
            case RIGHT_FOOT, LEFT_FOOT -> 0.55;
            case CHEST -> 0.6;
            case NECK, HEAD, RIGHT_GIRDLE, LEFT_GIRDLE -> 0.7;
            default -> 1.0;
        };
    }

    /** How far {@code p}'s bones are swollen, cm. */
    public double swell(final Part p) {
        return cell(p) / 3;
    }
}
