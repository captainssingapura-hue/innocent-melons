package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.worlds.Meadow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The main office: the biggest hut there is, the biggest on the campus; standing on level ground east of the green,
 * clear of every hut, road and lane, its door to the well; raised on its plinth, its steps up to it before its door,
 * each a step's height; furnished within its walls, nothing standing in its doorway.
 */
class MainOfficeTest {

    @Test
    void theBiggestHutThereIsAndOfTheGreatestLuxury() {
        assertEquals(1_400, MainOffice.SIZE.width(), "as wide as an earth house is made");
        for (final Office o : Campus.OFFICES) {
            assertTrue(o.hut().size().width() * 1.5 < MainOffice.SIZE.width(), o.name() + ": it half as wide again, and more");
        }
        final List<Solid> solids = MainOffice.solids();
        for (final Material luxury : List.of(Material.LIMESTONE, Material.GLAZED, Material.LACQUER, Material.GLASS)) {
            assertTrue(solids.stream().anyMatch(s -> s.material() == luxury), "some of it " + luxury);
        }
    }

    @Test
    void onLevelGroundClearOfEveryHutRoadAndLaneItsDoorToTheWell() {
        final Place at = MainOffice.place();
        final double reach = MainOffice.reach();
        for (final Office o : Campus.OFFICES) {
            final double apart = Math.hypot(o.place().x() - at.x(), o.place().z() - at.z());
            assertTrue(apart > reach + o.hut().size().width() / 2 + 500, o.name() + ": at least 5 m clear, " + apart);
        }
        for (final String road : List.of("village road", "church lane")) {
            for (final Vec2 v : Village.line(road)) {
                assertTrue(Math.hypot(v.x() - at.x(), v.z() - at.z()) > reach + 1_000, road + ": 10 m clear");
            }
        }
        for (int k = 0; k < 16; k++) {
            final double a = 2 * Math.PI * k / 16;
            final double x = at.x() + reach * Math.sin(a);
            final double z = at.z() + reach * Math.cos(a);
            assertEquals(at.height(), Meadow.land().height(x, z), 5, "level under it, all round");
        }
        final double toWell = Math.toDegrees(Math.atan2(Village.WELL.x() - at.x(), Village.WELL.z() - at.z()));
        assertEquals(toWell, at.yaw(), 1e-9, "its door to the well");
    }

    @Test
    void onItsPlinthItsStepsUpToItBeforeItsDoorEachAStepHigh() {
        final int steps = (int) Math.round(MainOffice.PLINTH / MainOffice.STEP);
        assertEquals(MainOffice.PLINTH, steps * MainOffice.STEP, 1e-9, "risers all one height");
        final List<Solid> treads = MainOffice.grounds().stream().filter(s -> s.name().startsWith("step ")).toList();
        assertEquals(steps - 1, treads.size(), "a step fewer than its risers: the plinth the top one");
        for (int s = 1; s < steps; s++) {
            final int n = s;
            final Solid step = treads.stream().filter(t -> t.name().equals("step " + n)).findFirst().orElseThrow();
            assertEquals(MainOffice.PLINTH - s * MainOffice.STEP, top(step), 1e-9, "step " + s);
            assertTrue(MainOffice.stepTo(s) > MainOffice.plinth(), "step " + s + " before the plinth");
            // the next step down begins where this one ends: no two of them overlapping, no faces in one plane
            if (s > 1) {
                assertEquals(MainOffice.stepTo(s - 1), MainOffice.stepFrom(s), 1e-9);
            }
        }
        assertTrue(MainOffice.STEPS_WIDE > MainOffice.SIZE.door(), "wider than its door");
    }

    @Test
    void furnishedWithinItsWallsItsDoorwayClear() {
        final double inside = MainOffice.inside() - MainOffice.CLEAR;
        // the doorway, from its middle out through the walls, so wide: nothing stands in it
        final double door = MainOffice.SIZE.door() / 2 + 20;
        for (final Solid s : MainOffice.interior()) {
            for (final Point p : s.corners()) {
                assertTrue(Math.hypot(p.across(), p.along()) <= inside + 1e-6, s.name() + ": within its walls");
                assertTrue(p.up() >= MainOffice.FLOOR - 1e-9, s.name() + ": on its floor");
            }
            if (!s.name().equals("rug")) {
                final boolean inDoorway = s.corners().stream().anyMatch(p -> Math.abs(p.across()) < door
                        && p.along() > MainOffice.inside() - 150);
                assertTrue(!inDoorway, s.name() + ": not in its doorway");
            }
        }
    }

    private static double top(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
