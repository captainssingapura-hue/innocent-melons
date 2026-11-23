package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.trunk.TrapeziusLower;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A region to be simulated soft and what it takes in: a muscle half in it taken, one only a sliver in it held as a
 * wall; where the taken is cut, on the region's faces; how much of it lies in the region, half of it.
 */
class RegionPiecesTest {

    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);
    private final SoftRegion region = new SoftRegion(new V3(0, -10, -10), new V3(10, 10, 10));

    @Test
    void halfInIsTakenASliverIsHeldAndTheCutIsOnTheFaces() {
        final RegionPieces.Piece half = piece(0);
        final RegionPieces.Piece sliver = piece(-2.8);
        final RegionPieces.Taken taken = RegionPieces.INSTANCE.taken(region, List.of(half, sliver), rig, 0.25);
        assertEquals(List.of(half), taken.taken(), "half in, taken");
        assertEquals(List.of(sliver), taken.held(), "a sliver in, held");
        assertTrue(!taken.cuts().isEmpty(), "cut where it goes out");
        for (final V3 c : taken.cuts()) {
            assertEquals(0, c.x(), 1e-9, "on the face it goes out through");
        }
        final RegionPieces.Filled filled = RegionPieces.INSTANCE.filled(region, taken.taken());
        assertEquals(0.5 * 4 * Math.PI * 27 / 3, filled.each()[0], 6, "half of it in");
        assertEquals(0, filled.shared(), 1e-9, "alone, sharing none");
    }

    /** A ball of 3 cm about ({@code x}, 0, 0), a muscle's shape. */
    private RegionPieces.Piece piece(final double x) {
        final Sketch s = new Sketch();
        s.ball(new V3(x, 0, 0), 3);
        final Mesher.Mesh m = Mesher.of(new Field(s.done().elements()), 0.3);
        final float[] all = new float[m.size()];
        Arrays.fill(all, 1);
        return new RegionPieces.Piece(TrapeziusLower.INSTANCE, false,
                new MuscleSurface.Surface(m, new float[m.size()], List.of(Part.CHEST), new float[][] {all}, 1));
    }
}
