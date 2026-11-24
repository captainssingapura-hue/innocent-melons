package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What is wrong with a form: two landmarks of one name; two so near they are one point, found by the cells of a grid
 * as big as the tolerance, so it is quick however many there are; an edge given twice, either way round; a landmark
 * no edge joins.
 */
final class Problems {

    private final Form form;

    Problems(final Form form) {
        this.form = form;
    }

    List<String> found() {
        final List<String> found = new ArrayList<>();
        named(found);
        near(found);
        joined(found);
        return found;
    }

    private void named(final List<String> found) {
        final Set<String> names = new HashSet<>();
        for (final Landmark l : form.landmarks()) {
            if (!names.add(l.name())) {
                found.add("two landmarks called " + l.name());
            }
        }
    }

    private void near(final List<String> found) {
        final Map<List<Long>, List<Landmark>> cells = new HashMap<>();
        for (final Landmark l : form.landmarks()) {
            final long[] cell = new long[3];
            for (int axis = 0; axis < 3; axis++) {
                cell[axis] = (long) Math.floor(l.at().coordinate(axis) / Form.TOLERANCE);
            }
            for (long dx = -1; dx <= 1; dx++) {
                for (long dy = -1; dy <= 1; dy++) {
                    for (long dz = -1; dz <= 1; dz++) {
                        for (final Landmark other : cells.getOrDefault(
                                List.of(cell[0] + dx, cell[1] + dy, cell[2] + dz), List.of())) {
                            if (other.at().distance(l.at()) < Form.TOLERANCE) {
                                found.add(other.name() + " and " + l.name() + " are one point");
                            }
                        }
                    }
                }
            }
            cells.computeIfAbsent(List.of(cell[0], cell[1], cell[2]), k -> new ArrayList<>()).add(l);
        }
    }

    private void joined(final List<String> found) {
        final Set<Set<Landmark>> pairs = new HashSet<>();
        final Set<Landmark> used = new HashSet<>();
        for (final Edge<Landmark, Course> e : form.edges()) {
            if (!pairs.add(Set.of(e.one(), e.other()))) {
                found.add("an edge twice, " + e.one().name() + " to " + e.other().name());
            }
            used.add(e.one());
            used.add(e.other());
        }
        for (final Landmark l : form.landmarks()) {
            if (!used.contains(l)) {
                found.add(l.name() + " joins nothing");
            }
        }
    }
}
