package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** A warp through points: each point where it was asked to go; points moved as one affine map, space moved so. */
class ThinPlateTest {

    private final List<V3> from = List.of(new V3(0, 0, 0), new V3(5, 0, 0), new V3(0, 5, 0), new V3(0, 0, 5),
            new V3(3, 4, 2), new V3(-2, 1, 3));

    @Test
    void eachPointGoesWhereItWasAsked() {
        final List<V3> to = new ArrayList<>();
        for (int i = 0; i < from.size(); i++) {
            to.add(from.get(i).plus(new V3(Math.sin(i), Math.cos(i), 0.3 * i)));
        }
        final Warp w = ThinPlate.INSTANCE.through(from, to);
        for (int i = 0; i < from.size(); i++) {
            assertEquals(0, w.apply(from.get(i)).minus(to.get(i)).length(), 1e-9, "point " + i);
        }
    }

    @Test
    void pointsMovedAsOneAffineMapMoveSpaceSo() {
        final List<V3> to = from.stream().map(this::affine).toList();
        final Warp w = ThinPlate.INSTANCE.through(from, to);
        final V3 elsewhere = new V3(7, -3, 11);
        assertEquals(0, w.apply(elsewhere).minus(affine(elsewhere)).length(), 1e-9);
    }

    private V3 affine(final V3 p) {
        return new V3(0.9 * p.x() + 1, 1.1 * p.y() + 0.2 * p.z(), p.z() - 2);
    }
}
