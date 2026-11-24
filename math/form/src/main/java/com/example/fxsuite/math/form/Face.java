package com.example.fxsuite.math.form;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * A face of a form: a surface spanned by a loop of its edges, named. Its loop is four sides in turn round it, each a
 * path of landmarks by their names along the form's edges, each side beginning where the one before ends and the
 * last ending where the first begins. A side of a single landmark is a corner the sides either side of it meet at, so
 * a three-sided face is a four-sided one with a side drawn to a point. The surface through its sides is its
 * {@link Patch}.
 *
 * @param name  what it is called
 * @param sides its four sides, in turn round it
 */
public record Face(String name, List<List<String>> sides) implements Scalar {

    public Face {
        sides = sides.stream().map(List::copyOf).toList();
        if (sides.size() != 4) {
            throw new IllegalArgumentException(name + ": a face has four sides, one of them a point if it has three");
        }
        for (int i = 0; i < 4; i++) {
            final List<String> side = sides.get(i);
            final List<String> next = sides.get((i + 1) % 4);
            if (side.isEmpty()) {
                throw new IllegalArgumentException(name + ": a side with no landmark");
            }
            if (!side.getLast().equals(next.getFirst())) {
                throw new IllegalArgumentException(name + ": side " + i + " ends at " + side.getLast()
                        + " and the next begins at " + next.getFirst());
            }
        }
    }

    /** Its corners, in turn: where each side begins. */
    public List<String> corners() {
        return sides.stream().map(List::getFirst).toList();
    }
}
