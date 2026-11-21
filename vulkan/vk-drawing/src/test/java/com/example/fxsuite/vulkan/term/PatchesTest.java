package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A surface in patches of no two shapes, and two surfaces as one: every point in one patch, about one patch to each
 * square of the grid, never further inside than a patch is big; and a surface standing over another, each keeping
 * its own terms and coming down to its own average.
 */
class PatchesTest {

    private final Seen seen = new Seen();
    private final Patches patches = new Patches(seen.at().across(), seen.at().up(), 10, 1);

    @Test
    void everyPointIsInOnePatchAboutOneToEachSquareAndNeverFurtherInThanAPatchIsBig() {
        final Reckoning inside = new Reckoning(patches.inside().node());
        final Reckoning which = new Reckoning(patches.scatter(3).number().node());
        final Set<Double> met = new HashSet<>();
        for (int across = 0; across < 100; across++) {
            for (int up = 0; up < 100; up++) {
                final Sample at = seen.sample(new Vec3(across * 1.01 - 50, up * 1.01 - 50, 0), 0);
                final double in = inside.at(at)[0];
                assertTrue(in >= 0 && in < 10, "inside its patch, by less than a square: " + in);
                met.add(which.at(at)[0]);
            }
        }
        // 101 cm by 101 cm in squares of 10: about a hundred patches, a few more at the edges
        assertTrue(met.size() > 90 && met.size() < 150, "about one patch to a square: " + met.size());
    }

    @Test
    void aPatchIsOnePatchAllOverAndItsNeighbourAnother() {
        final Reckoning which = new Reckoning(patches.scatter(3).number().node());
        final Reckoning inside = new Reckoning(patches.inside().node());
        int checked = 0;
        for (int step = 0; step < 400; step++) {
            final Vec3 here = new Vec3(step * 0.37, step * 0.21, 0);
            final Vec3 beside = here.plus(new Vec3(0.05, 0.05, 0));
            if (inside.at(seen.sample(here, 0))[0] > 0.2) {
                assertEquals(which.at(seen.sample(here, 0))[0], which.at(seen.sample(beside, 0))[0],
                        "two points well inside one patch");
                checked++;
            }
        }
        assertTrue(checked > 200, "most points are well inside a patch: " + checked);
    }

    @Test
    void oneSurfaceOverAnotherKeepsEachItsOwnTermsAndItsOwnAverage() {
        // white below the line at 50 cm up, half speckled black; black above it, a tenth speckled white
        final Real above = seen.at().up().past(new Real(50));
        final Scatter grains = new Scatter(seen.at().across().floored(), seen.at().up().floored(), 1, 1);
        final Surface under = new Surface(new Colour(1, 1, 1),
                List.of(new Term("black", new Colour(0, 0, 0), grains.some(0.5, seen.covers()), 1)),
                new Colour(0, 0, 0), 8);
        final Surface over = new Surface(new Colour(0, 0, 0),
                List.of(new Term("white", new Colour(1, 1, 1), grains.some(0.1, seen.covers()), 1)),
                new Colour(0, 0, 0), 8);
        final Surface both = over.above(under, above);
        assertEquals(List.of("black", "white"), both.terms().stream().map(Term::name).toList());
        final Surface far = both.coarser(100, new Averaged(new Panel(100, 100, 2.3).formed()));
        assertEquals(0.5, far.colour().node().worked(seen.sample(new Vec3(3, 20, 0), 0))[0], 0.03, "below: half");
        assertEquals(0.1, far.colour().node().worked(seen.sample(new Vec3(3, 80, 0), 0))[0], 0.03, "above: a tenth");
    }

    @Test
    void aPlaceMovedIsThatMuchFurtherAcrossAndUp() {
        final Point moved = seen.at().moved(2, -60);
        assertEquals(7, moved.across().node().worked(seen.sample(new Vec3(5, 63, 9), 0))[0]);
        assertEquals(3, moved.up().node().worked(seen.sample(new Vec3(5, 63, 9), 0))[0]);
    }
}
