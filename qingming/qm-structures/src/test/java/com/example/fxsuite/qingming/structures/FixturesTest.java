package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.qingming.land.Reach;
import com.example.fxsuite.qingming.land.Rivers;
import com.example.fxsuite.taxonomy.Leaf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What stands in the reach: nothing wrong with its map; the Rainbow Bridge at its origin, landing on the street both
 * ways; fourteen shops along the street, each facing it and clear of it; the wine house and its gateway, two tea
 * houses, two warehouses, seven stalls; the gate where the street meets the wall, facing the bridge, the street level
 * through it, the wall either side of it from its base to the reach's edges; the quay before the warehouses, its face
 * in the water, its deck level with the bank, its landings over the water; four farmsteads in the country, each facing
 * the road and clear of it; none of them in another, none but the bridge and the quay in the river; each a piece of
 * the world.
 */
class FixturesTest {

    private static final List<Atlas.Fixture> ALL = Fixtures.INSTANCE.fixtures();

    private static long count(final Leaf<?> thing) {
        return ALL.stream().filter(f -> f.thing().equals(thing)).count();
    }

    /** {@code f}'s floors' reach in the world: least and most east, least and most north; its turn a quarter's. */
    private static double[] reach(final Atlas.Fixture f) {
        return reach(f, f.structure().rooms().stream().flatMap(room -> room.outline().stream()).toList());
    }

    /** Each of {@code f}'s floors' reach in the world. */
    private static List<double[]> floors(final Atlas.Fixture f) {
        return f.structure().rooms().stream().map(room -> reach(f, room.outline())).toList();
    }

