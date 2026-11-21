package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.flesh.Torso;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * How a body's cage is shaped ({@link BodyCage}): its torso's widths at its heights; and beyond them, points moved,
 * as a bust, a chest's pectorals, the lats, the buttocks are; and lines along its edges, grooves pressed in and creased, as the abs', the
 * spine's, the folds under a bust or a buttock. Each on the right, its fellow on the left if said; columns round from
 * straight ahead, to the right, {@link BodyCage#COLUMNS} of them; in the drawn figure's centimetres.
 *
 * @param keys   the torso's widths at its heights, as its profile ({@link Profile})
 * @param pushes the points moved
 * @param lines  the lines creased
 */
public record Shaping(List<Torso.Key> keys, List<Push> pushes, List<Line> lines) implements Scalar {

    public Shaping {
        keys = List.copyOf(keys);
        pushes = List.copyOf(pushes);
        lines = List.copyOf(lines);
    }

    /** A point of the torso's cage: on a ring, in a column. */
    public record Spot(TorsoRing ring, int column) implements Scalar {
    }

    /**
     * A point moved: {@code turn} degrees further round its ring, {@code up}, {@code out} from the middle, and
     * {@code ahead}; on both sides, unless in the middle.
     */
    public record Push(String said, Spot at, double turn, double up, double out, double ahead) implements Scalar {
    }

    /**
     * A line along the edges from each of its points to the next: pressed in {@code depth}, creased {@code sharpness}
     * ({@link Crease}); a muscle's as defined as the body is; on both sides, if said.
     */
    public record Line(String said, List<Spot> path, double sharpness, double depth, boolean muscle, boolean both)
            implements Scalar {

        public Line {
            path = List.copyOf(path);
        }
    }
}
