package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.CityWallSpec;
import com.example.fxsuite.building.spec.GateTowerSpec;
import com.example.fxsuite.building.spec.GateTowerSpec.Ascent;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A Song city's wall and gate. The wall as the Yingzao Fashi has it: half as thick again at its foot as it is high, as
 * thick at its top as it is high; in the way, too steep to walk up, stood on along its top. The gate: its passage
 * open from end to end at the ground, its head well over a rider's, its sides and its base either side in the way; its
 * frames' beams across over head height; its tower on its base, its roof over it; its way up a ramp inside the wall,
 * a foot up for every five along, walked up from the ground to the base's top, on the side asked for; nothing wrong
 * with either as what it is for, and a base too small for its passage or its tower refused.
 */
class SongWallTest {

    private static final CityWallSpec<Chinese, ChineseEras.NorthernSong> WALL = new CityWallSpec<>(2_000, 900);
    private static final GateTowerSpec<Chinese, ChineseEras.NorthernSong> GATE = new GateTowerSpec<>(3_000, 1_930, 960,
            560, 5, Ascent.RIGHT);
    private static final double BODY = 20;

    /** How far {@code solids} reach either way along, at their feet ({@code top} false) or at their tops. */
    private static double reach(final List<Solid> solids, final boolean top) {
        final List<Point> all = solids.stream().flatMap(s -> s.corners().stream()).toList();
        final double at = top ? all.stream().mapToDouble(Point::up).max().orElseThrow()
                : all.stream().mapToDouble(Point::up).min().orElseThrow();
        return all.stream().filter(p -> p.up() == at).mapToDouble(p -> Math.abs(p.along())).max().orElseThrow();
    }

    /** Whether a body standing on the ground at ({@code x}, {@code z}) is in nothing of {@code built}. */
    private static boolean clear(final Structure built, final double x, final double z) {
        return built.shape().stream().allMatch(v -> v.push(x, z, BODY, 0, 180) == null);
    }

    @Test
    void theWallHalfAsThickAgainAtItsFootAsItIsHighAsThickAtItsTopAsItIsHigh() {
        final Structure wall = new SongCityWall().build(WALL);
        assertEquals(List.of(), wall.problems());
        final List<Solid> rampart = wall.solids(new Elements.Wall());
        assertEquals(2, rampart.size(), "rammed 10 m at a time");
        assertEquals(1.5 * WALL.high(), 2 * reach(rampart, false), 1e-6);
        assertEquals(WALL.high(), 2 * reach(rampart, true), 1e-6);
        assertEquals(new Spaces.Terrace(), wall.rooms().getFirst().kind());
        assertEquals(WALL.high(), wall.rooms().getFirst().level());
    }

    @Test
    void theWallInTheWayTooSteepToWalkUpStoodOnAlongItsTop() {
        final Structure wall = new SongCityWall().build(WALL);
        final Volume bank = wall.shape().getFirst();
        assertEquals(1, wall.shape().size());
        for (final double z : new double[] {-600, 0, 600}) {
            assertFalse(clear(wall, 0, z), "in the way at " + z);
        }
        assertTrue(clear(wall, 0, 720) && clear(wall, 0, -720), "its foot 6.75 m from its middle");
        assertTrue(clear(wall, 1_030, 0) && clear(wall, -1_030, 0), "past its ends");
        // on its face, either side: what is under a body rises faster than is stepped up
        for (final double z : new double[] {-660, -560, 560, 660}) {
            assertTrue(bank.steep(0, z, BODY, bank.topAt(0, z, BODY)), "its face at " + z);
        }
        for (final double x : new double[] {-990, 0, 990}) {
            assertEquals(WALL.high(), bank.topAt(x, 0, BODY), 1e-6, "its top at " + x);
            assertFalse(bank.steep(x, 0, BODY, WALL.high()));
        }
    }

    @Test
    void theGatesPassageOpenFromEndToEndItsSidesAndItsBaseInTheWay() {
        final Structure gate = new SongGateTower().build(GATE);
        assertEquals(List.of(), gate.problems());
        assertEquals(3, gate.waysIn().size(), "its two ends, and the foot of its way up");
        final double d = GATE.deep() / 2;
        for (double z = -d - 100; z <= d + 100; z += 25) {
            for (final double x : new double[] {-200, 0, 200}) {
                final double at = z;
                assertTrue(clear(gate, x, z), "open at " + x + ", " + z);
                for (final Volume v : gate.shape().stream().filter(v -> v.covers(x, at, BODY)).toList()) {
                    assertTrue(v.topAt(x, z, BODY, 25) <= 0, "nothing to step over at " + x + ", " + z);
                    assertTrue(v.undersideAt(x, z, BODY, 0) >= 400, "its head over a rider's at " + x + ", " + z);
                }
            }
        }
        assertEquals(SongGateTower.HEAD, gate.shape().get(2).undersideAt(0, 0, BODY, 0), 1e-6);
        for (final double x : new double[] {-1_400, -600, -290, 290, 600, 1_400}) {
            assertFalse(clear(gate, x, 0), "its base at " + x);
            assertFalse(clear(gate, x, d - 30), "and its face");
        }
        assertTrue(clear(gate, 1_540, 0), "past its base");
        // its faces battered as they are drawn: too steep to walk up, its top no wider than its railings
        final Volume pier = gate.shape().get(1);
        assertTrue(pier.steep(900, 850, BODY, pier.topAt(900, 850, BODY)) && pier.topAt(900, 850, BODY) < GATE.high());
        assertEquals(GATE.high(), pier.topAt(900, 700, BODY), 1e-6);
    }