    private static double[] reach(final Atlas.Fixture f, final List<Vec2> outline) {
        final double r = Math.toRadians(f.yaw());
        final double[] box = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY};
        for (final Vec2 p : outline) {
            // as the game turns a thing: its right (cos, -sin), its ahead (sin, cos)
            final double x = f.x() + Math.cos(r) * p.x() + Math.sin(r) * p.z();
            final double z = f.z() - Math.sin(r) * p.x() + Math.cos(r) * p.z();
            box[0] = Math.min(box[0], x);
            box[1] = Math.max(box[1], x);
            box[2] = Math.min(box[2], z);
            box[3] = Math.max(box[3], z);
        }
        return box;
    }

    @Test
    void nothingWrongWithItsMapTheBridgeAtItsOriginLandingOnTheStreet() {
        assertEquals(List.of(), Fixtures.INSTANCE.problems());
        final Atlas.Fixture bridge = ALL.getFirst();
        assertEquals("the Rainbow Bridge", bridge.name());
        assertEquals(new Buildings.Bridge(), bridge.thing());
        assertEquals(0, Math.hypot(bridge.x(), bridge.z()));
        assertEquals(0, Reach.INSTANCE.ground().height(0, 1_250), 30, "the street where it lands, north");
        assertEquals(0, Reach.INSTANCE.ground().height(0, -1_250), 30, "and south");
    }

    @Test
    void fourteenShopsAlongTheStreetEachFacingItAndClearOfIt() {
        final List<Atlas.Fixture> shops = ALL.stream().filter(f -> f.thing().equals(new Buildings.ShopHouse()))
                .toList();
        assertEquals(14, shops.size());
        for (final Atlas.Fixture shop : shops) {
            final boolean north = shop.z() > Fixtures.STREET;
            assertEquals(north ? 180 : 0, shop.yaw(), shop.name() + " facing the street");
            // its front 3 m back from the street's edge, its eaves, 90 cm out past it, clear of the street
            final double[] box = reach(shop);
            final double front = north ? box[2] - Fixtures.STREET : Fixtures.STREET - box[3];
            assertEquals(Fixtures.BACK, front, 1e-6, shop.name());
            assertTrue(front - 90 > 400, shop.name() + " clear of the street");
            assertTrue(shop.x() < -10_000 && shop.x() > -30_000, shop.name() + " on the straight stretch");
        }
    }

    @Test
    void theWineHouseAndItsGatewayTheTeaHousesWarehousesAndStalls() {
        assertEquals(1, count(new Buildings.WineHouse()));
        assertEquals(1, count(new Buildings.FestiveGateway()));
        assertEquals(3, count(new Buildings.TeaHouse()));
        assertEquals(2, count(new Buildings.Warehouse()));
        assertEquals(7, count(new Buildings.Stall()));
        // the gateway before the wine house's door: on its middle line, between it and the street
        final Atlas.Fixture wine = ALL.stream().filter(f -> f.name().equals("the wine house")).findFirst()
                .orElseThrow();
        final double[] gate = reach(ALL.stream().filter(f -> f.name().endsWith("gateway")).findFirst().orElseThrow());
        assertEquals(wine.x(), (gate[0] + gate[1]) / 2, 1e-6);
        assertTrue(gate[3] < reach(wine)[2] - 90 && gate[2] > Fixtures.STREET + 400,
                "clear of its eaves and the street");
    }

    @Test
    void theGateWhereTheStreetMeetsTheWallTheWallEitherSideOfIt() {
        final Atlas.Fixture gate = ALL.stream().filter(f -> f.thing().equals(new Buildings.GateTower())).findFirst()
                .orElseThrow();
        assertEquals(Reach.INSTANCE.gate(), new Vec2(gate.x(), gate.z()));
        assertEquals(90, gate.yaw(), "its outer face east, toward the bridge");
        // the street through its passage: level, and no more than a step from the ground the gate stands on
        for (double x = Reach.WALL - 1_500; x <= Reach.WALL + 1_500; x += 100) {
            for (final double z : new double[] {-200, 0, 200}) {
                assertEquals(0, Reach.INSTANCE.land().height(x, gate.z() + z), 20, "the street at " + x + ", " + z);
            }
        }
        final List<Atlas.Fixture> wall = ALL.stream().filter(f -> f.thing().equals(new Buildings.CityWall())).toList();
        assertEquals(2, wall.size());
        final double[] north = reach(wall.get(0));
        final double[] south = reach(wall.get(1));
        assertEquals(gate.z() + Fixtures.GATE.wide() / 2, north[2], 1e-6, "from the gate's base");
        assertEquals(Reach.NORTH - Fixtures.SHORT, north[3], 1e-6, "to the reach's edge");
        assertEquals(gate.z() - Fixtures.GATE.wide() / 2, south[3], 1e-6);
        // the gate's way up inside the wall, west of it, south of the gate: its foot 48 m from the gate's base
        final double[] up = floors(gate).get(3);
        assertTrue(up[1] <= Reach.WALL && up[3] <= south[3] + 1e-6, "inside the wall, south of the gate");
        assertEquals(5 * Fixtures.GATE.high(), up[3] - up[2], 1e-6);
        assertEquals(Reach.SOUTH + Fixtures.SHORT, south[2], 1e-6);
        for (final double[] box : List.of(north, south)) {
            assertEquals(Reach.WALL, (box[0] + box[1]) / 2, 1e-6, "on the wall's line");
            assertEquals(Fixtures.RAMPART, box[1] - box[0], 1e-6, "its top as wide as it is high");
        }
    }

    /** How far {@code p} is from {@code line}, a point every so far along it. */
    private static double from(final Vec2 p, final List<Vec2> line) {
        double best = Double.POSITIVE_INFINITY;
        for (int k = 0; k + 1 < line.size(); k++) {
            final Vec2 a = line.get(k);
            final Vec2 b = line.get(k + 1);
            final double dx = b.x() - a.x();
            final double dz = b.z() - a.z();
            final double t = Math.max(0, Math.min(1, ((p.x() - a.x()) * dx + (p.z() - a.z()) * dz)
                    / (dx * dx + dz * dz)));
            best = Math.min(best, Math.hypot(p.x() - a.x() - t * dx, p.z() - a.z() - t * dz));
        }
        return best;
    }

    /** Where {@code f}'s own ({@code across}, {@code along}) is in the world. */
    private static Vec2 world(final Atlas.Fixture f, final double across, final double along) {
        final double r = Math.toRadians(f.yaw());
        return new Vec2(f.x() + Math.cos(r) * across + Math.sin(r) * along,
                f.z() - Math.sin(r) * across + Math.cos(r) * along);
    }

    @Test
    void theQuayBeforeTheWarehousesItsFaceInTheWaterItsDeckLevelWithTheBank() {
        final List<Atlas.Fixture> quay = ALL.stream().filter(f -> f.thing().equals(new Buildings.Quay())).toList();
        assertEquals(2, quay.size());
        for (final Atlas.Fixture q : quay) {
            assertEquals(180, q.yaw(), 10, q.name() + " facing the river, south");
            assertEquals(0, Reach.INSTANCE.land().height(q.x(), q.z()), 2, q.name() + " on the bank's top, level");
            final double face = from(world(q, 0, 200), Rivers.INSTANCE.line());
            // the water's edge so far from the river's middle, up its bank from its bed's edge
            final double edge = Rivers.INSTANCE.channel().across(Rivers.DEPTH - Rivers.WATER);
            assertTrue(face > edge - 60 && face < edge, q.name() + " its face at " + face);
            final double landing = q.structure().rooms().get(1).level();
            assertTrue(landing > Rivers.WATER - Rivers.DEPTH && landing < Rivers.WATER - Rivers.DEPTH + 30,
                    q.name() + " its landing just over the water");
        }
        final double[] east = reach(quay.get(0));
        final double[] west = reach(quay.get(1));
        assertEquals(Fixtures.QUAY_EAST, east[1], 30, "its east end");
        assertEquals(Fixtures.QUAY_WEST, west[0], 30, "its west end");
        assertEquals(east[0], west[1], 30, "its stretches meeting");
    }

    @Test
    void fourFarmsteadsInTheCountryEachFacingTheRoadAndClearOfIt() {
        final List<Atlas.Fixture> farms = ALL.stream().filter(f -> f.thing().equals(new Buildings.Farmhouse()))
                .toList();
        assertEquals(4, farms.size());
        final List<Vec2> road = new java.util.ArrayList<>();
        final var way = Reach.INSTANCE.roads().roads().stream().filter(r -> r.name().equals("the road to the bridge"))
                .findFirst().orElseThrow();
        for (int k = 0; k < way.size(); k++) {
            road.add(way.point(k));
        }
        for (final Atlas.Fixture farm : farms) {
            assertTrue(farm.x() > 30_000, farm.name() + " in the country");
            final double gate = from(world(farm, 0, farm.structure().rooms().get(1).outline().get(2).z()), road);
            assertEquals(Fixtures.ROAD + Fixtures.FARM_BACK, gate, 100, farm.name() + " its gateway toward the road");
            final double[] box = reach(farm);
            for (final double x : new double[] {box[0], box[1]}) {
                for (final double z : new double[] {box[2], box[3]}) {
                    assertTrue(from(new Vec2(x, z), road) > Fixtures.ROAD + 400, farm.name() + " clear of the road");
                }
            }
        }
    }

    @Test
    void noneOfThemInAnotherNoneInTheRiver() {
        for (int i = 0; i < ALL.size(); i++) {
            final double[] a = reach(ALL.get(i));
            for (int j = i + 1; j < ALL.size(); j++) {
                // stretches of the quay meet end to end, at a little angle
                final Buildings.Quay quay = new Buildings.Quay();
                if (ALL.get(i).thing().equals(quay) && ALL.get(j).thing().equals(quay)) {
                    continue;
                }
                // no floor of the one over a floor of the other
                for (final double[] f : floors(ALL.get(i))) {
                    for (final double[] b : floors(ALL.get(j))) {
                        assertTrue(f[1] <= b[0] || b[1] <= f[0] || f[3] <= b[2] || b[3] <= f[2],
                                ALL.get(i).name() + " and " + ALL.get(j).name());
                    }
                }
            }
            if (ALL.get(i).thing().equals(new Buildings.Bridge()) || ALL.get(i).thing().equals(new Buildings.Quay())) {
                continue;
            }
            // all but the bridge and the quay on the plain: no corner of its floor down the river's bank
            for (final double x : new double[] {a[0], a[1]}) {
                for (final double z : new double[] {a[2], a[3]}) {
                    assertTrue(Reach.INSTANCE.ground().height(x, z) > -60,
                            ALL.get(i).name() + " on the plain at " + x + ", " + z);
                }
            }
        }
    }

    @Test
    void eachAPieceOfTheWorld() {
        assertEquals(38, Fixtures.INSTANCE.pieces().size());
    }
}
