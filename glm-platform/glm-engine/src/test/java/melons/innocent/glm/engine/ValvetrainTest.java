package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The valvetrains: each engine's put together in its heads, two camshafts, four valves and four springs to a cylinder;
 * every valve shut while its cylinder fires, compresses and burns, its exhaust open the most in its exhaust stroke and
 * its inlet in its inlet stroke; each cam's lobe reaching toward its valve's bucket as far as the valve is lifted,
 * however far the crank has turned; and no valve ever meeting its piston.
 */
class ValvetrainTest {

    private final EngineRange range = new EngineRange();

    @Test
    void eachPutTogetherInItsHeads() {
        for (final EngineFigures e : range.all()) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled built = new Assembly(inside.platform()).assembled();
            assertEquals(List.of(), built.problems(1e-6), e.name());
            assertEquals(2 * e.banks() + 8 * e.cylinders(), inside.valvetrain().count(), e.name());
            for (final var p : built.placed().keySet()) {
                assertEquals(0, built.at(p).length(), 1e-9, p.part().name() + " made where it goes");
            }
        }
    }

    @Test
    void everyValveShutAsItsCylinderFiresAndOpenInItsOwnStroke() {
        for (final EngineFigures e : range.all()) {
            final Valvetrain train = new EngineInternals(e).valvetrain();
            final Firing firing = new Firing(e);
            final ValveTiming timing = new ValveTiming(e);
            for (final Valve v : train.valves()) {
                final double fires = firing.fires(v.bank(), v.k());
                for (double since = -90; since <= 120; since += 5) {
                    assertEquals(0, train.lift(v, fires + since), 1e-12, e.name() + " " + v.called() + " " + since);
                }
                final double most = fires + (v.inlet() ? 450 : 270);
                assertTrue(train.lift(v, most) > timing.most() * 0.9, e.name() + " " + v.called() + " open");
            }
        }
    }

    @Test
    void eachLobeReachingAsFarAsItsValveIsLifted() {
        for (final EngineFigures e : List.of(range.l5(), range.v6(), range.v12())) {
            final Valvetrain train = new EngineInternals(e).valvetrain();
            for (final Camshaft cam : train.camshafts()) {
                for (final Valve v : train.valves()) {
                    if (v.bank() == cam.bank() && v.inlet() == cam.inlet()) {
                        for (double turned = 0; turned < 720; turned += 7) {
                            assertEquals(cam.base() + train.lift(v, turned), cam.reach(v, turned), 1e-9,
                                    e.name() + " " + v.called() + " at " + turned);
                        }
                    }
                }
            }
        }
    }

    @Test
    void noValveEverMeetingItsPiston() {
        for (final EngineFigures e : range.all()) {
            final Valvetrain train = new EngineInternals(e).valvetrain();
            final HeadFigures head = new HeadFigures(e);
            for (final Valve v : train.valves()) {
                final Vec3 a = head.axis(v.inlet());
                final double rim = head.seat(v.inlet(), v.side()).y() - head.valve(v.inlet()) / 2 * Math.abs(a.x());
                final Cylinder c = new Cylinder(e, v.bank(), v.k());
                for (double turned = 0; turned < 720; turned += 2) {
                    final double valve = rim - train.lift(v, turned) * a.y();
                    final double crown = c.smallEnd(turned).dot(c.up()) + e.crown() - e.deck();
                    assertTrue(valve - crown > 0.1, e.name() + " " + v.called() + " at " + turned + ": "
                            + (valve - crown));
                }
            }
        }
    }

    @Test
    void theValveAndItsSpringMovingTogether() {
        final EngineFigures e = range.v8();
        final Valvetrain train = new EngineInternals(e).valvetrain();
        final Valve v = train.valves().getFirst();
        final double lift = 0.8;
        final Vec3 top = v.springAt().plus(v.way().times(v.fitted()));
        assertEquals(0, moved(v.lifted(lift), top).minus(moved(v.squeezed(lift), top)).length(), 1e-9,
                "the retainer and the spring's top moving as one");
        assertEquals(0, moved(v.squeezed(lift), v.springAt()).minus(v.springAt()).length(), 1e-9,
                "the spring's seat staying put");
        final Part part = v.valve();
        assertTrue(part.name().contains("valve"));
    }

    private Vec3 moved(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }
}