    @Test
    void itsFramesBeamsOverHeadHeightItsTowerOnItsBaseUnderItsRoof() {
        final Structure gate = new SongGateTower().build(GATE);
        final List<Solid> beams = gate.solids().stream().filter(s -> s.name().equals("passage beam")).toList();
        assertEquals(13, beams.size(), "a frame every 1.5 m or so of its 19.3 m");
        assertTrue(beams.stream().flatMap(s -> s.corners().stream()).allMatch(p -> p.up() > 550 && p.up() < 600));
        assertEquals(26, gate.solids().stream().filter(s -> s.name().equals("passage post")).count());
        assertEquals(2, gate.solids(new Elements.Door()).size(), "two leaves, open");
        // its base: at its top narrower than at its foot by half its height; stood on
        final List<Solid> base = gate.solids(new Elements.Platform());
        assertEquals(GATE.deep(), 2 * reach(base, false), 1e-6);
        assertEquals(GATE.deep() - GATE.high() / 2, 2 * reach(base, true), 1e-6);
        assertEquals(GATE.high(), gate.shape().get(1).topAt(900, 0, BODY), 1e-6);
        // its tower: its floor the base's top, twelve columns standing on it, its roof over them
        final Structure.Room tower = gate.rooms().get(1);
        assertEquals(new Spaces.Hall(), tower.kind());
        assertEquals(GATE.high(), tower.level());
        final List<Solid> columns = gate.solids().stream().filter(s -> s.name().equals("column")).toList();
        assertEquals(12, columns.size());
        assertTrue(columns.stream().flatMap(s -> s.corners().stream()).allMatch(p -> p.up() >= GATE.high()));
        final double ridge = gate.solids(new Elements.Roof()).stream().flatMap(s -> s.corners().stream())
                .mapToDouble(Point::up).max().orElseThrow();
        assertTrue(ridge > GATE.high() + 500 && ridge < GATE.high() + 700, "its ridge at " + ridge);
        assertFalse(gate.solids(new Elements.Railing()).isEmpty());
    }

    @Test
    void itsWayUpARampOneInFiveWalkedUpFromTheGroundToItsBasesTop() {
        final Structure gate = new SongGateTower().build(GATE);
        final double[] up = new SongGateTower().wayUp(GATE);
        assertEquals(GATE.wide() / 2, up[0], 1e-6, "its head at the base's end, on its right");
        assertEquals(5 * GATE.high(), up[1] - up[0], 1e-6, "a foot up for every five along");
        assertTrue(up[3] < 0 && up[2] == -GATE.deep() / 2, "inside the wall");
        assertEquals(1, gate.solids(new Elements.Incline()).size());
        // its passage reached by its ends, its way up by its foot, its base's top by it; the tower stands open on that
        assertEquals(List.of(gate.rooms().get(0), gate.rooms().get(3), gate.rooms().get(2)), gate.reached());
        assertEquals(new Spaces.Terrace(), gate.rooms().get(2).kind());
        assertEquals(new Spaces.Ramp(), gate.rooms().get(3).kind());
        // walked up: from the ground before its foot to the base's top past its head, never more than a step at once
        final double z = up[3] - 150;
        double at = 0;
        for (double x = up[1] + 50; x >= up[0] - 100; x -= 10) {
            final double from = at;
            final double here = x;
            final double next = gate.shape().stream().filter(v -> v.covers(here, z, 5))
                    .mapToDouble(v -> v.topAt(here, z, 5, from + 20)).filter(t -> t <= from + 20).max().orElse(0);
            assertTrue(next >= at - 1e-6 && next - at <= 3, "at " + x + ": from " + at + " to " + next);
            assertTrue(gate.shape().stream().noneMatch(v -> v.push(here, z, BODY, next + 20, next + 180) != null),
                    "nothing in the way at " + x);
            at = next;
        }
        assertEquals(GATE.high(), at, 1e-6, "on the base's top");
        // what of its head is past the base's top's edge railed across: nothing to go on to there
        assertTrue(gate.shape().stream().anyMatch(v -> v.push(up[0], up[2] + 100, BODY, GATE.high() + 20,
                GATE.high() + 180) != null));
        // on the other side, asked for
        final double[] left = new SongGateTower().wayUp(new GateTowerSpec<>(3_000, 1_930, 960, 560, 5, Ascent.LEFT));
        assertEquals(-up[0], left[0], 1e-6);
        assertEquals(-up[1], left[1], 1e-6);
    }

    @Test
    void aBaseTooSmallForItsPassageOrItsTowerRefused() {
        assertThrows(IllegalArgumentException.class, () -> new GateTowerSpec<Chinese, ChineseEras.NorthernSong>(900,
                1_930, 960, 560, 1, Ascent.LEFT), "no wider than its passage");
        assertThrows(IllegalArgumentException.class, () -> new SongGateTower().build(new GateTowerSpec<>(3_000, 1_930,
                700, 560, 5, Ascent.LEFT)), "too low over its passage");
        assertThrows(IllegalArgumentException.class, () -> new SongGateTower().build(new GateTowerSpec<>(3_000, 900,
                960, 560, 5, Ascent.LEFT)), "too shallow for its tower");
        assertThrows(IllegalArgumentException.class, () -> new SongGateTower().build(new GateTowerSpec<>(1_800, 1_930,
                960, 560, 5, Ascent.LEFT)), "too narrow for its tower");
        assertThrows(IllegalArgumentException.class, () -> new CityWallSpec<Chinese, ChineseEras.NorthernSong>(2_000,
                100), "a wall under 2 m");
    }
}
