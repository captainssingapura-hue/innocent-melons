package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Rig;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * A torso's shape as it is seen, whole, from the crotch to under the jaw: at heights found on the bones
 * ({@link Landmark}), how far it reaches to either side, ahead and behind, and how far ahead its middle is; smooth
 * between them, never overshooting. Over that, its features, each a smooth swelling or a groove: the abs, the obliques,
 * a bust or a chest's pectorals, the shoulder blades, the spine's groove, the buttocks; the muscles' as defined as the
 * torso's {@code definition} says, from 0 smooth to 1 lean. In the drawn figure's centimetres; round from straight
 * ahead, to the right, in degrees.
 *
 * @param keys       its widths at its heights, bottom to top
 * @param features   its swellings and grooves
 * @param definition how defined its muscles are, 0 to 1
 */
public record Torso(List<Key> keys, List<Feature> features, double definition) implements Scalar {

    public Torso {
        keys = List.copyOf(keys);
        features = List.copyOf(features);
    }

    /** This torso, its muscles as defined as {@code d} says. */
    public Torso defined(final double d) {
        return new Torso(keys, features, d);
    }

    /** A height: {@code along} of the way from one landmark to another, and {@code off} above that, cm. */
    public record Height(Landmark at, Landmark toward, double along, double off) implements Scalar {

        /** How high it is on the body {@code bound} holds. */
        public double y(final Rig bound) {
            final double a = at.y(bound);
            return a + (toward.y(bound) - a) * along + off;
        }
    }

    /** At {@code height}: how far it reaches to either side, ahead and behind of its middle, its middle ahead. */
    public record Key(Height height, double side, double ahead, double behind, double middle) implements Scalar {
    }

    /**
     * How a feature lies: along a line; over a patch; or as a dome, as a breast is, round below with a fold under it,
     * a longer slope above.
     */
    public enum Kind { LINE, PATCH, DOME }

    /**
     * A swelling ({@code amount} out) or a groove (in): along a line from {@code from} degrees at {@code low} to
     * {@code to} at {@code high}, falling away over {@code soft} cm either side of it; or over the patch between those
     * angles and heights, falling away over {@code soft} cm round and {@code tall} up and down; on both sides, if said,
     * as on the right; a muscle's, as defined as the torso is.
     *
     * @param said   what it is
     * @param kind   a line or a patch
     * @param from   its first angle
     * @param low    its first height
     * @param to     its second angle
     * @param high   its second height
     * @param soft   how far round it falls away, cm
     * @param tall   how far up and down a patch falls away, cm
     * @param amount how far out, cm; in, if less than nothing
     * @param both   whether on both sides
     * @param muscle whether a muscle's, as defined as the torso
     */
    public record Feature(String said, Kind kind, double from, Height low, double to, Height high, double soft,
                          double tall, double amount, boolean both, boolean muscle) implements Scalar {
    }
}
