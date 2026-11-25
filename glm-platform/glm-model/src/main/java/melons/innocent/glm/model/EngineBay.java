package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Carrying;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineInternals;
import melons.innocent.glm.engine.EngineRange;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The engine in its bay: one of the range, lengthways, its timing end forward, as the current large SUV's is; its
 * crank's axis down the car's middle, 52 cm over the ground, its block's rear face 10 cm ahead of the front axle,
 * its cylinders ahead of it, clear of the subframe and the steering gear's pinion; with its covers on or off. Carried
 * from its own frame, front to back along its crank, into the car's, turned end for end. Held on two mounts, simple: on
 * each side a rubber mount on top of the subframe's strut tube, and a cast arm from it across to the block's boss.
 * Every figure an estimate.
 *
 * @param engine  which engine
 * @param covered whether its covers are on: its head covers, its coils, its timing cover and its trim cover
 */
public record EngineBay(EngineFigures engine, boolean covered) {

    /** How high its crank's axis stands over the ground; where its block's rear face is, ahead of the front axle. */
    private static final double CRANK = 52;
    private static final double REAR = 10;
    /** A mount's rubber: how far round, how high; its arm's thickness. */
    private static final double RUBBER = 3;
    private static final double RUBBER_HIGH = 4;
    private static final double ARM = 1.6;
    private static final Making RUBBER_MADE = new Making(new Made(Material.RUBBER, true));
    private static final Making CAST = new Making(new Made(Material.ALUMINIUM, true));

    /** The V6, covered, as the car comes. */
    public EngineBay() {
        this(new EngineRange().v6(), true);
    }

    /** What carries the engine from its own frame into the car's: turned end for end, set where it goes. */
    Carrying carrying() {
        return new Carrying(new Mat4(new double[] {-1, 0, 0, 0, 0, 1, 0, CRANK, 0, 0, -1, REAR + engine.length(),
            0, 0, 0, 1}));
    }

    /** How the engine and its mounts are joined to the subframe's strut tube {@code strut}, in the order walked. */
    List<Connection> joined(final Part strut, final FrontPoints points) {
        final Platform carried = carrying().carried(new EngineInternals(engine).platform(covered));
        final Part block = carried.root();
        final Vec3 seat = points.engineMount();
        final Vec3 top = seat.plus(new Vec3(0, RUBBER_HIGH, 0));
        final Vec3 boss = block.point("mount, right").at();
        final Shapes shapes = new Shapes(16);
        final Part rubber = RUBBER_MADE.part("engine mount", Group.ENGINE, true, named("foot", seat, "top", top),
                shapes.drum(seat, top, RUBBER));
        final Mesh arm = shapes.joined(List.of(shapes.pipe(List.of(top, new Vec3(top.x(), boss.y(), top.z()), boss),
                ARM), shapes.drum(top, top.plus(new Vec3(0, 1.2, 0)), RUBBER + 0.8)));
        final Part bracket = CAST.part("engine mount arm", Group.ENGINE, true, named("mount", top, "block", boss),
                arm);
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("engine mount", strut, "engine mount", rubber, "foot", Joining.SEATED));
        joined.add(new Connection("engine mount arm", rubber, "top", bracket, "mount", Joining.BUSHED));
        joined.add(new Connection("engine on its mounts", bracket, "block", block, "mount, right", Joining.BOLTED));
        joined.addAll(carried.connections());
        return joined;
    }

    private Map<String, Vec3> named(final String a, final Vec3 at, final String b, final Vec3 bt) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put(a, at);
        named.put(b, bt);
        return named;
    }
}
