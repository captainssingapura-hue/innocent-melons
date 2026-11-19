package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Material;
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
 * The main office: the biggest hut there is, the biggest on the campus, of the greatest luxury; standing on level
 * ground east of the green, clear of every hut, road and lane, its door to the well. What every hut of luxury holds to,
 * it does ({@link LuxuryHutTest}).
 */
class MainOfficeTest {

    @Test
    void theBiggestHutThereIsAndOfTheGreatestLuxury() {
        assertEquals(1_400, MainOffice.SIZE.width(), "as wide as an earth house is made");
        for (final Office o : Campus.OFFICES) {
            assertTrue(o.hut().size().width() * 1.5 < MainOffice.SIZE.width(), o.name() + ": it half as wide again, and more");
        }
        for (final Department d : Department.ALL) {
            assertTrue(d.hut().size().width() < MainOffice.SIZE.width(), d.name() + ": smaller than it");
        }
        final List<Solid> solids = MainOffice.HUT.solids();
        for (final Material luxury : List.of(Material.LIMESTONE, Material.GLAZED, Material.LACQUER, Material.GLASS)) {
            assertTrue(solids.stream().anyMatch(s -> s.material() == luxury), "some of it " + luxury);
        }
    }

    @Test
    void onLevelGroundClearOfEveryHutRoadAndLaneItsDoorToTheWell() {
        final Place at = MainOffice.place();
        final double reach = MainOffice.HUT.reach();
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
}
