package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;

import java.util.List;

/**
 * A spread's lines as posed ({@link MuscleForm.Spread}): where it arises, through each of its points in turn, where it
 * ends, and where its fibres pass over, if they do; and the way it faces at its origin. Each line taken by its share
 * of its length along, so a fibre from a share along the one runs to the same share along the other. A strap's: none.
 *
 * @param origin    where it arises, its points in turn
 * @param insertion where it ends
 * @param over      where its fibres pass over halfway; none, straight
 * @param facing    the way it faces at its origin
 */
public record SpreadLines(List<V3> origin, List<V3> insertion, List<V3> over, V3 facing) {

    public SpreadLines {
        origin = List.copyOf(origin);
        insertion = List.copyOf(insertion);
        over = List.copyOf(over);
    }

    /** Whether it is a spread's, with lines to run between. */
    public boolean spread() {
        return !origin.isEmpty();
    }

    /** Where {@code line} is {@code u} of its length along, from its first point; held at its ends. */
    public V3 along(final List<V3> line, final double u) {
        if (line.size() == 1 || u <= 0) {
            return line.getFirst();
        }
        double total = 0;
        for (int i = 1; i < line.size(); i++) {
            total += line.get(i).minus(line.get(i - 1)).length();
        }
        double want = Math.min(1, u) * total;
        for (int i = 1; i < line.size(); i++) {
            final V3 a = line.get(i - 1);
            final V3 b = line.get(i);
            final double piece = b.minus(a).length();
            if (want <= piece || i == line.size() - 1) {
                return piece <= 0 ? b : a.plus(b.minus(a).times(Math.min(1, want / piece)));
            }
            want -= piece;
        }
        return line.getLast();
    }
}
